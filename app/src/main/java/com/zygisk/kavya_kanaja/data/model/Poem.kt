package com.zygisk.kavya_kanaja.data.model

import com.google.gson.annotations.SerializedName

data class Poem(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("poet") val poet: String,
    @SerializedName("poetId") val poetId: Int,
    @SerializedName("theme") val theme: String,
    @SerializedName("content") val content: String,
    @SerializedName("meanings") val meanings: Map<String, String>,
    @SerializedName("audioFileName") val audioFileName: String?,
    @SerializedName("youtubeVideoId") val youtubeVideoId: String? = null,
    @SerializedName("releaseDate") val releaseDate: String? = null,
    @SerializedName("category") val category: String? = null
)