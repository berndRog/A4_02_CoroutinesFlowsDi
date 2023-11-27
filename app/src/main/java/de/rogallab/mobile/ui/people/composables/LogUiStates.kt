package de.rogallab.mobile.ui.people.composables

import android.util.Log
import androidx.compose.runtime.Composable
import de.rogallab.mobile.domain.UiState

@Composable
fun <T> LogUiStates(
   uiStateFlow: UiState<T>?,
   text: String,
   tag: String,
) {
   uiStateFlow?.let { it ->
      var up = it.upHandler
      var back = it.backHandler
      when (it) {
         UiState.Empty      -> Log.v(tag, "Compos. $text.Empty $up $back")
         UiState.Loading    -> Log.v(tag, "Compos. $text.Loading $up $back")
         is UiState.Success -> Log.v(tag, "Compos. $text.Success $up $back")
         is UiState.Error   -> Log.v(tag, "Compos. $text.Error $up $back")
      }
   }
}