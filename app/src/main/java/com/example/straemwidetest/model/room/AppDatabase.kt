package com.example.straemwidetest.model.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.straemwidetest.model.room.dao.ContactDao
import com.example.straemwidetest.model.room.dao.EmailDao
import com.example.straemwidetest.model.room.dao.PhoneNumberDao
import com.example.straemwidetest.model.room.dao.UserDao
import com.example.straemwidetest.model.room.entity.Contact
import com.example.straemwidetest.model.room.entity.Email
import com.example.straemwidetest.model.room.entity.PhoneNumber
import com.example.straemwidetest.model.room.entity.User

/**
 * This class will create and return database
 */
@Database(entities = [Contact::class,User::class,Email::class,PhoneNumber::class], version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getPhoneNumberDao(): PhoneNumberDao

    abstract fun getEmailDao(): EmailDao

    abstract fun getUserDao(): UserDao

    abstract fun getContactDao(): ContactDao

    companion object{

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase{
            return INSTANCE?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "stream_wide_test"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}