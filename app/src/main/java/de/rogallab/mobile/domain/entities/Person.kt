package de.rogallab.mobile.domain.entities

import de.rogallab.mobile.domain.utilities.as8
import java.util.UUID

class Person (
   val firstName: String = "",
   val lastName: String = "",
   val email: String? = null,
   val phone:String? = null,
   val imagePath: String? = "",
   // Relation Person --> Task [0..*]
// val tasks: MutableList<Task> = mutableListOf(),
   val id: UUID = UUID.randomUUID()
) {
   fun asString() : String = "$firstName $lastName ${id.as8()}"
}