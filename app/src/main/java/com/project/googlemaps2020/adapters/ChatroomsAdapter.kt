package com.project.googlemaps2020.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.googlemaps2020.R
import com.project.googlemaps2020.models.ChatMessage
import com.project.googlemaps2020.models.Chatroom
import kotlinx.android.synthetic.main.layout_item_chatroom.view.*

class ChatroomsAdapter :
    ListAdapter<Chatroom, ChatroomsAdapter.ChatroomsViewHolder>(ChatroomDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatroomsViewHolder {
        return ChatroomsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_item_chatroom, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ChatroomsViewHolder, position: Int) {
        val chatroom = currentList[position]
        holder.bind(chatroom)
    }

    fun setOnChatroomItemClickListener(listener: (Chatroom) -> Unit) {
        onChatroomItemClickListener = listener
    }

    private var onChatroomItemClickListener: ((Chatroom) -> Unit)? = null

    inner class ChatroomsViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(chatroom: Chatroom) {
            itemView.apply {
                Glide.with(this).load(chatroom.image)
                    .placeholder(ivChatroomImage.drawable).into(ivChatroomImage)

                tvChatroomName.text = chatroom.title

                setOnClickListener {
                    onChatroomItemClickListener?.let {
                        it(chatroom)
                    }
                }
            }
        }
    }
}

class ChatroomDiffCallback : DiffUtil.ItemCallback<Chatroom>() {
    override fun areItemsTheSame(oldItem: Chatroom, newItem: Chatroom): Boolean {
        return oldItem.chatroom_id == newItem.chatroom_id
    }

    override fun areContentsTheSame(oldItem: Chatroom, newItem: Chatroom): Boolean {
        return oldItem == newItem
    }
}