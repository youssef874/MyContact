package com.example.straemwidetest.model.content_provider.entity

import android.os.Parcel
import android.os.Parcelable

/**
 * This data class represent the the contact format we want to retrieve from the phone,
 * and the representation for a single item in the [RecycleView]
 */
data class PhoneContact(
    var id: Int? = -1,
    var name: String? = "",
    var photoUrlString: String? = "",
    var phoneNumbers: List<String>? = listOf(),
    var emails: List<String>? = listOf()
): Parcelable{

    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.createStringArrayList()
    ) {
    }

    /**
     * This method responsible on adding the phone numbers an emails after retrieving
     * it from the the device separately
     * @param number: [HashMap] when each contact id associated with all his phone numbers registered
     * @param mEmail: [HashMap] when each contact id associated with all his emails registered
     */
    fun configurePhoneContact(number: HashMap<String,ArrayList<String>>,mEmail: HashMap<String, ArrayList<String>>){
        number[id.toString()]?.let {
           phoneNumbers = it
        }
        mEmail[id.toString()]?.let {
            emails = it
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(name)
        parcel.writeString(photoUrlString)
        parcel.writeStringList(phoneNumbers)
        parcel.writeStringList(emails)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PhoneContact> {
        override fun createFromParcel(parcel: Parcel): PhoneContact {
            return PhoneContact(parcel)
        }

        override fun newArray(size: Int): Array<PhoneContact?> {
            return arrayOfNulls(size)
        }
    }
}
