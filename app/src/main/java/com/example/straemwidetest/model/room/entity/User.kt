package com.example.straemwidetest.model.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * This class represent the user table in the database which exist to allow us to know if we retrieve contact
 * from the device.
 * If it is we wont retrieve from the device again
 */
@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "is_contact_retrieved") var isContactRetrieved: Boolean
)
