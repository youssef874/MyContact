package com.example.straemwidetest.model

import android.util.Log
import com.example.straemwidetest.model.content_provider.IPhoneContactProvider
import com.example.straemwidetest.model.content_provider.entity.PhoneContact
import com.example.straemwidetest.model.room.AppDatabase
import com.example.straemwidetest.model.room.entity.Contact
import com.example.straemwidetest.model.room.entity.Email
import com.example.straemwidetest.model.room.entity.PhoneNumber
import com.example.straemwidetest.model.room.entity.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext

/**
 * This class will be responsible on the data operation and act as link between content provider and room
 */
class Repository(
    private val contactProvider: IPhoneContactProvider,
    val appDatabase: AppDatabase
) {

    /**
     * This method get all the needed contact information for device with the help of [IPhoneContactProvider]
     * and associate the in list of [PhoneContact]
     * @param dispatcher: the [CoroutineDispatcher] this function going to run
     * @return [PhoneContact] list, When each item contain all the data needed for this app
     */
    suspend fun retrieveContactFromDevice(dispatcher: CoroutineDispatcher): ArrayList<PhoneContact> {
        val contactList = ArrayList<PhoneContact>()
        withContext(dispatcher) {
            val asyncContact = async { contactProvider.getAllPhoneContact() }
            val asyncPhoneNumber = async { contactProvider.getContactNumbers() }
            val asyncEmail = async { contactProvider.getContactEmail() }

            val contact = asyncContact.await()
            val phoneNumber = asyncPhoneNumber.await()
            val email = asyncEmail.await()

            contact.forEach { phoneContact ->
                phoneContact.configurePhoneContact(phoneNumber, email)
            }
            contactList.addAll(contact)
        }
        return contactList
    }

    /**
     * load the database with the data retrieved from the device
     * @param phoneContactList: The data retrieved from the device
     * @param dispatcher: the [CoroutineDispatcher] this function going to run
     */
    suspend fun uploadContactDataIntoDataBase(
        dispatcher: CoroutineDispatcher,
        phoneContactList: List<PhoneContact>
    ): List<Contact> {
        val contactList = ArrayList<Contact>()
        withContext(dispatcher) {
            val currentUser = appDatabase.getUserDao().findAll()
            //Only create user if this table is empty
            if (currentUser.isEmpty()){
                createUser()
            }
            //Since it will be only one user get the first one
            val user: User = appDatabase.getUserDao().findAll()[0]
            phoneContactList.forEach { phoneContact ->
                val contact = uploadIntoContactTable(phoneContact, user.id)
                uploadIntoPhoneNumberTable(phoneContact)
                uploadIntoEmailTable(phoneContact)
                contactList.add(contact)
            }
        }
        return contactList
    }

    /**
     * Load the Email table of the database, Each contact could have multiple email.
     * @param phoneContact : One of the retrieved data from the device
     */
    private suspend fun uploadIntoEmailTable(phoneContact: PhoneContact) {
        phoneContact.emails?.forEach { email ->
            if (email.isNotEmpty())
                appDatabase.getEmailDao().insert(
                    Email(
                        email = email,
                        contactId = phoneContact.id!!
                    )
                )
        }
    }

    /**
     * Load the phone_number table of the database, Each contact could have multiple phone number.
     * @param : One of the retrieved data from the device
     */
    private suspend fun uploadIntoPhoneNumberTable(phoneContact: PhoneContact) {
        phoneContact.phoneNumbers?.forEach { number ->
            appDatabase.getPhoneNumberDao().insert(
                PhoneNumber(
                    contactId = phoneContact.id!!,
                    number = number
                )
            )
        }
    }

    /**
     * Load the user table of the database, Create user to maintain the retrieve data status.
     * So in this function we just initialize the use which mean isContactRetrieved = false
     * @return the added user and because we only going to add one user so we are going to get
     * the first one
     */
    private suspend fun createUser() {
        appDatabase.getUserDao().insert(
            User(
                isContactRetrieved = false
            )
        )
    }

    /**
     * Load the contact table of the database, from the retrieved contact from the device one by one.
     * @param phoneContact : One of the retrieved data from the device
     * @param userId: The user id from the user table of the database
     */
    private suspend fun uploadIntoContactTable(phoneContact: PhoneContact, userId: Int): Contact {
        val contact = Contact(
            id = phoneContact.id!!,
            name = phoneContact.name!!,
            photoUrlString = phoneContact.photoUrlString ?: "",
            userId = userId
        )
        appDatabase.getContactDao().insert(contact)
        return contact
    }

    /**
     * This method configure the user table to mark the the data loading into the database
     * process as complete
     */
    suspend fun loadDataIntoTheDatabaseComplete(dispatcher: CoroutineDispatcher) {
        withContext(dispatcher) {
            val asyncUser = async { appDatabase.getUserDao().findAll()[0] }
            val user: User = asyncUser.await()
            user.isContactRetrieved = true
            appDatabase.getUserDao().update(user)
        }
    }

    /**
     * This method will add phone numbers of a [Contact] to [PhoneContact]
     * @param contact: The [Contact] emails we are going to add
     * @param phoneContact: The [PhoneContact] that we going to add the phone numbers to
     */
    suspend fun addPhoneNumberToPhoneContact(
        contact: Contact,
        phoneContact: PhoneContact,
        dispatcher: CoroutineDispatcher
    ) {
        withContext(dispatcher) {
            val list = ArrayList<String>()
            appDatabase.getPhoneNumberDao().findAll().forEach {
                if (it.contactId == contact.id) {
                    list.add(it.number)
                }
            }
            phoneContact.phoneNumbers = list
        }
    }

    /**
     * This method will add emails of a [Contact] to [PhoneContact]
     * @param contact: The [Contact] emails we are going to add
     * @param phoneContact: The [PhoneContact] that we going to add the emails to
     */
    suspend fun addEmailsToPhoneContact(
        contact: Contact,
        phoneContact: PhoneContact,
        dispatcher: CoroutineDispatcher
    ) {
        withContext(dispatcher) {
            val list = ArrayList<String>()
            val emails = appDatabase.getEmailDao().findAll()
            if (!emails.isNullOrEmpty()) {
                emails.forEach {
                    if (it.contactId == contact.id) {
                        list.add(it.email)
                    }
                }
            }
            phoneContact.emails = list
        }
    }

    /**
     * This method will insert the user input into the database
     * @param phoneNumber: The new phone number of the new contact
     * @param name: The name of the new contact
     * @param lastItemId: The id of the last contact in the database
     */
    suspend fun insertData(
        phoneNumber: String,
        name: String,
        lastItemId: Int,
        dispatcher: CoroutineDispatcher
    ) {
        withContext(dispatcher) {
            appDatabase.getContactDao().insert(
                Contact(
                    id = lastItemId+1,
                    name = name,
                    userId = 1
               )
            )

            appDatabase.getPhoneNumberDao().insert(
                PhoneNumber(
                    contactId = lastItemId+1,
                    number = phoneNumber
                )
            )
        }
    }

    /**
     *This method will call delete() from [ContactDao] which will delete the created [Contact]
     * (of [PhoneContact]) from contact who is going to delete the corresponding [PhoneNumber]
     * from phone-number table
     * @param phoneContact: From this we are going to create the [Contact]
     */
    suspend fun deleteData(phoneContact: PhoneContact,dispatcher: CoroutineDispatcher){
        withContext(dispatcher){
            appDatabase.getContactDao().delete(
                Contact(
                    id = phoneContact.id!!,
                    name = phoneContact.name!!,
                    photoUrlString = "",
                    userId = 1
                )
            )
        }
    }

    /**
     * This method will check if the contact from the device added to the database
     */
    suspend fun isDataAddedToDatabase(dispatcher: CoroutineDispatcher): Boolean{
        return withContext(dispatcher){
            val user = appDatabase.getUserDao().findAll()
            user.isNotEmpty() && user[0].isContactRetrieved
        }
    }

}