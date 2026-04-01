package com.solo4.aggry.db

import com.solo4.aggry.data.AttachedFile
import com.solo4.aggry.data.ChatMessage
import com.solo4.aggry.data.Conversation

object ConversationMapper {

    fun fromDb(
        id: String,
        apiKeyId: String,
        modelId: String,
        modelName: String,
        title: String,
        createdAt: Long,
        updatedAt: Long
    ): Conversation {
        return Conversation(
            id = id,
            apiKeyId = apiKeyId,
            modelId = modelId,
            modelName = modelName,
            title = title,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    fun toTitle(messages: List<ChatMessage>): String {
        val firstUserMsg = messages.firstOrNull { it.isFromUser }?.content ?: return "New chat"
        return if (firstUserMsg.length > 50) firstUserMsg.take(50) + "..." else firstUserMsg
    }
}

object MessageMapper {

    fun fromDb(
        id: String,
        conversationId: String,
        content: String,
        isFromUser: Boolean,
        createdAt: Long,
        files: List<AttachedFile>
    ): ChatMessage {
        return ChatMessage(
            id = id,
            content = content,
            isFromUser = isFromUser,
            attachedFiles = files
        )
    }
}
