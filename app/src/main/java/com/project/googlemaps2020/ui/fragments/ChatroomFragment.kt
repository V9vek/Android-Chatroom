package com.project.googlemaps2020.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.project.googlemaps2020.R
import com.project.googlemaps2020.adapters.ChatMessageAdapter
import com.project.googlemaps2020.models.Chatroom
import com.project.googlemaps2020.utils.Resource
import com.project.googlemaps2020.viewmodels.ChatroomViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_chatroom.*

@AndroidEntryPoint
class ChatroomFragment : Fragment(R.layout.fragment_chatroom) {

    private val args: ChatroomFragmentArgs by navArgs()

    private val viewModel: ChatroomViewModel by activityViewModels()

    private lateinit var chatroom: Chatroom
    private lateinit var chatMessageAdapter: ChatMessageAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chatroom = args.chatroom
        joinChatroom()

        initUI()
        setupListeners()
        setUpRecyclerView()
        setupObservers()
    }

    private fun joinChatroom() {
        viewModel.joinChatroom(chatroom.chatroom_id)
    }

    private fun initUI() {
        Glide.with(requireContext())
            .load(chatroom.image)
            .placeholder(ivChatroomImage.drawable)
            .into(ivChatroomImage)

        tvChatroom.text = chatroom.title

        getChatMessages()
    }

    private fun setupListeners() {
        ivBackBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        ivSendBtn.setOnClickListener {
            insertNewMessage()
            clearMessage()
        }

        toolbarChats.setOnClickListener {
            val bundle = Bundle().apply {
                putParcelable("chatroom", chatroom)
            }
            findNavController().navigate(
                R.id.action_chatroomFragment_to_chatroomDetailsFragment,
                bundle
            )
        }
    }

    private fun insertNewMessage() {
        val message = etMessage.text.toString()
        if (message.isNotBlank()) {
            viewModel.insertNewMessage(chatroom.chatroom_id, message)
        }
    }

    private fun getChatMessages() {
        viewModel.getChatMessages(chatroom.chatroom_id)
    }

    private fun setupObservers() {
        viewModel.newMessageState.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    scrollToBottom()
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
            viewModel.unsetNewMessageState()
        })

        viewModel.chatMessagesState.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    progressBarTillFetch(false)
                    it.data?.let { chatMessageList ->
                        chatMessageAdapter.submitList(chatMessageList)
                        scrollToBottom()
                    }
                }
                is Resource.Error -> {
                    progressBarTillFetch(false)
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    progressBarTillFetch(true)
                }
            }
            viewModel.unsetChatMessagesState()
        })
    }

    private fun clearMessage() {
        etMessage.setText("")
    }

    private fun setUpRecyclerView() {
        rvChat.apply {
            chatMessageAdapter = ChatMessageAdapter()
            adapter = chatMessageAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        rvChat.addOnLayoutChangeListener { view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (bottom < oldBottom) {
                scrollToBottom()
            }
        }
    }

    private fun scrollToBottom() {
        if (chatMessageAdapter.currentList.size > 0) {
            rvChat.smoothScrollToPosition(chatMessageAdapter.itemCount)
        }
    }

    private fun progressBarTillFetch(visible: Boolean) {
        progressBarFetching.isVisible = visible
    }
}