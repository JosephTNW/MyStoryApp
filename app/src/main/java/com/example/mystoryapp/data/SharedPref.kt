package com.example.mystoryapp.data

import android.content.Context
import androidx.core.content.edit
import com.example.mystoryapp.utils.Constants.LOGIN_INFO
import com.example.mystoryapp.utils.Constants.NO_TOKEN
import com.example.mystoryapp.utils.Constants.TOKEN_KEY


class SharedPref(context: Context) {

    private val preferences = context.getSharedPreferences(LOGIN_INFO, Context.MODE_PRIVATE)

    fun saveLoginInfo(token: String) {
        val editor = preferences.edit()
        editor.putString(TOKEN_KEY, token)
        editor.apply()
    }

    fun readLoginInfo(): String? {
        return preferences.getString(TOKEN_KEY, NO_TOKEN)
    }

    fun clearLoginInfo() {
        preferences.edit(commit = true) {
            clear()
        }
    }
}