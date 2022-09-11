package com.example.straemwidetest.model.room.dao

import androidx.room.*
import com.example.straemwidetest.model.room.entity.Contact
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contact: Contact)

    @Delete
    suspend fun delete(contact: Contact)

    @Update
    suspend fun update(contact: Contact)

    @Query("SELECT * FROM contact ORDER BY contact_name ASC")
    fun findAll(): Flow<List<Contact>>

    @Query("SELECT * FROM contact WHERE id = :id")
    suspend fun findById(id: Int): List<Contact>
}