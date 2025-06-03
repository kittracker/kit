package edu.kitt.orm.entries

import com.typesafe.config.Optional
import kotlinx.serialization.Serializable

@Serializable
data class IssueLinkEntry(
    val linker: UInt,
    val linked: UInt,
)
