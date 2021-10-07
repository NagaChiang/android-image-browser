package com.timespawn.androidimagebrowser.models

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import com.github.kittinunf.result.Result
import com.timespawn.androidimagebrowser.BuildConfig
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

class PixabayApi {
    companion object {
        private const val TAG = "PixabayApi"
        private const val BASE_PATH = "https://pixabay.com/api/"
        private val ignoreUnknownJson = Json { ignoreUnknownKeys = true }

        suspend fun searchImages(query: String): ArrayList<ImageData>? {
            val (request, _, result) = Fuel.get(BASE_PATH, listOf(
                "key" to BuildConfig.PIXABAY_API_KEY,
                "q" to query,
            )).awaitStringResponseResult()

            Log.i(TAG, request.toString())

            var imageDatas: ArrayList<ImageData>? = null
            when (result) {
                is Result.Failure -> {
                    Log.w(TAG, "Failed to search images: ${result.getException()}")
                }

                is Result.Success -> {
                    val data = result.get()

                    Log.i(TAG, "Search image result: $data")

                    if (imageDatas == null) {
                        imageDatas = arrayListOf()
                    }

                    val dataJson = Json.parseToJsonElement(data)
                    dataJson.jsonObject["hits"]?.jsonArray?.forEach {
                        val imageData = ignoreUnknownJson.decodeFromJsonElement<ImageData>(it)
                        imageDatas.add(imageData)
                    }
                }
            }

            return imageDatas
        }
    }
}