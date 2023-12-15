package storage.settings

import KoverIgnore
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.KeychainSettings
import com.russhwolf.settings.coroutines.SuspendSettings
import com.russhwolf.settings.coroutines.toSuspendSettings

@KoverIgnore
@OptIn(ExperimentalSettingsApi::class, ExperimentalSettingsImplementation::class)
actual val settings: SuspendSettings by lazy {
    KeychainSettings("filamagenta").toSuspendSettings()
}
