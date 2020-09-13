package com.project.googlemaps2020.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.project.googlemaps2020.R
import com.project.googlemaps2020.adapters.ChatroomsAdapter
import com.project.googlemaps2020.models.Chatroom
import com.project.googlemaps2020.utils.Resource
import com.project.googlemaps2020.viewmodels.ChatroomViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_chats.*

private const val CREATE_CHATROOM_DIALOG_TAG = "CreateChatroom"

@AndroidEntryPoint
class ChatsFragment : Fragment(R.layout.fragment_chats) {

    private val viewModel: ChatroomViewModel by activityViewModels()

    private lateinit var chatroomsAdapter: ChatroomsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // On rotation resetting createListener of dialog, which is called when create btn is clicked
        if (savedInstanceState != null) {
            val createChatroomDialog = parentFragmentManager.findFragmentByTag(
                CREATE_CHATROOM_DIALOG_TAG
            ) as CreateChatroomDialog?

            viewModel.chatroomImageUri.observe(viewLifecycleOwner, { uri ->
                uri?.let {
                    createChatroomDialog?.setChatroomImage(it)
                }
            })
        }

        setupOnClickListeners()
        setupObservers()
        setUpRecyclerView()
    }

    private fun setupOnClickListeners() {
        layoutNewChatroom.setOnClickListener {
            showCreateChatroomDialog()
        }

        ivProfileImage.setOnClickListener {
            findNavController().navigate(R.id.action_chatsFragment_to_profileFragment)
        }
    }

    private fun showCreateChatroomDialog() {
        CreateChatroomDialog().show(parentFragmentManager, CREATE_CHATROOM_DIALOG_TAG)
    }

    private fun setupObservers() {
        viewModel.profileImageUri.observe(viewLifecycleOwner, {
            Glide.with(requireContext()).load(it).placeholder(ivProfileImage.drawable)
                .into(ivProfileImage)
        })

        viewModel.createChatroomState.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    progressBarTillCreate(false)
                    val chatroom = it.data
                    chatroom?.let { room ->
                        createBundleAndNavigateToChatroomFragment(room)
                    }
                }

                is Resource.Error -> {
                    progressBarTillCreate(false)
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }

                is Resource.Loading -> {
                    progressBarTillCreate(true)
                }
            }

            viewModel.unsetChatroomState()              // chatroom state do not get observed again ,so chatroom don't get created again
        })


        viewModel.chatroomsState.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    progressBarTillFetch(false)
                    it.data?.let { chatroomsList ->
                        chatroomsAdapter.submitList(chatroomsList)
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
        })
    }

    private fun setUpRecyclerView() {
        rvChatrooms.apply {
            chatroomsAdapter = ChatroomsAdapter()
            adapter = chatroomsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        chatroomsAdapter.setOnChatroomItemClickListener {
            createBundleAndNavigateToChatroomFragment(it)
        }
    }

    private fun createBundleAndNavigateToChatroomFragment(chatroom: Chatroom) {
        val bundle = Bundle().apply {
            putParcelable("chatroom", chatroom)
        }
        findNavController().navigate(R.id.action_chatsFragment_to_chatroomFragment, bundle)
    }


    private fun progressBarTillCreate(visible: Boolean) {
        progressBarEntering.isVisible = visible
        tvEntering.isVisible = visible
    }

    private fun progressBarTillFetch(visible: Boolean) {
        progressBarFetching.isVisible = visible
    }
}