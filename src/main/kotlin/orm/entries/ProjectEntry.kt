package edu.kitt.orm.entries

data class ProjectEntry(
    val id: Int,
    val name: String,
    val description: String,
    val archived: Boolean,
    val ownerID: Int,
)