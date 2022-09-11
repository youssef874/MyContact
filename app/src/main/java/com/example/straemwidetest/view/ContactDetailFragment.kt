package com.example.straemwidetest.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.navArgs
import com.example.straemwidetest.databinding.FragmentContactDetailBinding
import com.example.straemwidetest.model.content_provider.entity.PhoneContact
import com.example.straemwidetest.setImageViewByPhotoUrlString

/**
 * This class will display the selected phone contact data of the [RecycleView]
 */
class ContactDetailFragment : Fragment() {

    // Binding object instance corresponding to the fragment_contact_detail.xml layout
    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment.
    private var _binding: FragmentContactDetailBinding? = null

    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment.
    private val binding: FragmentContactDetailBinding get() = _binding!!

    //This property will hold the [PhoneContact] data sent vi argument in navigateToContactDetailFragment()
    //of [homeFragment]
    private var phoneContact = PhoneContact()

    private val args: ContactDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Hide the action bar
        (activity as AppCompatActivity).supportActionBar?.hide()

        phoneContact = args.phoneContact!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentContactDetailBinding.inflate(inflater,container,false)

        setupContactDetail()
        return binding.root
    }

    /**
     * Ths function will bind the [PhoneContact] data with the views in fragment_contact_detail.xml layout
     */
    private fun setupContactDetail() {
        binding.contactDetailImageView.setImageViewByPhotoUrlString(phoneContact,requireContext())
        binding.contactDetailNameTextView.text = phoneContact.name
        binding.contactDetailNumberTextView.text = phoneContact.phoneNumbers!![0]
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object{
        const val TAG = "ContactDetailFragment"
    }
}