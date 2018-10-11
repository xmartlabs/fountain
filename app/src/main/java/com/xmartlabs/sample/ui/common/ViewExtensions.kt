package com.xmartlabs.sample.ui.common

import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText

fun EditText.onSearchPerformed(action: () -> Unit) {
  setOnEditorActionListener { _, actionId, _ ->
    if (actionId == EditorInfo.IME_ACTION_GO) {
      action.invoke()
      true
    } else {
      false
    }
  }
  setOnKeyListener { _, keyCode, event ->
    if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
      action.invoke()
      true
    } else {
      false
    }
  }
}
