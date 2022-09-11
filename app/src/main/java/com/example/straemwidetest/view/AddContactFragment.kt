package com.example.straemwidetest.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.straemwidetest.R
import com.example.straemwidetest.StreamWideTestApplication
import com.example.straemwidetest.databinding.FragmentAddContactBinding
import com.example.straemwidetest.hideKeyboard
import com.example.straemwidetest.model.Result
import com.example.straemwidetest.setError
import com.example.straemwidetest.view_model.SharedViewModel
import com.example.straemwidetest.view_model.SharedViewModelFactory
import com.google.android.material.snackbar.Snackbar

/**
 * This class will hold the ui of add contact
 */
class AddContactFragment : Fragment() {

    // Binding object instance corresponding to the fragment_add_contact.xml layout
    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment.
    private var _binding: FragmentAddContactBinding? = null

    private val args: AddContactFragmentArgs by navArgs()

    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment.
    private val binding: FragmentAddContactBinding get() = _binding!!

    private val viewModel: SharedViewModel by activityViewModels {
        SharedViewModelFactory(
            (activity?.application as StreamWideTestApplication).repository
        )
    }

    private var lastItemId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lastItemId = args.lastItemId
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentAddContactBinding.inflate(inflater, container, false)
        Log.i(TAG, "$lastItemId")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.root.setOnClickListener {
            hideKeyboard()
        }

        binding.addContactButton.setOnClickListener {
            displayLoading()
            val number = binding.addContactPhoneNumberEditText.text.toString()
            val name = binding.addContactNameEditText.text.toString()
            if (!name.isNullOrEmpty() && !number.isNullOrEmpty()) {
                if (number.length == 8)
                    viewModel.insertData(number, name, lastItemId)
                else
                    binding.addContactPhoneNumberTextField.setError(
                        true,
                        getString(R.string.error_size_number_message)
                    )
            } else {
                displayAddButton()
                displayEmptyFieldErrorMessage(name, number)
            }
        }

        viewModel.navigateToHomeFragmentFromAddContactFragment.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    displayLoading()
                }
                is Result.Success -> {
                    navigateToHomeFragment()
                }
                else -> {
                    displayAddButton()
                    Snackbar.make(
                        binding.root,
                        getString(R.string.insert_data_error_message),
                        Snackbar.LENGTH_SHORT
                    ).show()
                    Log.i(TAG,"${it.message}")
                }
            }
        }
    }

    /**
     * This method responsible at navigation to [HomeFragment]
     */
    private fun navigateToHomeFragment() {
        val action = AddContactFragmentDirections.actionAddContactFragmentToHomeFragment()
        findNavController().navigate(action)
    }

    /**
     * This method will check the input if they are empty it will trigger
     * the our setError() extension function of the [TextInputLayout]
     * @param name: The input came from add_contact_name_edit_text
     * @param number: The input came from add_contact_phone_number_edit_text
     */
    private fun displayEmptyFieldErrorMessage(name: String, number: String) {
        if (name.isEmpty())
            binding.addContactNameTextField.setError(
                true,
                getString(R.string.empty_name_error_message)
            )
        if (number.isEmpty())
            binding.addContactPhoneNumberTextField.setError(
                true,
                getString(R.string.empty_number_error_message)
            )
    }

    /**
     * This method will hide the home_loading view and show the add_contact_button view
     */
    private fun displayAddButton() {
        binding.addContactButton.visibility = View.VISIBLE
        binding.addContactLoading.visibility = View.INVISIBLE
    }


    /**
     * This method will show the home_loading view and hide the add_contact_button view
     */
    private fun displayLoading() {
        binding.addContactLoading.visibility = View.VISIBLE
        binding.addContactButton.visibility = View.INVISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "AddContactFragment"
    }
}