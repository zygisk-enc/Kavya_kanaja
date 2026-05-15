package com.zygisk.kavya_kanaja.data.model

import com.google.gson.annotations.SerializedName

data class PoetBio(
    @SerializedName("name") val name: String,
    @SerializedName("wiki_title") val wikiTitle: String,
    @SerializedName("summary") val summary: String,
    @SerializedName("description") val description: String,
    @SerializedName("thumbnail") val thumbnail: String,
    @SerializedName("image_res") val imageRes: String
)
