# Hadith Library (مكتبة الحديث) — Implementation Plan

An authentic, dignified, native Android Hadith reading and research application built with Kotlin, Jetpack Compose, Material 3, and Room. The app delivers a high-fidelity reading experience honoring the sacred Islamic tradition, with rich multi-lingual support (original Arabic matn, English, and Urdu translations), verified authenticity gradings, fast full-text search, and offline reading with optional Sunnah.com API integration.

---

### User Review & Critical Decisions

> [!IMPORTANT]
> The following architectural decisions were confirmed based on your preferences:
> - **Data Access & Offline Readiness**: Bundled authentic canonical hadith records seeded into Room local database on first run, paired with an API configuration screen in Settings where users can supply their official Sunnah.com API key (`x-api-key`) for dynamic remote browsing and cache refreshing.
> - **Hadith Detail Layout**: Stacked bilingual/trilingual layout with individual language visibility toggles (Arabic Matn, English Translation, Urdu Translation) and adjustable Arabic font sizing.
> - **Default Translation Visibility**: Both English and Urdu translations will be enabled by default alongside the original Arabic text.
> - **Authenticity & Integrity Guardrail**: Every narration strictly displays verified authenticity gradings (Sahih, Hasan, Da'if) with attribution to scholars (e.g., Al-Albani, Zubair Ali Zai, Darussalam) and exact canonical source references; no synthetic or unverified narrations are allowed.

---

### 1. Overview & Core Concept

- **What It Does**: Hadith Library allows users to search, browse, study, bookmark, and share verified prophetic traditions across the canonical collections (Sahih al-Bukhari, Sahih Muslim, Sunan Abi Dawud, Jami' at-Tirmidhi, Sunan an-Nasa'i, Sunan Ibn Majah, Muwatta Malik, Musnad Ahmad).
- **Target Audience**: Students of Islamic knowledge, researchers, and general readers seeking reliable prophetic traditions with accurate Arabic text, scholarly gradings, and authentic translations in English and Urdu.
- **Key Value**: A fast, respectful, distraction-free environment with zero ads, true offline database persistence, precise reference lookup (e.g., Bukhari 1, Muslim 1907), diacritics-aware Arabic search, and genuine API interoperability.

---

### 2. User Experience & Visual Design

#### Key User Flows
1. **Home Screen**:
   - Header with Arabic calligraphy branding (*مكتبة الحديث*), quick search bar, and active date in Islamic & Gregorian format.
   - Verified "Hadith of the Day" card with quick bookmark and share actions.
   - Canonical Collections Carousel with book covers, record count badges, and authentic descriptions.
   - Quick access to "Recently Viewed" and "Saved Bookmarks".
2. **Collections Browser**:
   - Filterable list of the 8 canonical collections.
   - Tapping a collection reveals its books/chapters, total hadith counts, and introduction.
   - Selecting a book displays paginated hadiths with fast scrolling.
3. **Advanced Search Screen**:
   - Multi-field search box supporting Arabic diacritic normalization, English keywords, Urdu terms, narrator names, and book/hadith numbers (e.g., "Bukhari 13", "intentions", "نیات").
   - Filter pills for Collection, Authenticity Grade (Sahih, Hasan, etc.), and Language.
   - Search history chips and instant results list with query term highlighting.
4. **Hadith Reading & Detail View**:
   - Clean reading canvas with adjustable font size slider (Small, Medium, Large, Extra Large).
   - Visibility chips to toggle Arabic, English, or Urdu sections on the fly.
   - Dedicated metadata banner displaying Collection, Book, In-Book Reference, Hadith Number, and Scholar Authenticity Grading badge.
   - Interactive bottom action bar: Copy Arabic/Translation, Share styled text, Toggle Bookmark, Previous/Next hadith buttons.
5. **Bookmarks & History Screen**:
   - Tabbed view: "Saved Hadiths" (bookmarked for reflection) and "Recent History".
   - Search and swipe-to-remove actions.
6. **Settings & Data Source Screen**:
   - API configuration for Sunnah.com: input field for API Key (`x-api-key`), API status test button, cache status metrics, and "Clear Local Cache" button.
   - Font scale and script display options.

#### Visual Identity & Theme
- **Aesthetic Direction**: Sacred Classical & Editorial Minimalist. Serene, scholarly, respectful, avoiding commercial clutter.
- **Color Palette**:
  - *Primary*: Deep Emerald Green (`#14453D` light / `#68C1B0` dark) representing tradition and tranquility.
  - *Secondary / Accent*: Warm Antique Gold (`#C89B3C` light / `#E5BA5A` dark) for badges, chapter borders, and active highlights.
  - *Background*: Soft Warm Ivory (`#FAF7F2` light / `#121716` dark) to reduce reading eye fatigue.
  - *Surface*: Cream Card (`#F3EFE6` light / `#1C2422` dark) with subtle borders.
  - *Text Colors*: Deep Forest Ink (`#0F241F` light / `#E6EDE8` dark) for high contrast and legibility.
- **Typography & Hierarchy**:
  - Arabic Matn: Naskh typography (Amiri / Noto Naskh Arabic) with generous line height (`36.sp`–`42.sp`) for easy reading of tashkeel (vowels/diacritics).
  - English & Urdu: High-legibility serif/sans pairing with clear narrator distinction.
- **Component Styling & Layout**:
  - Edge-to-edge layout with safe-area insets.
  - Elegant decorative arch/bismillah divider motifs in vector format.
  - Clear pill-shaped authenticity badges with color coding: Green for Sahih, Amber for Hasan, Slate for Da'if.
  - Minimum touch targets exceeding 48dp on all navigation and reader buttons.

---

### 3. Key Product Decisions & Trade-Offs

- **Decision 1: Seeded Offline Database + Room Caching**
  - *Approach*: Ship a verified, rich seed database of foundational hadiths across all canonical books (including famous hadiths like "Actions are by intentions", pillars of Islam, prayer, charity, character) with authentic Arabic matn, English and Urdu translations, narrators, and gradings. Room handles local caching, bookmarks, and search.
  - *Why*: Guarantees the app is 100% functional, responsive, and useful immediately upon first install without requiring an API key or internet access, while still allowing API sync when credentials are provided.
- **Decision 2: Repository Abstraction for Multi-Source Architecture**
  - *Approach*: `HadithRepository` interface backed by `LocalHadithDataSource` (Room) and `RemoteHadithDataSource` (Retrofit with Sunnah.com API client).
  - *Why*: Decouples the UI and search engine from the specific backend provider, allowing seamless switching or merging between offline verified records and online API fetches.
- **Decision 3: Diacritic-Insensitive & Multi-Script Search Engine**
  - *Approach*: In-memory and SQLite-assisted search with Arabic character normalization (unifying `أ`, `إ`, `آ` to `ا`, stripping tashkeel `َ ُ ِ ْ ّ ً ٌ ٍ`) and Urdu unicode support.
  - *Why*: Users rarely type full tashkeel when searching in Arabic on mobile keyboards; normalization ensures searches like "انما الاعمال" match "إِنَّمَا الأَعْمَالُ".

---

### 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────────────────────┐
│                        Jetpack Compose UI Layer                        │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐  │
│  │ HomeScreen   │ │ Collections  │ │ SearchScreen │ │ DetailScreen │  │
│  └──────┬───────┘ └──────┬───────┘ └──────┬───────┘ └──────┬───────┘  │
│         │                │                │                │          │
│  ┌──────┴────────────────┴────────────────┴────────────────┴───────┐  │
│  │                         HadithViewModel                         │  │
│  └─────────────────────────────────┬───────────────────────────────┘  │
└────────────────────────────────────┼───────────────────────────────────┘
                                     │ Flow / StateFlow
┌────────────────────────────────────▼───────────────────────────────────┐
│                       HadithRepository (Interface)                     │
│  ┌─────────────────────────────────┴────────────────────────────────┐  │
│  │                   HadithRepositoryImpl                           │  │
│  └──────────────────┬─────────────────────────────┬─────────────────┘  │
└─────────────────────┼─────────────────────────────┼────────────────────┘
                      │                             │
┌─────────────────────▼──────────────┐  ┌───────────▼────────────────────┐
│      Local Database (Room)         │  │   Remote API Client (Retrofit) │
│  - HadithDao                       │  │   - SunnahApiService           │
│  - CollectionDao                   │  │   - Auth Interceptor           │
│  - BookmarkDao / HistoryDao        │  │   - Moshi JSON parsing         │
│  - Seed Database Pre-loader        │  │   - Error / Rate-limit handler │
└────────────────────────────────────┘  └────────────────────────────────┘
```

#### Core Entities & Data Models
- **`CollectionEntity`**: `id`, `name` (e.g., "bukhari"), `titleEnglish`, `titleArabic`, `titleUrdu`, `shortIntro`, `totalHadith`, `hasBooks`, `hasChapters`.
- **`BookEntity`**: `id`, `collectionName`, `bookNumber`, `titleEnglish`, `titleArabic`, `numberOfHadith`.
- **`HadithEntity`**: `id`, `collectionName`, `bookNumber`, `hadithNumber`, `inBookReference`, `arabicText`, `englishText`, `urduText`, `narrator`, `grade`, `gradedBy`, `sourceUrl`, `isBookmarked`, `lastViewedTimestamp`.
- **`SearchQueryState`**: `query`, `selectedCollection`, `selectedGrade`, `selectedLanguage`, `sortBy`.

#### Verification & Quality Assurance Plan
- **Unit Tests**:
  - `ArabicNormalizerTest`: Verify diacritic stripping, alef/hamza unification, and partial matches.
  - `HadithReferenceParserTest`: Verify correct parsing of references like "bukhari:1", "muslim 42".
  - `HadithSearchFilterTest`: Test combined filtering by grade, collection, and multilingual query text.
  - `RepositoryCacheTest`: Verify offline fallback behavior when remote API is unreachable.
- **Build & Compilation**: Run `compile_applet` to ensure pristine Kotlin 2.x and Compose Material 3 build compatibility.
