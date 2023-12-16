package ui.screen.model

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
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
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
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.launch
import ui.composition.LocalWindowSizeClass
import ui.data.NavigationItem

@ExperimentalFoundationApi
@ExperimentalMaterial3WindowSizeClassApi
abstract class NavigationScreen<SM : NavigationScreenModel>(
    localizedTitle: StringResource? = null,
    ignoreBackPresses: Boolean = true,
    protected open val navigationItems: List<NavigationItem> = emptyList(),
    @Suppress("UNCHECKED_CAST")
    factory: AppScreenModelFactory<SM> = NavigationScreenModel.Factory(navigationItems) as AppScreenModelFactory<SM>
) : AppScreen<SM>(localizedTitle, ignoreBackPresses, factory) {
    companion object {
        const val TEST_TAG_BOTTOM_BAR = "app_screen_bottom_bar"
        const val TEST_TAG_RAIL = "app_screen_rail"
        const val TEST_TAG_NAV_ITEM = "app_screen_nav_item"
    }

    @Composable
    private fun BottomNavigationBar(model: SM, visible: Boolean) {
        AnimatedContent(
            targetState = visible,
            transitionSpec = {
                slideInVertically { -it } togetherWith slideOutVertically { -it }
            }
        ) { isVisible ->
            if (isVisible && navigationItems.isNotEmpty()) {
                val selection by model.navigationSelection.collectAsState()

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
                                onClick = { model.navigationSelection.tryEmit(index) },
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
    private fun NavigationRail(model: SM, visible: Boolean) {
        AnimatedContent(
            targetState = visible,
            transitionSpec = {
                slideInHorizontally { -it } togetherWith slideOutHorizontally { -it }
            }
        ) { isVisible ->
            if (isVisible && navigationItems.isNotEmpty()) {
                val selection by model.navigationSelection.collectAsState()

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
                                onClick = { model.navigationSelection.tryEmit(index) },
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

    /**
     * Handles key events
     */
    @Suppress("MagicNumber", "CyclomaticComplexMethod")
    private fun keyEventHandler(model: SM, event: KeyEvent): Boolean = with(event) {
        when {
            // Only handle release events
            type != KeyEventType.KeyUp -> false

            isAltPressed && key == Key.NumPad1 -> model.selectNavigationItem(0)
            isAltPressed && key == Key.NumPad2 -> model.selectNavigationItem(1)
            isAltPressed && key == Key.NumPad3 -> model.selectNavigationItem(2)
            isAltPressed && key == Key.NumPad4 -> model.selectNavigationItem(3)
            isAltPressed && key == Key.NumPad5 -> model.selectNavigationItem(4)
            isAltPressed && key == Key.NumPad6 -> model.selectNavigationItem(5)
            isAltPressed && key == Key.NumPad7 -> model.selectNavigationItem(6)
            isAltPressed && key == Key.NumPad8 -> model.selectNavigationItem(7)
            isAltPressed && key == Key.NumPad9 -> model.selectNavigationItem(8)
            isAltPressed && key == Key.NumPad0 -> model.selectNavigationItem(9)

            else -> false
        }
    }

    /**
     * Checks the current window size class to define which navigation method to use: navigation rail or bottom
     * navigation bar.
     *
     * @return `true` if the [WindowWidthSizeClass] is equal to [WindowWidthSizeClass.Compact], `false` otherwise.
     */
    @Composable
    private fun shouldDisplayBottomNavigation(): Boolean {
        val windowSizeClassProvider = LocalWindowSizeClass.current
        val windowSizeClass = windowSizeClassProvider.calculate()
        return windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
    }

    /**
     * Makes sure that the selected page in [pagerState] and navigationSelection are synchronized.
     * This makes sure that if navigationSelection is updated, the current page is switched automatically; and if the
     * current page is switched by the user, navigationSelection is updated accordingly.
     *
     * Also handles wrong values to navigationSelection, so if, for example, an invisible page is selected, the value
     * will be adjusted automatically.
     *
     * @param pagerState The pager state to watch.
     */
    @Composable
    private fun PagerStateSynchronizer(model: SM, pagerState: PagerState) {
        val scope = rememberCoroutineScope()
        val selection by model.navigationSelection.collectAsState()

        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.settledPage }
                .collect { page ->
                    if (selection != page) {
                        model.navigationSelection.emit(page)
                    }
                }
        }
        LaunchedEffect(Unit) {
            snapshotFlow { selection }
                .collect { page ->
                    val navItem = navigationItems.getOrNull(page)
                    if (navItem?.visible?.value == false) {
                        if (navigationItems.size >= page + 1) {
                            model.navigationSelection.tryEmit(page + 1)
                        } else {
                            model.navigationSelection.tryEmit(page - 1)
                        }
                    } else if (pagerState.settledPage != page) scope.launch {
                        pagerState.animateScrollToPage(page)
                    }
                }
        }
    }

    @Composable
    override fun ScreenContent(paddingValues: PaddingValues, screenModel: SM) {
        val displayBottomNavigation = shouldDisplayBottomNavigation()

        // The focus requester is added so that the keyEventHandler receives key events
        val rootFocusRequester = remember { FocusRequester() }
        LaunchedEffect(Unit) { rootFocusRequester.requestFocus() }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .onPreviewKeyEvent { keyEventHandler(screenModel, it) }
                .focusable()
                .focusRequester(rootFocusRequester)
        ) {
            NavigationRail(screenModel, !displayBottomNavigation)

            val pagerState = rememberPagerState { navigationItems.size }
            PagerStateSynchronizer(screenModel, pagerState)

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
    override fun BottomBar(screenModel: SM) {
        val displayBottomNavigation = shouldDisplayBottomNavigation()

        BottomNavigationBar(screenModel, displayBottomNavigation)
    }

    @Composable
    abstract fun PagerScope.PageContent(page: Int)
}
