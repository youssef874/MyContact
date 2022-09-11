package com.example.straemwidetest.model.room.dao

import androidx.room.*
import com.example.straemwidetest.model.room.entity.Email
import kotlinx.coroutines.flow.Flow

@Dao
interface EmailDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(email: Email)

    @Delete
    suspend fun delete(email: Email)

    @Delete
    suspend fun update(email: Email)

    @Query("SELECT * FROM email")
    suspend fun findAll(): List<Email>

    @Query("SELECT * FROM email WHERE id = :id")
    suspend fun findById(id: Int): Email
}