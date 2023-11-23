package de.rogallab.mobile.ui.people.composables

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import de.rogallab.mobile.domain.utilities.logDebug

suspend fun showErrorMessage(
   snackbarHostState: SnackbarHostState,                     // State ↓
   errorMessage: String,                                     // State ↓
   actionLabel: String?,                                     // State ↓
   duration: SnackbarDuration = SnackbarDuration.Long,       // State ↓
   onErrorAction: () -> Unit                                 // Event ↑
) {
   val tag = "ok>de.rogallab.mobile.ui.people.composables.showErrorMessage   ."

   val snackbarResult = snackbarHostState.showSnackbar(
      message = errorMessage,
      actionLabel = actionLabel,
      withDismissAction = false,
      duration = duration
   )
   if (snackbarResult == SnackbarResult.ActionPerformed) {
      logDebug(tag,"SnackbarResult.ActionPerformed")
      onErrorAction()
   }
}