package com.timespawn.androidimagebrowser.models

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import com.github.kittinunf.result.Result
import com.timespawn.androidimagebrowser.BuildConfig
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class RemoteConfig(val defaultLayoutMode: Int) {
    companion object {
        private const val TAG = "RemoteConfig"

        suspend fun fromUrl(url: String = BuildConfig.REMOTE_CONFIG_URL): RemoteConfig? {
            val (request, _, result) = Fuel.get(url).awaitStringResponseResult()

            Log.i(TAG, request.toString())

            var config: RemoteConfig? = null
            when (result) {
                is Result.Failure -> {
                    Log.e(TAG, "Failed to get remote config: ${result.getException()}")
                }

                is Result.Success -> {
                    val data = result.get()

                    Log.i(TAG, "Remote config result: $data")

                    config = Json.decodeFromString(data)
                }
            }

            return config
        }
    }
}
