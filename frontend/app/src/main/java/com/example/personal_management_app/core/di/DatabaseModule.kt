package com.example.personal_management_app.core.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.personal_management_app.data.local.AppDatabase
import com.example.personal_management_app.data.local.dao.NoteDao
import com.example.personal_management_app.data.local.dao.TagDao
import com.example.personal_management_app.data.local.dao.UserDao
import com.example.personal_management_app.utils.exportDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        val database = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "personal_management_app.db"
        )
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    val now = System.currentTimeMillis()
                    // Seed initial default test user
                    db.execSQL("""
                        INSERT OR IGNORE INTO users (id, email, password, name, username, avatar, role, createdAt, updatedAt, deletedAt)
                        VALUES ('user_01', 'abc@gmail.com', 'password123', 'Nguyễn Minh Đức', 'BiOwO', NULL, 'Student / Developer', $now, $now, NULL)
                    """)
                    // Seed initial default tags for user_01
                    db.execSQL("""
                        INSERT OR IGNORE INTO tags (id, userId, name, color) VALUES ('tag-work', 'user_01', 'Công việc', '0xFF64B5F6')
                    """)
                    db.execSQL("""
                        INSERT OR IGNORE INTO tags (id, userId, name, color) VALUES ('tag-travel', 'user_01', 'Du lịch', '0xFF4DB6AC')
                    """)
                    // Zero dummy notes! Database starts completely clean.
                }

                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    exportDatabase(context)
                }
            })
            .build()

        // Pre-warm database to trigger onCreate and open the DB file immediately
        database.openHelper.writableDatabase
        exportDatabase(context)

        return database
    }

    @Provides
    fun provideNoteDao(database: AppDatabase): NoteDao = database.noteDao()

    @Provides
    fun provideTagDao(database: AppDatabase): TagDao = database.tagDao()

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao = database.userDao()
}
