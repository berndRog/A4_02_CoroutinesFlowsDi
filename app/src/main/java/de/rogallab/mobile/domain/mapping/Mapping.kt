package de.rogallab.mobile.domain.mapping

import de.rogallab.mobile.data.models.PersonDto
import de.rogallab.mobile.domain.entities.Person

fun PersonDto.toDomain(): Person = Person(
   firstName = this.firstName,
   lastName = this.lastName,
   email = this.email,
   phone = this.phone,
   imagePath = this.imagePath,
   id = this.id
)

fun List<PersonDto>.toDomain(): List<Person> {
   val people = mutableListOf<Person>()
   this.forEach { personDto ->
      people.add(personDto.toDomain())
   }
   return people
}

fun Person.toModel(): PersonDto = PersonDto(
   firstName = this.firstName,
   lastName = this.lastName,
   email = this.email,
   phone = this.phone,
   imagePath = this.imagePath,
   id = this.id
)

fun List<Person>.toModel(): List<PersonDto> {
   val peopleDto = mutableListOf<PersonDto>()
   this.forEach { person ->
      peopleDto.add(person.toModel())
   }
   return peopleDto
}