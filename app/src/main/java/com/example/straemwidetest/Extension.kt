package com.example.straemwidetest


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.straemwidetest.model.content_provider.entity.PhoneContact
import com.google.android.material.textfield.TextInputLayout

/**
 * This extension method to request the read contact permission
 * @param launcher:  this the alternative for onActivityResult(), To launch the permission if is not granted
 */
fun Context.requestReadContactPermission(launcher: ActivityResultLauncher<String> ): Boolean{
    val permission = "android.permission.READ_CONTACTS"
        return if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ){
            launcher.launch(permission)
            false
        }else
            true
}

/**
 * This extension function will an [ImageView] according to [PhoneContact] photoUrlString attribute
 * @param phoneContact: This param hold the photoUrlString attribute
 */
fun ImageView.setImageViewByPhotoUrlString(phoneContact: PhoneContact,context: Context){
    if (phoneContact.photoUrlString!!.isEmpty()) {
        this.setImageDrawable(
            ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.ic_baseline_person_24,
                null
            )
        )
    } else {
        this.setImageURI(
            Uri.parse(phoneContact.photoUrlString!!)
        )
    }
}

/**
 * This is an extension method of [TextInputLayout] to enable the error and display the message
 * @param isWong: if there error or not
 * @param msg: the msg is going to display if there an error
 */
fun TextInputLayout.setError(isWong: Boolean, msg:String = ""){
    if (isWong){
        isErrorEnabled =true
        error = msg
    }else{
        isErrorEnabled = false
        error = null
    }
}

/**
 * This extension function will hide the keyboard
 * @param view: the how from it is click we going to hide the keyboard
 */
fun Context.hideKeyboard(view: View){
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

/**
 * This extension function will call hideKeyboard() method above of th [Context]
 * and this going to be accessible in any [Activity] in this project
 */
fun Activity.hideKeyBoard(){
    hideKeyboard(currentFocus ?: View(this))
}

/**
 * This extension function will call hideKeyboard() method above of th [Context]
 * and this going to be accessible in any [Fragment] in this project
 */
fun Fragment.hideKeyboard(){
    view?.let { activity?.hideKeyboard(it) }
}
