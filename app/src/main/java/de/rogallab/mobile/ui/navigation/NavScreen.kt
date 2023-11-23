package de.rogallab.mobile.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavScreen(
   val route: String,
   val title: String,
   val selectedIcon: ImageVector,
   val unSelectedIcon: ImageVector,
   val hasNews: Boolean = false,
   val badgeCount: Int? = null
) {
   data object PeopleList: NavScreen(
      route = "peopleListScreen",
      title = "Personen",
      selectedIcon =  Icons.Outlined.Group,
      unSelectedIcon =  Icons.Filled.Group
   )
   data object PersonInput: NavScreen(
      route = "personInputScreen",
      title = "Person hinzufügen",
      selectedIcon = Icons.Outlined.PersonAdd,
      unSelectedIcon = Icons.Filled.PersonAdd,
   )
   data object PersonDetail: NavScreen(
      route = "personDetailScreen",
      title = "Person ändern",
      selectedIcon = Icons.Outlined.Person,
      unSelectedIcon = Icons.Filled.Person,
   )
}