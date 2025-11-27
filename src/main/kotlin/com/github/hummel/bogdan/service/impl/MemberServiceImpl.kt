package com.github.hummel.bogdan.service.impl

import com.github.hummel.bogdan.bean.BotData
import com.github.hummel.bogdan.factory.ServiceFactory
import com.github.hummel.bogdan.integration.getPorfirevichInteractionResult
import com.github.hummel.bogdan.service.DataService
import com.github.hummel.bogdan.service.MemberService
import com.github.hummel.bogdan.utils.I18n
import com.github.hummel.bogdan.utils.error
import com.github.hummel.bogdan.utils.success
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

			guildData.birthdays.removeIf {
				guild.getMemberById(it.memberId) == null
			}
			guildData.managerRoleIds.removeIf {
				guild.getRoleById(it) == null
			}
			guildData.excludedChannelIds.removeIf {
				guild.getTextChannelById(it) == null && guild.getThreadChannelById(it) == null
			}
			guildData.excludedChannelIds.removeIf {
				guild.getTextChannelById(it) == null && guild.getThreadChannelById(it) == null
			}

			val text = buildString {
				val langName = I18n.of(guildData.lang, guildData)
				append(I18n.of("info_language", guildData).format(langName), "\r\n")
				append(I18n.of("info_chance_message", guildData).format(guildData.chanceMessage), "\r\n")
				append(I18n.of("info_chance_emoji", guildData).format(guildData.chanceEmoji), "\r\n")
				append(I18n.of("info_chance_ai", guildData).format(guildData.chanceAI), "\r\n")

				if (guildData.managerRoleIds.isEmpty()) {
					append("\r\n", I18n.of("no_manager_roles", guildData), "\r\n")
				} else {
					append("\r\n", I18n.of("has_manager_roles", guildData), "\r\n")
					guildData.managerRoleIds.joinTo(this, "\r\n") {
						I18n.of("manager_role", guildData).format(it)
					}
					append("\r\n")
				}

				if (guildData.excludedChannelIds.isEmpty()) {
					append("\r\n", I18n.of("no_excluded_channels", guildData), "\r\n")
				} else {
					append("\r\n", I18n.of("has_excluded_channels", guildData), "\r\n")
					guildData.excludedChannelIds.joinTo(this, "\r\n") {
						I18n.of("excluded_channel", guildData).format(it)
					}
					append("\r\n")
				}

				if (guildData.birthdays.isEmpty()) {
					append("\r\n", I18n.of("no_birthdays", guildData), "\r\n")
				} else {
					append("\r\n", I18n.of("has_birthdays", guildData), "\r\n")
					guildData.birthdays.sortedWith(
						compareBy({ it.date.month }, { it.date.day })
					).joinTo(this, "\r\n") { (memberId, date) ->
						val month = Month.of(date.month)
						val day = date.day
						val numericDate = "%02d.%02d".format(day, date.month)
						val dateFormatted = "${I18n.of(month.name.lowercase(), guildData).format(day)} ($numericDate)"

						I18n.of("birthday", guildData).format(memberId, dateFormatted)
					}
					append("\r\n")
				}

				append("\r\n", I18n.of("info_name", guildData).format(guildData.name), "\r\n")
				append("\r\n", I18n.of("info_preprompt", guildData).format(guildData.preprompt), "\r\n")
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
						event.member, guildData, I18n.of("msg_error_http", guildData).format(error)
					)
				}
			} catch (_: Exception) {
				EmbedBuilder().error(event.member, guildData, I18n.of("msg_error_format", guildData))
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

			val embed = EmbedBuilder().success(event.member, guildData, I18n.of("clear_context", guildData))

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}
}