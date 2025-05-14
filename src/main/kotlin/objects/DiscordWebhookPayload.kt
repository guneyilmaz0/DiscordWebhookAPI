package net.guneyilmaz0.webhook.objects

import kotlinx.serialization.Serializable

@Serializable
data class DiscordWebhookPayload(
    var content: String? = null,
    var username: String? = null,
    var avatar_url: String? = null,
    var embeds: MutableList<DiscordEmbed>? = null,
    var tts: Boolean = false
)