package com.jcthenerd.kafka.rest.source.connector.task

import com.google.gson.JsonObject
import com.jcthenerd.kafka.rest.source.connector.config.KafkaRestConfigDef
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import io.ktor.client.request.url
import kotlinx.coroutines.runBlocking
import org.apache.kafka.connect.source.SourceRecord
import org.apache.kafka.connect.source.SourceTask
import java.lang.RuntimeException
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class KafkaRestSourceTask: SourceTask() {

    lateinit var restUrls: List<String>
    lateinit var topicName: String
    private val urlQueue = LinkedList<String>()
    var pollOffset: MutableMap<String, Long> = mutableMapOf()

    private var currentlyRunning: AtomicBoolean = AtomicBoolean(false)

    private val client = HttpClient(Apache) {
        followRedirects = true

        install(JsonFeature) {
            serializer = GsonSerializer()
        }
    }

    override fun start(props: MutableMap<String, String>?) {

        restUrls = props?.get(KafkaRestConfigDef.REST_CONFIG.NAME)?.split(",") ?: throw RuntimeException()
        topicName = props[KafkaRestConfigDef.TOPIC_CONFIG.NAME] ?: throw RuntimeException()


        urlQueue.addAll(restUrls)
        println("QUEUE: ${urlQueue}")
    }

    @Synchronized
    override fun stop() {
        // Does Nothing
    }

    override fun version() = "0.0.1"

    override fun poll(): MutableList<SourceRecord>? {
        val recordList = mutableListOf<SourceRecord>()
        val urlToSearch = urlQueue.peek()
        println(urlToSearch)
        println(urlQueue)

        if (pollOffset[urlToSearch] == null) {
            val offset = context.offsetStorageReader()
                .offset(Collections.singletonMap(KafkaRestConfigDef.REST_CONFIG.NAME, urlToSearch))

            pollOffset[urlToSearch] = if (offset != null) {
                offset["Index"] as Long? ?: 1
            } else 1
        }

        if (!currentlyRunning.get()) {

            currentlyRunning.set(true)
            val currentOffset = pollOffset[urlToSearch]!!

            runBlocking {
                val response = client.get<JsonObject> {
                    url("$urlToSearch/$currentOffset")
                }

                val key = Collections.singletonMap(KafkaRestConfigDef.REST_CONFIG.NAME, urlToSearch)
                val value = Collections.singletonMap("Index", currentOffset + 1)

                val sourceRecord = SourceRecord(key, value, topicName, null, response.toString())

                recordList.add(sourceRecord)
                pollOffset[urlToSearch] = currentOffset.inc()
            }

            urlQueue.poll()
            urlQueue.add(urlToSearch)

            currentlyRunning.set(false)
            return recordList
        }

        return null
    }
}