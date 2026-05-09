package io.github.hummel009.discord.bogdan.service.impl

import io.github.hummel009.discord.bogdan.factory.ServiceFactory
import io.github.hummel009.discord.bogdan.integration.getGlobalSupportInteractionResult
import io.github.hummel009.discord.bogdan.service.BotService
import io.github.hummel009.discord.bogdan.service.DataService
import io.github.hummel009.discord.bogdan.utils.I18n
import io.github.hummel009.discord.bogdan.utils.error
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.User
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

		val author = event.message.author
		val message = event.message.contentRaw

		val context = dataService.getContextForChannel(channelId) ?: mutableListOf()
		context.add(message)
		if (context.size >= 10) {
			context.removeAt(0)
		}
		dataService.setContextForChannel(channelId, context)

		if (message.length !in 2..445) {
			return
		}

		if (guildData.excludedChannelIds.any { it == channelId }) {
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
		fun ai(guild: Guild, channelId: Long) {
			val guildData = dataService.loadGuildData(guild)

			val context = dataService.getContextForChannel(channelId) ?: return
			val prompt = context.joinToString(
				prefix = I18n.of("preprompt_template", guildData, guildData.name, guildData.preprompt).s(),
				separator = "\n— "
			)

			val (data, error) = getGlobalSupportInteractionResult(prompt)

			data?.let {
				if (it.length > 2000) {
					val embed = EmbedBuilder().error(
						event.member, I18n.of("msg_error_long", guildData)
					)
					event.channel.sendMessageEmbeds(embed).queue()
				} else {
					event.channel.sendMessage(it).queue()
				}
			} ?: run {
				val embed = EmbedBuilder().error(
					event.member, I18n.of("msg_error_http", guildData, error)
				)
				event.channel.sendMessageEmbeds(embed).queue()
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

		val summonRule = hasBotMention(event.message.contentRaw, guildData.name)
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

	private fun suitableForBank(author: User, message: String, botName: String): Boolean {
		val message = message.lowercase()

		val contain = setOf("@", "http://", "https://", "gopher://", ".gif")
		val start = setOf("!", "?", "/")

		if (author.isBot) {
			return false
		}

		return start.none {
			message.startsWith(it)
		} && contain.none {
			message.contains(it)
		} && !hasBotMention(message, botName)
	}

	private fun hasBotMention(message: String, botName: String): Boolean {
		val message = message.lowercase()
		val botName = botName.lowercase()

		val startRule = message.startsWith("$botName,")
		val endRule1 = message.endsWith(", $botName")
		val endRule2 = message.endsWith(", $botName.")
		val endRule3 = message.endsWith(", $botName!")
		val endRule4 = message.endsWith(", $botName?")

		return startRule || endRule1 || endRule2 || endRule3 || endRule4
	}
}