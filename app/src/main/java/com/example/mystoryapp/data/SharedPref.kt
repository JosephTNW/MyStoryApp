package com.example.mystoryapp.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.Preferences


class SharedPref(private val dataStore: DataStore<Preferences>) {

    suspend fun saveLoginInfo(token: String){
        dataStore.edit { pref ->
            pref[TOKEN_KEY] = token
        }
    }

    fun readLoginInfo(): Flow<String>{
        return dataStore.data.map { pref ->
            pref[TOKEN_KEY] ?: "token"
        }
    }

    suspend fun clearLoginInfo() {
        dataStore.edit { pref ->
            pref[TOKEN_KEY] = ""
        }
    }


    companion object {
        private val TOKEN_KEY = stringPreferencesKey("token_key")

        @Volatile
        var INSTANCE: SharedPref? = null

        fun getInstance(dataStore: DataStore<Preferences>): SharedPref {
            return INSTANCE ?: synchronized(this) {
                val instance = SharedPref(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}