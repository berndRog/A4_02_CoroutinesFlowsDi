package de.rogallab.mobile.ui.people

import de.rogallab.mobile.ui.navigation.NavScreen
import android.app.Activity
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import de.rogallab.mobile.R
import de.rogallab.mobile.domain.UiState
import de.rogallab.mobile.domain.entities.Person
import de.rogallab.mobile.domain.utilities.logDebug
import de.rogallab.mobile.domain.utilities.logError
import de.rogallab.mobile.domain.utilities.logInfo
import de.rogallab.mobile.ui.people.composables.showErrorMessage
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun PeopleListScreen(
   navController: NavController,
   viewModel: PeopleViewModel
) {
   val tag = "ok>PeopleListScreen   ."

   val uiStateFlow: UiState<Person>
      by viewModel.uiStateFlow.collectAsStateWithLifecycle()

   val uiStateListFlow: UiState<List<Person>>
      by viewModel.uiStateListFlow.collectAsStateWithLifecycle()

   val up = uiStateFlow.upHandler
   val back =uiStateFlow.backHandler
   if(uiStateFlow is UiState.Empty)
      Log.v(tag,"Composition UiState.Empty $up $back")
   else if(uiStateFlow is UiState.Loading)
      Log.v(tag,"Composition UiState.Loading $up $back")
   else if(uiStateFlow is UiState.Success)
      Log.v(tag,"Composition UiState.Success $up $back")
   else if(uiStateFlow is UiState.Error)
      Log.v(tag,"Composition UiState.Error $up $back")

   val upList = uiStateFlow.upHandler
   val backList = uiStateFlow.backHandler

   if (uiStateListFlow is UiState.Empty)
      Log.v(tag, "Composition UiStateListFlow UiState.Empty $upList $backList")
   else if (uiStateListFlow is UiState.Loading)
      Log.v(tag, "Composition UiStateListFlow UiState.Loading $upList $backList")
   else if (uiStateListFlow is UiState.Success)
      Log.v(tag, "Composition UiStateListFlow UiState.Success $upList $backList")
   else if (uiStateListFlow is UiState.Error)
      Log.v(tag, "Composition UiStateListFlow UiState.Error $upList $backList")

   val snackbarHostState = remember { SnackbarHostState() }

   Scaffold(
      topBar = {
         TopAppBar(
            title = { Text(stringResource(R.string.people_list)) },
            navigationIcon = {
               val activity = LocalContext.current as Activity
               IconButton(
                  onClick = {
                     logDebug(tag, "Lateral Navigation: finish app")
                     // Finish the app
                     activity.finish()
                  }) {
                  Icon(imageVector = Icons.Default.Menu,
                     contentDescription = stringResource(R.string.back))
               }
            }
         )
      },
      floatingActionButton = {
         FloatingActionButton(
            containerColor = MaterialTheme.colorScheme.tertiary,
            onClick = {
               // FAB clicked -> InputScreen initialized
               logDebug(tag, "Forward Navigation: FAB clicked")
               viewModel.clearState()
               // Navigate to PersonDetail and put PeopleList on the back stack
               navController.navigate(route = NavScreen.PersonInput.route)
            }
         ) {
            Icon(Icons.Default.Add, "Add a contact")
         }
      },
      snackbarHost = {
         SnackbarHost(hostState = snackbarHostState) { data ->
            Snackbar(
               snackbarData = data,
               actionOnNewLine = true
            )
         }
      }) { innerPadding ->

      if (uiStateListFlow == UiState.Empty) {
         logDebug(tag, "uiStatePeople.Empty")
         // nothing to do
      } else if (uiStateListFlow == UiState.Loading) {
         logDebug(tag, "uiStatePeople.Loading")
         Column(
            modifier = Modifier
               .padding(bottom = innerPadding.calculateBottomPadding())
               .padding(horizontal = 8.dp)
               .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
         ) {
            CircularProgressIndicator(modifier = Modifier.size(160.dp))
         }
      } else if (uiStateListFlow is UiState.Success<List<Person>> ||
         uiStateListFlow is UiState.Error ||
         uiStateFlow is UiState.Error) {

         var list: MutableList<Person> = remember { mutableListOf<Person>() }
         if (uiStateListFlow is UiState.Success) {
            list = (uiStateListFlow as UiState.Success<List<Person>>).data as MutableList<Person>
            logDebug(tag, "uiStatePeople.Success items.size ${list.size}")
         }

         LazyColumn(
            modifier = Modifier
               .padding(top = innerPadding.calculateTopPadding())
               .padding(bottom = innerPadding.calculateBottomPadding())
               .padding(horizontal = 8.dp),
            state = rememberLazyListState()
         ) {
            items(
               items = list
            ) { person ->
               PersonListItem(
                  id = person.id,
                  firstName = person.firstName,
                  lastName = person.lastName,
                  email = person.email,
                  phone = person.phone,
                  imagePath = person.imagePath ?: "",
                  onClick = { id ->
                     // LazyColum item clicked -> DetailScreen initialized
                     logInfo(tag, "Forward Navigation: Item clicked")
                     // Navigate to 'PersonDetail' destination and put 'PeopleList' on the back stack
                     navController.navigate(route = NavScreen.PersonDetail.route + "/$id")
                  },
               )
            }
         }
      }
   }

   if (uiStateListFlow is UiState.Error) {
      LaunchedEffect(key1 = uiStateListFlow is UiState.Error) {
         val message = (uiStateListFlow as UiState.Error).message
         logError(tag, "uiStateListFlow.Error $message")
         showErrorMessage(
            snackbarHostState = snackbarHostState,
            errorMessage = message,
            actionLabel = "Ok",
            onErrorAction = { }
         )
      }
   }

   if (uiStateFlow is UiState.Error) {
      LaunchedEffect(key1 = uiStateFlow is UiState.Error) {
         val message = (uiStateFlow as UiState.Error).message
         logDebug(tag, "uiStateFlow.Error $message")
         showErrorMessage(
            snackbarHostState = snackbarHostState,
            errorMessage = message,
            actionLabel = "Ok",
            onErrorAction = { }
         )
         viewModel.onUiStateFlowChange(UiState.Success(Person()))
      }
   }
}

@Composable
fun PersonListItem(
   id: UUID,
   firstName: String,
   lastName: String,
   email: String?,
   phone: String?,
   imagePath: String?,
   onClick: (UUID) -> Unit    // Event ↑  Person
) {
//12345678901234567890123
   val tag = "ok>PersonListItem     ."

   Column {
      Row(
         verticalAlignment = Alignment.CenterVertically,
         modifier = Modifier
            .clickable {
               logDebug(tag, "Row onClick()")
               onClick(id)  // Event ↑
            }
      ) {
         Column {
            Text(
               text = "$firstName $lastName",
               style = MaterialTheme.typography.bodyLarge,
            )
            email?.let {
               Text(
                  modifier = Modifier.padding(top = 4.dp),
                  text = it,
                  style = MaterialTheme.typography.bodyMedium
               )
            }
            phone?.let {
               Text(
                  text = phone,
                  style = MaterialTheme.typography.bodyMedium,
                  modifier = Modifier
               )
            }
         }
      }
      Divider(modifier = Modifier.padding(vertical = 8.dp))
   }
}

//inline fun <reified T> MutableList<*>.asMutableListOfType(): MutableList<T>? =
//   if (all { it is T })
//      @Suppress("UNCHECKED_CAST")
//      this as MutableList<T>
//   else null