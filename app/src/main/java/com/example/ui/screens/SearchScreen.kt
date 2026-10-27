package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.HadithViewModel
import com.example.ui.Screen
import com.example.ui.components.EmptyStateView
import com.example.ui.components.HadithCard
import com.example.ui.components.SearchInputField
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.GoldAccent

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    viewModel: HadithViewModel,
    modifier: Modifier = Modifier
) {
    val filterState by viewModel.filterState.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val searchHistory by viewModel.recentSearchQueries.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Search Hadith Library",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "البحث في الأحاديث النبوية",
                            fontFamily = AmiriFontFamily,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    if (filterState.query.isNotBlank() || filterState.collection != null || filterState.grade != null || filterState.language != null) {
                        TextButton(
                            onClick = { viewModel.clearSearchFilters() },
                            modifier = Modifier.testTag("reset_filters_button")
                        ) {
                            Text(
                                text = "Reset",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.testTag("search_screen")
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Search Input Box
            item {
                SearchInputField(
                    query = filterState.query,
                    onQueryChange = { viewModel.updateSearchQuery(it) },
                    onSearch = { viewModel.submitSearch(it) }
                )
            }

            // Recent Search History (if query is empty and history exists)
            if (filterState.query.isEmpty() && searchHistory.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Recent Searches",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        TextButton(
                            onClick = { viewModel.clearSearchHistory() },
                            modifier = Modifier.testTag("clear_history_button")
                        ) {
                            Text(
                                text = "Clear",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        searchHistory.forEach { queryItem ->
                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .clickable { viewModel.submitSearch(queryItem) }
                                    .testTag("history_chip_$queryItem")
                            ) {
                                Text(
                                    text = queryItem,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Filter Chips Section
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Filter by Collection",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    // Collection horizontal filters
                    val collectionsList = listOf(
                        null to "All Collections",
                        "bukhari" to "Bukhari",
                        "muslim" to "Muslim",
                        "abudawud" to "Abu Dawud",
                        "tirmidhi" to "Tirmidhi",
                        "nasai" to "Nasa'i",
                        "ibnmajah" to "Ibn Majah",
                        "malik" to "Muwatta",
                        "ahmad" to "Musnad Ahmad"
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(collectionsList) { (colKey, colLabel) ->
                            val isSelected = filterState.collection == colKey
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    viewModel.setCollectionFilter(if (isSelected) null else colKey)
                                },
                                label = { Text(colLabel, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                modifier = Modifier.testTag("filter_collection_${colKey ?: "all"}")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Authenticity Grade Filters & Language Filter Row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Grade:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        FilterChip(
                            selected = filterState.grade == "Sahih",
                            onClick = {
                                viewModel.setGradeFilter(if (filterState.grade == "Sahih") null else "Sahih")
                            },
                            label = { Text("Sahih", fontSize = 11.sp) },
                            modifier = Modifier.testTag("filter_grade_sahih")
                        )
                        FilterChip(
                            selected = filterState.grade == "Hasan",
                            onClick = {
                                viewModel.setGradeFilter(if (filterState.grade == "Hasan") null else "Hasan")
                            },
                            label = { Text("Hasan", fontSize = 11.sp) },
                            modifier = Modifier.testTag("filter_grade_hasan")
                        )

                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Lang:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        FilterChip(
                            selected = filterState.language == "ar",
                            onClick = {
                                viewModel.setLanguageFilter(if (filterState.language == "ar") null else "ar")
                            },
                            label = { Text("عربي", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = filterState.language == "en",
                            onClick = {
                                viewModel.setLanguageFilter(if (filterState.language == "en") null else "en")
                            },
                            label = { Text("EN", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = filterState.language == "ur",
                            onClick = {
                                viewModel.setLanguageFilter(if (filterState.language == "ur") null else "ur")
                            },
                            label = { Text("اردو", fontSize = 11.sp) }
                        )
                    }
                }
            }

            // Results count banner
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Results (${searchResults.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (filterState.query.isNotBlank()) {
                        Text(
                            text = "Query: \"${filterState.query}\"",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Results list or Empty State
            if (searchResults.isEmpty()) {
                item {
                    EmptyStateView(
                        message = "No matching hadiths found",
                        tip = "Try searching by Arabic keywords (e.g. نية, صلاة), English words (intentions, belief), Urdu (ایمان), narrator (Umar, Abu Huraira), or number (e.g. 'bukhari 1', '13')."
                    )
                }
            } else {
                items(searchResults, key = { it.id }) { hadith ->
                    HadithCard(
                        hadith = hadith,
                        onClick = {
                            viewModel.navigateTo(Screen.HadithDetail(hadith.id, Screen.Search))
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
