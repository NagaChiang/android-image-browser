package com.timespawn.androidimagebrowser.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageData(
    val id: Int,
    @SerialName("previewURL") val previewUrl: String,
    @SerialName("views") val viewCount: Int,
    @SerialName("likes") val likeCount: Int,
    @SerialName("downloads") val downloadCount: Int,
    @SerialName("comments") val commentCount: Int,
)