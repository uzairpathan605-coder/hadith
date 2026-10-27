package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.HadithViewModel
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.GoldAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: HadithViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val apiKey by viewModel.apiKey.collectAsStateWithLifecycle()
    val apiStatusMessage by viewModel.apiStatusMessage.collectAsStateWithLifecycle()
    val isTestingApi by viewModel.isTestingApi.collectAsStateWithLifecycle()

    val arabicFontSize by viewModel.arabicFontSize.collectAsStateWithLifecycle()
    val showEnglish by viewModel.showEnglish.collectAsStateWithLifecycle()
    val showUrdu by viewModel.showUrdu.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Settings & Data Source",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "الإعدادات ومصادر البيانات",
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
        modifier = modifier.testTag("settings_screen")
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. External API Integration (Sunnah.com)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Sunnah.com API Configuration",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Hadith Library works 100% offline with bundled verified canonical narrations. To access live online queries or extended collections, configure your official Sunnah.com API key below.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = apiKey,
                            onValueChange = { viewModel.updateApiKey(it) },
                            label = { Text("API Key (x-api-key)") },
                            placeholder = { Text("Enter your Sunnah.com API key") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("api_key_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { viewModel.testApiConnection() },
                                enabled = !isTestingApi,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.testTag("test_api_connection_button")
                            ) {
                                if (isTestingApi) {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Testing...", fontSize = 12.sp)
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Test Connection", fontSize = 12.sp)
                                }
                            }

                            OutlinedButton(
                                onClick = {
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://sunnah.stoplight.io/docs/api/")
                                    )
                                    context.startActivity(intent)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.OpenInNew,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Get API Key", fontSize = 11.sp)
                            }
                        }

                        apiStatusMessage?.let { msg ->
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                color = if (msg.startsWith("Success"))
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.errorContainer,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = msg,
                                    fontSize = 12.sp,
                                    color = if (msg.startsWith("Success"))
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    else
                                        MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 2. Reading Preferences
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Reading Preferences",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // English Translation toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("English Translation", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("Display English translation by default", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = showEnglish,
                                onCheckedChange = { viewModel.toggleEnglishVisibility() },
                                modifier = Modifier.testTag("setting_toggle_english")
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(8.dp))

                        // Urdu Translation toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Urdu Translation (اردو ترجمہ)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("Display Urdu translation by default", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = showUrdu,
                                onCheckedChange = { viewModel.toggleUrduVisibility() },
                                modifier = Modifier.testTag("setting_toggle_urdu")
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Arabic font size
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Arabic Matn Font Size", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text("${arabicFontSize.toInt()} sp", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Slider(
                            value = arabicFontSize,
                            onValueChange = { viewModel.setArabicFontSize(it) },
                            valueRange = 18f..38f,
                            steps = 10,
                            modifier = Modifier.testTag("setting_font_slider")
                        )
                    }
                }
            }

            // 3. Cache & Storage Management
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CleaningServices,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Local Storage & Cache",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Hadith Library stores seed collections and downloaded records locally in Room database for offline reading. Your saved bookmarks will never be deleted when clearing cache.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = { viewModel.clearCache() },
                            modifier = Modifier.testTag("clear_cache_button")
                        ) {
                            Text("Clear Unsaved Cache")
                        }
                    }
                }
            }

            // 4. Scholarly Integrity & Licensing Notice
            item {
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "About Hadith Library",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Hadith Library is built with high devotion to preserving original Arabic matn text, canonical numbering, and verified authenticity gradings. All records are attributed to authorized sources, and no AI-synthesized hadith text is ever generated.",
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Version 1.0.0 • Purely Native Android & Jetpack Compose",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
