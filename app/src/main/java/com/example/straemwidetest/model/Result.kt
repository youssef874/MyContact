package com.example.straemwidetest.model

/**
 * This sealed class will help dealing with error
 */
sealed class Result<T>
    (
    val data: T? = null,
    val message: String? = null
) {

    //If data fetching still in progress
    class Loading<T> : Result<T>()

    //If data retrieve is success
    class Success<T>(data: T?) : Result<T>(data)

    //If the data retrieve not successful
    class Error<T>(data: T?,message: String?) : Result<T>(data,message)
}
