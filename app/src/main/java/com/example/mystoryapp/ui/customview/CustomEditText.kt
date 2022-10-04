package com.example.mystoryapp.ui.customview

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.content.res.TypedArrayUtils.getString
import com.example.mystoryapp.R

class CustomEditText : AppCompatEditText {

    var textValid: Boolean ?= null

    private lateinit var errorImage: Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        errorImage = ContextCompat.getDrawable(context, R.drawable.error_24) as Drawable

        addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

                if (text != null && text?.isNotEmpty() == true){
                    if (inputType == InputType.TYPE_CLASS_TEXT + InputType.TYPE_TEXT_VARIATION_PASSWORD){
                        if (length() < 6) {
                            error = context.getString(R.string.password_error)
                            showError()
                            textValid = false
                        } else {
                            textValid = true
                            hideError()
                        }
                    } else if (inputType == InputType.TYPE_CLASS_TEXT + InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS){
                        if (!Patterns.EMAIL_ADDRESS.matcher(text.toString()).matches()) {
                            error = context.getString(R.string.email_error)
                            showError()
                            textValid = false
                        } else {
                            textValid = true
                            hideError()
                        }
                    } else {
                        textValid = true
                        hideError()
                    }
                } else {
                    error = context.getString(R.string.empty_error)
                    textValid = false
                    showError()
                }
            }

        })
    }
    private fun showError() {
        setDrawable(endOfTheText = errorImage)
    }

    private fun hideError() {
        setDrawable()
    }

    private fun setDrawable(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ){
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }
}