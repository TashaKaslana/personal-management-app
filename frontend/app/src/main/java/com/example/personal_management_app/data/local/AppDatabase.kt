package com.example.personal_management_app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.personal_management_app.data.local.converters.RoomConverters
import com.example.personal_management_app.data.local.dao.FeedbackDao
import com.example.personal_management_app.data.local.dao.NoteDao
import com.example.personal_management_app.data.local.dao.TagDao
import com.example.personal_management_app.data.local.dao.UserDao
import com.example.personal_management_app.data.local.entities.UserEntity
import com.example.personal_management_app.data.entities.FeedbackEntity
import com.example.personal_management_app.data.entities.NoteEntity
import com.example.personal_management_app.data.entities.TagEntity

@Database(
    entities = [
        NoteEntity::class,
        TagEntity::class,
        UserEntity::class,
        FeedbackEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun tagDao(): TagDao
    abstract fun userDao(): UserDao
    abstract fun feedbackDao(): FeedbackDao
}
