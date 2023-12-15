package ui.screen.model

import KoverIgnore
import dev.icerock.moko.resources.StringResource

/**
 * Screens inheriting from this class won't support going back.
 */
@KoverIgnore
abstract class BaseScreen(
    localizedTitle: StringResource? = null
) : AppScreen(localizedTitle)
