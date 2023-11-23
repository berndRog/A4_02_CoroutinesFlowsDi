package de.rogallab.mobile.data.seed

import de.rogallab.mobile.domain.IPeopleRepository
import de.rogallab.mobile.domain.entities.Person
import de.rogallab.mobile.domain.mapping.toModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import kotlin.random.Random

class Seed @Inject constructor(
   private val _repository: IPeopleRepository,
   private val _dispatcher: CoroutineDispatcher,
   private val _exceptionHandler: CoroutineExceptionHandler
) {

   fun initDatabase() {
      val people: List<Person> = initialzePeople()

      val coroutineScope = CoroutineScope(Job() + _dispatcher + _exceptionHandler)

      val job = coroutineScope.launch {
         coroutineScope.async {
            _repository.addAll(people.toModel())
         }.await()
      }
      coroutineScope.launch {
         job.join()
      }
   }

   companion object {
      private val firstNames = mutableListOf(
         "Arne", "Berta", "Cord", "Dagmar", "Ernst", "Frieda", "Günter", "Hanna",
         "Ingo", "Johanna", "Klaus", "Luise", "Martin", "Norbert", "Paula", "Otto",
         "Rosi", "Stefan", "Therese", "Uwe", "Veronika", "Walter", "Zwantje")
      private val lastNames = mutableListOf(
         "Arndt", "Bauer", "Conrad", "Diehl", "Engel", "Fischer", "Grabe", "Hoffmann",
         "Imhof", "Jung", "Klein", "Lang", "Meier", "Neumann", "Peters", "Opitz",
         "Richter", "Schmidt", "Thormann", "Ulrich", "Vogel", "Wagner", "Zander")

      private val emailProvider = mutableListOf("gmail.com", "icloud.com", "outlook.com", "yahoo.com",
         "t-online.de", "gmx.de", "freenet.de", "mailbox.org")

      private fun initialzePeople(): List<Person> {
         val people = mutableListOf<Person>()
         for (index in 0..<firstNames.size) {
            val firstName = firstNames[index]
            val lastName = lastNames[index]
            val email =
               "${firstName.lowercase(Locale.getDefault())}." +
                  "${lastName.lowercase(Locale.getDefault())}@" +
                  emailProvider.random()
            val phone =
               "0${Random.nextInt(1234, 9999)} " +
                  "${Random.nextInt(100, 999)}-" +
                  "${Random.nextInt(10, 9999)}"

            val person = Person(firstName, lastName, email, phone)
            people.add(person)
         }
         val person = Person(
            firstName = "Erika",
            lastName = "Mustermann",
            email = "e.mustermann@t-online.de",
            phone = "0987 6543-210",
            id = UUID.fromString("10000000-0000-0000-0000-000000000000"))
         people.add(person)
         return people
      }
   }
}