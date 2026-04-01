package com.solo4.aggry.db

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.solo4.aggry.data.ChatMessage
import com.solo4.aggry.data.Conversation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ChatRepository {

    private val queries get() = AggryDatabaseProvider.database.aggryDatabaseQueries
    private val fileCache get() = AggryDatabaseProvider.fileCache

    fun getConversationsByApiKey(apiKeyId: String): Flow<List<Conversation>> {
        return queries.selectConversationsByApiKey(apiKeyId) { id, keyId, modelId, modelName, title, createdAt, updatedAt ->
            ConversationMapper.fromDb(id, keyId, modelId, modelName, title, createdAt, updatedAt)
        }.asFlow().mapToList(Dispatchers.Default)
    }

    suspend fun getMessages(conversationId: String): List<ChatMessage> = withContext(Dispatchers.Default) {
        val messageRows = queries.selectMessagesByConversation(conversationId).executeAsList()
        messageRows.map { row ->
            val files = queries.selectFilesByMessage(row.id).executeAsList().mapNotNull { fileRow ->
                fileCache.loadFile(fileRow.cached_path)
            }
            MessageMapper.fromDb(
                id = row.id,
                conversationId = row.conversation_id,
                content = row.content,
                isFromUser = row.is_from_user == 1L,
                createdAt = row.created_at,
                files = files
            )
        }
    }

    suspend fun getConversation(conversationId: String): Conversation? = withContext(Dispatchers.Default) {
        queries.selectConversationById(conversationId).executeAsOneOrNull()?.let { row ->
            ConversationMapper.fromDb(
                id = row.id,
                apiKeyId = row.api_key_id,
                modelId = row.model_id,
                modelName = row.model_name,
                title = row.title,
                createdAt = row.created_at,
                updatedAt = row.updated_at
            )
        }
    }

    suspend fun updateConversationModel(
        conversationId: String,
        modelId: String,
        modelName: String
    ) = withContext(Dispatchers.Default) {
        queries.updateConversationModel(
            id = conversationId,
            modelId = modelId,
            modelName = modelName,
            updatedAt = currentTimeMillis()
        )
    }

    @OptIn(ExperimentalUuidApi::class)
    suspend fun createConversation(
        apiKeyId: String,
        modelId: String,
        modelName: String
    ): String = withContext(Dispatchers.Default) {
        val id = Uuid.random().toString()
        val now = currentTimeMillis()
        queries.insertConversation(
            id = id,
            apiKeyId = apiKeyId,
            modelId = modelId,
            modelName = modelName,
            title = "New chat",
            createdAt = now,
            updatedAt = now
        )
        id
    }

    suspend fun saveMessage(
        conversationId: String,
        message: ChatMessage
    ) = withContext(Dispatchers.Default) {
        val now = currentTimeMillis()
        queries.insertMessage(
            id = message.id,
            conversationId = conversationId,
            content = message.content,
            isFromUser = if (message.isFromUser) 1L else 0L,
            createdAt = now
        )
        message.attachedFiles.forEach { file ->
            val cachedPath = fileCache.saveFile(file)
            queries.insertFile(
                messageId = message.id,
                name = file.name,
                cachedPath = cachedPath,
                mimeType = file.mimeType,
                size = file.bytes.size.toLong()
            )
        }
        queries.updateConversationTimestamp(id = conversationId, updatedAt = now)
        updateTitleIfNeeded(conversationId, message)
    }

    private fun updateTitleIfNeeded(conversationId: String, message: ChatMessage) {
        if (!message.isFromUser) return
        val conv = queries.selectConversationById(conversationId).executeAsOneOrNull() ?: return
        if (conv.title == "New chat") {
            val title = ConversationMapper.toTitle(listOf(message))
            queries.updateConversationTitle(
                id = conversationId,
                title = title,
                updatedAt = currentTimeMillis()
            )
        }
    }

    suspend fun deleteConversation(conversationId: String) = withContext(Dispatchers.Default) {
        val messages = queries.selectMessagesByConversation(conversationId).executeAsList()
        messages.forEach { msg ->
            queries.selectFilesByMessage(msg.id).executeAsList().forEach { fileRow ->
                fileCache.deleteFile(fileRow.cached_path)
            }
        }
        queries.deleteConversation(conversationId)
    }
}
