package com.example.straemwidetest.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.straemwidetest.R
import com.example.straemwidetest.StreamWideTestApplication
import com.example.straemwidetest.databinding.FragmentSetUpBinding
import com.example.straemwidetest.model.Result
import com.example.straemwidetest.requestReadContactPermission
import com.example.straemwidetest.view_model.SharedViewModel
import com.example.straemwidetest.view_model.SharedViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * This [Fragment] will be the start direction for the navigation component it is responsible for asking
 * the read contact permission from the user, fetch his contact info then load the into a local database
 * of this app
 */
class SetUpFragment : Fragment() {

    // Binding object instance corresponding to the fragment_set_up.xml layout
    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment.
    private var _binding: FragmentSetUpBinding? = null

    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment.
    private val binding get() = _binding!!

    //This attribute will be responsible in launching the permission and track it's status
    private lateinit var readContactPermission: ActivityResultLauncher<String>

    private val viewModel: SharedViewModel by activityViewModels {
        SharedViewModelFactory(
            (activity?.application as StreamWideTestApplication).repository
        )
    }

    /**
     * As soon as this [Fragment] initialized call displayWarningDialog() method to ask the user permission
     * for reading the contact
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Hide the action bar
        (activity as AppCompatActivity).supportActionBar?.hide()
        //Assign readContactPermission first to avoid crush the track the permission result.
        //Get the permission request response if the launch() method triggered
        readContactPermission =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                viewModel.setReadContactPermissionStatus(isGranted)
            }

        displayLoadingContainer()
        //Always call this method to check the permission status and update isReadContactPermissionGranted
        //attribute of the [SharedViewModel]
        context?.requestReadContactPermission(readContactPermission)?.let {
            viewModel.setReadContactPermissionStatus(
                it
            )
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSetUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //This local variable will hold the PhoneContact list size retrieved from the phone
        var contactSize = -1

        viewModel.isReadContactPermissionGranted.observe(viewLifecycleOwner) { isGranted ->
            //This value is null mean the system did not ask the user for the READ_CONTACT permission
            //yet so will call displayLoadingContainer()
            if (isGranted == null) {
                displayLoadingContainer()
            } else {
                //If the user did not give the permission call displayPermissionErrorContainer()
                if (!isGranted)
                    displayPermissionErrorContainer()
                //If the user give the permission start retrieve the contact data from the device
                //and call displayLoadingContainer()
                else {
                    viewModel.setIsDataAddedToDatabase()
                    displayLoadingContainer()
                }
            }
        }

        viewModel.isDataAddedToDatabase.observe(viewLifecycleOwner) {
            when (it) {
                //If is still looking in the database to see if the data added after second launch of
                // the app
                is Result.Loading -> {
                    displayLoadingContainer()
                }
                //Something went wrong(data retrieve from device not complete ...)
                is Result.Error -> {
                    viewModel.retrievePhoneContactFromDevice()
                }
                else -> {
                    //Data exist in the data base and it is in the complete form
                    if (it.data!!) {
                        //Notify the [SharedViewModel] the device contact has data
                        viewModel.setIsDataContactEmpty(false)
                        navigateToHomeFragment()
                    }
                    //Data loading in the database did not complete
                    else {
                        viewModel.retrievePhoneContactFromDevice()
                    }
                }
            }
        }

        viewModel.contactListFromDevice.observe(viewLifecycleOwner) {
            when (it) {
                //If the data retrieved successfully and it's not empty, load this data into the database
                //and change the contactSize to this list size
                is Result.Success -> {
                    contactSize = it.data!!.size
                    viewModel.loadDataIntoDatabase(it.data!!)
                }
                //If the data still in the loading status call displayLoadingContainer()
                is Result.Loading -> {
                    displayLoadingContainer()
                }
                //If there no contact in the device yet update the contactSize to 0
                else -> {
                    contactSize = 0
                    //Notify the [SharedViewModel] the device contact has no data
                    viewModel.setIsDataContactEmpty(true)
                }
            }
        }

        viewModel.contactAddedToTheDatabase.observe(viewLifecycleOwner) {
            when (it) {
                //If the data loaded to database successfully
                is Result.Success -> {
                    //First make sure the contactSize changed which mean either there no data or data retrieval
                    //was success from the device
                    if (contactSize != -1) {
                        //Call loadingDataIntoDatabaseComplete() from [SharedViewModel]
                        viewModel.loadingDataIntoDatabaseComplete(it.data!!, contactSize)
                    }
                }
                //If the data loaded to database was not successfully
                is Result.Error -> {
                }
                //The data still loading
                else -> {
                    displayLoadingContainer()
                }
            }
        }

        viewModel.navigateToHomeFragment.observe(viewLifecycleOwner) {
            if (it) {
                //Notify the [SharedViewModel] the device contact has data
                viewModel.setIsDataContactEmpty(false)
                navigateToHomeFragment()
            } else
                displayLoadingContainer()
        }

        binding.permissionErrorButton.setOnClickListener {
            displayWarningDialog()
        }
    }

    /**
     *This method responsible for navigating for navigating [HomeFragment]
     */
    private fun navigateToHomeFragment() {
        val action = SetUpFragmentDirections.actionSetUpFragmentToHomeFragment()
        findNavController().navigate(action)
    }

    /**
     *This method responsible o displaying an alert dialog to the user after this [Fragment] initialized
     * to ask for his permission to read contacts
     */
    private fun displayWarningDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.request_permission_alert_dialog_title))
            .setMessage(
                getString(
                    R.string.request_permission_for_read_contact_alert_dialog_message
                )
            )
            .setPositiveButton(
                getString(
                    R.string.request_permission_alert_dialog_positive_button_label
                )
            ) { _, _ ->
                requireContext().requestReadContactPermission(readContactPermission)
            }
            .setNegativeButton(
                getString(
                    R.string.request_permission_alert_dialog_negative_button_label
                )
            ) { _, _ ->
                viewModel.setReadContactPermissionStatus(false)
            }
            .show()
    }

    /**
     * his method responsible on showing the permission error container view group
     * and hide the loading container view group
     */
    private fun displayPermissionErrorContainer() {
        binding.loadingContainer.visibility = View.INVISIBLE
        binding.permissionErrorContainer.visibility = View.VISIBLE
    }

    /**
     * This method responsible on showing the loading container view group and hide the permission error
     * container view group
     */
    private fun displayLoadingContainer() {
        binding.loadingContainer.visibility = View.VISIBLE
        binding.permissionErrorContainer.visibility = View.INVISIBLE
    }

    /**
     * This fragment lifecycle method is called when the view hierarchy associated with the fragment
     * is being removed. As a result, clear out the binding object.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}