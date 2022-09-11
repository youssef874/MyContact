package com.example.straemwidetest.model.room.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * This class represent the contact table in the database which will hold all information needed for
 * a contact need to have for this application
 */
@Entity(tableName = "contact", foreignKeys = [ForeignKey(
    entity = User::class,
    parentColumns = ["id"],
    childColumns = ["user_id"],
    onDelete = ForeignKey.CASCADE,
    onUpdate = ForeignKey.CASCADE
)])
data class Contact(
    @PrimaryKey(autoGenerate = false) val id: Int,
    @NonNull @ColumnInfo(name = "contact_name") var name: String="",
    @ColumnInfo(name = "contact_photo") var photoUrlString: String="",
    @NonNull @ColumnInfo(name = "user_id") var userId: Int=0
)