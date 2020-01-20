package com.jcthenerd.kafka.rest.source.connector.config

import com.jcthenerd.kafka.rest.source.connector.extensions.addDefinition
import com.jcthenerd.kafka.rest.source.connector.extensions.buildConfigDef
import org.apache.kafka.common.config.ConfigDef

object KafkaRestConfigDef: ConfigDef() {

    object TOPIC_CONFIG {
        const val NAME = "topic"
        const val DOC = "The topic to publish data to"
        const val DEFAULT = "test"
    }

    object REST_CONFIG {
        const val NAME = "urls"
        const val DOC = "List of rest urls to poll."
        const val DEFAULT = ""
    }

    fun baseConfigDef(): ConfigDef {
        val configDef = buildConfigDef {
            addDefinition {
                name = REST_CONFIG.NAME
                type = Type.LIST
                defaultValue = REST_CONFIG.DEFAULT
                importance = Importance.HIGH
                documentation = REST_CONFIG.DOC
            }

            addDefinition {
                name = TOPIC_CONFIG.NAME
                type = Type.STRING
                defaultValue = TOPIC_CONFIG.DEFAULT
                importance = Importance.HIGH
                documentation = TOPIC_CONFIG.DOC
            }
        }

        println(configDef.names().toString())

        return configDef
    }
}