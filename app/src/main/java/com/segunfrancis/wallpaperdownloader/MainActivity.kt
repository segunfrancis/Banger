package com.segunfrancis.wallpaperdownloader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.Keep
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.segunfrancis.author_details.ui.AuthorDetailsScreen
import com.segunfrancis.details.ui.DetailsScreen
import com.segunfrancis.favourites.ui.ui.FavouriteScreen
import com.segunfrancis.home.ui.CategoryDetailsScreen
import com.segunfrancis.home.ui.HomeScreen
import com.segunfrancis.local.AppTheme
import com.segunfrancis.profile.ui.ProfileScreen
import com.segunfrancis.settings.ui.SettingsScreen
import com.segunfrancis.theme.WallpaperDownloaderTheme
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val mainViewModel = koinViewModel<MainViewModel>()
            val currentTheme by mainViewModel.theme.collectAsStateWithLifecycle()
            val isDarkTheme = when (currentTheme) {
                AppTheme.Dark -> {
                    true
                }

                AppTheme.Light -> {
                    false
                }

                else -> {
                    isSystemInDarkTheme()
                }
            }
            WallpaperDownloaderTheme(darkTheme = isDarkTheme) {
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

    Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = {
        if (currentDestination?.hierarchy?.any { it.hasRoute(AppDestinations.Home::class) } == true
            || currentDestination?.hierarchy?.any { it.hasRoute(AppDestinations.Favourites::class) } == true
            || currentDestination?.hierarchy?.any { it.hasRoute(AppDestinations.Profile::class) } == true) {
            NavigationBar(modifier = Modifier.fillMaxWidth()) {
                NavMenuItems.entries.forEach { menuItem ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                menuItem.icon,
                                contentDescription = menuItem.label
                            )
                        },
                        label = { Text(menuItem.label) },
                        selected = currentDestination.hierarchy.any { it.hasRoute(menuItem.route::class) },
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
        }
    }) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppDestinations.Home,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<AppDestinations.Home> {
                HomeScreen(onCategoryClick = {
                    navController.navigate(AppDestinations.CategoryDetails(it))
                }, onMenuActionClick = {
                    navController.navigate(AppDestinations.Settings)
                })
            }
            composable<AppDestinations.Profile> { ProfileScreen() }
            composable<AppDestinations.Favourites> {
                FavouriteScreen(onMenuActionClick = {
                    navController.navigate(
                        AppDestinations.Settings
                    )
                })
            }
            composable<AppDestinations.CategoryDetails> {
                val route = it.toRoute<AppDestinations.CategoryDetails>()
                CategoryDetailsScreen(
                    title = route.category,
                    navigateBack = { navController.navigateUp() },
                    onPhotoClick = { id ->
                        navController.navigate(AppDestinations.Details(id))
                    })
            }
            composable<AppDestinations.Details> {
                DetailsScreen(
                    onBackClick = { navController.navigateUp() },
                    viewAuthorDetails = { id, name, username, bio, profileImage, blurHash ->
                        navController.navigate(
                            AppDestinations.AuthorDetails(
                                id = id,
                                name = name,
                                username = username,
                                bio = bio,
                                profileImage = profileImage,
                                blurHash = blurHash
                            )
                        )
                    }
                )
            }
            composable<AppDestinations.Settings> { SettingsScreen(onBackClick = { navController.navigateUp() }) }
            composable<AppDestinations.AuthorDetails> {
                val route = it.toRoute<AppDestinations.AuthorDetails>()
                AuthorDetailsScreen(
                    onBackClick = { navController.navigateUp() },
                    onImageClick = { imageId: String ->
                        navController.navigate(
                            AppDestinations.Details(
                                imageId
                            )
                        )
                    },
                    name = route.name,
                    username = route.username,
                    profileImage = route.profileImage,
                    blurHash = route.blurHash,
                    bio = route.bio
                )
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

    @Serializable
    data class Details(val id: String) : AppDestinations()

    @Serializable
    data object Settings : AppDestinations()

    @Serializable
    data class AuthorDetails(
        val id: String,
        val name: String,
        val username: String,
        val bio: String,
        val profileImage: String,
        val blurHash: String
    ) : AppDestinations()

    @Serializable
    data class CategoryDetails(val category: String) : AppDestinations()
}
