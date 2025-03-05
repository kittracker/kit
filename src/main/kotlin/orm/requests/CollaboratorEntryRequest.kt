
import com.typesafe.config.Optional
import kotlinx.serialization.Serializable

@Serializable
data class CollaboratorEntryRequest(
    val userID: Int,
    val projectID: Int,
)