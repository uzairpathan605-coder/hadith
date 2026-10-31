package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.HadithDao
import com.example.data.dao.SearchHistoryDao
import com.example.data.model.BookEntity
import com.example.data.model.CollectionEntity
import com.example.data.model.HadithEntity
import com.example.data.model.SearchHistoryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        CollectionEntity::class,
        BookEntity::class,
        HadithEntity::class,
        SearchHistoryEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class HadithDatabase : RoomDatabase() {

    abstract fun hadithDao(): HadithDao
    abstract fun searchHistoryDao(): SearchHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: HadithDatabase? = null

        fun getInstance(context: Context): HadithDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HadithDatabase::class.java,
                    "hadith_library.db"
                )
                    .createFromAsset("database/hadith_library.db")
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                        }

                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                val database = getInstance(context)
                                val dao = database.hadithDao()
                                if (dao.getHadithCount() < 1000) {
                                    populateSeedData(database)
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun populateSeedData(database: HadithDatabase) {
            val dao = database.hadithDao()
            dao.insertCollections(SeedData.collections)
            dao.insertBooks(SeedData.books)
            dao.insertHadiths(SeedData.hadiths)
        }
    }
}
