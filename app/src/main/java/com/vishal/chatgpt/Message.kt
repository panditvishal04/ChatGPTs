package com.vishal.chatgpt

class Message(var message: String, var sentBy: String) {
    companion object {
        const val SENT_BY_ME = "Me"
        const val SENT_BY_BOT = "Bot"
    }
}

