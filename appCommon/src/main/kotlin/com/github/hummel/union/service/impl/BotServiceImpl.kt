package com.github.hummel.union.service.impl

import com.github.hummel.union.bean.BotData
import com.github.hummel.union.bean.ServerData
import com.github.hummel.union.factory.ServiceFactory
import com.github.hummel.union.integration.getDuckGptLiveResponse
import com.github.hummel.union.lang.I18n
import com.github.hummel.union.service.BotService
import com.github.hummel.union.service.DataService
import com.github.hummel.union.utils.build
import com.github.hummel.union.utils.error
import com.github.hummel.union.utils.prepromptTemplate
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.event.message.MessageCreateEvent
import java.time.LocalDate
import kotlin.random.Random

class BotServiceImpl : BotService {
	private val dataService: DataService = ServiceFactory.dataService

	override fun addRandomEmoji(event: MessageCreateEvent) {
		if (event.messageAuthor.isYourself) {
			return
		}

		val server = event.server.get()
		val serverData = dataService.loadServerData(server)

		if (Random.nextInt(100) < serverData.chanceEmoji) {
			val emoji = event.server.get().customEmojis.random()
			event.addReactionToMessage(emoji)
		}
	}

	override fun saveMessage(event: MessageCreateEvent) {
		val channelId = event.channel.id
		val msg = event.messageContent.replace("\r", " ").replace("\n", " ").replace("  ", " ")

		BotData.channelHistories.putIfAbsent(channelId, mutableListOf())
		val channelHistory = BotData.channelHistories[channelId] ?: return

		channelHistory.add(msg)
		if (channelHistory.size >= 10) {
			channelHistory.removeAt(0)
		}

		val server = event.server.get()
		val serverData = dataService.loadServerData(server)

		if (event.messageCanBeSaved(serverData.name)) {
			if (!serverData.secretChannels.any { it.id == channelId }) {
				val crypt = encodeMessage(msg)
				dataService.saveServerMessage(server, crypt)
			}
		}
	}

	override fun sendRandomMessage(event: MessageCreateEvent) {
		if (event.messageAuthor.isYourself) {
			return
		}

		val server = event.server.get()
		val serverData = dataService.loadServerData(server)
		val channelId = event.channel.id

		if (serverData.mutedChannels.any { it.id == channelId }) {
			return
		}

		if (event.messageHasBotMention(serverData.name) || Random.nextInt(100) < serverData.chanceMessage) {
			val channelHistory = BotData.channelHistories.getOrDefault(channelId, null)

			if ((event.messageHasBotMention(serverData.name) || Random.nextInt(100) < serverData.chanceAI) && channelHistory != null) {
				val prompt = channelHistory.joinToString(
					prefix = prepromptTemplate.build(serverData.name, serverData.preprompt), separator = "\n"
				)

				val (status, response) = getDuckGptLiveResponse(prompt)
				response?.let {
					if (it.length > 2000) {
						val embed = EmbedBuilder().error(
							event.messageAuthor.asUser().get(), serverData, I18n.of("long_message", serverData)
						)
						event.channel.sendMessage(embed)
					} else {
						event.channel.sendMessage(it)
					}
				} ?: run {
					val embed = EmbedBuilder().error(
						event.messageAuthor.asUser().get(),
						serverData,
						I18n.of("site_error", serverData).format(status.first, status.second)
					)
					event.channel.sendMessage(embed)
				}
			} else {
				val crypt = dataService.getServerRandomMessage(server)
				crypt?.let {
					val msg = decodeMessage(it)
					event.channel.sendMessage(msg)
				}
			}
		}
	}

	override fun sendBirthdayMessage(event: MessageCreateEvent) {
		val server = event.server.get()
		val serverData = dataService.loadServerData(server)

		val currentDate = LocalDate.now()
		val currentDay = currentDate.dayOfMonth
		val currentMonth = currentDate.monthValue

		val (isBirthday, userIds) = isBirthdayToday(serverData)

		if (isBirthday && (currentDay != serverData.lastWish.day || currentMonth != serverData.lastWish.month)) {
			userIds.forEach { event.channel.sendMessage(I18n.of("happy_birthday", serverData).format(it)) }
			serverData.lastWish.day = currentDay
			serverData.lastWish.month = currentMonth
			dataService.saveServerData(server, serverData)
		}
	}

	private fun encodeMessage(msg: String): String = msg.codePoints().toArray().joinToString(" ")

	private fun decodeMessage(msg: String): String {
		val unicodeCodes = msg.split(" ").map { it.toInt() }
		val unicodeChars = unicodeCodes.map { it.toChar() }.toCharArray()
		return String(unicodeChars)
	}

	private fun isBirthdayToday(serverData: ServerData): Pair<Boolean, Set<Long>> {
		val currentDate = LocalDate.now()
		val currentDay = currentDate.dayOfMonth
		val currentMonth = currentDate.monthValue
		val userIds = HashSet<Long>()
		var isBirthday = false

		for ((userId, date) in serverData.birthdays) {
			if (date.day == currentDay && date.month == currentMonth) {
				isBirthday = true
				userIds.add(userId)
			}
		}
		return isBirthday to userIds
	}

	private fun MessageCreateEvent.messageCanBeSaved(name: String): Boolean {
		val contain = setOf("@", "https://", "http://", "gopher://")
		val start = setOf("!", "?", "/", name, name.lowercase(), name.lowercase())

		if (messageContent.length !in 2..445) {
			return false
		}
		if (messageAuthor.isYourself) {
			return false
		}

		return start.none {
			messageContent.startsWith(it)
		} && contain.none {
			messageContent.contains(it)
		}
	}

	private fun MessageCreateEvent.messageHasBotMention(name: String): Boolean {
		val start = setOf("$name,", "${name.lowercase()},", "${name.uppercase()},")

		return start.any {
			messageContent.startsWith(it)
		}
	}
}