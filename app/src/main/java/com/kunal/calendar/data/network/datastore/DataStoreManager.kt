package com.kunal.calendar.data.network.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DatastoreManager(
    private val context: Context
) {

    companion object {
        private const val USER_STORE_NAME = "user_data_store"
        private val USER_ID = stringPreferencesKey("userId")
        private val IS_FIRST_LAUNCH = booleanPreferencesKey("isFirstLaunch")
    }

    private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = USER_STORE_NAME)

    val userId: Flow<String?>
        get() = context.userDataStore.data.map { preferences ->
            preferences[USER_ID]
        }

    suspend fun saveUserId(userId: String) {
        context.userDataStore.edit { preferences ->
            preferences[USER_ID] = userId
        }
    }

    val isFirstLaunch: Flow<Boolean?>
        get() = context.userDataStore.data.map { preferences ->
            preferences[IS_FIRST_LAUNCH] ?: true
        }

    suspend fun setIsFirstLaunch(isFirstLaunch: Boolean) {
        context.userDataStore.edit { preferences ->
            preferences[IS_FIRST_LAUNCH] = isFirstLaunch
        }
    }
}