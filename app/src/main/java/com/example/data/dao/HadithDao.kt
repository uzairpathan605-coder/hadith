package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.BookEntity
import com.example.data.model.CollectionEntity
import com.example.data.model.HadithEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HadithDao {
    @Query("SELECT * FROM collections ORDER BY name ASC")
    fun getAllCollections(): Flow<List<CollectionEntity>>

    @Query("SELECT * FROM collections WHERE name = :name LIMIT 1")
    fun getCollection(name: String): Flow<CollectionEntity?>

    @Query("SELECT * FROM books WHERE collectionName = :collectionName ORDER BY CAST(bookNumber AS INTEGER) ASC")
    fun getBooksForCollection(collectionName: String): Flow<List<BookEntity>>

    @Query("SELECT * FROM hadiths WHERE collectionName = :collectionName AND bookNumber = :bookNumber ORDER BY CAST(hadithNumber AS INTEGER) ASC")
    fun getHadithsForBook(collectionName: String, bookNumber: String): Flow<List<HadithEntity>>

    @Query("SELECT * FROM hadiths WHERE collectionName = :collectionName ORDER BY CAST(hadithNumber AS INTEGER) ASC")
    fun getHadithsForCollection(collectionName: String): Flow<List<HadithEntity>>

    @Query("SELECT * FROM hadiths WHERE id = :id LIMIT 1")
    fun getHadithById(id: String): Flow<HadithEntity?>

    @Query("SELECT * FROM hadiths WHERE collectionName = :collectionName AND hadithNumber = :hadithNumber LIMIT 1")
    fun getHadithByNumber(collectionName: String, hadithNumber: String): Flow<HadithEntity?>

    @Query("SELECT * FROM hadiths WHERE isBookmarked = 1 ORDER BY hadithNumber ASC")
    fun getBookmarkedHadiths(): Flow<List<HadithEntity>>

    @Query("SELECT * FROM hadiths WHERE lastViewedTimestamp IS NOT NULL ORDER BY lastViewedTimestamp DESC LIMIT :limit")
    fun getRecentHistory(limit: Int = 20): Flow<List<HadithEntity>>

    @Query("SELECT * FROM hadiths")
    fun getAllHadiths(): Flow<List<HadithEntity>>

    @Query("SELECT * FROM hadiths ORDER BY RANDOM() LIMIT 1")
    fun getRandomHadith(): Flow<HadithEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollections(collections: List<CollectionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<BookEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHadiths(hadiths: List<HadithEntity>)

    @Query("UPDATE hadiths SET isBookmarked = :isBookmarked WHERE id = :hadithId")
    suspend fun setBookmark(hadithId: String, isBookmarked: Boolean)

    @Query("UPDATE hadiths SET lastViewedTimestamp = :timestamp WHERE id = :hadithId")
    suspend fun updateLastViewed(hadithId: String, timestamp: Long)

    @Query("SELECT COUNT(*) FROM hadiths")
    suspend fun getHadithCount(): Int

    @Query("DELETE FROM hadiths WHERE id NOT IN (SELECT id FROM hadiths WHERE isBookmarked = 1)")
    suspend fun clearUnsavedCache()
}
