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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.BookEntity
import com.example.data.model.CollectionEntity
import com.example.ui.HadithViewModel
import com.example.ui.Screen
import com.example.ui.components.EmptyStateView
import com.example.ui.components.HadithCard
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.GoldAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionsScreen(
    viewModel: HadithViewModel,
    modifier: Modifier = Modifier
) {
    val collections by viewModel.collections.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Hadith Collections",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "المجموعات الحديثية المعتمدة",
                            fontFamily = AmiriFontFamily,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        modifier = modifier.testTag("collections_screen")
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Canonical Compilations (Kutub al-Sittah & Major Works)",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Browse through authentic books, chapters, and narrations with scholarly gradings.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(collections, key = { it.name }) { collection ->
                CollectionListItem(
                    collection = collection,
                    onClick = {
                        viewModel.navigateTo(Screen.CollectionDetail(collection.name))
                    }
                )
            }
        }
    }
}

@Composable
fun CollectionListItem(
    collection: CollectionEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("collection_item_${collection.name}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Book,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Text Info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = collection.titleEnglish,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = collection.titleArabic,
                        fontFamily = AmiriFontFamily,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = collection.author,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "${collection.totalHadith} Hadiths",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "عربي • English • اردو",
                        fontSize = 11.sp,
                        color = GoldAccent,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = "Open collection",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionDetailScreen(
    collectionName: String,
    viewModel: HadithViewModel,
    modifier: Modifier = Modifier
) {
    val collection by viewModel.getCollection(collectionName).collectAsStateWithLifecycle(initialValue = null)
    val books by viewModel.getBooksForCollection(collectionName).collectAsStateWithLifecycle(initialValue = emptyList())
    val hadiths by viewModel.getHadithsForCollection(collectionName).collectAsStateWithLifecycle(initialValue = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = collection?.titleEnglish ?: "Collection",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = collection?.titleArabic ?: "",
                            fontFamily = AmiriFontFamily,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("collection_detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.testTag("collection_detail_screen")
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Author and intro banner
            item {
                collection?.let { col ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = col.titleArabic,
                                fontFamily = AmiriFontFamily,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = col.author,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = col.shortIntro,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Total verified narrations: ${col.totalHadith}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Books / Chapters Section
            if (books.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Books & Chapters (${books.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                items(books, key = { it.id }) { book ->
                    BookListItem(
                        book = book,
                        onClick = {
                            viewModel.navigateTo(Screen.BookDetail(collectionName, book.bookNumber))
                        }
                    )
                }
            }

            // Available hadiths preview
            if (hadiths.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Available Narrations (${hadiths.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                items(hadiths, key = { it.id }) { hadith ->
                    HadithCard(
                        hadith = hadith,
                        onClick = {
                            viewModel.navigateTo(Screen.HadithDetail(hadith.id, Screen.CollectionDetail(collectionName)))
                        },
                        onToggleBookmark = {
                            viewModel.toggleBookmark(hadith)
                        }
                    )
                }
            } else if (books.isEmpty()) {
                item {
                    EmptyStateView(
                        message = "No records cached locally",
                        tip = "Connect with your Sunnah.com API key in Settings to sync and view live books."
                    )
                }
            }
        }
    }
}

@Composable
fun BookListItem(
    book: BookEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("book_item_${book.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = book.bookNumber,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.titleEnglish,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = book.titleArabic,
                    fontFamily = AmiriFontFamily,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                if (book.titleUrdu.isNotBlank()) {
                    Text(
                        text = book.titleUrdu,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "${book.numberOfHadith} Hadiths",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.width(6.dp))

            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    collectionName: String,
    bookNumber: String,
    viewModel: HadithViewModel,
    modifier: Modifier = Modifier
) {
    val hadiths by viewModel.getHadithsForBook(collectionName, bookNumber).collectAsStateWithLifecycle(initialValue = emptyList())
    val collection by viewModel.getCollection(collectionName).collectAsStateWithLifecycle(initialValue = null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "${collection?.titleEnglish ?: collectionName} — Book $bookNumber",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = collection?.titleArabic ?: "",
                            fontFamily = AmiriFontFamily,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("book_detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.testTag("book_detail_screen")
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (hadiths.isEmpty()) {
                item {
                    EmptyStateView(
                        message = "No hadiths found for this book",
                        tip = "Records for this book may not be cached locally yet. Check back or configure API sync in Settings."
                    )
                }
            } else {
                items(hadiths, key = { it.id }) { hadith ->
                    HadithCard(
                        hadith = hadith,
                        onClick = {
                            viewModel.navigateTo(Screen.HadithDetail(hadith.id, Screen.BookDetail(collectionName, bookNumber)))
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
