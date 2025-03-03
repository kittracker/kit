package edu.kitt.orm.entries

import com.typesafe.config.Optional
import kotlinx.serialization.Serializable

@Serializable
data class CollaboratorEntry(
    @Optional val userID: Int? = null,
    val projectID: Int,
)