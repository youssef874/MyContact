package com.example.straemwidetest.model.content_provider

import com.example.straemwidetest.model.content_provider.entity.PhoneContact

/**
 * This interface will hol the [PhoneContactProvider]
 */
interface IPhoneContactProvider {

    suspend fun getAllPhoneContact(): ArrayList<PhoneContact>

    suspend fun getContactNumbers(): HashMap<String,ArrayList<String>>

    suspend fun getContactEmail(): HashMap<String,ArrayList<String>>
}