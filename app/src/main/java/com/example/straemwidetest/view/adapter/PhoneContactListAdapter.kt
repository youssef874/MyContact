package com.example.straemwidetest.view.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.straemwidetest.R
import com.example.straemwidetest.databinding.PhoneContactListItemBinding
import com.example.straemwidetest.model.content_provider.entity.PhoneContact
import com.example.straemwidetest.setImageViewByPhotoUrlString

/**
 * This class responsible on adapting [PhoneContact] to [RecyclerView] items
 * @param onDeleteItemSelected: This param is the function who is going to be invoked when
 * the delete button is selected
 * @param onItemSelected: This param is the function who is going to be invoked when
 * an item clicked
 */
class PhoneContactListAdapter(
    private val context: Context,
    val onDeleteItemSelected: (phoneContact: PhoneContact) -> Unit,
    val onItemSelected: (phoneContact: PhoneContact) -> Unit
) :
    ListAdapter<PhoneContact, PhoneContactListAdapter.PhoneContactViewHolder>(DiffCallback) {

    /**
     * This inner class responsible on binding [PhoneContact] to the phone_contact_list_item.xml
     * view using dataBinding
     */
    inner class PhoneContactViewHolder(val binding: PhoneContactListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(phoneContact: PhoneContact) {
            binding.phoneContact = phoneContact
            binding.executePendingBindings()
            binding.contactListItemImageView.setImageViewByPhotoUrlString(phoneContact,context)
        }
    }

    /**
     * This object will notify the [RecyclerView] to update an item if there is a changes in data
     */
    companion object DiffCallback : DiffUtil.ItemCallback<PhoneContact>() {
        override fun areItemsTheSame(oldItem: PhoneContact, newItem: PhoneContact): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PhoneContact, newItem: PhoneContact): Boolean {
            return oldItem == newItem
        }

    }

    /**
     * This function will create the item view of the [RecyclerView]
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhoneContactViewHolder {
        return PhoneContactViewHolder(
            PhoneContactListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    /**
     * This method to provide the data to the [RecyclerView] items
     */
    override fun onBindViewHolder(holder: PhoneContactViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)

        holder.binding.deleteConactListItemBotton.setOnClickListener {
            onDeleteItemSelected(data)
        }

        holder.itemView.setOnClickListener {
            onItemSelected(data)
        }

    }


}