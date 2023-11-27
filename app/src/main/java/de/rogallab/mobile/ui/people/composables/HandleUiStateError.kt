package de.rogallab.mobile.ui.people.composables

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import de.rogallab.mobile.domain.UiState
import de.rogallab.mobile.domain.utilities.logInfo
import de.rogallab.mobile.ui.navigation.NavScreen
import kotlinx.coroutines.launch

@Composable
fun <T> HandleUiStateError(
   uiStateFlow: UiState<T>,                        // State ↓
   actionLabel: String?,                           // State ↓
   onErrorAction: () -> Unit,                      // Event ↑
   navController: NavController,                   // State ↓
   snackbarHostState: SnackbarHostState,           // State ↓
   onUiStateFlowChange: (UiState<out T>) -> Unit,  // Event ↑
   tag: String,                                    // State ↓
) {

   val coroutineScope = rememberCoroutineScope()
   LaunchedEffect(uiStateFlow is UiState.Error) {
      val message = (uiStateFlow as UiState.Error).message
      val backHandler = uiStateFlow.backHandler
      val job = coroutineScope.launch {
         ShowErrorMessage(
            snackbarHostState = snackbarHostState,
            errorMessage = message,
            actionLabel = actionLabel,
            onErrorAction = { onErrorAction() }
         )
      }
      coroutineScope.launch {
         job.join()
         if (backHandler) {
            logInfo(tag, "Back Navigation (Abort)")
            navController.popBackStack(
               route = NavScreen.PeopleList.route,
               inclusive = false
            )
         }
         onUiStateFlowChange( UiState.Empty )
      }
   }
}