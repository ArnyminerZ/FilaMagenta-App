package ui.screen.model

import accounts.Account
import accounts.AccountManager
import accounts.liveSelectedAccount
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import com.russhwolf.settings.ExperimentalSettingsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ui.data.NavigationItem
import ui.data.NavigationItemOption

@OptIn(ExperimentalSettingsApi::class)
open class NavigationScreenModel(
    private val navigationItems: List<NavigationItem>
) : AppScreenModel() {
    class Factory(
        private val navigationItems: List<NavigationItem>
    ) : AppScreenModelFactory<NavigationScreenModel> {
        override fun build(): NavigationScreenModel {
            return NavigationScreenModel(navigationItems)
        }
    }

    val navigationSelection = MutableStateFlow(0)

    init {
        val liveAccount = AccountManager.liveSelectedAccount()

        CoroutineScope(Dispatchers.IO).launch {
            liveAccount.collect {
                updateNavigationItemsVisibility(it, windowSizeClass.value)
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            windowSizeClass.collect {
                updateNavigationItemsVisibility(liveAccount.value, it)
            }
        }
    }

    suspend fun updateNavigationItemsVisibility(account: Account?, windowSizeClass: WindowSizeClass?) {
        // Iterate all the items
        for (item in navigationItems) {
            var display = true

            // Iterate all the options if any. If not, display will always be true
            for (option in item.options) {
                val check = when (option) {
                    // Display only if the account has a given role
                    is NavigationItemOption.DisplayIfHasRole -> option.check(account)

                    // Display only if the screen width size class is one of the given
                    is NavigationItemOption.DisplayIfWidthSizeClass -> option.check(windowSizeClass?.widthSizeClass)
                }
                if (!check) {
                    display = false
                }
            }
            item.visible.emit(display)
        }
    }

    /**
     * Updates [navigationSelection] with the [index] given, if it's in bounds.
     *
     * @param index The index to set. Must be between zero and the amount of items in [navigationItems].
     */
    fun selectNavigationItem(index: Int): Boolean {
        return if (index >= 0 && navigationItems.size >= index + 1) {
            navigationSelection.tryEmit(index)
            true
        } else {
            false
        }
    }
}
