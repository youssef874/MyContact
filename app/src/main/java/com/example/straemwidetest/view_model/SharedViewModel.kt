package com.example.straemwidetest.view_model

import android.util.Log
import androidx.lifecycle.*
import com.example.straemwidetest.ZeroSizeCursorException
import com.example.straemwidetest.model.Repository
import com.example.straemwidetest.model.Result
import com.example.straemwidetest.model.content_provider.entity.PhoneContact
import com.example.straemwidetest.model.room.entity.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

/**
 * This class act as a [ViewModel] for all the [Fragment] in this application
 */
class SharedViewModel(private val repository: Repository) : ViewModel() {

    //This property going to hold the read contact permission status
    private var _isReadContactPermissionGranted = MutableLiveData<Boolean?>()
    val isReadContactPermissionGranted: LiveData<Boolean?> get() = _isReadContactPermissionGranted

    //This property going to hold the contact information retrieved from the device
    private var _contactListFromDevice = MutableLiveData<Result<List<PhoneContact>>>()
    val contactListFromDevice: LiveData<Result<List<PhoneContact>>> get() = _contactListFromDevice

    //This property going to hold the contact information who jus added to the database
    //to compare it withe the contact in the device to update the isContactRetrieved of the use table
    //of the data base
    private var _contactAddedToTheDatabase = MutableLiveData<Result<List<Contact>>>()
    val contactAddedToTheDatabase: LiveData<Result<List<Contact>>> get() = _contactAddedToTheDatabase

    //This property will be true when the data finish loading into the database which mean
    // contactAddedToTheDatabase size equals to contactListFromDevice size the it will
    //permit the navigation into the home fragment
    private var _navigateToHomeFragment = MutableLiveData<Boolean>()
    val navigateToHomeFragment: LiveData<Boolean> get() = _navigateToHomeFragment

    //This property will be tre when there no contact in the device
    private var _isDataContactEmpty = MutableLiveData<Boolean>()
    val isDataContactEmpty: LiveData<Boolean> get() = _isDataContactEmpty

    //This property will hold list of [PhoneContact] loaded from the database
    private var _phoneContactLoadedFromDataBase = MutableLiveData<Result<List<PhoneContact>>>()
    val phoneContactLoadedFromDataBase: LiveData<Result<List<PhoneContact>>>
        get() = _phoneContactLoadedFromDataBase

    //This property will hold the [PhoneContact] of the recycle view item selected
    //once this property is set we will navigate to [ContactDetailFragment] with the associated data
    //to be displayed
    private var _navigateToContactDetailFragment = MutableLiveData<PhoneContact?>()
    val navigateToContactDetailFragment: LiveData<PhoneContact?>
        get() = _navigateToContactDetailFragment

    //This attribute to track the data insertion if it success we will navigate to [HomeFragment]
    private var _navigateToHomeFragmentFromAddContactFragment = MutableLiveData<Result<Int>>()
    val navigateToHomeFragmentFromAddContactFragment: LiveData<Result<Int>>
        get() = _navigateToHomeFragmentFromAddContactFragment

    //This property will tell us if there added to the database, basically this for non first launching app
    //if this true mean there data so the app will proceed to the [HomeFragment] navigation
    //without retrieve data from device, and without load data into database
    private var _isDataAddedToDatabase = MutableLiveData<Result<Boolean>>()
    val isDataAddedToDatabase: LiveData<Result<Boolean>> get() = _isDataAddedToDatabase

    /**
     * This method will set _isDataAddedToDatabase attribute according  isDataAddedToDatabase()
     * of [Repository] value and success
     */
    fun setIsDataAddedToDatabase(){
        _isDataAddedToDatabase.value = Result.Loading()
        viewModelScope.launch {
            try {
                _isDataAddedToDatabase.postValue(
                    Result.Success(repository.isDataAddedToDatabase(Dispatchers.IO))
                )
            }catch (e: Exception){
                _isDataAddedToDatabase.postValue(Result.Error(null,e.message))
            }
        }
    }

    /**
     * This method responsible on setting isReadContactPermissionGranted value
     * @param isGranted: the read contact permission status
     */
    fun setReadContactPermissionStatus(isGranted: Boolean) {
        _isReadContactPermissionGranted.value = isGranted
    }

    /**
     * This method will set the _isDataContactEmpty property
     */
    fun setIsDataContactEmpty(mIsDataContactEmpty: Boolean) {
        _isDataContactEmpty.value = mIsDataContactEmpty
    }

    /**
     * This function will call retrieveContactFromDevice() from [Repository] and run it
     * in [Dispatchers.IO] thread then update the _contactListFromDevice value
     */
    fun retrievePhoneContactFromDevice() {
        viewModelScope.launch {
            _contactListFromDevice.postValue(Result.Loading())
            try {
                _contactListFromDevice.postValue(
                    Result.Success(
                        repository.retrieveContactFromDevice(Dispatchers.IO)
                    )
                )
                //If the data is Empty
            } catch (e: ZeroSizeCursorException) {
                _contactListFromDevice.postValue(Result.Error(listOf(), e.message))
            } catch (e: Exception) {
                Log.i(TAG, "error: ${e.message}")
            }
        }
    }

    /**
     * This method will trigger uploadContactDataIntoDataBase() of the [Repository]
     * and update _contactAddedToTheDatabase according to the late method success
     * @param list: [PhoneContact] list retrieved from the device to load it into database
     */
    fun loadDataIntoDatabase(list: List<PhoneContact>) {
        _contactAddedToTheDatabase.value = Result.Loading()
        viewModelScope.launch {
            try {
                _contactAddedToTheDatabase
                    .postValue(
                        Result.Success(
                            repository.uploadContactDataIntoDataBase(Dispatchers.IO, list)
                        )
                    )
            } catch (e: Exception) {
                e.printStackTrace()
                _contactAddedToTheDatabase.postValue(Result.Error(listOf(), e.message))
            }
        }
    }

    /**
     * This method responsible on comparing the data size from the device with the size of data added
     * to the database the update _navigateToHomeFragment accordingly.
     * @param list: [Contact] list added to the database
     * @param contactSize: [PhoneContact] list size from device
     */
    fun loadingDataIntoDatabaseComplete(list: List<Contact>, contactSize: Int) {
        viewModelScope.launch {
            //while the contact list size retrieved from device more than the contact
            //list size added to the database
            while (list.size < contactSize) {
                //Cannot navigate to the home fragment
                _navigateToHomeFragment.postValue(false)
            }
            repository.loadDataIntoTheDatabaseComplete(Dispatchers.IO)
            _navigateToHomeFragment.postValue(true)
        }
    }

    /**
     *This method will load all the data from the data (from all tables) and collect them into
     * _phoneContactLoadedFromDataBase
     */
    fun loadFromDatabase() {
        _phoneContactLoadedFromDataBase.value = Result.Loading()
        viewModelScope.launch {
            try {
                repository.appDatabase.getContactDao().findAll().collect { it ->
                    _phoneContactLoadedFromDataBase.postValue(
                        Result.Success(
                            //Transform List<Contact> represented get from findAll() of [ContactDao]
                            //into List<PhoneContact> to post it into _phoneContactLoadedFromDataBase
                            it.map { contact ->
                                //Instantiate [PhoneContact] object from data in form of [Contact] that came
                                // from findAll() of [ContactDao]
                                val phoneContact = PhoneContact(
                                    id = contact.id,
                                    name = contact.name,
                                    photoUrlString = contact.photoUrlString
                                )

                                //Add the data that came from addEmailsToPhoneContact() of [Repository]
                                //into the [PhoneContact] instance
                                repository.addEmailsToPhoneContact(
                                    contact,
                                    phoneContact,
                                    Dispatchers.IO
                                )
                                //Add the data that came from addPhoneNumberToPhoneContact() of [Repository]
                                //into the [PhoneContact] instance
                                repository.addPhoneNumberToPhoneContact(
                                    contact,
                                    phoneContact,
                                    Dispatchers.IO
                                )
                                //Return the configured version of [PhoneContact] instance to complete
                                //the transformation
                                phoneContact
                            }
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _phoneContactLoadedFromDataBase.postValue(Result.Error(listOf(), e.message))
            }
        }
    }

    /**
     * This method will filter the [PhoneContact] list in phoneContactLoadedFromDataBase
     * for the one who has name contain search
     * @param name: the user input
     */
    fun searchContactByName(name: String) {
        _phoneContactLoadedFromDataBase.value = Result.Success(
            (phoneContactLoadedFromDataBase.value!!.data as List<PhoneContact>).filter {
                it.name!!.lowercase(Locale.getDefault()).contains(name)
            }
        )
    }

    /**
     * This method will set the _navigateToContactDetailFragment property
     */
    fun displayContactDetail(phoneContact: PhoneContact) {
        _navigateToContactDetailFragment.value = phoneContact
    }

    /**
     * This method will set the _navigateToContactDetailFragment property to null
     * once we navigate to [ContactDetailFragment] so for the next navigation it won't have any
     * mix
     */
    fun displayContactDetailComplete() {
        _navigateToContactDetailFragment.value = null
    }

    /**
     * This method will set _navigateToHomeFragmentFromAddContactFragment property according
     * to insertData() of the [Repository] success providing it with the user input needed
     * @param phoneNumber: The new phone number of the new contact
     * @param name: The name of the new contact
     * @param lastItemId: The id of the last contact in the database
     */
    fun insertData(phoneNumber: String, name: String, lastItemId: Int) {
        _navigateToHomeFragmentFromAddContactFragment.value = Result.Loading()
        viewModelScope.launch {
            try {
                repository.insertData(phoneNumber,name,lastItemId,Dispatchers.IO)
                _navigateToHomeFragmentFromAddContactFragment.postValue(Result.Success(1))
            } catch (e: Exception) {
                e.printStackTrace()
                _navigateToHomeFragmentFromAddContactFragment.postValue(Result.Error(0, e.message))
            }
        }
    }

    /**
     * This method will call insertData() of the [Repository] providing the [PhoneContact]
     * data of the selected item
     * @param phoneContact: This hold the data to be deleted
     */
    fun deleteData(phoneContact: PhoneContact){
        viewModelScope.launch {
            try {
                repository.deleteData(phoneContact,Dispatchers.IO)
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    companion object {
        const val TAG = "SharedViewModel"
    }
}

class SharedViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedViewModel::class.java)) {
            return SharedViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}