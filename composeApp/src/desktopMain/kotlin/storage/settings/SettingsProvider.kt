package storage.settings

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.coroutines.SuspendSettings
import com.russhwolf.settings.coroutines.toSuspendSettings
import java.util.prefs.Preferences

private val delegate by lazy {
    Preferences.userRoot().node("filamagenta")
}

@OptIn(ExperimentalSettingsApi::class)
actual val settings: SuspendSettings by lazy {
    PreferencesSettings(delegate).toSuspendSettings()
}
