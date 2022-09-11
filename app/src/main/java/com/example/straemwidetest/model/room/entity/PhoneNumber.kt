package com.example.straemwidetest.model.room.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.straemwidetest.model.room.entity.Contact

/**
 * This class represent phone_number table structure in the database which will hold
 * the contact phone_number if is exist
 */
@Entity(tableName = "phone_number", foreignKeys = [ForeignKey(
    entity = Contact::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("contact_id"),
    onDelete = ForeignKey.CASCADE,
    onUpdate = ForeignKey.CASCADE
)])
data class PhoneNumber(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @NonNull @ColumnInfo(name = "contact_id") var contactId: Int,
    @NonNull var number: String
)
