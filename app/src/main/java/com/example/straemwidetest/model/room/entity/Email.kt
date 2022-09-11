package com.example.straemwidetest.model.room.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.straemwidetest.model.room.entity.Contact

/**
 * This class represent email table structure in the database which will hold
 * the contact emails if is exist
 */
@Entity(tableName = "email", foreignKeys = [ForeignKey(
    entity = Contact::class,
    parentColumns = ["id"],
    childColumns = ["contact_id"],
    onDelete = ForeignKey.CASCADE,
    onUpdate = ForeignKey.CASCADE
)])
data class Email(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var email: String,
    @NonNull @ColumnInfo(name = "contact_id") val contactId: Int
)
