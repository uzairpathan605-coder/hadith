package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.HadithViewModel
import com.example.ui.Screen
import com.example.ui.components.EmptyStateView
import com.example.ui.components.HadithCard
import com.example.ui.theme.AmiriFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(
    viewModel: HadithViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val bookmarkedHadiths by viewModel.bookmarkedHadiths.collectAsStateWithLifecycle()
    val recentHistory by viewModel.recentHistory.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (selectedTab == 0) "Saved Bookmarks" else "Reading History",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = if (selectedTab == 0) "الأحاديث المحفوظة" else "سجل القراءة",
                            fontFamily = AmiriFontFamily,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.testTag("bookmarks_screen")
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Bookmarks (${bookmarkedHadiths.size})", fontSize = 13.sp, fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.Bookmark, contentDescription = null) },
                    modifier = Modifier.testTag("bookmarks_tab")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("History (${recentHistory.size})", fontSize = 13.sp, fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.History, contentDescription = null) },
                    modifier = Modifier.testTag("history_tab")
                )
            }

            val currentList = if (selectedTab == 0) bookmarkedHadiths else recentHistory

            if (currentList.isEmpty()) {
                EmptyStateView(
                    message = if (selectedTab == 0) "No bookmarks saved yet" else "No reading history yet",
                    tip = if (selectedTab == 0)
                        "Tap the bookmark icon on any hadith card to save it for quick reference."
                    else
                        "Hadiths you open to read will appear here in chronological order."
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(currentList, key = { "${selectedTab}_${it.id}" }) { hadith ->
                        HadithCard(
                            hadith = hadith,
                            onClick = {
                                viewModel.navigateTo(Screen.HadithDetail(hadith.id, Screen.Bookmarks))
                            },
                            onToggleBookmark = {
                                viewModel.toggleBookmark(hadith)
                            }
                        )
                    }
                }
            }
        }
    }
}
