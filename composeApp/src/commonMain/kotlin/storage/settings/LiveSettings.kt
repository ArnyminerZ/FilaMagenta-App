package storage.settings

import androidx.compose.runtime.Composable
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.SuspendSettings
import com.russhwolf.settings.coroutines.getStringFlow
import com.russhwolf.settings.coroutines.getStringOrNullFlow
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Starts collecting updates of the given [SuspendSettings]. Only settings extending [FlowSettings] or
 * [ObservableSettings] are supported.
 *
 * If the settings provided doesn't support observing, the current value will be fetched, but no updates are going to be
 * sent back.
 *
 * @param key The key of the settings entry to fetch.
 * @param defaultValue The default value to give to the preference if nothing is stored.
 */
@Composable
@ExperimentalSettingsApi
fun SuspendSettings.getStringLive(
    key: String,
    defaultValue: String
): Flow<String> {
    return when (this) {
        is FlowSettings -> {
            getStringFlow(key, defaultValue)
        }
        is ObservableSettings -> {
            getStringFlow(key, defaultValue)
        }
        else -> {
            flow {
                Napier.w { "Current platform doesn't support live settings. Updates to $key won't be watched." }

                val value = getString(key, defaultValue)
                emit(value)
            }
        }
    }
}

/**
 * Starts collecting updates of the given [SuspendSettings]. Only settings extending [FlowSettings] or
 * [ObservableSettings] are supported.
 *
 * If the settings provided doesn't support observing, the current value will be fetched, but no updates are going to be
 * sent back.
 *
 * @param key The key of the settings entry to fetch.
 */
@ExperimentalSettingsApi
fun SuspendSettings.getStringLiveOrNull(
    key: String
): Flow<String?> {
    return when (this) {
        is FlowSettings -> {
            getStringOrNullFlow(key)
        }
        is ObservableSettings -> {
            getStringOrNullFlow(key)
        }
        else -> {
            flow {
                Napier.w { "Current platform doesn't support live settings. Updates to $key won't be watched." }

                val value = getStringOrNull(key)
                emit(value)
            }
        }
    }
}
