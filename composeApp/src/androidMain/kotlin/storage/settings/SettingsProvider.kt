package storage.settings

import KoverIgnore
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.filamagenta.android.applicationContext
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.coroutines.SuspendSettings
import com.russhwolf.settings.datastore.DataStoreSettings

@KoverIgnore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore("filamagenta")

@KoverIgnore
@OptIn(ExperimentalSettingsApi::class, ExperimentalSettingsImplementation::class)
actual val settings: SuspendSettings by lazy {
    DataStoreSettings(applicationContext.dataStore)
}
