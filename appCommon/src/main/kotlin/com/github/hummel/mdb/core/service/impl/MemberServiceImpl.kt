package com.github.hummel.mdb.core.service.impl

import com.github.hummel.mdb.core.bean.BotData
import com.github.hummel.mdb.core.factory.ServiceFactory
import com.github.hummel.mdb.core.integration.getPorfirevichInteractionResult
import com.github.hummel.mdb.core.service.DataService
import com.github.hummel.mdb.core.service.MemberService
import com.github.hummel.mdb.core.utils.I18n
import com.github.hummel.mdb.core.utils.error
import com.github.hummel.mdb.core.utils.success
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import java.time.Month

class MemberServiceImpl : MemberService {
	private val dataService: DataService = ServiceFactory.dataService

	override fun info(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "info") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			guildData.birthdays.removeIf { guild.getMemberById(it.id) == null }
			guildData.managers.removeIf { guild.getRoleById(it.id) == null }
			guildData.secretChannels.removeIf { guild.getTextChannelById(it.id) == null }
			guildData.mutedChannels.removeIf { guild.getTextChannelById(it.id) == null }

			val text = buildString {
				val langName = I18n.of(guildData.lang, guildData)
				append(I18n.of("current_language", guildData).format(langName), "\r\n")
				append(I18n.of("current_chance_message", guildData).format(guildData.chanceMessage), "\r\n")
				append(I18n.of("current_chance_emoji", guildData).format(guildData.chanceEmoji), "\r\n")
				append(I18n.of("current_chance_ai", guildData).format(guildData.chanceAI), "\r\n")
				if (guildData.birthdays.isEmpty()) {
					append("\r\n", I18n.of("no_birthdays", guildData), "\r\n")
				} else {
					append("\r\n", I18n.of("has_birthdays", guildData), "\r\n")
					guildData.birthdays.sortedWith(
						compareBy({
							it.date.month
						}, {
							it.date.day
						})
					).joinTo(this, "\r\n") {
						val memberId = it.id
						val month = Month.of(it.date.month)
						val day = it.date.day
						val date = I18n.of(month.name.lowercase(), guildData).format(day)

						I18n.of("birthday", guildData).format(memberId, date)
					}
					append("\r\n")
				}
				if (guildData.managers.isEmpty()) {
					append("\r\n", I18n.of("no_managers", guildData), "\r\n")
				} else {
					append("\r\n", I18n.of("has_managers", guildData), "\r\n")
					guildData.managers.sortedWith(compareBy {
						it.id
					}).joinTo(this, "\r\n") {
						I18n.of("manager", guildData).format(it.id)
					}
					append("\r\n")
				}
				if (guildData.secretChannels.isEmpty()) {
					append("\r\n", I18n.of("no_secret_channels", guildData), "\r\n")
				} else {
					append("\r\n", I18n.of("has_secret_channels", guildData), "\r\n")
					guildData.secretChannels.sortedWith(compareBy {
						it.id
					}).joinTo(this, "\r\n") {
						I18n.of("secret_channel", guildData).format(it.id)
					}
					append("\r\n")
				}
				if (guildData.mutedChannels.isEmpty()) {
					append("\r\n", I18n.of("no_muted_channels", guildData), "\r\n")
				} else {
					append("\r\n", I18n.of("has_muted_channels", guildData), "\r\n")
					guildData.mutedChannels.sortedWith(compareBy {
						it.id
					}).joinTo(this, "\r\n") {
						I18n.of("muted_channel", guildData).format(it.id)
					}
					append("\r\n")
				}
				append("\r\n", I18n.of("current_name", guildData).format(guildData.name), "\r\n")
				append("\r\n", I18n.of("current_preprompt", guildData).format(guildData.preprompt), "\r\n")
			}
			dataService.saveGuildData(guild, guildData)

			val embed = EmbedBuilder().success(event.member, guildData, text)

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}

	override fun complete(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "complete") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)
			val embed = try {
				val arguments = event.getOption("arguments")?.asString ?: throw Exception()
				val prompt = arguments.trim()

				if (prompt.isEmpty()) {
					throw Exception()
				}

				val (data, error) = getPorfirevichInteractionResult(prompt)
				data?.let {
					EmbedBuilder().success(event.member, guildData, it)
				} ?: run {
					EmbedBuilder().error(
						event.member, guildData, I18n.of("site_error", guildData).format(error)
					)
				}
			} catch (_: Exception) {
				EmbedBuilder().error(event.member, guildData, I18n.of("invalid_arg", guildData))
			}

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}

	override fun clearContext(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "clear_context") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)
			val channelId = event.channel.idLong

			BotData.channelHistories[channelId] = mutableListOf()

			val embed = EmbedBuilder().success(event.member, guildData, I18n.of("cleared_context", guildData))

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}
}