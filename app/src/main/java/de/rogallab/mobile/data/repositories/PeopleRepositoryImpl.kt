package de.rogallab.mobile.data.repositories

import de.rogallab.mobile.data.models.PersonDto
import de.rogallab.mobile.domain.IPeopleRepository
import de.rogallab.mobile.domain.utilities.logDebug
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class PeopleRepositoryImpl @Inject constructor(
   private val _dispatcher: CoroutineDispatcher,
   private val _exceptionHandler: CoroutineExceptionHandler
): IPeopleRepository {

            //12345678901234567890123
   val tag = "ok>PeopleRepositoryImpl"

   private val _peopleDto = mutableListOf<PersonDto>()
   private val _mutex = Mutex()

   // producer of a cold Flow<List<PersonDto>>
   override fun selectAll(): Flow<MutableList<PersonDto>> = flow {
      delay(1000L) // simulate a long running operation
      _peopleDto.sortBy { it.lastName }
      logDebug(tag, "selectAll2():Flow emit ${_peopleDto.size}")
      emit(_peopleDto)
   }.flowOn(_dispatcher+_exceptionHandler)

   override suspend fun findById(id: UUID): PersonDto? =
      withContext(_dispatcher+_exceptionHandler) {
         delay(1000L) // simulate a long running operation
         logDebug(tag,"suspend findById()")
         return@withContext _peopleDto.firstOrNull { it.id == id }
      }

   override suspend fun count(): Int =
      withContext(_dispatcher+_exceptionHandler) {
         return@withContext _peopleDto.size
      }

   override suspend fun add(personDto: PersonDto): Boolean {
      val result = withContext(_dispatcher+_exceptionHandler) {
         logDebug(tag,"suspend add()")
         if(_peopleDto.firstOrNull { it.id == personDto.id } != null) {
            throw Exception("PersonDto with given id already exists")
         }
         //throw Exception("TestErrror in PeopleRespository.add()")
         _mutex.withLock {
            _peopleDto.add(personDto)
         }
         return@withContext true
      }
      return result
   }

   override suspend fun addAll(peopleDto: List<PersonDto>): Boolean =
      withContext(_dispatcher+_exceptionHandler) {
         logDebug(tag,"suspend addAll()")
         delay(1000L) // simulate a long running operation
         _mutex.withLock { _peopleDto.addAll(peopleDto) }
         return@withContext true
      }

   override suspend fun update(upPersonDto: PersonDto): Boolean =
      withContext(_dispatcher+_exceptionHandler) {
         logDebug(tag, "suspend update()")
         val personDto = _peopleDto.firstOrNull { it.id == upPersonDto.id }
         personDto?.let {
            _mutex.withLock {
               _peopleDto.remove(personDto)
               _peopleDto.add(upPersonDto)
            }
         } ?: run {
            throw Exception("Update person with given id not found")
         }
         return@withContext true
      }

   override suspend fun remove(personDto: PersonDto): Boolean =
      withContext(_dispatcher + _exceptionHandler) {
         logDebug(tag, "suspend remove()")
         delay(10L) // simulate a long running operation
         _mutex.withLock { _peopleDto.remove(personDto) }
         return@withContext true
      }
}
