package com.github.hummel.bogdan.service

import net.dv8tion.jda.api.events.message.MessageReceivedEvent

interface BotService {
	fun saveMessage(event: MessageReceivedEvent)

	fun addRandomEmoji(event: MessageReceivedEvent)
	fun sendRandomMessage(event: MessageReceivedEvent)

	fun sendBirthdayMessage(event: MessageReceivedEvent)
}