package ui.screen.model

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import cafe.adriel.voyager.core.model.ScreenModel
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.flow.MutableStateFlow

open class AppScreenModel : ScreenModel {
    companion object : AppScreenModelFactory<AppScreenModel> {
        override fun build(): AppScreenModel {
            return AppScreenModel()
        }
    }

    /**
     * The value can be updated at any moment. If it's not null, a snackbar with the given text will be displayed.
     */
    val snackbarError = MutableStateFlow<StringResource?>(null)

    val windowSizeClass = MutableStateFlow<WindowSizeClass?>(null)
}
