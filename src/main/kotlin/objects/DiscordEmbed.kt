package net.guneyilmaz0.webhook.objects

import kotlinx.serialization.Serializable

@Serializable
data class DiscordEmbed(
    var title: String? = null,
    var description: String? = null,
    var url: String? = null,
    var color: Int? = null,
    var footer: Footer? = null,
    var image: Image? = null,
    var thumbnail: Thumbnail? = null,
    var author: Author? = null,
    var fields: MutableList<Field>? = null
) {
    fun addField(name: String, value: String, inline: Boolean = false): DiscordEmbed {
        if (fields == null) fields = mutableListOf()
        fields!!.add(Field(name, value, inline))
        return this
    }

    @Serializable
    data class Footer(
        val text: String,
        val icon_url: String? = null
    )

    @Serializable
    data class Image(
        val url: String? = null
    )

    @Serializable
    data class Thumbnail(
        val url: String? = null
    )

    @Serializable
    data class Author(
        val name: String,
        val url: String? = null,
        val icon_url: String? = null
    )

    @Serializable
    data class Field(
        val name: String,
        val value: String,
        val inline: Boolean = false
    )
}