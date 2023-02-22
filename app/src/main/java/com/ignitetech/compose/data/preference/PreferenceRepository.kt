package com.ignitetech.compose.data.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.ignitetech.compose.data.preference.PreferenceRepository.PreferencesKeys.ONBOARD_COMPLETE
import com.ignitetech.compose.data.preference.PreferenceRepository.PreferencesKeys.USER_ID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferenceRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object PreferencesKeys {
        val ONBOARD_COMPLETE = booleanPreferencesKey("onboard_complete")
        val USER_ID = intPreferencesKey("user_id")
    }

    val onboardCompleteFlow: Flow<Boolean> = dataStore.data.map {
        it[ONBOARD_COMPLETE] ?: false
    }

    suspend fun onboardComplete(): Boolean {
        return dataStore.data.first().toPreferences()[ONBOARD_COMPLETE] ?: false
    }

    suspend fun onboardComplete(completed: Boolean) {
        dataStore.edit {
            it[ONBOARD_COMPLETE] = completed
        }
    }

    suspend fun userId(): Int? {
        //TODO Temporary hardcoded value
        return dataStore.data.first().toPreferences()[USER_ID] ?: 0
    }

    suspend fun userId(userId: Int?) {
        dataStore.edit {
            when (userId) {
                null -> it.clear()
                else -> it[USER_ID] = userId
            }
        }
    }
}