package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {
    @Query("SELECT query FROM search_history ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentQueries(limit: Int = 10): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuery(item: SearchHistoryEntity)

    @Query("DELETE FROM search_history WHERE query = :query")
    suspend fun deleteQuery(query: String)

    @Query("DELETE FROM search_history")
    suspend fun clearAll()
}
