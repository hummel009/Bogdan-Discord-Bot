package io.github.hummel009.discord.bogdan.service.impl

import io.github.hummel009.discord.bogdan.factory.ServiceFactory
import io.github.hummel009.discord.bogdan.integration.getPorfirevichInteractionResult
import io.github.hummel009.discord.bogdan.service.DataService
import io.github.hummel009.discord.bogdan.service.MemberService
import io.github.hummel009.discord.bogdan.utils.I18n
import io.github.hummel009.discord.bogdan.utils.error
import io.github.hummel009.discord.bogdan.utils.success
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
				val langName = I18n.of(guildData.lang.code, guildData)
				append(I18n.of("info_language", guildData, langName), "\n")
				append(I18n.of("info_chance_message", guildData, guildData.chanceMessage), "\n")
				append(I18n.of("info_chance_emoji", guildData, guildData.chanceEmoji), "\n")
				append(I18n.of("info_chance_ai", guildData, guildData.chanceAI), "\n")

				if (guildData.managerRoleIds.isEmpty()) {
					append("\n", I18n.of("no_manager_roles", guildData), "\n")
				} else {
					append("\n", I18n.of("has_manager_roles", guildData), "\n")
					guildData.managerRoleIds.joinTo(this, "\n") {
						I18n.of("manager_role", guildData, it).s()
					}
					append("\n")
				}

				if (guildData.excludedChannelIds.isEmpty()) {
					append("\n", I18n.of("no_excluded_channels", guildData), "\n")
				} else {
					append("\n", I18n.of("has_excluded_channels", guildData), "\n")
					guildData.excludedChannelIds.joinTo(this, "\n") {
						I18n.of("excluded_channel", guildData, it).s()
					}
					append("\n")
				}

				if (guildData.birthdays.isEmpty()) {
					append("\n", I18n.of("no_birthdays", guildData), "\n")
				} else {
					append("\n", I18n.of("has_birthdays", guildData), "\n")
					guildData.birthdays.sortedWith(
						compareBy({ it.date.month }, { it.date.day })
					).joinTo(this, "\n") { (memberId, date) ->
						val month = Month.of(date.month)
						val day = date.day
						val numericDate = "%02d.%02d".format(day, date.month)
						val date = "${I18n.of(month.name.lowercase(), guildData, day)} ($numericDate)"

						I18n.of("birthday", guildData, memberId, date).s()
					}
					append("\n")
				}

				append("\n", I18n.of("info_name", guildData, guildData.name), "\n")
				append("\n", I18n.of("info_preprompt", guildData, guildData.preprompt), "\n")
			}
			dataService.saveGuildData(guild, guildData)

			val embed = EmbedBuilder().success(event.member, I18n.new(text, guildData))

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
					EmbedBuilder().success(event.member, I18n.new(it, guildData))
				} ?: run {
					EmbedBuilder().error(
						event.member, I18n.of("msg_error_http", guildData, error)
					)
				}
			} catch (_: Exception) {
				EmbedBuilder().error(event.member, I18n.of("msg_error_format", guildData))
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

			dataService.setContextForChannel(channelId, mutableListOf())

			val embed = EmbedBuilder().success(event.member, I18n.of("clear_context", guildData))

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}
}