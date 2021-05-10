/*
 * Copyright 2019 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.vector.app.core.extensions

import android.text.Editable
import android.text.InputType
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.annotation.DrawableRes
import com.google.android.material.textfield.TextInputEditText
import im.vector.app.R
import im.vector.app.core.epoxy.VectorEpoxyHolder
import im.vector.app.core.platform.SimpleTextWatcher

fun EditText.setupAsSearch(@DrawableRes searchIconRes: Int = R.drawable.ic_search,
                           @DrawableRes clearIconRes: Int = R.drawable.ic_x_gray) {
    addTextChangedListener(object : SimpleTextWatcher() {
        override fun afterTextChanged(s: Editable) {
            val clearIcon = if (s.isNotEmpty()) clearIconRes else 0
            setCompoundDrawablesWithIntrinsicBounds(searchIconRes, 0, clearIcon, 0)
        }
    })

    maxLines = 1
    inputType = InputType.TYPE_CLASS_TEXT
    imeOptions = EditorInfo.IME_ACTION_SEARCH
    setOnEditorActionListener { _, actionId, _ ->
        var consumed = false
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            hideKeyboard()
            consumed = true
        }
        consumed
    }

    setOnTouchListener(View.OnTouchListener { _, event ->
        if (event.action == MotionEvent.ACTION_UP) {
            if (event.rawX >= (this.right - this.compoundPaddingRight)) {
                text = null
                return@OnTouchListener true
            }
        }
        return@OnTouchListener false
    })
}

/**
 * Set the initial value of the textEdit.
 * Avoids issue with two way bindings, the value is only set the first time
 */
fun TextInputEditText.setValueOnce(value: String?, holder: VectorEpoxyHolder) {
    if (holder.view.isAttachedToWindow) {
        // the view is attached to the window
        // So it is a rebind of new data and you could ignore it assuming this is text that was already inputted into the view.
        // Downside is if you ever wanted to programmatically change the content of the edit text while it is on screen you would not be able to
    } else {
        setText(value)
    }
}
