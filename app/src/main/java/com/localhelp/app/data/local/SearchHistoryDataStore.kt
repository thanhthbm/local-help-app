package com.localhelp.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "search_history"
)

@Singleton
class SearchHistoryDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val KEY_HISTORY = stringSetPreferencesKey("search_history")
        const val MAX_HISTORY   = 10
    }

    val historyFlow: Flow<List<String>> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_HISTORY]?.toList()?.take(MAX_HISTORY) ?: emptyList()
        }

    suspend fun addHistory(query: String) {
        if (query.isBlank()) return
        context.dataStore.edit { preferences ->
            val current = preferences[KEY_HISTORY]?.toMutableList() ?: mutableListOf()
            current.remove(query)
            current.add(0, query)
            preferences[KEY_HISTORY] = current.toSet()
        }
    }

    suspend fun removeHistory(query: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[KEY_HISTORY]?.toMutableList() ?: return@edit
            current.remove(query)
            preferences[KEY_HISTORY] = current.toSet()
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.remove(KEY_HISTORY)
        }
    }
}