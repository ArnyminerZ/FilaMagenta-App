package ui.model

import ui.data.NavigationItem
import ui.screen.model.AppScreenModelFactory
import ui.screen.model.NavigationScreenModel

class MainScreenModel(
    navigationItems: List<NavigationItem>
) : NavigationScreenModel(navigationItems) {
    class Factory(
        private val navigationItems: List<NavigationItem>
    ) : AppScreenModelFactory<MainScreenModel> {
        override fun build(): MainScreenModel = MainScreenModel(navigationItems)
    }
}
