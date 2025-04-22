package com.segunfrancis.wallpaperdownloader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.Keep
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.segunfrancis.favourites.ui.FavouriteScreen
import com.segunfrancis.home.ui.ui.HomeScreen
import com.segunfrancis.profile.ui.ProfileScreen
import com.segunfrancis.theme.WallpaperDownloaderTheme
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.KoinAndroidContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WallpaperDownloaderTheme {
                KoinAndroidContext {
                    WallpaperDownloaderApp()
                }
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun WallpaperDownloaderApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    NavigationSuiteScaffold(
        navigationSuiteItems = {
            NavMenuItems.entries.forEach { menuItem ->
                item(
                    icon = {
                        Icon(
                            menuItem.icon,
                            contentDescription = menuItem.label
                        )
                    },
                    label = { Text(menuItem.label) },
                    selected = currentDestination?.hierarchy?.any { it.hasRoute(menuItem.route::class) } == true,
                    onClick = {
                        navController.navigate(menuItem.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = AppDestinations.Home,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable<AppDestinations.Home> { HomeScreen() }
                composable<AppDestinations.Profile> { ProfileScreen() }
                composable<AppDestinations.Favourites> { FavouriteScreen() }
            }
        }
    }
}

@Serializable
@Keep
enum class NavMenuItems(
    val label: String,
    val icon: ImageVector,
    val route: AppDestinations
) {
    HOME("Home", Icons.Default.Home, AppDestinations.Home),
    FAVORITES("Favorites", Icons.Default.Favorite, AppDestinations.Favourites),
    PROFILE("Profile", Icons.Default.AccountBox, AppDestinations.Profile),
}

sealed class AppDestinations {
    @Serializable
    data object Home : AppDestinations()

    @Serializable
    data object Favourites : AppDestinations()

    @Serializable
    data object Profile : AppDestinations()
}
