import kotlinx.serialization.Serializable

@Serializable
data class CollaboratorEntryRequest(
    val userID: UInt,
    val projectID: UInt,
)