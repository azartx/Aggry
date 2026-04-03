package com.solo4.aggry.data

data class AIModel(
    val id: String,
    val name: String,
    val inputModalities: List<String> = emptyList(),
    val outputModalities: List<String> = emptyList(),
    val contextLength: Long? = null,
    val inputPrice: Double? = null,
    val outputPrice: Double? = null
) {
    val canGenerateImages: Boolean get() = "image" in outputModalities
    val canProcessImages: Boolean get() = "image" in inputModalities
    val canProcessFiles: Boolean get() = "file" in inputModalities
}

data class ChatMessage(
    val id: String,
    val content: String,
    val isFromUser: Boolean,
    val status: MessageStatus = MessageStatus.SENT,
    val attachedFiles: List<AttachedFile> = emptyList(),
    val generatedImages: List<GeneratedImage> = emptyList()
)

enum class MessageStatus {
    SENT,
    FAILED
}

data class GeneratedImage(
    val cachedPath: String,
    val mimeType: String = "image/png"
)

data class AttachedFile(
    val name: String,
    val bytes: ByteArray,
    val mimeType: String
) {
    val isImage: Boolean get() = mimeType.startsWith("image/")
    val isPdf: Boolean get() = mimeType == "application/pdf"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AttachedFile) return false
        return name == other.name && bytes.contentEquals(other.bytes) && mimeType == other.mimeType
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + bytes.contentHashCode()
        result = 31 * result + mimeType.hashCode()
        return result
    }
}
