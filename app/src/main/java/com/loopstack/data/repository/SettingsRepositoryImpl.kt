package com.loopstack.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.loopstack.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    private object PreferencesKeys {
        val ROUTER_PORT = intPreferencesKey("router_port")
        val API_PATH_PREFIX = stringPreferencesKey("api_path_prefix")
        val API_KEY = stringPreferencesKey("api_key")
    }

    override val routerPort: Flow<Int> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.ROUTER_PORT] ?: 20128
        }

    override val apiPathPrefix: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.API_PATH_PREFIX] ?: "/v1"
        }

    override val apiKey: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.API_KEY] ?: ""
        }

    override suspend fun saveSettings(port: Int, pathPrefix: String, key: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ROUTER_PORT] = port
            preferences[PreferencesKeys.API_PATH_PREFIX] = pathPrefix
            preferences[PreferencesKeys.API_KEY] = key
        }
    }
}
