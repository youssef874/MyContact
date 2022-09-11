package com.example.straemwidetest.model.room.dao

import androidx.room.*
import com.example.straemwidetest.model.room.entity.User
import kotlinx.coroutines.flow.Flow

/**
 * This class contain CRUD operations for user table, Since is going to be only one user in
 * this table we won't implement findById() method, We will get the data by get the first element of
 * user list
 */
@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Delete
    suspend fun delete(user: User)

    @Update
    suspend fun update(user: User)

    @Query("SELECT * FROM user")
    fun findAll(): List<User>
}