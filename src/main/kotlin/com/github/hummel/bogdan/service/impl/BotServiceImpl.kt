package com.github.hummel.bogdan.service.impl

import com.github.hummel.bogdan.bean.BotData
import com.github.hummel.bogdan.factory.ServiceFactory
import com.github.hummel.bogdan.integration.getVeniceInteractionResult
import com.github.hummel.bogdan.service.BotService
import com.github.hummel.bogdan.service.DataService
import com.github.hummel.bogdan.utils.I18n
import com.github.hummel.bogdan.utils.build
import com.github.hummel.bogdan.utils.error
import com.github.hummel.bogdan.utils.prepromptTemplate
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.time.LocalDate
import kotlin.random.Random

class BotServiceImpl : BotService {
	private val dataService: DataService = ServiceFactory.dataService

	override fun saveMessage(event: MessageReceivedEvent) {
		val guild = event.guild
		val guildData = dataService.loadGuildData(guild)
		val channelId = event.channel.idLong

		BotData.channelHistories.putIfAbsent(channelId, mutableListOf())
		val channelHistory = BotData.channelHistories[channelId]!!

		val message = event.message.contentRaw.replace("\r", " ").replace("\n", " ").replace("  ", " ")
		val author = event.message.author

		channelHistory.add(message)
		if (channelHistory.size >= 10) {
			channelHistory.removeAt(0)
		}

		if (guildData.secretChannelIds.any { it == channelId }) {
			return
		}

		if (suitableForBank(author, message, guildData.name)) {
			dataService.saveMessage(guild, message)
		}
	}

	override fun addRandomEmoji(event: MessageReceivedEvent) {
		if (event.author.isBot) {
			return
		}

		val guild = event.guild
		val guildData = dataService.loadGuildData(guild)

		if (Random.nextInt(100) < guildData.chanceEmoji) {
			if (Random.nextInt(100) != 0) {
				event.message.addReaction(guild.emojis.random()).queue()
			} else {
				val emojis = listOf(
					"🇳", "🇦", "🇪", "🇧", "🅰️", "🇱", "🇴", "🇻", "🅾️"
				)

				emojis.forEach { emojiUnicode ->
					event.message.addReaction(Emoji.fromUnicode(emojiUnicode)).queue()
				}
			}
		}
	}

	override fun sendRandomMessage(event: MessageReceivedEvent) {
		if (event.jda.selfUser.idLong == event.message.author.idLong) {
			return
		}

		val guild = event.guild
		val guildData = dataService.loadGuildData(guild)
		val channelId = event.channel.idLong

		if (guildData.mutedChannelIds.any { it == channelId }) {
			return
		}

		val aiRule1 = hasBotMention(event.message.contentRaw, guildData.name) && guildData.chanceAI != -1
		val aiRule2 = Random.nextInt(100) < guildData.chanceMessage && Random.nextInt(100) < guildData.chanceAI

		// DEFAULT
		if (!aiRule1 && !aiRule2) {
			if (Random.nextInt(100) < guildData.chanceMessage) {
				val message = dataService.getMessage(guild)
				message?.let {
					event.channel.sendMessage(it).queue()
				}
			}
			return
		}

		// AI
		val channelHistory = BotData.channelHistories.getOrDefault(channelId, null) ?: return
		val prompt = channelHistory.joinToString(
			prefix = prepromptTemplate.build(guildData.name, guildData.preprompt), separator = "\n"
		)
		val (data, error) = getVeniceInteractionResult(prompt)
		data?.let {
			if (it.length > 2000) {
				val embed = EmbedBuilder().error(
					event.member, guildData, I18n.of("msg_error_long", guildData)
				)
				event.channel.sendMessageEmbeds(embed).queue()
			} else {
				event.channel.sendMessage(it).queue()
			}
		} ?: run {
			val embed = EmbedBuilder().error(
				event.member, guildData, I18n.of("msg_error_http", guildData).format(error)
			)
			event.channel.sendMessageEmbeds(embed).queue()
		}

		return
	}

	override fun sendBirthdayMessage(event: MessageReceivedEvent) {
		val guild = event.guild
		val guildData = dataService.loadGuildData(guild)

		val today = LocalDate.now()
		val todayDay = today.dayOfMonth
		val todayMonth = today.monthValue

		val birthdayMemberIds = guildData.birthdays.filter {
			it.date.day == todayDay && it.date.month == todayMonth
		}.map {
			it.id
		}

		if (birthdayMemberIds.isNotEmpty() && (guildData.lastWish.day != todayDay || guildData.lastWish.month != todayMonth)) {
			birthdayMemberIds.forEach { memberId ->
				event.channel.sendMessage(I18n.of("happy_birthday", guildData).format(memberId)).queue()
			}

			guildData.lastWish.day = todayDay
			guildData.lastWish.month = todayMonth

			dataService.saveGuildData(guild, guildData)
		}
	}

	private fun suitableForBank(author: User, message: String, botName: String): Boolean {
		val contain = setOf("@", "https://", "http://", "gopher://")
		val start = setOf("!", "?", "/", botName, botName.lowercase(), botName.uppercase())

		if (message.length !in 2..400) {
			return false
		}
		if (author.isBot) {
			return false
		}

		return start.none {
			message.startsWith(it)
		} && contain.none {
			message.contains(it)
		}
	}

	private fun hasBotMention(message: String, botName: String): Boolean {
		val start = setOf("$botName,", "${botName.lowercase()},", "${botName.uppercase()},")

		return start.any {
			message.startsWith(it)
		}
	}
}