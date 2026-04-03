package com.solo4.aggry.save

actual suspend fun savePhotoToGallery(path: String): Result<Unit> {
    return Result.failure(NotImplementedError("savePhotoToGallery is TODO on iOS"))
}
