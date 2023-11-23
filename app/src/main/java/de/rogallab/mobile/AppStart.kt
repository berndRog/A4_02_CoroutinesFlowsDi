package de.rogallab.mobile

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import de.rogallab.mobile.domain.utilities.logInfo

@HiltAndroidApp
class AppStart : Application() {
   override fun onCreate() {
      super.onCreate()

      val maxMemory = (Runtime.getRuntime().maxMemory() / 1024 ).toInt()
      logInfo(tag, "onCreate() maxMemory $maxMemory kB")
   }

   companion object {
      //                       12345678901234567890123
      private const val tag = "ok>AppStart           ."
      const val isInfo = true
      const val isDebug = true
      const val URL:              String = "http://10.0.2.2:5000/api/v1.0/"

   }
}