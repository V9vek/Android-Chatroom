package com.project.googlemaps2020.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.project.googlemaps2020.R
import com.project.googlemaps2020.adapters.ChatroomUsersAdapter
import com.project.googlemaps2020.models.Chatroom
import com.project.googlemaps2020.utils.Resource
import com.project.googlemaps2020.viewmodels.ChatroomViewModel
import kotlinx.android.synthetic.main.fragment_chatroom_details.*

class ChatroomDetailsFragment : Fragment(R.layout.fragment_chatroom_details) {

    private val args: ChatroomDetailsFragmentArgs by navArgs()

    private val viewModel: ChatroomViewModel by activityViewModels()

    private lateinit var chatroom: Chatroom
    private lateinit var chatroomUsersAdapter: ChatroomUsersAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chatroom = args.chatroom

        initUI()
        setupListeners()
        setUpRecyclerView()
        setupObservers()
    }

    private fun initUI() {
        Glide.with(requireContext())
            .load(chatroom.image)
            .placeholder(ivChatroomImage.drawable)
            .into(ivChatroomImage)

        tvChatroomName.text = chatroom.title

        getChatroomUsers()
    }

    private fun getChatroomUsers() {
        viewModel.getChatroomUsers(chatroom.chatroom_id)
    }

    private fun setupListeners() {
        ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        tvLeaveChatroom.setOnClickListener {
            leaveChatroom()
        }
    }

    private fun leaveChatroom() {
        viewModel.leaveChatroom(chatroom.chatroom_id)
        findNavController().navigate(R.id.action_chatroomDetailsFragment_to_chatsFragment)
    }

    private fun setupObservers() {
        viewModel.chatroomUsersState.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    chatroomUsersAdapter.submitList(it.data)
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun setUpRecyclerView() {
        rvUsers.apply {
            chatroomUsersAdapter = ChatroomUsersAdapter()
            adapter = chatroomUsersAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
}