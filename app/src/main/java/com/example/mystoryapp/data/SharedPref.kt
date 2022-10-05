package com.example.mystoryapp.data

import android.content.Context
import android.content.SharedPreferences
import android.text.method.TextKeyListener.clear
import androidx.core.content.edit
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.Preferences


class SharedPref(context: Context) {

    private val preferences = context.getSharedPreferences(LOGIN_INFO, Context.MODE_PRIVATE)

    fun saveLoginInfo(token: String){
        val editor = preferences.edit()
        editor.putString(TOKEN_KEY, token)
        editor.apply()
    }

    fun readLoginInfo(): String?{
        return preferences.getString(TOKEN_KEY, "")
    }

    fun clearLoginInfo() {
       preferences.edit(commit = true){
           clear()
       }
    }


    companion object {
        private const val TOKEN_KEY = "token_key"
        private const val LOGIN_INFO = "login_info"
    }
}