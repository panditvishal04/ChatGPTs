package com.vishal.chatgpt

import com.vishal.chatgpt.databinding.ActivityMainBinding
import okhttp3.logging.HttpLoggingInterceptor
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private val apiKey = "AIzaSyBdgdoCLwTK2lmcmBjAU5EG71giugHfFNk"
    private lateinit var binding: ActivityMainBinding
    private val messageList = mutableListOf<Message>()
    private val messageAdapter = MessageAdapter(messageList)
    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)

        with(binding) {
            recyclerView.adapter = messageAdapter
            val layoutManager = LinearLayoutManager(this@MainActivity)
            layoutManager.stackFromEnd = true
            recyclerView.layoutManager = layoutManager

            binding.sendBtn.setOnClickListener {
                val question = messageEt.text.toString().trim { it <= ' ' }
                if (question.isNotEmpty()) {
                    addToChat(question, Message.SENT_BY_ME)
                    addTypingIndicator()
                    messageEt.text.clear()
                    callAPI(question)
                    welcomeText.visibility = View.GONE
                }
            }
        }
    }

    private fun addToChat(message: String, sentBy: String) {
        runOnUiThread {
            messageList.add(Message(message, sentBy))
            messageAdapter.notifyItemInserted(messageList.size - 1)
            binding.recyclerView.smoothScrollToPosition(messageAdapter.itemCount - 1)
        }
    }
    private fun addTypingIndicator() {
        messageList.add(Message("Typing...", Message.SENT_BY_BOT))
        messageAdapter.notifyItemInserted(messageList.size - 1)
    }


    fun addResponse(response: String?) {
        // Safely check if the last message is "Typing..." and remove it.
        if (messageList.isNotEmpty() && messageList.last().message == "Typing...") {
            val removePosition = messageList.size - 1
            messageList.removeAt(removePosition)
            messageAdapter.notifyItemRemoved(removePosition)
        }

        // Add the actual response, ensuring it's not null or blank.
        response?.takeIf { it.isNotBlank() }?.let {
            messageList.add(Message(it, Message.SENT_BY_BOT))
            messageAdapter.notifyItemInserted(messageList.size - 1)
        }

        // Ensure the RecyclerView scrolls to show the latest added message.
        binding.recyclerView.scrollToPosition(messageList.size - 1)
    }

    private fun callAPI(question:String){
        val generativeModel = GenerativeModel(
            // For text-only input, use the gemini-pro model
            modelName = "gemini-pro",
            // Access your API key as a Build Configuration variable (see "Set up your API key" above)
            apiKey = apiKey
        )
        runBlocking {
            launch{
                addResponse(generativeModel.generateContent(question).text)
            }
        }

}
}
