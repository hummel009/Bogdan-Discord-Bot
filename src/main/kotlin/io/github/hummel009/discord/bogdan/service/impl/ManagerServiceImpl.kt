package io.github.hummel009.discord.bogdan.service.impl

import io.github.hummel009.discord.bogdan.bean.GuildData
import io.github.hummel009.discord.bogdan.factory.ServiceFactory
import io.github.hummel009.discord.bogdan.service.AccessService
import io.github.hummel009.discord.bogdan.service.DataService
import io.github.hummel009.discord.bogdan.service.ManagerService
import io.github.hummel009.discord.bogdan.utils.*
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import java.time.Month

class ManagerServiceImpl : ManagerService {
	private val dataService: DataService = ServiceFactory.dataService
	private val accessService: AccessService = ServiceFactory.accessService

	override fun setLanguage(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "set_language") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			val embed = run(fun(): MessageEmbed {
				if (!accessService.fromManagerAtLeast(event, guildData)) {
					return EmbedBuilder().access(event.member, I18n.of("msg_access", guildData))
				}

				val arguments = event.getOption("arguments")?.asString?.split(" ") ?: emptyList()
				if (arguments.size != 1) {
					return EmbedBuilder().error(event.member, I18n.of("msg_error_arg", guildData))
				}

				try {
					val lang = Lang.of(arguments[0]) ?: throw Exception()
					guildData.lang = lang

					val langName = I18n.of(lang.code, guildData)

					return EmbedBuilder().success(
						event.member, I18n.of("set_language", guildData, langName)
					)
				} catch (_: Exception) {
					return EmbedBuilder().error(event.member, I18n.of("msg_error_format", guildData))
				}
			})

			dataService.saveGuildData(guild, guildData)

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}

	override fun addManagerRole(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "add_manager_role") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			val embed = run(fun(): MessageEmbed {
				if (!accessService.fromManagerAtLeast(event, guildData)) {
					return EmbedBuilder().access(event.member, I18n.of("msg_access", guildData))
				}

				val arguments = event.getOption("arguments")?.asString?.split(" ") ?: emptyList()
				if (arguments.size != 1) {
					return EmbedBuilder().error(event.member, I18n.of("msg_error_arg", guildData))
				}

				try {
					val roleId = arguments[0].toLong().also {
						requireNotNull(guild.getRoleById(it))
					}

					guildData.managerRoleIds.add(roleId)

					return EmbedBuilder().success(
						event.member, I18n.of("add_manager_role", guildData, roleId)
					)
				} catch (_: Exception) {
					return EmbedBuilder().error(event.member, I18n.of("msg_error_format", guildData))
				}
			})

			dataService.saveGuildData(guild, guildData)

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}

	override fun clearManagerRoles(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "clear_manager_roles") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			val embed = run(fun(): MessageEmbed {
				if (!accessService.fromManagerAtLeast(event, guildData)) {
					return EmbedBuilder().access(event.member, I18n.of("msg_access", guildData))
				}

				val arguments = event.getOption("arguments")?.asString?.split(" ") ?: emptyList()
				if (arguments.isEmpty()) {
					guildData.managerRoleIds.clear()

					return EmbedBuilder().success(event.member, I18n.of("clear_manager_roles", guildData))
				}
				if (arguments.size != 1) {
					return EmbedBuilder().error(event.member, I18n.of("msg_error_arg", guildData))
				}

				try {
					val roleId = arguments[0].toLong().also {
						requireNotNull(guild.getRoleById(it))
					}

					require(guildData.managerRoleIds.removeIf { it == roleId })

					return EmbedBuilder().success(
						event.member, I18n.of("clear_manager_roles_single", guildData, roleId)
					)
				} catch (_: Exception) {
					return EmbedBuilder().error(event.member, I18n.of("msg_error_format", guildData))
				}
			})

			dataService.saveGuildData(guild, guildData)

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}

	override fun addExcludedChannel(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "add_excluded_channel") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			val embed = run(fun(): MessageEmbed {
				if (!accessService.fromManagerAtLeast(event, guildData)) {
					return EmbedBuilder().access(event.member, I18n.of("msg_access", guildData))
				}

				val arguments = event.getOption("arguments")?.asString?.split(" ") ?: emptyList()
				if (arguments.size != 1) {
					return EmbedBuilder().error(event.member, I18n.of("msg_error_arg", guildData))
				}

				try {
					val channelId = arguments[0].toLong().also {
						requireNotNull(guild.getMessageChannelById(it))
					}

					guildData.excludedChannelIds.add(channelId)

					return EmbedBuilder().success(
						event.member, I18n.of("add_excluded_channel", guildData, channelId)
					)
				} catch (_: Exception) {
					return EmbedBuilder().error(event.member, I18n.of("msg_error_format", guildData))
				}
			})

			dataService.saveGuildData(guild, guildData)

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}

	override fun clearExcludedChannels(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "clear_excluded_channels") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			val embed = run(fun(): MessageEmbed {
				if (!accessService.fromManagerAtLeast(event, guildData)) {
					return EmbedBuilder().access(event.member, I18n.of("msg_access", guildData))
				}

				val arguments = event.getOption("arguments")?.asString?.split(" ") ?: emptyList()
				if (arguments.isEmpty()) {
					guildData.excludedChannelIds.clear()

					return EmbedBuilder().success(event.member, I18n.of("clear_excluded_channels", guildData))
				}
				if (arguments.size != 1) {
					return EmbedBuilder().error(event.member, I18n.of("msg_error_arg", guildData))
				}

				try {
					val channelId = arguments[0].toLong().also {
						requireNotNull(guild.getMessageChannelById(it))
					}

					require(guildData.excludedChannelIds.removeIf { it == channelId })

					return EmbedBuilder().success(
						event.member, I18n.of("cleared_excluded_channels_single", guildData, channelId)
					)
				} catch (_: Exception) {
					return EmbedBuilder().error(event.member, I18n.of("msg_error_format", guildData))
				}
			})

			dataService.saveGuildData(guild, guildData)

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}

	override fun addBirthday(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "add_birthday") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			val embed = run(fun(): MessageEmbed {
				if (!accessService.fromManagerAtLeast(event, guildData)) {
					return EmbedBuilder().access(event.member, I18n.of("msg_access", guildData))
				}

				val arguments = event.getOption("arguments")?.asString?.split(" ") ?: emptyList()
				if (arguments.size != 3) {
					return EmbedBuilder().error(event.member, I18n.of("msg_error_arg", guildData))
				}

				try {
					val memberId = arguments[0].toLong().also {
						requireNotNull(guild.getMemberById(it))
					}
					val month = arguments[2].toInt().also {
						require(it in 1..12)
					}
					val day = arguments[1].toInt().also {
						require(it in 1..Month.of(month).length(true))
					}

					guildData.birthdays.add(GuildData.Birthday(memberId, GuildData.Date(day, month)))

					val langDate = I18n.of(Month.of(month).name.lowercase(), guildData, day)
					val numericDate = "%02d.%02d".format(day, month)
					val date = "$langDate ($numericDate)"

					return EmbedBuilder().success(
						event.member, I18n.of("add_birthday", guildData, memberId, date)
					)
				} catch (_: Exception) {
					return EmbedBuilder().error(event.member, I18n.of("msg_error_format", guildData))
				}
			})

			dataService.saveGuildData(guild, guildData)

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}

	override fun clearBirthdays(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "clear_birthdays") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			val embed = run(fun(): MessageEmbed {
				if (!accessService.fromManagerAtLeast(event, guildData)) {
					return EmbedBuilder().access(event.member, I18n.of("msg_access", guildData))
				}

				val arguments = event.getOption("arguments")?.asString?.split(" ") ?: emptyList()
				if (arguments.isEmpty()) {
					guildData.birthdays.clear()

					return EmbedBuilder().success(event.member, I18n.of("clear_birthdays", guildData))
				}
				if (arguments.size != 1) {
					return EmbedBuilder().error(event.member, I18n.of("msg_error_arg", guildData))
				}

				try {
					val memberId = arguments[0].toLong().also {
						requireNotNull(guild.getMemberById(it))
					}

					require(guildData.birthdays.removeIf { it.memberId == memberId })

					return EmbedBuilder().success(
						event.member, I18n.of("clear_birthdays_single", guildData, memberId)
					)
				} catch (_: Exception) {
					return EmbedBuilder().error(event.member, I18n.of("msg_error_format", guildData))
				}
			})

			dataService.saveGuildData(guild, guildData)

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}

	override fun setChanceMessage(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "set_chance_message") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			val embed = run(fun(): MessageEmbed {
				if (!accessService.fromManagerAtLeast(event, guildData)) {
					return EmbedBuilder().access(event.member, I18n.of("msg_access", guildData))
				}

				val arguments = event.getOption("arguments")?.asString?.split(" ") ?: emptyList()
				if (arguments.size != 1) {
					return EmbedBuilder().error(event.member, I18n.of("msg_error_arg", guildData))
				}

				try {
					val chance = arguments[0].toInt().also {
						require(it in 0..100)
					}

					guildData.chanceMessage = chance

					return EmbedBuilder().success(
						event.member, I18n.of("set_chance_message", guildData, chance)
					)
				} catch (_: Exception) {
					return EmbedBuilder().error(event.member, I18n.of("msg_error_format", guildData))
				}
			})

			dataService.saveGuildData(guild, guildData)

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}

	override fun setChanceEmoji(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "set_chance_emoji") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			val embed = run(fun(): MessageEmbed {
				if (!accessService.fromManagerAtLeast(event, guildData)) {
					return EmbedBuilder().access(event.member, I18n.of("msg_access", guildData))
				}

				val arguments = event.getOption("arguments")?.asString?.split(" ") ?: emptyList()
				if (arguments.size != 1) {
					return EmbedBuilder().error(event.member, I18n.of("msg_error_arg", guildData))
				}

				try {
					val chance = arguments[0].toInt().also {
						require(it in 0..100)
					}

					guildData.chanceEmoji = chance

					return EmbedBuilder().success(
						event.member, I18n.of("set_chance_emoji", guildData, chance)
					)
				} catch (_: Exception) {
					return EmbedBuilder().error(event.member, I18n.of("msg_error_format", guildData))
				}
			})

			dataService.saveGuildData(guild, guildData)

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}

	override fun setChanceAI(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "set_chance_ai") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			val embed = run(fun(): MessageEmbed {
				if (!accessService.fromManagerAtLeast(event, guildData)) {
					return EmbedBuilder().access(event.member, I18n.of("msg_access", guildData))
				}

				val arguments = event.getOption("arguments")?.asString?.split(" ") ?: emptyList()
				if (arguments.size != 1) {
					return EmbedBuilder().error(event.member, I18n.of("msg_error_arg", guildData))
				}

				try {
					val chance = arguments[0].toInt().also {
						require(it in -1..100)
					}

					guildData.chanceAI = chance

					return EmbedBuilder().success(
						event.member, I18n.of("set_chance_ai", guildData, chance)
					)
				} catch (_: Exception) {
					return EmbedBuilder().error(event.member, I18n.of("msg_error_format", guildData))
				}
			})

			dataService.saveGuildData(guild, guildData)

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}

	override fun setName(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "set_name") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			val embed = run(fun(): MessageEmbed {
				if (!accessService.fromManagerAtLeast(event, guildData)) {
					return EmbedBuilder().access(event.member, I18n.of("msg_access", guildData))
				}

				val argument = event.getOption("arguments")?.asString?.trim() ?: ""
				if (argument.isBlank()) {
					return EmbedBuilder().error(event.member, I18n.of("msg_error_arg", guildData))
				}

				try {
					guildData.name = argument

					val bot = guild.getMemberById(event.jda.selfUser.idLong)
					bot?.modifyNickname(argument)?.queue()

					return EmbedBuilder().success(event.member, I18n.of("set_name", guildData, argument))
				} catch (_: Exception) {
					return EmbedBuilder().error(event.member, I18n.of("msg_error_format", guildData))
				}
			})

			dataService.saveGuildData(guild, guildData)

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}

	override fun resetName(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "reset_name") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			val embed = run(fun(): MessageEmbed {
				if (!accessService.fromManagerAtLeast(event, guildData)) {
					return EmbedBuilder().access(event.member, I18n.of("msg_access", guildData))
				}

				try {
					guildData.name = I18n.of("default_name", guildData).s()

					val bot = guild.getMemberById(event.jda.selfUser.idLong)
					bot?.modifyNickname(guildData.name)?.queue()

					return EmbedBuilder().success(
						event.member, I18n.of("reset_name", guildData, guildData.name)
					)
				} catch (_: Exception) {
					return EmbedBuilder().error(event.member, I18n.of("msg_error_format", guildData))
				}
			})

			dataService.saveGuildData(guild, guildData)

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}

	override fun setPreprompt(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "set_preprompt") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			val embed = run(fun(): MessageEmbed {
				if (!accessService.fromManagerAtLeast(event, guildData)) {
					return EmbedBuilder().access(event.member, I18n.of("msg_access", guildData))
				}

				val argument = event.getOption("arguments")?.asString?.trim() ?: ""
				if (argument.isBlank()) {
					return EmbedBuilder().error(event.member, I18n.of("msg_error_arg", guildData))
				}

				try {
					guildData.preprompt = argument

					return EmbedBuilder().success(
						event.member, I18n.of("set_preprompt", guildData, argument)
					)
				} catch (_: Exception) {
					return EmbedBuilder().error(event.member, I18n.of("msg_error_format", guildData))
				}
			})

			dataService.saveGuildData(guild, guildData)

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}

	override fun resetPreprompt(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "reset_preprompt") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			val embed = run(fun(): MessageEmbed {
				if (!accessService.fromManagerAtLeast(event, guildData)) {
					return EmbedBuilder().access(event.member, I18n.of("msg_access", guildData))
				}

				try {
					guildData.preprompt = I18n.of("default_preprompt", guildData).s()

					return EmbedBuilder().success(
						event.member, I18n.of("reset_preprompt", guildData, guildData.preprompt)
					)
				} catch (_: Exception) {
					return EmbedBuilder().error(event.member, I18n.of("msg_error_format", guildData))
				}
			})

			event.hook.sendMessageEmbeds(embed).queue()

			dataService.saveGuildData(guild, guildData)
		}
	}

	override fun wipeData(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "wipe_data") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			val embed = run(fun(): MessageEmbed {
				if (!accessService.fromManagerAtLeast(event, guildData)) {
					return EmbedBuilder().access(event.member, I18n.of("msg_access", guildData))
				}

				dataService.wipeGuildData(guild)

				return EmbedBuilder().success(event.member, I18n.of("wipe_data", guildData))
			})

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}

	override fun wipeBank(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "wipe_bank") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			val embed = run(fun(): MessageEmbed {
				if (!accessService.fromManagerAtLeast(event, guildData)) {
					return EmbedBuilder().access(event.member, I18n.of("msg_access", guildData))
				}

				dataService.wipeGuildBank(guild)

				return EmbedBuilder().success(event.member, I18n.of("wipe_bank", guildData))
			})

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}
}