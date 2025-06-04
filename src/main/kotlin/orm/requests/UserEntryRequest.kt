package edu.kitt.orm.requests

import com.typesafe.config.Optional
import kotlinx.serialization.Serializable

@Serializable
data class UserEntryRequest(
    val id: Int? = null,
    val emailAddress: String? = null,
    val username: String? = null
)
