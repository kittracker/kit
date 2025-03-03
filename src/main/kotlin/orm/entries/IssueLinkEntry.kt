package edu.kitt.orm.entries

import com.typesafe.config.Optional
import kotlinx.serialization.Serializable

@Serializable
data class IssueLinkEntry(
    @Optional val linker: Int? = null,
    val linked: Int,
)
