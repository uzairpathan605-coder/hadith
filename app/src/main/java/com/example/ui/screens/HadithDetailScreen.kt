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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import com.example.data.sync.SunnahCrossCheckResult
import com.example.data.sync.SunnahCrossCheckStatus
import com.example.data.sync.SunnahCrossChecker
import com.example.ui.theme.SahihGreen
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
import com.example.ui.components.LanguageAvailabilityPills
import com.example.ui.components.LanguageToggleRow
import com.example.ui.components.SourceVerificationBadge
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NotoNastaliqUrduFontFamily

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
                                Text("Source Record", fontSize = 12.sp)
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

            // Metadata card: Collection, Source Type, Authenticity Grade, Languages
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

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SourceVerificationBadge(
                                    sourceType = h.sourceType,
                                    isOfflineCached = h.isOfflineCached
                                )
                                LanguageAvailabilityPills(
                                    hasArabic = h.hasArabic,
                                    hasEnglish = h.hasEnglish,
                                    hasUrdu = h.hasUrdu
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

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

                                if (h.hasArabic) {
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
                                } else {
                                    Text(
                                        text = "Arabic text not supplied by source for this record.",
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

                                if (h.hasEnglish) {
                                    Text(
                                        text = h.englishText,
                                        fontSize = 15.sp,
                                        lineHeight = 23.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                } else {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Outlined.Info,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
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

                                if (h.hasUrdu) {
                                    Text(
                                        text = h.urduText,
                                        fontFamily = NotoNastaliqUrduFontFamily,
                                        fontSize = 17.sp,
                                        lineHeight = 32.sp,
                                        textAlign = TextAlign.End,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            textDirection = TextDirection.Rtl
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                } else {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        Text(
                                            text = "اردو ترجمہ اس ریکارڈ کے لیے دستیاب نہیں ہے۔",
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            textAlign = TextAlign.End
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Outlined.Info,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
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
                                text = "Grade: ${h.grade} • Graded by: ${h.gradedBy}. Status: ${h.sourceType}. Preserved from authentic reference records. Source link: ${h.sourceUrl}",
                                fontSize = 11.sp,
                                lineHeight = 16.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }

            // 4. Sunnah.com Cross-Check & Verification
            item {
                hadith?.let { h ->
                    SunnahCrossCheckCard(
                        hadith = h,
                        viewModel = viewModel,
                        context = context
                    )
                }
            }
        }
    }
}

@Composable
private fun SunnahCrossCheckCard(
    hadith: HadithEntity,
    viewModel: HadithViewModel,
    context: Context,
    modifier: Modifier = Modifier
) {
    val apiKey by viewModel.apiKey.collectAsStateWithLifecycle()
    var isChecking by remember { mutableStateOf(false) }
    var checkResult by remember { mutableStateOf<SunnahCrossCheckResult?>(null) }
    val sunnahUrl = remember(hadith) {
        SunnahCrossChecker.generateSunnahUrl(hadith.collectionName, hadith.hadithNumber)
    }

    LaunchedEffect(hadith.id, apiKey) {
        if (apiKey.isBlank()) {
            checkResult = SunnahCrossCheckResult(
                status = SunnahCrossCheckStatus.UNAVAILABLE_OR_UNCONFIGURED,
                title = "State D: API Access Not Configured",
                message = "Sunnah.com cross-check unavailable — API access not configured.",
                sunnahUrl = sunnahUrl,
                isApiConfigured = false
            )
        } else {
            isChecking = true
            checkResult = viewModel.crossCheckHadith(hadith)
            isChecking = false
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("sunnah_cross_check_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "س",
                            fontFamily = AmiriFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Sunnah.com Cross-Check",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Independent scholarly reference cross-check",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (apiKey.isNotBlank()) {
                    IconButton(
                        onClick = {
                            viewModel.viewModelScopeLaunch {
                                isChecking = true
                                checkResult = viewModel.crossCheckHadith(hadith)
                                isChecking = false
                            }
                        },
                        modifier = Modifier.testTag("refresh_cross_check_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Re-check",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isChecking) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Querying official Sunnah.com API...",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                checkResult?.let { result ->
                    val (badgeBg, badgeText, iconVector, iconTint) = when (result.status) {
                        SunnahCrossCheckStatus.MATCHING_RECORD_FOUND -> Quadruple(
                            Color(0xFFE6F4EA),
                            Color(0xFF137333),
                            Icons.Default.CheckCircle,
                            SahihGreen
                        )
                        SunnahCrossCheckStatus.REQUIRES_REVIEW -> Quadruple(
                            Color(0xFFFEF7E0),
                            Color(0xFFB06000),
                            Icons.Default.Warning,
                            Color(0xFFB06000)
                        )
                        SunnahCrossCheckStatus.SOURCE_RECORD_FOUND -> Quadruple(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.onPrimaryContainer,
                            Icons.Outlined.Info,
                            MaterialTheme.colorScheme.primary
                        )
                        SunnahCrossCheckStatus.UNAVAILABLE_OR_UNCONFIGURED -> Quadruple(
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.onSurfaceVariant,
                            Icons.Outlined.Info,
                            MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        color = badgeBg,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = iconVector,
                                contentDescription = null,
                                tint = iconTint,
                                modifier = Modifier.size(20.dp).padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = result.title,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = badgeText
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = result.message,
                                    fontSize = 12.sp,
                                    color = badgeText,
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(sunnahUrl))
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("open_sunnah_com_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Sunnah.com Link", fontSize = 12.sp)
                }

                if (apiKey.isBlank()) {
                    OutlinedButton(
                        onClick = { viewModel.navigateTo(Screen.Settings) },
                        modifier = Modifier.testTag("configure_sunnah_api_key_button")
                    ) {
                        Text("Configure API", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

private fun copyHadithText(context: Context, hadith: HadithEntity) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val formatted = buildString {
        appendLine("${hadith.collectionName.uppercase()} #${hadith.hadithNumber} (${hadith.inBookReference})")
        appendLine("Grade: ${hadith.grade} (${hadith.gradedBy})")
        if (hadith.narrator.isNotBlank()) appendLine("Narrator: ${hadith.narrator}")
        appendLine()
        if (hadith.hasArabic) {
            appendLine("--- Arabic Matn ---")
            appendLine(hadith.arabicText)
            appendLine()
        }
        if (hadith.hasEnglish) {
            appendLine("--- English Translation ---")
            appendLine(hadith.englishText)
            appendLine()
        }
        if (hadith.hasUrdu) {
            appendLine("--- Urdu Translation ---")
            appendLine(hadith.urduText)
            appendLine()
        }
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
        if (hadith.hasArabic) {
            appendLine(hadith.arabicText)
            appendLine()
        }
        if (hadith.hasEnglish) {
            appendLine(hadith.englishText)
            appendLine()
        }
        if (hadith.hasUrdu) {
            appendLine(hadith.urduText)
            appendLine()
        }
        appendLine("Read verified record: ${hadith.sourceUrl}")
    }
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Hadith ${hadith.collectionName.uppercase()} #${hadith.hadithNumber}")
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    context.startActivity(Intent.createChooser(intent, "Share Hadith"))
}
