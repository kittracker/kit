package edu.kitt.orm.entries

import com.typesafe.config.Optional
import kotlinx.serialization.Serializable

@Serializable
data class UserEntry(
    @Optional val id: Int? = null,
    val emailAddress: String,
    val username: String
)
