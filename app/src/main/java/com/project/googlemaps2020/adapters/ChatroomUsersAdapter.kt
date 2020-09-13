package com.project.googlemaps2020.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.googlemaps2020.R
import com.project.googlemaps2020.models.User
import kotlinx.android.synthetic.main.layout_item_chatroom_user.view.*

class ChatroomUsersAdapter :
    ListAdapter<User, ChatroomUsersAdapter.ChatroomUsersViewHolder>(ChatroomUsersDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatroomUsersViewHolder {
        return ChatroomUsersViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_item_chatroom_user, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ChatroomUsersViewHolder, position: Int) {
        val chatroomUser = currentList[position]
        holder.bind(chatroomUser)
    }

    class ChatroomUsersViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(chatroomUser: User) {
            itemView.apply {
                Glide.with(this).load(chatroomUser.profile_image)
                    .placeholder(ivUserImage.drawable).into(ivUserImage)

                tvUsername.text = chatroomUser.username
            }
        }
    }
}

class ChatroomUsersDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.user_id == newItem.user_id
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }
}