package storage.settings

import KoverIgnore
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.coroutines.SuspendSettings
import com.russhwolf.settings.coroutines.toSuspendSettings
import java.util.prefs.Preferences

@KoverIgnore
private val delegate by lazy {
    Preferences.userRoot().node("filamagenta")
}

@KoverIgnore
@OptIn(ExperimentalSettingsApi::class)
actual val settings: SuspendSettings by lazy {
    PreferencesSettings(delegate).toSuspendSettings()
}
