package edu.kitt.orm.entries

import com.typesafe.config.Optional
import kotlinx.serialization.Serializable

@Serializable
data class ProjectEntry(
    val id: UInt,
    val name: String,
    val description: String,
    val archived: Boolean,
    val ownerID: UInt,
)