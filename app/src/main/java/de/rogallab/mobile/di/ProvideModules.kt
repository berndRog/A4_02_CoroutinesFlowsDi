package de.rogallab.mobile.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.rogallab.mobile.data.seed.Seed
import de.rogallab.mobile.domain.IPeopleRepository
import de.rogallab.mobile.domain.utilities.logError
import de.rogallab.mobile.domain.utilities.logInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

// https://medium.com/androiddevelopers/dependency-injection-on-android-with-hilt-67b6031e62d

// @Provides: Adds a binding for a type that cannot be constructor injected

// open class BaseComponentActivity(
//    private val _tag: String
// ) : ComponentActivity() {
// property injection with Dagger/Hilt
// @Inject
// lateinit var _logger: ILogger

@Module
@InstallIn(SingletonComponent::class)
object ProvideModules {
   //12345678901234567890123
   private const val tag = "ok>AppProvidesModules ."

   @Singleton
   @Provides
   fun provideContext(
      application: Application // provided by Hilt
   ): Context {
      logInfo(tag, "providesContext()")
      return application.applicationContext
   }

   @Singleton
   @Provides
   fun provideCoroutineExceptionHandler(
   ): CoroutineExceptionHandler {
      logInfo(tag, "providesCoroutineExceptionHandler()")
      return CoroutineExceptionHandler { _, exception ->
         exception.localizedMessage?.let {
            logError("ok>CoroutineException", it)
         } ?: run {
            exception.stackTrace.forEach {
               logError("ok>CoroutineException", it.toString())
            }
         }
      }
   }
   @Singleton
   @Provides
   fun provideCoroutineDispatcher(
   ): CoroutineDispatcher {
      logInfo(tag, "providesCoroutineDispatcher()")
      return Dispatchers.IO
   }

   @Provides
   @Singleton
   fun provideSeed(
      repository: IPeopleRepository,
      dispatcher: CoroutineDispatcher,
      exceptionHandler: CoroutineExceptionHandler
   ): Seed {
      logInfo(tag, "providesSeed()")
      return Seed(repository, dispatcher, exceptionHandler)
   }
}