package com.ignitetech.compose.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.ignitetech.compose.data.call.Call
import com.ignitetech.compose.data.call.CallDao
import com.ignitetech.compose.data.chat.Chat
import com.ignitetech.compose.data.chat.ChatDao
import com.ignitetech.compose.data.seed.DatabaseSeedWorker
import com.ignitetech.compose.data.user.User
import com.ignitetech.compose.data.user.UserDao
import com.ignitetech.compose.utility.Constants

@Database(entities = [User::class, Chat::class, Call::class], version = 3)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun chatDao(): ChatDao
    abstract fun callDao(): CallDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, Constants.DATABASE_NAME)
                .addCallback(
                    object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            WorkManager.getInstance(context).enqueue(
                                OneTimeWorkRequestBuilder<DatabaseSeedWorker>()
                                    .build()
                            )
                        }
                    }
                )
                .build()
        }
    }
}
