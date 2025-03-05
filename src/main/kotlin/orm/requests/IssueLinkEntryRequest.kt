package edu.kitt.orm.requests

import com.typesafe.config.Optional
import kotlinx.serialization.Serializable

@Serializable
data class IssueLinkEntryRequest(
    val linker: Int,
    val linked: Int,
)
