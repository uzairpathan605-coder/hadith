package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.HadithDatabase
import com.example.data.model.BookEntity
import com.example.data.model.CollectionEntity
import com.example.data.model.HadithEntity
import com.example.data.repository.HadithRepository
import com.example.data.repository.HadithRepositoryImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
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
    data class HadithDetail(val hadithId: String, val returnScreen: Screen = Home) : Screen()
}

data class SearchFilterState(
    val query: String = "",
    val collection: String? = null,
    val grade: String? = null,
    val language: String? = null
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
        // If bottom nav destination, reset stack root
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

    // Search & Filter State
    private val _filterState = MutableStateFlow(SearchFilterState())
    val filterState: StateFlow<SearchFilterState> = _filterState.asStateFlow()

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
    }

    fun submitSearch(query: String) {
        updateSearchQuery(query)
        viewModelScope.launch {
            repository.recordSearchQuery(query)
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

    fun clearCache() {
        viewModelScope.launch {
            repository.clearCache()
            _apiStatusMessage.value = "Cache cleared (bookmarks preserved)."
        }
    }
}
