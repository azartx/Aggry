package com.solo4.aggry.save

expect suspend fun savePhotoToGallery(path: String, mimeType: String): Result<Unit>
