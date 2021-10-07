package com.timespawn.androidimagebrowser.models

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import com.github.kittinunf.result.Result
import com.timespawn.androidimagebrowser.BuildConfig
import kotlinx.serialization.json.*

class PixabayApi {
    companion object {
        private val TAG = "PixabayApi"
        private val BASE_PATH = "https://pixabay.com/api/"
        private val ignoreUnknownJson = Json { ignoreUnknownKeys = true }

        suspend fun searchImages(query: String): ArrayList<ImageData>? {
            val (request, response, result) = Fuel.get(BASE_PATH, listOf(
                "key" to BuildConfig.PIXABAY_API_KEY,
                "q" to "flower",
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

                    val dataJson = Json.parseToJsonElement(data)
                    dataJson.jsonObject["hits"]?.jsonArray?.forEach {
                        val data = ignoreUnknownJson.decodeFromJsonElement<ImageData>(it)
                        if (imageDatas == null) {
                            imageDatas = arrayListOf()
                        }

                        imageDatas!!.add(data)
                    }
                }
            }

            return imageDatas
        }
    }
}