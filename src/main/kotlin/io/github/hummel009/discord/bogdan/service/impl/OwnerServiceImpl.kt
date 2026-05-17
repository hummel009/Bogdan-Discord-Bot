package io.github.hummel009.discord.bogdan.service.impl

import io.github.hummel009.discord.bogdan.factory.ServiceFactory
import io.github.hummel009.discord.bogdan.service.AccessService
import io.github.hummel009.discord.bogdan.service.DataService
import io.github.hummel009.discord.bogdan.service.OwnerService
import io.github.hummel009.discord.bogdan.utils.I18n
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.utils.FileUpload
import java.net.URI
import kotlin.system.exitProcess

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

			accessService.ownerAccessRestricted(event, guildData)?.let {
				return@queue
			}

			try {
				val attachment = requireNotNull(event.getOption("arguments")?.asAttachment)
				val byteArray = URI(attachment.proxyUrl).toURL().readBytes()

				dataService.importBotData(byteArray)

				val embed = I18n.of("import", guildData).asSuccess(event.member)

				event.hook.sendMessageEmbeds(embed).queue()
			} catch (_: Exception) {
				val embed = I18n.of("msg_error_format", guildData).asError(event.member)

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

			accessService.ownerAccessRestricted(event, guildData)?.let {
				return@queue
			}

			val byteArray = dataService.exportBotData()

			event.hook.sendFiles(FileUpload.fromData(byteArray, "bot.zip")).queue()
		}
	}

	override fun exit(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "exit") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			accessService.ownerAccessRestricted(event, guildData)?.let {
				return@queue
			}

			val embed = I18n.of("exit", guildData).asSuccess(event.member)

			event.hook.sendMessageEmbeds(embed).queue { exitProcess(0) }
		}
	}
}