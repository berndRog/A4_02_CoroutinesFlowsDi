package de.rogallab.mobile.ui.base


inline fun <reified T> MutableList<*>.asMutableListOfType(): MutableList<T>? =
   if (all { it is T })
      @Suppress("UNCHECKED_CAST")
      this as MutableList<T>
      else null