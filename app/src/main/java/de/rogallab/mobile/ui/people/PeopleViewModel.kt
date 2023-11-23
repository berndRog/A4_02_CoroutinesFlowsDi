package de.rogallab.mobile.ui.people

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.rogallab.mobile.data.models.PersonDto
import de.rogallab.mobile.data.seed.Seed
import de.rogallab.mobile.domain.IPeopleRepository
import de.rogallab.mobile.domain.UiState
import de.rogallab.mobile.domain.entities.Person
import de.rogallab.mobile.domain.mapping.toDomain
import de.rogallab.mobile.domain.mapping.toModel
import de.rogallab.mobile.domain.utilities.UUIDEmpty
import de.rogallab.mobile.domain.utilities.logDebug
import de.rogallab.mobile.domain.utilities.logError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class PeopleViewModel @Inject constructor(
   private val _repository: IPeopleRepository,
   private val _seed: Seed,
   private val _dispatcher: CoroutineDispatcher
) : ViewModel() {

   private var _id: UUID = UUID.randomUUID()

   // State = Observables (DataBinding)
   private var _firstName: String by mutableStateOf(value = "")
   val firstName
      get() = _firstName
   fun onFirstNameChange(value: String) {
      if(value != _firstName )  _firstName = value }

   private var _lastName: String by mutableStateOf(value = "")
   val lastName
      get() = _lastName
   fun onLastNameChange(value: String) {
      if(value != _lastName )  _lastName = value
   }

   private var _email: String? by mutableStateOf(value = null)
   val email
      get() = _email
   fun onEmailChange(value: String) {
      if(value != _email )  _email = value
   }

   private var _phone: String? by mutableStateOf(value = null)
   val phone
      get() = _phone
   fun onPhoneChange(value: String) {
      if(value != _phone )  _phone = value
   }

   private var _imagePath: String? by mutableStateOf(value = null)
   val imagePath
      get() = _imagePath
   fun onImagePathChange(value: String?) {
      if(value != _imagePath )  _imagePath = value
   }

   // error handling
   fun onErrorAction() {
      logDebug(tag, "onErrorAction()")
      // toDo
   }

   // Coroutine ExceptionHandler
   private val _exceptionHandler = CoroutineExceptionHandler { _, exception ->
      exception.localizedMessage?.let {
         logError(tag, it)
         _uiStateFlow.value = UiState.Error(it, true)
      } ?: run {
         exception.stackTrace.forEach {
            logError(tag, it.toString())
         }
      }
   }
   // Coroutine Context
   private val _coroutineContext = SupervisorJob() + _dispatcher + _exceptionHandler
   // Coroutine Scope
   private val _coroutineScope = CoroutineScope(_coroutineContext)

   override fun onCleared() {
      // cancel all coroutines, when lifecycle of the viewmodel ends
      logDebug(tag,"Cancel all child coroutines")
      _coroutineContext.cancelChildren()
      _coroutineContext.cancel()
   }

   // mutableStateList with observer
   // var snapShotPeople: SnapshotStateList<Person> = mutableStateListOf<Person>()

   init {
      _coroutineScope.launch {
         val count = _coroutineScope.async {
            return@async _repository.count()
         }.await()
         if(count == 0) {
            _seed.initDatabase()
         }
      }
   }

   // StateFlow for Input&Detail Screens
   private var _uiStateFlow: MutableStateFlow<UiState<Person>> =
      MutableStateFlow(value = UiState.Success(Person()))
   val uiStateFlow: StateFlow<UiState<Person>>
      get() = _uiStateFlow
   fun onUiStateFlowChange(uiState: UiState<Person>) {
      _uiStateFlow.value = uiState
   }

   // StateFlow for List Screens
   val uiStateListFlow: StateFlow<UiState<List<Person>>> = flow<UiState<List<Person>>> {
      emit(UiState.Loading)
      val peopleRead: List<Person> = _coroutineScope.async {
         var peopleCollected = mutableListOf<Person>()
         _repository.selectAll().collect { peopleDto: List<PersonDto> ->
            logDebug(tag, "${peopleDto.size} collected")
            peopleCollected =  peopleDto.toDomain().toMutableList()
            peopleCollected.sortBy{ it.lastName }
         }
         return@async peopleCollected.toList()
      }.await()
      emit(UiState.Success(peopleRead))
   }.stateIn(
      viewModelScope,
      SharingStarted.WhileSubscribed(1_000),
      UiState.Empty
   )

   fun readById(id: UUID) {
      try {
         _coroutineScope.launch {
            val personDto = viewModelScope.async(_dispatcher+_exceptionHandler) {
               return@async _repository.findById(id)
            }.await()
            personDto?.let{
               val person = it.toDomain()
               // person values are set as observable states in the viewmodel
               setStateFromPerson(person)
               logDebug(tag, "findById() ${person.asString()}")
               _uiStateFlow.value = UiState.Empty  // no return neeeded
            } ?: run {
               throw Exception("Person with given id not found")
            }
         }
      }
      catch (e: Exception) {
         val message:String = e.localizedMessage ?: e.stackTraceToString()
         logError(tag,message)
         _uiStateFlow.value =  UiState.Error(message)
      }
   }

//   private fun waitUntilJobIsCompleted(job: Job?, text: String = "") {
//      job?.let{
//         viewModelScope.launch(_dispatcher+_exceptionHandler) {
//            it.join()
//            logDebug(tag, "$text isActive:${it.isActive} " +
//               "isCompleted:${it.isCompleted} isCanceled:${it.isCancelled}")
//         }
//      }
//   }

   fun add() {
      try {
         val personDto = getPersonFromState().toModel()
         _coroutineScope.launch {
            val result = _coroutineScope.async {
               _repository.add(personDto)
            }.await()
            logDebug(tag, "after await()")
            if (result) {
               logDebug(tag, "add() ${personDto.asString()}")
               _uiStateFlow.value = UiState.Empty
            } else {
               val message = "Error in add()"
               logError(tag, message)
               _uiStateFlow.value = UiState.Error(message,false,true)
            }
         }
      } catch (e: Exception) {
         val message: String = e.localizedMessage ?: e.stackTraceToString()
         logError(tag, message)
         _uiStateFlow.value = UiState.Error(message, false, true)
      }
   }

   fun update(id:UUID) {
      try {
         val upPersonDto = getPersonFromState(id).toModel()
         _coroutineScope.launch {
            val result = _coroutineScope.async {
               _repository.update(upPersonDto)
            }.await()
            if(result) {
               logDebug(tag, "update() ${upPersonDto.asString()}")
               _uiStateFlow.value = UiState.Empty
            } else {
               val message = "Error in update()"
               logError(tag, message)
               _uiStateFlow.value = UiState.Error(message, false, true)
            }
         }
      } catch (e: Exception) {
         val message: String = e.localizedMessage ?: e.stackTraceToString()
         logError(tag,message)
         _uiStateFlow.value =  UiState.Error(message, false, true)
      }
   }

   fun remove(id:UUID) {
      try {
         _coroutineScope.launch {
            val personDto = _coroutineScope.async {
               return@async _repository.findById(id)
            }.await()
            personDto?.let{
               val result = _coroutineScope.async {
                  _repository.remove(personDto)
               }.await()
               if(result) {
                  logDebug(tag, "removed() ${personDto.asString()}")
                  _uiStateFlow.value = UiState.Success(null)
               } else {
                  val message = "Error in remove()"
                  logError(tag, message)
                  _uiStateFlow.value = UiState.Error(message, false, true)
               }
            } ?: run {
               throw Exception("remove(): Person with given id not found")
            }
         }
      } catch (e: Exception) {
         val message = e.localizedMessage ?: e.stackTraceToString()
         logError(tag,message)
         _uiStateFlow.value =  UiState.Error(message, false, true)
      }
   }

   fun getPersonFromState(id:UUID? = null): Person {
      val person = id?.let {
         return@let Person(_firstName, _lastName, _email, _phone, _imagePath, id)
      } ?: run {
         return@run Person(_firstName, _lastName, _email, _phone, _imagePath, _id)
      }
      return person
   }

   fun setStateFromPerson(person: Person?) {
      _firstName = person?.firstName ?: ""
      _lastName  = person?.lastName ?: ""
      _email     = person?.email
      _phone     = person?.phone
      _imagePath = person?.imagePath
      _id        = person?.id ?: UUIDEmpty
   }

   fun clearState() {
      logDebug(tag, "clearState")
      _firstName = ""
      _lastName  = ""
      _email     = null
      _phone     = null
      _imagePath = null
      _id        = UUID.randomUUID()
   }

   companion object {
      const val tag = "ok>PeopleViewModel    ."
   }
}
