package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CollectionEntity
import com.example.ui.HadithViewModel
import com.example.ui.Screen
import com.example.ui.components.AuthenticityBadge
import com.example.ui.components.HadithCard
import com.example.ui.components.IslamicHeaderBanner
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.GoldAccent

@Composable
fun HomeScreen(
    viewModel: HadithViewModel,
    modifier: Modifier = Modifier
) {
    val collections by viewModel.collections.collectAsStateWithLifecycle()
    val featuredHadith by viewModel.featuredHadith.collectAsStateWithLifecycle()
    val bookmarkedHadiths by viewModel.bookmarkedHadiths.collectAsStateWithLifecycle()
    val recentHistory by viewModel.recentHistory.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("home_screen_content"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // 1. Header Banner
        item {
            IslamicHeaderBanner(
                title = "مكتبة الحديث",
                subtitle = "Hadith Library — Searchable Prophetic Traditions"
            )
        }

        // 2. Search Box Trigger
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { viewModel.navigateTo(Screen.Search) }
                        .testTag("home_search_trigger"),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Search hadith, Arabic, English, Urdu, narrator...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // 3. Featured Hadith of the Day
        item {
            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader(
                title = "Hadith of the Day",
                arabicTitle = "حديث اليوم",
                icon = Icons.Default.Star,
                onViewAll = null
            )
            Spacer(modifier = Modifier.height(8.dp))

            featuredHadith?.let { hadith ->
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    HadithCard(
                        hadith = hadith,
                        onClick = {
                            viewModel.navigateTo(Screen.HadithDetail(hadith.id, Screen.Home))
                        },
                        onToggleBookmark = {
                            viewModel.toggleBookmark(hadith)
                        }
                    )
                }
            }
        }

        // 4. Browse Collections Carousel
        item {
            Spacer(modifier = Modifier.height(24.dp))
            SectionHeader(
                title = "Canonical Collections",
                arabicTitle = "كتب الحديث المعتمدة",
                icon = Icons.Default.AutoStories,
                onViewAll = { viewModel.navigateTo(Screen.Collections) }
            )
            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(collections, key = { it.name }) { collection ->
                    CollectionCarouselCard(
                        collection = collection,
                        onClick = {
                            viewModel.navigateTo(Screen.CollectionDetail(collection.name))
                        }
                    )
                }
            }
        }

        // 5. Bookmarks Section (if any)
        if (bookmarkedHadiths.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader(
                    title = "Saved Bookmarks",
                    arabicTitle = "المحفوظات",
                    icon = Icons.Default.Bookmark,
                    onViewAll = { viewModel.navigateTo(Screen.Bookmarks) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(bookmarkedHadiths.take(2), key = { "home_bm_${it.id}" }) { hadith ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    HadithCard(
                        hadith = hadith,
                        onClick = {
                            viewModel.navigateTo(Screen.HadithDetail(hadith.id, Screen.Home))
                        },
                        onToggleBookmark = {
                            viewModel.toggleBookmark(hadith)
                        }
                    )
                }
            }
        }

        // 6. Recent History Section (if any)
        if (recentHistory.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader(
                    title = "Recently Viewed",
                    arabicTitle = "شوهد مؤخراً",
                    icon = Icons.Default.History,
                    onViewAll = { viewModel.navigateTo(Screen.Bookmarks) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(recentHistory.take(2), key = { "home_rec_${it.id}" }) { hadith ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    HadithCard(
                        hadith = hadith,
                        onClick = {
                            viewModel.navigateTo(Screen.HadithDetail(hadith.id, Screen.Home))
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

@Composable
fun SectionHeader(
    title: String,
    arabicTitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onViewAll: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = arabicTitle,
                    fontFamily = AmiriFontFamily,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (onViewAll != null) {
            TextButton(
                onClick = onViewAll,
                modifier = Modifier.testTag("view_all_button_${title.lowercase().replace(" ", "_")}")
            ) {
                Text(
                    text = "View All",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun CollectionCarouselCard(
    collection: CollectionEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier
            .width(170.dp)
            .clickable(onClick = onClick)
            .testTag("collection_carousel_card_${collection.name}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            // Gold accent emblem
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(GoldAccent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoStories,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Arabic title
            Text(
                text = collection.titleArabic,
                fontFamily = AmiriFontFamily,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // English title
            Text(
                text = collection.titleEnglish,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Urdu title
            Text(
                text = collection.titleUrdu,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Hadith Count
            Text(
                text = "${collection.totalHadith} Hadiths",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
