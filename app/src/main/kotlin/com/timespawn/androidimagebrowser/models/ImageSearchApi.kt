package com.timespawn.androidimagebrowser.models

interface ImageSearchApi {
    suspend fun searchImages(query: String): ArrayList<ImageData>?
}