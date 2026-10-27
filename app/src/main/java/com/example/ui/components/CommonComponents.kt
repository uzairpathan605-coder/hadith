package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HadithEntity
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.DaifSlate
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.HasanAmber
import com.example.ui.theme.SahihGreen

@Composable
fun AuthenticityBadge(
    grade: String,
    gradedBy: String? = null,
    modifier: Modifier = Modifier
) {
    val (badgeBg, badgeText) = when {
        grade.contains("Sahih", ignoreCase = true) -> SahihGreen to Color.White
        grade.contains("Hasan", ignoreCase = true) -> HasanAmber to Color.White
        else -> DaifSlate to Color.White
    }

    Surface(
        color = badgeBg,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.testTag("authenticity_badge_${grade.lowercase()}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = badgeText,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (gradedBy.isNullOrBlank()) grade else "$grade ($gradedBy)",
                color = badgeText,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LanguageToggleRow(
    showArabic: Boolean,
    showEnglish: Boolean,
    showUrdu: Boolean,
    onToggleArabic: () -> Unit,
    onToggleEnglish: () -> Unit,
    onToggleUrdu: () -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.Center
    ) {
        FilterChip(
            selected = showArabic,
            onClick = onToggleArabic,
            label = { Text("عربي (Arabic)", fontSize = 12.sp) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            modifier = Modifier.testTag("toggle_arabic_chip")
        )
        FilterChip(
            selected = showEnglish,
            onClick = onToggleEnglish,
            label = { Text("English", fontSize = 12.sp) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            modifier = Modifier.testTag("toggle_english_chip")
        )
        FilterChip(
            selected = showUrdu,
            onClick = onToggleUrdu,
            label = { Text("اردو (Urdu)", fontSize = 12.sp) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            modifier = Modifier.testTag("toggle_urdu_chip")
        )
    }
}

@Composable
fun HadithCard(
    hadith: HadithEntity,
    onClick: () -> Unit,
    onToggleBookmark: () -> Unit,
    modifier: Modifier = Modifier,
    collectionTitle: String? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("hadith_card_${hadith.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header Row: Reference badge & Bookmark action
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${collectionTitle ?: hadith.collectionName.uppercase()} #${hadith.hadithNumber}",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    AuthenticityBadge(grade = hadith.grade)
                }

                IconButton(
                    onClick = onToggleBookmark,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("bookmark_button_${hadith.id}")
                ) {
                    Icon(
                        imageVector = if (hadith.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = if (hadith.isBookmarked) "Remove bookmark" else "Add bookmark",
                        tint = if (hadith.isBookmarked) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Chapter title if present
            if (hadith.chapterTitleEnglish.isNotBlank() || hadith.chapterTitleArabic.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = hadith.chapterTitleEnglish.ifBlank { hadith.chapterTitleArabic },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Arabic text snippet
            Text(
                text = hadith.arabicText,
                fontFamily = AmiriFontFamily,
                fontSize = 19.sp,
                lineHeight = 30.sp,
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.bodyLarge.copy(
                    textDirection = TextDirection.Rtl
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // English Translation preview
            if (hadith.englishText.isNotBlank()) {
                Text(
                    text = hadith.englishText,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Urdu Translation preview
            if (hadith.urduText.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = hadith.urduText,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        textDirection = TextDirection.Rtl
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer metadata
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (hadith.narrator.isNotBlank()) {
                    Text(
                        text = "Narrated: ${hadith.narrator}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
                Text(
                    text = hadith.inBookReference,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun IslamicHeaderBanner(
    title: String = "مكتبة الحديث",
    subtitle: String = "Hadith Library — Authentic Canonical Prophetic Traditions",
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Elegant gold crest icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(GoldAccent.copy(alpha = 0.2f))
                    .border(1.dp, GoldAccent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.AutoStories,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Arabic title
            Text(
                text = title,
                fontFamily = AmiriFontFamily,
                fontSize = 28.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // English subtitle
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun SearchInputField(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search hadith, Arabic, English, Urdu, #..."
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = placeholder,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search icon",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingIcon = {
            AnimatedVisibility(visible = query.isNotEmpty(), enter = fadeIn(), exit = fadeOut()) {
                IconButton(
                    onClick = {
                        onQueryChange("")
                        onSearch("")
                    },
                    modifier = Modifier.testTag("clear_search_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("search_input_field")
    )
}

@Composable
fun EmptyStateView(
    message: String,
    tip: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            modifier = Modifier.size(54.dp)
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = tip,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
