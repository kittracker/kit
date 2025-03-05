package edu.kitt.orm.requests

import com.typesafe.config.Optional
import kotlinx.serialization.Serializable

@Serializable
data class ProjectEntryRequest(
    val id: Int? = null,
    val name: String? = null,
    val description: String? = null,
    val archived: Boolean? = null
)