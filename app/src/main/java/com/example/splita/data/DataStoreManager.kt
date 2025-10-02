package com.example.splita.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.splita.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class DataStoreManager(context: Context) {

    private val dataStore = context.dataStore

    companion object {
        val USERS_KEY = stringPreferencesKey("users")
    }

    suspend fun saveUsers(users: List<User>) {
        dataStore.edit {
            it[USERS_KEY] = Json.encodeToString(users)
        }
    }

    val usersFlow: Flow<List<User>> = dataStore.data.map {
        val usersJson = it[USERS_KEY]
        if (usersJson != null) {
            Json.decodeFromString<List<User>>(usersJson)
        } else {
            listOf(User(id = 1)) // Default to one user
        }
    }
}