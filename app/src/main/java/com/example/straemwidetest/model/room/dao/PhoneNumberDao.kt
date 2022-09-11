package com.example.straemwidetest.model.room.dao

import androidx.room.*
import com.example.straemwidetest.model.room.entity.PhoneNumber
import kotlinx.coroutines.flow.Flow

@androidx.room.Dao
interface PhoneNumberDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(phoneNumber: PhoneNumber)

    @Delete
    suspend fun delete(phoneNumber: PhoneNumber)

    @Update
    suspend fun update(phoneNumber: PhoneNumber)

    @Query("SELECT * FROM phone_number")
    suspend fun findAll(): List<PhoneNumber>

    @Query("SELECT * FROM phone_number WHERE id = :id")
    suspend fun findById(id: Int): PhoneNumber
}