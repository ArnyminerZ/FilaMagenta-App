package ui.data

import KoverIgnore
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.flow.MutableStateFlow

@KoverIgnore
data class NavigationItem(
    val label: @Composable () -> Unit,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon,
    val iconContentDescription: @Composable () -> String? = { null },
    val selectedIconContentDescription: @Composable () -> String? = iconContentDescription,
    val options: List<NavigationItemOption> = emptyList()
) {
    val visible = MutableStateFlow(false)
}
