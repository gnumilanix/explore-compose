package com.ignitetech.compose.data.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.ignitetech.compose.data.preference.PreferenceRepository.PreferencesKeys.ONBOARD_COMPLETE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferenceRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object PreferencesKeys {
        val ONBOARD_COMPLETE = booleanPreferencesKey("onboarding_complete")
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
}