package com.github.hummel.mdb.core.service.impl

import com.github.hummel.mdb.core.bean.BotData
import com.github.hummel.mdb.core.factory.ServiceFactory
import com.github.hummel.mdb.core.service.AccessService
import com.github.hummel.mdb.core.service.DataService
import com.github.hummel.mdb.core.service.OwnerService
import com.github.hummel.mdb.core.utils.I18n
import com.github.hummel.mdb.core.utils.access
import com.github.hummel.mdb.core.utils.error
import com.github.hummel.mdb.core.utils.success
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.utils.FileProxy
import net.dv8tion.jda.api.utils.FileUpload

class OwnerServiceImpl : OwnerService {
	private val dataService: DataService = ServiceFactory.dataService
	private val accessService: AccessService = ServiceFactory.accessService

	override fun import(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "import") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			if (!accessService.fromOwnerAtLeast(event)) {
				val embed = EmbedBuilder().access(event.member, guildData, I18n.of("no_access", guildData))

				event.hook.sendMessageEmbeds(embed).queue()
			} else {
				val embed = try {
					val attachment = event.getOption("arguments")?.asAttachment ?: throw Exception()
					val byteArray = FileProxy(attachment.url).download().join().readBytes()

					dataService.importBotData(byteArray)

					EmbedBuilder().success(event.member, guildData, I18n.of("import", guildData))
				} catch (_: Exception) {
					EmbedBuilder().error(event.member, guildData, I18n.of("invalid_format", guildData))
				}
				event.hook.sendMessageEmbeds(embed).queue()
			}
		}
	}

	override fun export(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "export") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			if (!accessService.fromOwnerAtLeast(event)) {
				val embed = EmbedBuilder().access(event.member, guildData, I18n.of("no_access", guildData))

				event.hook.sendMessageEmbeds(embed).queue()
			} else {
				try {
					val byteArray = dataService.exportBotData()

					event.hook.sendFiles(FileUpload.fromData(byteArray, "bot.zip")).queue()
				} catch (_: Exception) {
					val embed = EmbedBuilder().error(event.member, guildData, I18n.of("invalid_format", guildData))

					event.hook.sendMessageEmbeds(embed).queue()
				}
			}
		}
	}

	override fun exit(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "exit") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			if (!accessService.fromOwnerAtLeast(event)) {
				val embed = EmbedBuilder().access(event.member, guildData, I18n.of("no_access", guildData))

				event.hook.sendMessageEmbeds(embed).queue()
			} else {
				val embed = EmbedBuilder().success(event.member, guildData, I18n.of("exit", guildData))

				event.hook.sendMessageEmbeds(embed).queue {
					BotData.exitFunction.invoke()
				}
			}
		}
	}
}