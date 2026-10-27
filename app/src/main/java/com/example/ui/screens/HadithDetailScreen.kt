package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.HadithEntity
import com.example.ui.HadithViewModel
import com.example.ui.Screen
import com.example.ui.components.AuthenticityBadge
import com.example.ui.components.LanguageToggleRow
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.GoldAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HadithDetailScreen(
    hadithId: String,
    returnScreen: Screen,
    viewModel: HadithViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val hadith by viewModel.getHadithById(hadithId).collectAsStateWithLifecycle(initialValue = null)

    val arabicFontSize by viewModel.arabicFontSize.collectAsStateWithLifecycle()
    val showArabic by viewModel.showArabic.collectAsStateWithLifecycle()
    val showEnglish by viewModel.showEnglish.collectAsStateWithLifecycle()
    val showUrdu by viewModel.showUrdu.collectAsStateWithLifecycle()

    var showFontSlider by remember { mutableStateOf(false) }

    LaunchedEffect(hadithId) {
        viewModel.markHadithViewed(hadithId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = hadith?.let { "${it.collectionName.uppercase()} #${it.hadithNumber}" } ?: "Hadith Detail",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            text = hadith?.inBookReference ?: "",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("hadith_detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showFontSlider = !showFontSlider },
                        modifier = Modifier.testTag("toggle_font_size_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatSize,
                            contentDescription = "Adjust text size",
                            tint = if (showFontSlider) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    hadith?.let { h ->
                        IconButton(
                            onClick = { viewModel.toggleBookmark(h) },
                            modifier = Modifier.testTag("detail_bookmark_button")
                        ) {
                            Icon(
                                imageVector = if (h.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = if (h.isBookmarked) GoldAccent else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(
                            onClick = { shareHadith(context, h) },
                            modifier = Modifier.testTag("detail_share_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            hadith?.let { h ->
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { copyHadithText(context, h) },
                            modifier = Modifier.testTag("copy_hadith_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy Hadith", fontSize = 12.sp)
                        }

                        if (h.sourceUrl.isNotBlank()) {
                            OutlinedButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(h.sourceUrl))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.testTag("view_source_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.OpenInNew,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Sunnah.com", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        },
        modifier = modifier.testTag("hadith_detail_screen")
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Font size slider drawer
            item {
                AnimatedVisibility(
                    visible = showFontSlider,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Arabic Font Size",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${arabicFontSize.toInt()} sp",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Slider(
                                value = arabicFontSize,
                                onValueChange = { viewModel.setArabicFontSize(it) },
                                valueRange = 18f..38f,
                                steps = 10,
                                modifier = Modifier.testTag("font_size_slider")
                            )
                        }
                    }
                }
            }

            // Language visibility toggles
            item {
                LanguageToggleRow(
                    showArabic = showArabic,
                    showEnglish = showEnglish,
                    showUrdu = showUrdu,
                    onToggleArabic = { viewModel.toggleArabicVisibility() },
                    onToggleEnglish = { viewModel.toggleEnglishVisibility() },
                    onToggleUrdu = { viewModel.toggleUrduVisibility() }
                )
            }

            // Metadata card: Collection, Book, In-book ref, Authenticity Grade & Scholar
            item {
                hadith?.let { h ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = h.collectionName.replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                AuthenticityBadge(grade = h.grade, gradedBy = h.gradedBy)
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            if (h.chapterTitleEnglish.isNotBlank()) {
                                Text(
                                    text = "Chapter: ${h.chapterTitleEnglish}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            if (h.chapterTitleArabic.isNotBlank()) {
                                Text(
                                    text = h.chapterTitleArabic,
                                    fontFamily = AmiriFontFamily,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.End
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "In-book: ${h.inBookReference}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Hadith #${h.hadithNumber}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            if (h.narrator.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Narrated by: ${h.narrator}",
                                    fontSize = 12.sp,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Arabic Matn Card (exact original text)
            if (showArabic) {
                item {
                    hadith?.let { h ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "المتن الأصلي (Arabic Matn)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(GoldAccent.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "ع",
                                            fontFamily = AmiriFontFamily,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldAccent
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = h.arabicText,
                                    fontFamily = AmiriFontFamily,
                                    fontSize = arabicFontSize.sp,
                                    lineHeight = (arabicFontSize * 1.7f).sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.End,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        textDirection = TextDirection.Rtl
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }

            // English Translation Card
            if (showEnglish) {
                item {
                    hadith?.let { h ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "English Translation",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "EN",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                if (h.englishText.isNotBlank()) {
                                    Text(
                                        text = h.englishText,
                                        fontSize = 15.sp,
                                        lineHeight = 23.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                } else {
                                    Text(
                                        text = "English translation not provided by source for this record.",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Urdu Translation Card
            if (showUrdu) {
                item {
                    hadith?.let { h ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "اردو ترجمہ (Urdu Translation)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "اردو",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldAccent
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                if (h.urduText.isNotBlank()) {
                                    Text(
                                        text = h.urduText,
                                        fontSize = 16.sp,
                                        lineHeight = 26.sp,
                                        textAlign = TextAlign.End,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            textDirection = TextDirection.Rtl
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                } else {
                                    Text(
                                        text = "اردو ترجمہ اس ریکارڈ کے لیے دستیاب نہیں ہے۔",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Scholarly Disclaimer / Verification Notice
            item {
                hadith?.let { h ->
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Authenticity Verification",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Grade: ${h.grade} • Graded by: ${h.gradedBy}. This narration is preserved from authentic classical manuscripts and verified reference records. Original reference URL: ${h.sourceUrl}",
                                fontSize = 11.sp,
                                lineHeight = 16.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun copyHadithText(context: Context, hadith: HadithEntity) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val formatted = buildString {
        appendLine("${hadith.collectionName.uppercase()} #${hadith.hadithNumber} (${hadith.inBookReference})")
        appendLine("Grade: ${hadith.grade} (${hadith.gradedBy})")
        if (hadith.narrator.isNotBlank()) appendLine("Narrator: ${hadith.narrator}")
        appendLine()
        appendLine("--- Arabic Matn ---")
        appendLine(hadith.arabicText)
        if (hadith.englishText.isNotBlank()) {
            appendLine()
            appendLine("--- English Translation ---")
            appendLine(hadith.englishText)
        }
        if (hadith.urduText.isNotBlank()) {
            appendLine()
            appendLine("--- Urdu Translation ---")
            appendLine(hadith.urduText)
        }
        appendLine()
        appendLine("Source: ${hadith.sourceUrl}")
    }
    val clip = ClipData.newPlainText("Hadith ${hadith.collectionName} ${hadith.hadithNumber}", formatted)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "Hadith copied to clipboard", Toast.LENGTH_SHORT).show()
}

private fun shareHadith(context: Context, hadith: HadithEntity) {
    val shareText = buildString {
        appendLine("Hadith: ${hadith.collectionName.uppercase()} #${hadith.hadithNumber}")
        appendLine("Grade: ${hadith.grade} - ${hadith.gradedBy}")
        appendLine()
        appendLine(hadith.arabicText)
        if (hadith.englishText.isNotBlank()) {
            appendLine()
            appendLine(hadith.englishText)
        }
        if (hadith.urduText.isNotBlank()) {
            appendLine()
            appendLine(hadith.urduText)
        }
        appendLine()
        appendLine("Read verified hadith: ${hadith.sourceUrl}")
    }
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Hadith ${hadith.collectionName.uppercase()} #${hadith.hadithNumber}")
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    context.startActivity(Intent.createChooser(intent, "Share Hadith"))
}
