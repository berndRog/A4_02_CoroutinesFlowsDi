package de.rogallab.mobile.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import de.rogallab.mobile.domain.utilities.as8
import java.util.UUID

@Entity(tableName = "people")
data class PersonDto (
   val firstName: String = "",
   val lastName: String = "",
   val email: String? = null,
   val phone:String? = null,
   val imagePath: String? = "",
   @PrimaryKey
   val id: UUID = UUID.randomUUID()
) {
   fun asString() : String = "$firstName $lastName ${id.as8()}"
}