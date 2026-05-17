package io.github.hummel009.discord.bogdan.service.impl

import io.github.hummel009.discord.bogdan.bean.GuildData
import io.github.hummel009.discord.bogdan.factory.ServiceFactory
import io.github.hummel009.discord.bogdan.integration.getGlobalSupportInteractionResult
import io.github.hummel009.discord.bogdan.service.BotService
import io.github.hummel009.discord.bogdan.service.DataService
import io.github.hummel009.discord.bogdan.utils.I18n
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.random.Random

class BotServiceImpl : BotService {
	private val dataService: DataService = ServiceFactory.dataService

	override fun saveMessage(event: MessageReceivedEvent) {
		val guild = event.guild
		val guildData = dataService.loadGuildData(guild)

		val channelId = event.channel.idLong

		val context = dataService.getContext(channelId) ?: mutableListOf()
		context.add(event.message.contentRaw)
		if (context.size >= 10) {
			context.removeAt(0)
		}
		dataService.setContext(channelId, context)

		if (guildData.excludedChannelIds.any { it == channelId }) {
			return
		}

		if (event.message.isSuitableForBank(guildData)) {
			dataService.saveMessage(guild, event.message.contentRaw)
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

				emojis.forEach {
					event.message.addReaction(Emoji.fromUnicode(it)).queue()
				}
			}
		}
	}

	override fun sendRandomMessage(event: MessageReceivedEvent) {
		fun ai(guild: Guild, channelId: Long) {
			val guildData = dataService.loadGuildData(guild)

			val context = dataService.getContext(channelId) ?: return
			val prompt = context.joinToString(
				prefix = I18n.of(
					"preprompt_template", guildData, guildData.name, guildData.name, guildData.preprompt
				).s(), separator = "\n— "
			)

			val (data, error) = getGlobalSupportInteractionResult(prompt)

			if (data == null) {
				val embed = I18n.of("msg_error_http", guildData, error).asError(event.member)
				event.channel.sendMessageEmbeds(embed).queue()
				return
			}

			data.split().forEach { part ->
				event.channel.sendMessage(part).queue()
			}
		}

		fun quote(guild: Guild) {
			val message = dataService.getMessage(guild)
			message?.let {
				event.channel.sendMessage(it).queue()
			}
		}

		if (event.jda.selfUser.idLong == event.message.author.idLong) {
			return
		}

		val guild = event.guild
		val guildData = dataService.loadGuildData(guild)
		val channelId = event.channel.idLong

		val chanceQuote = Random.nextInt(100)
		val chanceAi = Random.nextInt(100)

		val summonRule = event.message.hasBotMention(guildData)
		val spontaneousRule = chanceQuote < guildData.chanceMessage

		if (guildData.excludedChannelIds.any { it == channelId } && !summonRule) {
			return
		}

		if (summonRule) {
			if (guildData.chanceAI != -1) {
				ai(guild, channelId)
			}
		} else if (spontaneousRule) {
			if (guildData.chanceAI > chanceAi) {
				ai(guild, channelId)
			} else {
				quote(guild)
			}
		}
	}

	override fun sendBirthdayMessage(event: MessageReceivedEvent) {
		val guild = event.guild
		val guildData = dataService.loadGuildData(guild)

		val mskZone = ZoneId.of("Europe/Moscow")
		val today = ZonedDateTime.now(mskZone)
		val todayDay = today.dayOfMonth
		val todayMonth = today.monthValue

		val birthdayMemberIds = guildData.birthdays.filter {
			it.date.day == todayDay && it.date.month == todayMonth
		}.map {
			it.memberId
		}

		if (birthdayMemberIds.isNotEmpty() && (guildData.lastWish.day != todayDay || guildData.lastWish.month != todayMonth)) {
			birthdayMemberIds.forEach { memberId ->
				event.channel.sendMessage(I18n.of("happy_birthday", guildData, memberId).s()).queue()
			}

			guildData.lastWish.day = todayDay
			guildData.lastWish.month = todayMonth

			dataService.saveGuildData(guild, guildData)
		}
	}

	private fun Message.isSuitableForBank(guildData: GuildData): Boolean {
		if (contentRaw.length !in 2..445) {
			return false
		}

		if (author.isBot) {
			return false
		}

		val message = contentRaw.lowercase()

		val contain = setOf("@", "http://", "https://", "gopher://", ".gif")
		val start = setOf("!", "?", "/")

		return start.none {
			message.startsWith(it)
		} && contain.none {
			message.contains(it)
		} && !hasBotMention(guildData)
	}

	private fun Message.hasBotMention(guildData: GuildData): Boolean {
		val message = contentRaw.lowercase()
		val botName = guildData.name.lowercase()

		val startRule = message.startsWith("$botName,")
		val endRule1 = message.endsWith(", $botName")
		val endRule2 = message.endsWith(", $botName.")
		val endRule3 = message.endsWith(", $botName!")
		val endRule4 = message.endsWith(", $botName?")

		return startRule || endRule1 || endRule2 || endRule3 || endRule4
	}

	private fun String.split(): List<String> {
		if (length <= 1999) {
			return listOf(this)
		}

		val parts = mutableListOf<String>()
		var remaining = this

		while (remaining.length > 1999) {
			val splitIndex = findSplitIndex(remaining, 1999)
			parts.add(remaining.take(splitIndex))
			remaining = remaining.substring(splitIndex).trimStart()
		}

		if (remaining.isNotEmpty()) {
			parts.add(remaining)
		}

		return parts
	}

	private fun findSplitIndex(text: String, maxLength: Int): Int {
		val textToCheck = text.take(maxLength)

		val lastParagraph = textToCheck.lastIndexOf("\n\n")
		if (lastParagraph > 0 && lastParagraph < maxLength - 10) {
			return lastParagraph + 2
		}

		val lastDotSpace = textToCheck.lastIndexOf(". ")
		if (lastDotSpace > 0 && lastDotSpace < maxLength - 5) {
			return lastDotSpace + 2
		}

		val punctuationPattern = "[!?;:] ".toRegex()
		val match = punctuationPattern.findAll(textToCheck).lastOrNull { it.range.last < maxLength - 5 }
		if (match != null) {
			return match.range.last + 1
		}

		val lastSpace = textToCheck.lastIndexOf(' ')
		if (lastSpace > 0 && lastSpace < maxLength - 5) {
			return lastSpace + 1
		}

		return maxLength
	}
}