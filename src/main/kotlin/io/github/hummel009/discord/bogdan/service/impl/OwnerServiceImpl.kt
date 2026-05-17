package io.github.hummel009.discord.bogdan.service.impl

import io.github.hummel009.discord.bogdan.factory.ServiceFactory
import io.github.hummel009.discord.bogdan.service.AccessService
import io.github.hummel009.discord.bogdan.service.DataService
import io.github.hummel009.discord.bogdan.service.OwnerService
import io.github.hummel009.discord.bogdan.utils.I18n
import io.github.hummel009.discord.bogdan.utils.error
import io.github.hummel009.discord.bogdan.utils.success
import net.dv8tion.jda.api.EmbedBuilder
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

				val embed = EmbedBuilder().success(event.member, I18n.of("import", guildData))

				event.hook.sendMessageEmbeds(embed).queue()
			} catch (_: Exception) {
				val embed = EmbedBuilder().error(event.member, I18n.of("msg_error_format", guildData))

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

			try {
				val byteArray = dataService.exportBotData()

				event.hook.sendFiles(FileUpload.fromData(byteArray, "bot.zip")).queue()
			} catch (_: Exception) {
				val embed = EmbedBuilder().error(event.member, I18n.of("msg_error_format", guildData))

				event.hook.sendMessageEmbeds(embed).queue()
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

			accessService.ownerAccessRestricted(event, guildData)?.let {
				return@queue
			}

			val embed = EmbedBuilder().success(event.member, I18n.of("exit", guildData))

			event.hook.sendMessageEmbeds(embed).queue { exitProcess(0) }
		}
	}
}