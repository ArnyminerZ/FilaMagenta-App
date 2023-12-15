package ui.screen.model

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ui.composition.LocalWindowSizeClass
import ui.data.NavigationItem

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

    @Composable
    private fun BottomNavigationBar(visible: Boolean) {
        val selection by navigationSelection.collectAsState()

        AnimatedContent(
            targetState = visible,
            transitionSpec = {
                slideInVertically { -it } togetherWith slideOutVertically { -it }
            }
        ) { isVisible ->
            if (isVisible && navigationItems.isNotEmpty()) {
                NavigationBar(
                    modifier = Modifier.testTag(TEST_TAG_BOTTOM_BAR)
                ) {
                    for ((i, item) in navigationItems.withIndex()) {
                        val selected = selection == i
                        NavigationBarItem(
                            selected = selected,
                            onClick = { navigationSelection.tryEmit(i) },
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

    @Composable
    private fun NavigationRail(visible: Boolean) {
        val selection by navigationSelection.collectAsState()

        AnimatedContent(
            targetState = visible,
            transitionSpec = {
                slideInHorizontally { -it } togetherWith slideOutHorizontally { -it }
            }
        ) { isVisible ->
            if (isVisible && navigationItems.isNotEmpty()) {
                androidx.compose.material3.NavigationRail(
                    modifier = Modifier.testTag(TEST_TAG_RAIL)
                ) {
                    for ((i, item) in navigationItems.withIndex()) {
                        val selected = selection == i
                        NavigationRailItem(
                            selected = selected,
                            onClick = { navigationSelection.tryEmit(i) },
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

    @Composable
    override fun ScreenContent(paddingValues: PaddingValues) {
        val scope = rememberCoroutineScope()

        val windowSizeClassProvider = LocalWindowSizeClass.current
        val windowSizeClass = windowSizeClassProvider.calculate()
        val displayBottomNavigation = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

        Row(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            NavigationRail(!displayBottomNavigation)

            val pagerState = rememberPagerState { navigationItems.size }
            val selection by navigationSelection.collectAsState()

            LaunchedEffect(pagerState) {
                snapshotFlow { pagerState.currentPage }
                    .collect { page ->
                        if (selection != page) {
                            navigationSelection.emit(page)
                        }
                    }
            }
            LaunchedEffect(Unit) {
                snapshotFlow { selection }
                    .collect { page ->
                        if (pagerState.currentPage != page) scope.launch {
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
