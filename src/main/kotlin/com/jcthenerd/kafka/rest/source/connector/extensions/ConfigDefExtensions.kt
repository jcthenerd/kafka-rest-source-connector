package com.jcthenerd.kafka.rest.source.connector.extensions

import org.apache.kafka.common.config.ConfigDef

data class ConfigDefInfo(
    var name: String = "",
    var type: ConfigDef.Type = ConfigDef.Type.STRING,
    var defaultValue: Any = "",
    var importance: ConfigDef.Importance = ConfigDef.Importance.LOW,
    var documentation: String = ""
)

fun ConfigDef.addDefinition(block: ConfigDefInfo.() -> Unit): ConfigDef {
    val configDefInfo = ConfigDefInfo().apply(block)

    return this.define(
        configDefInfo.name,
        configDefInfo.type,
        configDefInfo.defaultValue,
        configDefInfo.importance,
        configDefInfo.documentation
    )
}

fun buildConfigDef(block: ConfigDef.() -> Unit): ConfigDef {
    val configDef = ConfigDef()

    return configDef.apply(block)
}