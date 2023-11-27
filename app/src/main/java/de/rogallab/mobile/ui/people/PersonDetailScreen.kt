package de.rogallab.mobile.ui.people

import de.rogallab.mobile.ui.navigation.NavScreen
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import de.rogallab.mobile.R
import de.rogallab.mobile.domain.UiState
import de.rogallab.mobile.domain.utilities.logDebug
import de.rogallab.mobile.domain.utilities.logInfo
import de.rogallab.mobile.ui.people.composables.HandleUiStateError
import de.rogallab.mobile.ui.people.composables.InputNameMailPhone
import de.rogallab.mobile.ui.people.composables.isInputValid
import de.rogallab.mobile.ui.people.composables.LogUiStates
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonDetailScreen(
   id: UUID?,
   navController: NavController,
   viewModel: PeopleViewModel,
) {
   val tag = "ok>PersonDetailScreen ."

   BackHandler(
      enabled = true,
      onBack = {
         logInfo(tag, "Back Navigation (Abort)")
         navController.popBackStack(
            route = NavScreen.PeopleList.route,
            inclusive = false
         )
      }
   )

   val uiStateFlow by viewModel.uiStateFlow.collectAsStateWithLifecycle()
   LogUiStates(uiStateFlow,"UiStateFlow", tag )

   val context = LocalContext.current
   val snackbarHostState = remember { SnackbarHostState() }

   id?.let {
      LaunchedEffect(Unit) {
         logDebug(tag, "ReadById()")
         viewModel.readById(id)
      }
   } ?: run {
      viewModel.onUiStateFlowChange(UiState.Error("No id for person is given"))
   }

   Scaffold(
      topBar = {
         TopAppBar(
            title = { Text(stringResource(R.string.person_detail)) },
            navigationIcon = {
               IconButton(onClick = {
                  if(! isInputValid(context, viewModel)) {
                     viewModel.update(id!!)
                  }
                  if(viewModel.uiStateFlow.value.upHandler) {
                     logInfo(tag, "Reverse Navigation (Up) viewModel.update()")
                     navController.navigate(route = NavScreen.PeopleList.route) {
                        popUpTo(route = NavScreen.PeopleList.route) { inclusive = true }
                     }
                  }
                  if(viewModel.uiStateFlow.value.backHandler) {
                     logInfo(tag, "Back Navigation, Error in viewModel.update()")
                     navController.popBackStack(
                        route = NavScreen.PeopleList.route,
                        inclusive = false
                     )
                  }
               }) {
                  Icon(
                     imageVector = Icons.Default.ArrowBack,
                     contentDescription = stringResource(R.string.back)
                  )
               }
            }
         )
      },
      snackbarHost = {
         SnackbarHost(hostState = snackbarHostState) { data ->
            Snackbar(
               snackbarData = data,
               actionOnNewLine = true
            )
         }
      }
   ) { innerPadding ->

      Column(
         modifier = Modifier
            .padding(top = innerPadding.calculateTopPadding(),
               bottom = innerPadding.calculateBottomPadding())
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
            .verticalScroll(state = rememberScrollState())
      ) {
         InputNameMailPhone(
            firstName = viewModel.firstName,                         // State ↓
            onFirstNameChange = { viewModel.onFirstNameChange(it) }, // Event ↑
            lastName = viewModel.lastName,                           // State ↓
            onLastNameChange = { viewModel.onLastNameChange(it) },   // Event ↑
            email = viewModel.email,                                 // State ↓
            onEmailChange = { viewModel.onEmailChange(it) },         // Event ↑
            phone = viewModel.phone,                                 // State ↓
            onPhoneChange = { viewModel.onPhoneChange(it) }          // Event ↑
         )
      }
   }

   if (uiStateFlow is UiState.Error) {
      HandleUiStateError(
         uiStateFlow = uiStateFlow,
         actionLabel = "Ok",
         onErrorAction = { },
         navController = navController,
         snackbarHostState = snackbarHostState,
         onUiStateFlowChange = { viewModel.onUiStateFlowChange(it) },
         tag = tag
      )
   }
}

