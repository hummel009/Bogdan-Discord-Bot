package com.github.hummel.mdb.core.service.impl

import com.github.hummel.mdb.core.bean.BotData
import com.github.hummel.mdb.core.factory.ServiceFactory
import com.github.hummel.mdb.core.integration.getPorfirevichInteractionResult
import com.github.hummel.mdb.core.service.DataService
import com.github.hummel.mdb.core.service.UserService
import com.github.hummel.mdb.core.utils.I18n
import com.github.hummel.mdb.core.utils.error
import com.github.hummel.mdb.core.utils.success
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.event.interaction.InteractionCreateEvent
import java.time.Month

class UserServiceImpl : UserService {
	private val dataService: DataService = ServiceFactory.dataService

	override fun info(event: InteractionCreateEvent) {
		val sc = event.slashCommandInteraction.get()

		if (sc.fullCommandName == "info") {
			sc.respondLater().thenAccept {
				val server = sc.server.get()
				val serverData = dataService.loadServerData(server)

				serverData.birthdays.removeIf { !server.getMemberById(it.id).isPresent }
				val text = buildString {
					val langName = I18n.of(serverData.lang, serverData)
					append(I18n.of("current_language", serverData).format(langName), "\r\n")
					append(I18n.of("current_chance_message", serverData).format(serverData.chanceMessage), "\r\n")
					append(I18n.of("current_chance_emoji", serverData).format(serverData.chanceEmoji), "\r\n")
					append(I18n.of("current_chance_ai", serverData).format(serverData.chanceAI), "\r\n")
					if (serverData.birthdays.isEmpty()) {
						append("\r\n", I18n.of("no_birthdays", serverData), "\r\n")
					} else {
						append("\r\n", I18n.of("has_birthdays", serverData), "\r\n")
						serverData.birthdays.sortedWith(
							compareBy({ it.date.month }, { it.date.day })
						).joinTo(this, "\r\n") {
							val userId = it.id
							val month = Month.of(it.date.month)
							val day = it.date.day
							val date = I18n.of(month.name.lowercase(), serverData).format(day)

							I18n.of("birthday", serverData).format(userId, date)
						}
						append("\r\n")
					}
					if (serverData.managers.isEmpty()) {
						append("\r\n", I18n.of("no_managers", serverData), "\r\n")
					} else {
						append("\r\n", I18n.of("has_managers", serverData), "\r\n")
						serverData.managers.sortedWith(compareBy { it.id }).joinTo(this, "\r\n") {
							val userId = it.id

							I18n.of("manager", serverData).format(userId)
						}
						append("\r\n")
					}
					if (serverData.secretChannels.isEmpty()) {
						append("\r\n", I18n.of("no_secret_channels", serverData), "\r\n")
					} else {
						append("\r\n", I18n.of("has_secret_channels", serverData), "\r\n")
						serverData.secretChannels.sortedWith(compareBy { it.id }).joinTo(this, "\r\n") {
							val channelId = it.id

							I18n.of("secret_channel", serverData).format(channelId)
						}
						append("\r\n")
					}
					if (serverData.mutedChannels.isEmpty()) {
						append("\r\n", I18n.of("no_muted_channels", serverData), "\r\n")
					} else {
						append("\r\n", I18n.of("has_muted_channels", serverData), "\r\n")
						serverData.mutedChannels.sortedWith(compareBy { it.id }).joinTo(this, "\r\n") {
							val channelId = it.id

							I18n.of("muted_channel", serverData).format(channelId)
						}
						append("\r\n")
					}
					append("\r\n", I18n.of("current_name", serverData).format(serverData.name), "\r\n")
					append("\r\n", I18n.of("current_preprompt", serverData).format(serverData.preprompt), "\r\n")
				}
				val embed = EmbedBuilder().success(sc.user, serverData, text)
				sc.createFollowupMessageBuilder().addEmbed(embed).send().get()

				dataService.saveServerData(sc.server.get(), serverData)
			}.get()
		}
	}

	override fun complete(event: InteractionCreateEvent) {
		val sc = event.slashCommandInteraction.get()

		if (sc.fullCommandName == "complete") {
			sc.respondLater().thenAccept {
				val server = sc.server.get()
				val serverData = dataService.loadServerData(server)

				val prompt = sc.arguments[0].stringValue.get()
				val embed = if (prompt.isNotEmpty()) {
					val (data, error) = getPorfirevichInteractionResult(prompt)
					data?.let {
						EmbedBuilder().success(sc.user, serverData, it)
					} ?: run {
						EmbedBuilder().error(
							sc.user, serverData, I18n.of("site_error", serverData).format(error)
						)
					}
				} else {
					EmbedBuilder().error(
						sc.user, serverData, I18n.of("invalid_arg", serverData)
					)
				}
				sc.createFollowupMessageBuilder().addEmbed(embed).send().get()
			}.get()
		}
	}

	override fun clearContext(event: InteractionCreateEvent) {
		val sc = event.slashCommandInteraction.get()

		if (sc.fullCommandName == "clear_context") {
			sc.respondLater().thenAccept {
				val server = sc.server.get()
				val serverData = dataService.loadServerData(server)

				val channelId = sc.channel.get().id
				BotData.channelHistories[channelId] = mutableListOf()

				val embed = EmbedBuilder().success(sc.user, serverData, I18n.of("cleared_context", serverData))

				sc.createFollowupMessageBuilder().addEmbed(embed).send().get()
			}.get()
		}
	}
}