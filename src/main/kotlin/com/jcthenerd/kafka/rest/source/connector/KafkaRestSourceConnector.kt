package com.jcthenerd.kafka.rest.source.connector

import com.jcthenerd.kafka.rest.source.connector.config.KafkaRestConfigDef
import com.jcthenerd.kafka.rest.source.connector.task.KafkaRestSourceTask
import org.apache.kafka.common.config.AbstractConfig
import org.apache.kafka.connect.source.SourceConnector
import org.apache.kafka.connect.util.ConnectorUtils
import kotlin.math.min

class KafkaRestSourceConnector: SourceConnector() {

    val configDef = KafkaRestConfigDef.baseConfigDef()

    lateinit var topicConfig: String
    lateinit var restUrls: List<String>

    override fun taskConfigs(maxTasks: Int): MutableList<MutableMap<String, String>> {

        val groupCount = min(maxTasks, restUrls.count())
        val taskConfigList = mutableListOf<MutableMap<String, String>>()

        val restUrlGroups = ConnectorUtils.groupPartitions(restUrls, groupCount)

        println(restUrlGroups)

        restUrlGroups.forEach {
            val taskConfig = mutableMapOf<String, String>()
            taskConfig[KafkaRestConfigDef.TOPIC_CONFIG.NAME] = topicConfig
            taskConfig[KafkaRestConfigDef.REST_CONFIG.NAME] = it.joinToString(",")
            taskConfigList.add(taskConfig)
        }

        return taskConfigList
    }

    override fun start(props: MutableMap<String, String>?) {

        val parsedConfig = AbstractConfig(configDef, props)

        topicConfig = parsedConfig.getString(KafkaRestConfigDef.TOPIC_CONFIG.NAME)
        restUrls = parsedConfig.getList(KafkaRestConfigDef.REST_CONFIG.NAME)

    }

    override fun stop() {
        // empty for now
    }

    override fun version() = "0.0.1"

    override fun taskClass() = KafkaRestSourceTask::class.java

    override fun config() = configDef
}