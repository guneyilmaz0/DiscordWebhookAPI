package net.guneyilmaz0.webhook

import cn.nukkit.Server
import cn.nukkit.plugin.Plugin
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.guneyilmaz0.webhook.objects.DiscordEmbed
import net.guneyilmaz0.webhook.objects.DiscordWebhookPayload
import java.net.HttpURLConnection
import java.net.URI

class DiscordWebhook(
    private val webhookUrl: String,
    private val pluginInstance: Plugin,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) {
    private val payload = DiscordWebhookPayload()
    private val logger = pluginInstance.logger

    companion object {
        private val json = Json {
            ignoreUnknownKeys = true
            prettyPrint = false
            encodeDefaults = false
        }
    }

    init {
        if (webhookUrl.isBlank()) logger.warning("DiscordWebhook initialized with an empty URL. Sending will fail.")
    }

    fun setContent(content: String?): DiscordWebhook {
        payload.content = content
        return this
    }

    fun setUsername(username: String?): DiscordWebhook {
        payload.username = username
        return this
    }

    fun setAvatarUrl(avatarUrl: String?): DiscordWebhook {
        payload.avatar_url = avatarUrl
        return this
    }

    fun addEmbed(embed: DiscordEmbed): DiscordWebhook {
        if (payload.embeds == null) payload.embeds = mutableListOf()
        if (payload.embeds!!.size < 10) payload.embeds!!.add(embed)
        else logger.warning("A Discord webhook message can contain a maximum of 10 embeds.")
        return this
    }

    fun addEmbed(configurator: DiscordEmbed.() -> Unit): DiscordWebhook {
        val embed = DiscordEmbed().apply(configurator)
        return addEmbed(embed)
    }


    fun setTTS(ttsEnabled: Boolean): DiscordWebhook {
        payload.tts = ttsEnabled
        return this
    }

    fun send(onComplete: ((success: Boolean, message: String) -> Unit)? = null) {
        if (webhookUrl.isBlank()) {
            val errorMsg = "Webhook URL is empty. Message cannot be sent."
            logger.warning(errorMsg)
            executeOnMainThreadIfNeeded(onComplete, false, errorMsg)
            return
        }

        if (payload.content.isNullOrBlank() && payload.embeds.isNullOrEmpty()) {
            val errorMsg = "Webhook content and embeds are empty. Empty messages cannot be sent."
            logger.warning(errorMsg)
            executeOnMainThreadIfNeeded(onComplete, false, errorMsg)
            return
        }

        coroutineScope.launch {
            var success = false
            var responseMessageText: String
            try {
                val url = URI(webhookUrl).toURL()
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                connection.setRequestProperty("User-Agent", "NukkitWebhookPlugin/1.0 (${pluginInstance.description.name}/${pluginInstance.description.version})")
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                connection.doOutput = true

                val jsonPayload = json.encodeToString(payload)
                logger.debug("Discord Payload to be sent: $jsonPayload")

                connection.outputStream.use { os ->
                    os.write(jsonPayload.toByteArray(Charsets.UTF_8))
                }

                val responseCode = connection.responseCode
                val httpResponseMessage = connection.responseMessage ?: ""

                if (responseCode in 200..299) {
                    success = true
                    responseMessageText = "Webhook sent successfully. Response: $responseCode $httpResponseMessage"
                    logger.info(responseMessageText)
                } else {
                    val errorBody = connection.errorStream?.bufferedReader()?.readText() ?: "No details"
                   responseMessageText = "Webhook could not be sent. Response: $responseCode $httpResponseMessage. Error Details: $errorBody"
                    logger.error(responseMessageText)
                }
            } catch (e: Exception) {
                responseMessageText = "An exception occurred while sending the webhook: ${e.message}"
                logger.error(responseMessageText, e)
            }

            executeOnMainThreadIfNeeded(onComplete, success, responseMessageText)
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun executeOnMainThreadIfNeeded(
        callback: ((Boolean, String) -> Unit)?,
        success: Boolean,
        message: String
    ) {
        callback?.let { cb ->
            if (Dispatchers.Main.immediate != coroutineScope.coroutineContext[kotlinx.coroutines.CoroutineDispatcher.Key]) {
                Server.getInstance().scheduler.scheduleTask(pluginInstance) {
                    cb(success, message)
                }
            } else {
                cb(success, message)
            }
        }
    }
}