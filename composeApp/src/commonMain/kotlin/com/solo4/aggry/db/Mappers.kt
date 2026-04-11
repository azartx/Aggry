package com.solo4.aggry.db

import com.solo4.aggry.data.AttachedFile
import com.solo4.aggry.data.ChatMessage
import com.solo4.aggry.data.Conversation
import com.solo4.aggry.data.GeneratedImage
import com.solo4.aggry.data.MessageStatus

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
        status: String,
        createdAt: Long,
        files: List<AttachedFile>,
        images: List<GeneratedImage> = emptyList()
    ): ChatMessage {
        val messageStatus = try {
            MessageStatus.valueOf(status)
        } catch (e: Exception) {
            MessageStatus.SENT
        }
        return ChatMessage(
            id = id,
            content = content,
            isFromUser = isFromUser,
            status = messageStatus,
            attachedFiles = files,
            generatedImages = images
        )
    }
}
