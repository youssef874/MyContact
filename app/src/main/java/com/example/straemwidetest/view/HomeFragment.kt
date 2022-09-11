package com.example.straemwidetest.view

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.example.straemwidetest.R
import com.example.straemwidetest.StreamWideTestApplication
import com.example.straemwidetest.databinding.FragmentHomeBinding
import com.example.straemwidetest.model.Result
import com.example.straemwidetest.model.content_provider.entity.PhoneContact
import com.example.straemwidetest.view.adapter.PhoneContactListAdapter
import com.example.straemwidetest.view_model.SharedViewModel
import com.example.straemwidetest.view_model.SharedViewModelFactory
import com.google.android.material.snackbar.Snackbar
import java.util.*

class HomeFragment : Fragment() {

    // Binding object instance corresponding to the fragment_home.xml layout
    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment.
    private var _binding: FragmentHomeBinding? = null

    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment.
    private val binding: FragmentHomeBinding get() = _binding!!

    private val viewModel: SharedViewModel by activityViewModels {
        SharedViewModelFactory(
            (activity?.application as StreamWideTestApplication).repository
        )
    }

    //This variable hold PhoneContactListAdapter instance
    private var adapter: PhoneContactListAdapter? = null

    //This property will hold the last contact id to send it to [AddContactFragment]
    //to increment by 1 from it and add the new [Contact with the incremented id
    private var lastItemId = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        //Show the action bar
        (activity as AppCompatActivity).supportActionBar?.show()

        viewModel.isDataContactEmpty.observe(viewLifecycleOwner) {
            if (it)
                displayNoContactText()
            else {
                displayLoading()
                viewModel.loadFromDatabase()
            }
        }

        //Initialize the adapter variable, and define the onItemSelected function
        adapter = PhoneContactListAdapter(
            requireContext(),
            { itemToDelete: PhoneContact ->
                viewModel.deleteData(itemToDelete)
            }
        ) {
            viewModel.displayContactDetail(it)
        }

        //Bind the recycle_view adapter to the adapter variable
        binding.recycleView.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setupMenu()

        viewModel.phoneContactLoadedFromDataBase.observe(viewLifecycleOwner) {
            when (it) {
                //If loading from the database was successful
                is Result.Success -> {
                    //Hide the loading and noContactTextView view
                    displayRecycleView()
                    adapter?.submitList(it.data)
                    //Sort the list by [PhoneContact) id the assign the last item id
                    //of the list into lastItemId
                    lastItemId = it.data!!.sortedBy { phoneContact ->
                        phoneContact.id
                    }.last().id!!
                }
                //Hide the loading and noContactTextView view
                is Result.Error -> {
                    displayRecycleView()
                    Snackbar.make(
                        binding.root,
                        "An error occurred please restart the app",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    displayLoading()
                }
            }
        }

        viewModel.navigateToContactDetailFragment.observe(viewLifecycleOwner) {
            if (it != null) {
                navigateToContactDetailFragment(it)
                viewModel.displayContactDetailComplete()
            }
        }
    }

    /**
     * This method will navigate to [AddContactFragment] sending the selected
     */
    private fun navigateToAddContactFragment() {
        val action = HomeFragmentDirections.actionHomeFragmentToAddContactFragment(lastItemId)
        findNavController().navigate(action)
    }

    /**
     * This method will navigate to [ContactDetailFragment] sending the selected
     * [PhoneContact] in argument
     * @param phoneContact: The selected [PhoneContact]
     */
    private fun navigateToContactDetailFragment(phoneContact: PhoneContact) {
        val action = HomeFragmentDirections.actionHomeFragmentToContactDetailFragment(phoneContact)
        findNavController().navigate(action)
    }

    /**
     * Setup the search item in action bar using [MenuProvider] which is an alternative
     * for onCreateOptionsMenu() onOptionsItemSelected() ... who are deprecated
     */
    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.home_menu, menu)
                searchForContactByName(menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.add_contact_menu_item){
                    navigateToAddContactFragment()
                }
                return true
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    /**
     * This method will get the [SearchView] from search_menu.xml app_bar_search item
     * and call for searchContactByName() of [SharedViewModel]
     * @param menu: The [Menu] we are going look into to find the [SearchView]
     */
    private fun searchForContactByName(menu: Menu) {
        val searchView = menu.findItem(R.id.app_bar_search)
            .actionView as SearchView

        searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let {
                        if (it.isNotEmpty()) {
                            viewModel.searchContactByName(it)
                            return true
                        }
                    }
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let {
                        if (it.isNotEmpty()) {
                            viewModel.searchContactByName(it.lowercase(Locale.getDefault()))
                        }
                    }
                    return false
                }

            }
        )
    }

    /**
     * This method will hide the home_loading view and hide the no_contact_text_view view
     */
    private fun displayRecycleView() {
        binding.homeLoading.visibility = View.INVISIBLE
        binding.noContactTextView.visibility = View.INVISIBLE
    }

    /**
     * This method will show the home_loading view and hide the no_contact_text_view view
     */
    private fun displayLoading() {
        binding.homeLoading.visibility = View.VISIBLE
        binding.noContactTextView.visibility = View.INVISIBLE
    }

    /**
     * This method will show no_contact_text_view view and hide home_loading view
     */
    private fun displayNoContactText() {
        binding.noContactTextView.visibility = View.VISIBLE
        binding.homeLoading.visibility = View.INVISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "HomeFragment"
    }
}