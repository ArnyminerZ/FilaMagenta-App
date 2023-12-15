package storage.settings

import KoverIgnore
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.SuspendSettings

@KoverIgnore
@OptIn(ExperimentalSettingsApi::class)
expect val settings: SuspendSettings
