package com.vishal.chatgpt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vishal.chatgpt.databinding.ChatItemBinding

class MessageAdapter(
    private var messageList: List<Message>
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    inner class MessageViewHolder(private val binding: ChatItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message) {
            if (message.sentBy == Message.SENT_BY_ME) {
                binding.leftChatView.visibility = View.GONE
                binding.rightChatView.visibility = View.VISIBLE
                binding.rightChatTextView.text = message.message
            } else {
                binding.leftChatView.visibility = View.VISIBLE
                binding.rightChatView.visibility = View.GONE
                binding.leftChatTextView.text = message.message
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = ChatItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messageList[position])
    }

    override fun getItemCount(): Int = messageList.size
}
