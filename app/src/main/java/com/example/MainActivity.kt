package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.HadithViewModel
import com.example.ui.Screen
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.BookDetailScreen
import com.example.ui.screens.BookmarksScreen
import com.example.ui.screens.CollectionDetailScreen
import com.example.ui.screens.CollectionsScreen
import com.example.ui.screens.HadithDetailScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: HadithViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                HadithAppRoot(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun HadithAppRoot(viewModel: HadithViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val hasSeenOnboarding by viewModel.hasSeenOnboarding.collectAsStateWithLifecycle()

    // Handle Android system back press
    BackHandler(enabled = currentScreen !is Screen.Home) {
        if (!viewModel.navigateBack()) {
            viewModel.navigateTo(Screen.Home)
        }
    }

    val isTopLevelDestination = currentScreen is Screen.Home ||
            currentScreen is Screen.Collections ||
            currentScreen is Screen.Search ||
            currentScreen is Screen.Bookmarks ||
            currentScreen is Screen.Settings

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (isTopLevelDestination) {
                HadithBottomNavBar(
                    currentScreen = currentScreen,
                    onNavigate = { viewModel.navigateTo(it) }
                )
            }
        }
    ) { innerPadding ->
        val screenModifier = Modifier.padding(innerPadding)

        when (val screen = currentScreen) {
            is Screen.Home -> HomeScreen(viewModel, modifier = screenModifier)
            is Screen.Collections -> CollectionsScreen(viewModel, modifier = screenModifier)
            is Screen.CollectionDetail -> CollectionDetailScreen(screen.collectionName, viewModel, modifier = screenModifier)
            is Screen.BookDetail -> BookDetailScreen(screen.collectionName, screen.bookNumber, viewModel, modifier = screenModifier)
            is Screen.Search -> SearchScreen(viewModel, modifier = screenModifier)
            is Screen.Bookmarks -> BookmarksScreen(viewModel, modifier = screenModifier)
            is Screen.Settings -> SettingsScreen(viewModel, modifier = screenModifier)
            is Screen.AdminDashboard -> AdminDashboardScreen(viewModel, modifier = screenModifier)
            is Screen.HadithDetail -> HadithDetailScreen(
                hadithId = screen.hadithId,
                returnScreen = screen.returnScreen,
                viewModel = viewModel,
                modifier = screenModifier
            )
        }

        if (!hasSeenOnboarding) {
            OnboardingDialog(onDismiss = { viewModel.dismissOnboarding() })
        }
    }
}

@Composable
fun OnboardingDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "DEEN-E-ILM | دینِ علم",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Authentic Prophetic Hadith Library & Search",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column {
                Text(
                    text = "مرحباً بكم في تطبيق دینِ علم لدراسة وتصفح أحاديث خير الأنام ﷺ.",
                    fontFamily = AmiriFontFamily,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "• 100% Free & Offline-First: All bundled hadiths work completely without internet.\n\n" +
                            "• Exact Reference Search: Quickly look up references like \"Sahih Bukhari 1\" or \"Bukhari 13\".\n\n" +
                            "• Arabic, English, & Urdu: Verified translations alongside classical Arabic matn and scholar authenticity gradings.\n\n" +
                            "• Zero Tracking & Privacy: Local bookmarks, no advertisements, and zero telemetry collection.",
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("onboarding_get_started_button")
            ) {
                Text("Get Started / ابدأ")
            }
        }
    )
}

@Composable
fun HadithBottomNavBar(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
            .navigationBarsPadding()
            .testTag("main_bottom_nav_bar")
    ) {
        NavigationBarItem(
            selected = currentScreen is Screen.Home,
            onClick = { onNavigate(Screen.Home) },
            icon = {
                Icon(
                    imageVector = if (currentScreen is Screen.Home) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Home"
                )
            },
            label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("nav_item_home")
        )

        NavigationBarItem(
            selected = currentScreen is Screen.Collections || currentScreen is Screen.CollectionDetail || currentScreen is Screen.BookDetail,
            onClick = { onNavigate(Screen.Collections) },
            icon = {
                Icon(
                    imageVector = if (currentScreen is Screen.Collections) Icons.Filled.AutoStories else Icons.Outlined.AutoStories,
                    contentDescription = "Collections"
                )
            },
            label = { Text("Collections", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("nav_item_collections")
        )

        NavigationBarItem(
            selected = currentScreen is Screen.Search,
            onClick = { onNavigate(Screen.Search) },
            icon = {
                Icon(
                    imageVector = if (currentScreen is Screen.Search) Icons.Filled.Search else Icons.Outlined.Search,
                    contentDescription = "Search"
                )
            },
            label = { Text("Search", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("nav_item_search")
        )

        NavigationBarItem(
            selected = currentScreen is Screen.Bookmarks,
            onClick = { onNavigate(Screen.Bookmarks) },
            icon = {
                Icon(
                    imageVector = if (currentScreen is Screen.Bookmarks) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = "Bookmarks"
                )
            },
            label = { Text("Bookmarks", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("nav_item_bookmarks")
        )

        NavigationBarItem(
            selected = currentScreen is Screen.Settings,
            onClick = { onNavigate(Screen.Settings) },
            icon = {
                Icon(
                    imageVector = if (currentScreen is Screen.Settings) Icons.Filled.Settings else Icons.Outlined.Settings,
                    contentDescription = "Settings"
                )
            },
            label = { Text("Settings", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("nav_item_settings")
        )
    }
}
