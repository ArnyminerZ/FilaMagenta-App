package ui.screen.model

import accounts.Account
import accounts.AccountManager
import accounts.liveSelectedAccount
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.testTag
import com.russhwolf.settings.ExperimentalSettingsApi
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ui.composition.LocalWindowSizeClass
import ui.data.NavigationItem
import ui.data.NavigationItemOption

@ExperimentalFoundationApi
@ExperimentalMaterial3WindowSizeClassApi
abstract class NavigationScreen(
    localizedTitle: StringResource? = null,
    ignoreBackPresses: Boolean = true
) : AppScreen(localizedTitle, ignoreBackPresses) {
    companion object {
        const val TEST_TAG_BOTTOM_BAR = "app_screen_bottom_bar"
        const val TEST_TAG_RAIL = "app_screen_rail"
        const val TEST_TAG_NAV_ITEM = "app_screen_nav_item"
    }

    protected open val navigationItems: List<NavigationItem> = emptyList()

    val navigationSelection = MutableStateFlow(0)

    private suspend fun updateNavigationItemsVisibility(account: Account?, windowSizeClass: WindowSizeClass?) {
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

    @OptIn(ExperimentalSettingsApi::class)
    private val liveAccount = AccountManager.liveSelectedAccount()

    init {
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

    @Composable
    private fun BottomNavigationBar(visible: Boolean) {
        AnimatedContent(
            targetState = visible,
            transitionSpec = {
                slideInVertically { -it } togetherWith slideOutVertically { -it }
            }
        ) { isVisible ->
            if (isVisible && navigationItems.isNotEmpty()) {
                val selection by navigationSelection.collectAsState()

                NavigationBar(
                    modifier = Modifier.testTag(TEST_TAG_BOTTOM_BAR)
                ) {
                    for ((index, item) in navigationItems.withIndex()) {
                        val itemVisible by item.visible.collectAsState(false)
                        AnimatedVisibility(
                            visible = itemVisible,
                            enter = slideInHorizontally { -it },
                            exit = slideOutHorizontally { -it }
                        ) {
                            val selected = selection == index

                            NavigationBarItem(
                                selected = selected,
                                onClick = { navigationSelection.tryEmit(index) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) {
                                            item.selectedIcon
                                        } else {
                                            item.icon
                                        },
                                        contentDescription = if (selected) {
                                            item.selectedIconContentDescription()
                                        } else {
                                            item.iconContentDescription()
                                        }
                                    )
                                },
                                label = item.label,
                                modifier = Modifier.testTag(TEST_TAG_NAV_ITEM)
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun NavigationRail(visible: Boolean) {
        AnimatedContent(
            targetState = visible,
            transitionSpec = {
                slideInHorizontally { -it } togetherWith slideOutHorizontally { -it }
            }
        ) { isVisible ->
            if (isVisible && navigationItems.isNotEmpty()) {
                val selection by navigationSelection.collectAsState()

                androidx.compose.material3.NavigationRail(
                    modifier = Modifier.testTag(TEST_TAG_RAIL)
                ) {
                    for ((index, item) in navigationItems.withIndex()) {
                        val itemVisible by item.visible.collectAsState(false)
                        AnimatedVisibility(
                            visible = itemVisible,
                            enter = slideInHorizontally { -it },
                            exit = slideOutHorizontally { -it }
                        ) {
                            val selected = selection == index

                            NavigationRailItem(
                                selected = selected,
                                onClick = { navigationSelection.tryEmit(index) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) {
                                            item.selectedIcon
                                        } else {
                                            item.icon
                                        },
                                        contentDescription = if (selected) {
                                            item.selectedIconContentDescription()
                                        } else {
                                            item.iconContentDescription()
                                        }
                                    )
                                },
                                label = item.label,
                                modifier = Modifier.testTag(TEST_TAG_NAV_ITEM)
                            )
                        }
                    }
                }
            }
        }
    }

    private fun selectNavigationItem(index: Int): Boolean {
        return if (navigationItems.size >= index + 1) {
            navigationSelection.tryEmit(index)
            true
        } else {
            false
        }
    }

    /**
     * Handles key events
     */
    @Suppress("MagicNumber")
    private fun keyEventHandler(event: KeyEvent): Boolean {
        // Only handle release events
        if (event.type != KeyEventType.KeyUp) return false

        return when {
            event.isAltPressed -> {
                when (event.key) {
                    Key.NumPad1 -> selectNavigationItem(0)
                    Key.NumPad2 -> selectNavigationItem(1)
                    Key.NumPad3 -> selectNavigationItem(2)
                    Key.NumPad4 -> selectNavigationItem(3)
                    Key.NumPad5 -> selectNavigationItem(4)
                    Key.NumPad6 -> selectNavigationItem(5)
                    Key.NumPad7 -> selectNavigationItem(6)
                    Key.NumPad8 -> selectNavigationItem(7)
                    Key.NumPad9 -> selectNavigationItem(8)
                    Key.NumPad0 -> selectNavigationItem(9)

                    else -> false
                }
            }

            else -> false
        }
    }

    @Composable
    override fun ScreenContent(paddingValues: PaddingValues) {
        val scope = rememberCoroutineScope()

        val windowSizeClassProvider = LocalWindowSizeClass.current
        val windowSizeClass = windowSizeClassProvider.calculate()
        val displayBottomNavigation = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

        // The focus requester is added so that the keyEventHandler receives key events
        val rootFocusRequester = remember { FocusRequester() }
        LaunchedEffect(Unit) { rootFocusRequester.requestFocus() }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .onPreviewKeyEvent(::keyEventHandler)
                .focusable()
                .focusRequester(rootFocusRequester)
        ) {
            NavigationRail(!displayBottomNavigation)

            val pagerState = rememberPagerState { navigationItems.size }
            val selection by navigationSelection.collectAsState()

            LaunchedEffect(pagerState) {
                snapshotFlow { pagerState.settledPage }
                    .collect { page ->
                        if (selection != page) {
                            navigationSelection.emit(page)
                        }
                    }
            }
            LaunchedEffect(Unit) {
                snapshotFlow { selection }
                    .collect { page ->
                        val navItem = navigationItems.getOrNull(page)
                        if (navItem?.visible?.value == false) {
                            if (navigationItems.size >= page + 1) {
                                navigationSelection.tryEmit(page + 1)
                            } else {
                                navigationSelection.tryEmit(page - 1)
                            }
                        } else if (pagerState.settledPage != page) scope.launch {
                            pagerState.animateScrollToPage(page)
                        }
                    }
            }

            if (displayBottomNavigation) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) { page ->
                    PageContent(page)
                }
            } else {
                VerticalPager(
                    state = pagerState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    userScrollEnabled = false
                ) { page ->
                    PageContent(page)
                }
            }
        }
    }

    @Composable
    override fun BottomBar() {
        val windowSizeClassProvider = LocalWindowSizeClass.current
        val windowSizeClass = windowSizeClassProvider.calculate()
        val displayBottomNavigation = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

        BottomNavigationBar(displayBottomNavigation)
    }

    @Composable
    abstract fun PagerScope.PageContent(page: Int)
}
