package de.rogallab.mobile.domain

import de.rogallab.mobile.data.models.PersonDto
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface IPeopleRepository {
   fun selectAll(): Flow<List<PersonDto>>
   suspend fun findById(id: UUID): PersonDto?
   suspend fun count(): Int
   suspend fun add(personDto: PersonDto): Boolean
   suspend fun addAll(peopleDto: List<PersonDto>): Boolean
   suspend fun update(upPersonDto: PersonDto): Boolean
   suspend fun remove(personDto: PersonDto): Boolean
}