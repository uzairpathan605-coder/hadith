package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.HadithDatabase
import com.example.data.model.BookEntity
import com.example.data.model.CollectionEntity
import com.example.data.model.HadithEntity
import com.example.data.repository.DatabaseStats
import com.example.data.repository.HadithRepository
import com.example.data.repository.HadithRepositoryImpl
import com.example.util.ParsedReference
import com.example.util.ReferenceParser
import com.example.data.sync.DatabaseIntegrityReport
import com.example.data.sync.PackDownloadState
import com.example.data.sync.SunnahCrossCheckResult
import com.example.data.sync.SunnahCrossChecker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen {
    data object Home : Screen()
    data object Collections : Screen()
    data class CollectionDetail(val collectionName: String) : Screen()
    data class BookDetail(val collectionName: String, val bookNumber: String) : Screen()
    data object Search : Screen()
    data object Bookmarks : Screen()
    data object Settings : Screen()
    data object AdminDashboard : Screen()
    data class HadithDetail(val hadithId: String, val returnScreen: Screen = Home) : Screen()
}

data class SearchFilterState(
    val query: String = "",
    val collection: String? = null,
    val grade: String? = null,
    val language: String? = null
)

data class SearchSuggestionItem(
    val hadith: HadithEntity,
    val collectionBadge: String,
    val titleSnippet: String,
    val matchedSnippet: String,
    val isArabicSnippet: Boolean = false,
    val isUrduSnippet: Boolean = false,
    val matchReason: String = "Match"
)

private data class SuggestionPreviewTuple(
    val snippet: String,
    val isArabic: Boolean,
    val isUrdu: Boolean,
    val reason: String
)

class HadithViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HadithRepository

    init {
        val database = HadithDatabase.getInstance(application)
        repository = HadithRepositoryImpl(
            hadithDao = database.hadithDao(),
            searchHistoryDao = database.searchHistoryDao(),
            context = application
        )

        viewModelScope.launch {
            repository.ensureSeeded()
        }
    }

    // Navigation BackStack
    private val _navigationStack = MutableStateFlow<List<Screen>>(listOf(Screen.Home))
    val currentScreen: StateFlow<Screen> = _navigationStack.combine(MutableStateFlow(Unit)) { stack, _ ->
        stack.lastOrNull() ?: Screen.Home
    }.stateIn(viewModelScope, SharingStarted.Eagerly, Screen.Home)

    fun navigateTo(screen: Screen) {
        val currentList = _navigationStack.value.toMutableList()
        when (screen) {
            Screen.Home, Screen.Collections, Screen.Search, Screen.Bookmarks, Screen.Settings -> {
                _navigationStack.value = listOf(screen)
            }
            else -> {
                currentList.add(screen)
                _navigationStack.value = currentList
            }
        }
    }

    fun navigateBack(): Boolean {
        val currentList = _navigationStack.value.toMutableList()
        return if (currentList.size > 1) {
            currentList.removeAt(currentList.lastIndex)
            _navigationStack.value = currentList
            true
        } else {
            false
        }
    }

    // Data Streams
    val collections: StateFlow<List<CollectionEntity>> = repository.getAllCollections()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val featuredHadith: StateFlow<HadithEntity?> = repository.getRandomFeaturedHadith()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val bookmarkedHadiths: StateFlow<List<HadithEntity>> = repository.getBookmarkedHadiths()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentHistory: StateFlow<List<HadithEntity>> = repository.getRecentHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentSearchQueries: StateFlow<List<String>> = repository.getRecentSearchQueries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val databaseStats: StateFlow<DatabaseStats> = repository.getDatabaseStats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DatabaseStats())

    // Search & Filter State
    private val _filterState = MutableStateFlow(SearchFilterState())
    val filterState: StateFlow<SearchFilterState> = _filterState.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _detectedReference = MutableStateFlow<ParsedReference?>(null)
    val detectedReference: StateFlow<ParsedReference?> = _detectedReference.asStateFlow()

    private val _searchErrorMessage = MutableStateFlow<String?>(null)
    val searchErrorMessage: StateFlow<String?> = _searchErrorMessage.asStateFlow()

    // Live Query for instant debounced typing suggestions (TikTok style)
    private val _liveQuery = MutableStateFlow("")
    val liveQuery: StateFlow<String> = _liveQuery.asStateFlow()

    @OptIn(kotlinx.coroutines.FlowPreview::class, ExperimentalCoroutinesApi::class)
    val searchSuggestions: StateFlow<List<SearchSuggestionItem>> = _liveQuery
        .debounce(250)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                kotlinx.coroutines.flow.flowOf(emptyList())
            } else {
                repository.searchHadiths(query).map { list ->
                    buildSuggestionItems(list, query)
                }
            }
        }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResults: StateFlow<List<HadithEntity>> = _filterState.flatMapLatest { filter ->
        repository.searchHadiths(
            query = filter.query,
            collectionFilter = filter.collection,
            gradeFilter = filter.grade,
            languageFilter = filter.language
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        _filterState.value = _filterState.value.copy(query = query)
        _liveQuery.value = query
        _detectedReference.value = ReferenceParser.parse(query)
        _searchErrorMessage.value = null
    }

    private fun buildSuggestionItems(list: List<HadithEntity>, rawQuery: String): List<SearchSuggestionItem> {
        val query = rawQuery.trim()
        val parsedRef = ReferenceParser.parse(query)

        return list.distinctBy { it.id }.take(8).map { hadith ->
            val colBadge = "${ReferenceParser.getCanonicalName(hadith.collectionName)} #${hadith.hadithNumber}"
            val chapter = hadith.chapterTitleEnglish.ifBlank { hadith.chapterTitleArabic.ifBlank { "Book ${hadith.bookNumber}" } }

            val previewTuple = when {
                parsedRef != null && (parsedRef.collection == null || hadith.collectionName.equals(parsedRef.collection, ignoreCase = true)) && hadith.hadithNumber == parsedRef.hadithNumber -> {
                    val preview = hadith.englishText.ifBlank { hadith.arabicText }.take(100)
                    SuggestionPreviewTuple(preview, hadith.englishText.isBlank(), false, "Exact Reference")
                }
                com.example.util.ArabicNormalizer.containsNormalized(hadith.arabicText, query) -> {
                    val preview = hadith.arabicText.take(110)
                    SuggestionPreviewTuple(preview, true, false, "Arabic Matn")
                }
                hadith.urduText.contains(query, ignoreCase = true) -> {
                    val preview = hadith.urduText.take(110)
                    SuggestionPreviewTuple(preview, false, true, "Urdu Translation")
                }
                hadith.englishText.contains(query, ignoreCase = true) -> {
                    val preview = hadith.englishText.take(100)
                    SuggestionPreviewTuple(preview, false, false, "English Translation")
                }
                hadith.hadithNumber.startsWith(query) -> {
                    val preview = hadith.englishText.ifBlank { hadith.arabicText }.take(100)
                    SuggestionPreviewTuple(preview, hadith.englishText.isBlank(), false, "Hadith #${hadith.hadithNumber}")
                }
                else -> {
                    val preview = hadith.englishText.ifBlank { hadith.arabicText }.take(100)
                    SuggestionPreviewTuple(preview, hadith.englishText.isBlank(), false, "Matching Record")
                }
            }

            SearchSuggestionItem(
                hadith = hadith,
                collectionBadge = colBadge,
                titleSnippet = chapter,
                matchedSnippet = previewTuple.snippet,
                isArabicSnippet = previewTuple.isArabic,
                isUrduSnippet = previewTuple.isUrdu,
                matchReason = previewTuple.reason
            )
        }
    }

    fun onSuggestionSelected(suggestion: SearchSuggestionItem) {
        viewModelScope.launch {
            repository.recordSearchQuery(_filterState.value.query.ifBlank { suggestion.collectionBadge })
        }
        navigateTo(Screen.HadithDetail(suggestion.hadith.id, Screen.Search))
    }

    fun submitSearch(query: String) {
        updateSearchQuery(query)
        viewModelScope.launch {
            repository.recordSearchQuery(query)
        }

        val parsed = ReferenceParser.parse(query)
        if (parsed != null && parsed.collection != null) {
            viewModelScope.launch {
                _isSearching.value = true
                try {
                    val result = repository.fetchDirectReferenceIfMissing(parsed)
                    if (result == null && searchResults.value.none { it.hadithNumber == parsed.hadithNumber && it.collectionName == parsed.collection }) {
                        _searchErrorMessage.value = "Hadith #${parsed.hadithNumber} not found in ${parsed.canonicalName}."
                    }
                } catch (e: Exception) {
                    _searchErrorMessage.value = "Network lookup failed: ${e.message}"
                } finally {
                    _isSearching.value = false
                }
            }
        }
    }

    fun setCollectionFilter(collection: String?) {
        _filterState.value = _filterState.value.copy(collection = collection)
    }

    fun setGradeFilter(grade: String?) {
        _filterState.value = _filterState.value.copy(grade = grade)
    }

    fun setLanguageFilter(lang: String?) {
        _filterState.value = _filterState.value.copy(language = lang)
    }

    fun clearSearchFilters() {
        _filterState.value = SearchFilterState()
        _detectedReference.value = null
        _searchErrorMessage.value = null
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            repository.clearSearchHistory()
        }
    }

    // Bookmarking & History
    fun toggleBookmark(hadith: HadithEntity) {
        viewModelScope.launch {
            repository.setBookmark(hadith.id, !hadith.isBookmarked)
        }
    }

    fun markHadithViewed(hadithId: String) {
        viewModelScope.launch {
            repository.markAsViewed(hadithId)
        }
    }

    // Detail Screen Data Fetchers
    fun getCollection(name: String) = repository.getCollection(name)
    fun getBooksForCollection(name: String) = repository.getBooksForCollection(name)
    fun getHadithsForBook(name: String, bookNumber: String) = repository.getHadithsForBook(name, bookNumber)
    fun getHadithsForCollection(name: String) = repository.getHadithsForCollection(name)
    fun getHadithById(id: String) = repository.getHadithById(id)

    // Reader Preferences
    private val _arabicFontSize = MutableStateFlow(repository.getArabicFontSize())
    val arabicFontSize: StateFlow<Float> = _arabicFontSize.asStateFlow()

    fun setArabicFontSize(size: Float) {
        _arabicFontSize.value = size
        repository.saveArabicFontSize(size)
    }

    private val _showArabic = MutableStateFlow(true)
    val showArabic: StateFlow<Boolean> = _showArabic.asStateFlow()
    fun toggleArabicVisibility() { _showArabic.value = !_showArabic.value }

    private val _showEnglish = MutableStateFlow(repository.isEnglishEnabled())
    val showEnglish: StateFlow<Boolean> = _showEnglish.asStateFlow()
    fun toggleEnglishVisibility() {
        val newVal = !_showEnglish.value
        _showEnglish.value = newVal
        repository.saveEnglishEnabled(newVal)
    }

    private val _showUrdu = MutableStateFlow(repository.isUrduEnabled())
    val showUrdu: StateFlow<Boolean> = _showUrdu.asStateFlow()
    fun toggleUrduVisibility() {
        val newVal = !_showUrdu.value
        _showUrdu.value = newVal
        repository.saveUrduEnabled(newVal)
    }

    // Settings & API Configuration
    private val _apiKey = MutableStateFlow(repository.getApiKey())
    val apiKey: StateFlow<String> = _apiKey.asStateFlow()

    private val _apiStatusMessage = MutableStateFlow<String?>(null)
    val apiStatusMessage: StateFlow<String?> = _apiStatusMessage.asStateFlow()

    private val _isTestingApi = MutableStateFlow(false)
    val isTestingApi: StateFlow<Boolean> = _isTestingApi.asStateFlow()

    fun updateApiKey(key: String) {
        _apiKey.value = key
        repository.saveApiKey(key)
    }

    fun testApiConnection() {
        val key = _apiKey.value.trim()
        viewModelScope.launch {
            _isTestingApi.value = true
            _apiStatusMessage.value = null
            val result = repository.testApiConnection(key)
            _isTestingApi.value = false
            if (result.isSuccess) {
                _apiStatusMessage.value = "Success: ${result.getOrNull()}"
            } else {
                _apiStatusMessage.value = "Error: ${result.exceptionOrNull()?.message ?: "Unknown error"}"
            }
        }
    }

    suspend fun crossCheckHadith(hadith: HadithEntity): SunnahCrossCheckResult {
        return SunnahCrossChecker.checkHadith(hadith, _apiKey.value.trim().takeIf { it.isNotBlank() })
    }

    fun clearCache() {
        viewModelScope.launch {
            repository.clearCache()
            _apiStatusMessage.value = "Cache cleared (bookmarks preserved)."
        }
    }

    // Offline Pack Downloader & Database Integrity
    val packDownloadState: StateFlow<PackDownloadState> = repository.packDownloadState

    fun getHadithCountForCollection(collectionName: String): Flow<Int> {
        return repository.getHadithCountForCollection(collectionName)
    }

    fun downloadCollectionPack(collectionName: String) {
        viewModelScope.launch {
            repository.downloadCollectionPack(collectionName)
        }
    }

    fun downloadBook(collectionName: String, bookNumber: String) {
        viewModelScope.launch {
            repository.downloadBook(collectionName, bookNumber)
        }
    }

    fun downloadAllCollections() {
        viewModelScope.launch {
            repository.downloadAllCollections()
        }
    }

    fun retryLastDownload() {
        viewModelScope.launch {
            repository.retryLastDownload()
        }
    }

    fun resetPackDownloadState() {
        repository.resetPackDownloadState()
    }

    suspend fun checkDatabaseIntegrity(): DatabaseIntegrityReport {
        return repository.checkDatabaseIntegrity()
    }

    // Admin Security & Authentication
    private val _isAdminAuthenticated = MutableStateFlow(false)
    val isAdminAuthenticated: StateFlow<Boolean> = _isAdminAuthenticated.asStateFlow()

    fun isAdminPasscodeConfigured(): Boolean = repository.isAdminPasscodeConfigured()

    fun setupAdminPasscode(passcode: String): Boolean {
        if (passcode.trim().length < 4) return false
        repository.setAdminPasscode(passcode.trim())
        _isAdminAuthenticated.value = true
        return true
    }

    fun verifyAdminPasscode(passcode: String): Boolean {
        val valid = repository.verifyAdminPasscode(passcode.trim())
        if (valid) {
            _isAdminAuthenticated.value = true
        }
        return valid
    }

    fun signOutAdmin() {
        _isAdminAuthenticated.value = false
        navigateTo(Screen.Settings)
    }

    // Onboarding State
    private val _hasSeenOnboarding = MutableStateFlow(repository.hasSeenOnboarding())
    val hasSeenOnboarding: StateFlow<Boolean> = _hasSeenOnboarding.asStateFlow()

    fun dismissOnboarding() {
        repository.setSeenOnboarding(true)
        _hasSeenOnboarding.value = true
    }

    fun viewModelScopeLaunch(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch { block() }
    }
}
