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
import kotlinx.android.synthetic.main.layout_chat_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class ChatMessageAdapter :
    ListAdapter<ChatMessage, ChatMessageAdapter.ChatMessageViewHolder>(ChatDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageViewHolder {
        return ChatMessageViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_chat_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ChatMessageViewHolder, position: Int) {
        val chatMessage = currentList[position]
        holder.bind(chatMessage)
    }

    class ChatMessageViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(chatMessage: ChatMessage) {
            itemView.apply {
                Glide.with(this).load(chatMessage.user?.profile_image)
                    .placeholder(ivProfileImage.drawable)
                    .into(ivProfileImage)

                tvProfileName.text = chatMessage.user?.username

                tvMessage.text = chatMessage.message

                val calendar = Calendar.getInstance().apply {
                    timeInMillis = chatMessage.timestamp
                }
                val dateFormat = SimpleDateFormat("dd MMM yyyy hh:mm aa", Locale.getDefault())
                tvTimestamp.text = dateFormat.format(calendar.time)
            }
        }
    }
}

class ChatDiffCallback : DiffUtil.ItemCallback<ChatMessage>() {
    override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
        return oldItem.message_id == newItem.message_id
    }

    override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
        return oldItem == newItem
    }
}