package edu.kitt.orm.entries

import com.typesafe.config.Optional
import kotlinx.serialization.Serializable

@Serializable
data class UserEntry(
    val id: Int,
    val emailAddress: String,
    val username: String,

    val firstName: String,
    val lastName: String,
    val notificationsActive: Boolean,
)
