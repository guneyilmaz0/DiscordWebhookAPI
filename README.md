# Discord Webhook Plugin for Nukkit

This project provides a simple and customizable way to send Discord Webhook messages from a Nukkit server using Kotlin. You can configure the message content, username, avatar URL, and rich embeds.

## Features

- Send messages via Discord Webhooks
- Fully customizable embed support
- JSON serialization via Kotlinx
- Asynchronous sending using coroutines
- Designed specifically for Nukkit plugins

## Installation

### Gradle

Add the repository and dependency to your `build.gradle` file:

```gradle
repositories {
    mavenCentral()
    // Add repository if not published to Maven Central
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.guneyilmaz0:DiscordWebhookAPI:VERSION'
}
```

### Maven

Add the dependency to your `pom.xml` file:

```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<dependency>
    <groupId>com.github.guneyilmaz0</groupId>
    <artifactId>DiscordWebhookAPI</artifactId>
    <version>VERSION</version>
</dependency>
```

## Usage

```kotlin
val webhook = DiscordWebhook(
    webhookUrl = "https://discord.com/api/webhooks/your_webhook_id/your_webhook_token",
    pluginInstance = this // Reference from your plugin's main class
)

webhook
    .setUsername("SkyblockBot")
    .setAvatarUrl("https://i.imgur.com/example.png")
    .setContent("A new player has joined the server!")
    .addEmbed {
        title = "Player Joined"
        description = "SwadeDev has joined the server!"
        color = 0x00ff00
        footer = DiscordEmbed.Footer("Nukkit Server", null)
        addField("Player Name", "SwadeDev", inline = true)
    }
    .send { success, message ->
        if (success) {
            plugin.logger.info("Webhook sent successfully!")
        } else {
            plugin.logger.warning("Failed to send webhook: $message")
        }
    }
```