package com.example.straemwidetest.model.content_provider

import android.app.Application
import android.provider.ContactsContract
import com.example.straemwidetest.ZeroSizeCursorException
import com.example.straemwidetest.model.content_provider.entity.PhoneContact

/**
 * This class contain all the logic to retrieve all the contact info needed
 * @constructor : accept [Application] value to get [ContactResolver] to retrieve contact data
 */
class PhoneContactProvider(private val application: Application) : IPhoneContactProvider {

    /**
     * This method will retrieve data from Contact table(_ID and DISPLAY_NAME_PRIMARY columns)
     * @return list of data retrieved in form of [PhoneContact]
     */
    override suspend fun getAllPhoneContact(): ArrayList<PhoneContact> {
        val phoneContactList = ArrayList<PhoneContact>()

        //Define the column that we want to extract
        val projection: Array<out String> = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.Contacts.PHOTO_THUMBNAIL_URI
        )

        val cursor = application.contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            projection,
            null,
            null,
            null
        )

        //If there is a contact in the device
        if (cursor != null && cursor.count > 0) {
            while (cursor.moveToNext()) {
                phoneContactList.add(
                    PhoneContact(
                        //get the value of CONTACT_ID column
                        id = cursor.getLong(0).toInt(),
                        //get the value of DISPLAY_NAME_PRIMARY column
                        name = cursor.getString(1),
                        //get the value of PHOTO_THUMBNAIL_URI column
                        photoUrlString = cursor.getString(2)
                    )
                )
            }
            cursor.close()
        } else if (cursor != null && cursor.count == 0) {
            throw ZeroSizeCursorException()
        }
        return phoneContactList
    }

    /**
     * This method retrieve the contact phone numbers
     * @return a [HashMap] when each contact id associated with all his phone numbers registered
     */
    override suspend fun getContactNumbers(): HashMap<String, ArrayList<String>> {
        val contactNumber = HashMap<String, ArrayList<String>>()

        //Define the column that we want to extract
        val projection: Array<out String> = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        val cursor = application.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            null
        )

        if (cursor != null && cursor.count > 0) {
            while (cursor.moveToNext()) {
                //get the value of CONTACT_ID column
                val contactId = cursor.getString(0)
                //get the value of NUMBER column
                val number = cursor.getString(1)
                if (contactNumber.containsKey(contactId)) {
                    contactNumber[contactId]?.add(number)
                } else {
                    contactNumber[contactId] = arrayListOf(number)
                }
            }
            cursor.close()
        }
        return contactNumber
    }

    /**
     * This method retrieve the contact emails
     * @return a [HashMap] when each contact id associated with all his emails registered
     */
    override suspend fun getContactEmail(): HashMap<String, ArrayList<String>> {
        val contactEmails = HashMap<String, ArrayList<String>>()

        //Define the column that we want to extract
        val projection: Array<out String> = arrayOf(
            ContactsContract.CommonDataKinds.Email.CONTACT_ID,
            ContactsContract.CommonDataKinds.Email.ADDRESS
        )
        val cursor = application.contentResolver.query(
            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
            projection,
            null,
            null,
            null
        )
        if (cursor != null && cursor.count > 0) {
            while (cursor.moveToNext()) {
                //get the value of CONTACT_ID column
                val contactId = cursor.getString(0)
                //get the value of ADDRESS column
                val email = cursor.getString(1)
                if (contactEmails.containsKey(contactId))
                    contactEmails[contactId]?.add(email)
                else
                    contactEmails[contactId] = arrayListOf(email)
            }
            cursor.close()
        }
        return contactEmails
    }

}