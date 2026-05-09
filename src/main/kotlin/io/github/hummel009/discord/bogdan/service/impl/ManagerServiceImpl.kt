package io.github.hummel009.discord.bogdan.service.impl

import io.github.hummel009.discord.bogdan.bean.GuildData
import io.github.hummel009.discord.bogdan.factory.ServiceFactory
import io.github.hummel009.discord.bogdan.service.AccessService
import io.github.hummel009.discord.bogdan.service.DataService
import io.github.hummel009.discord.bogdan.service.ManagerService
import io.github.hummel009.discord.bogdan.utils.I18n
import io.github.hummel009.discord.bogdan.utils.Lang
import io.github.hummel009.discord.bogdan.utils.access
import io.github.hummel009.discord.bogdan.utils.error
import io.github.hummel009.discord.bogdan.utils.success
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import java.time.Month

class ManagerServiceImpl : ManagerService {
	private val dataService: DataService = ServiceFactory.dataService
	private val accessService: AccessService = ServiceFactory.accessService

	private val ranges: Map<Int, IntRange> = mapOf(
		1 to 1..31,
		2 to 1..29,
		3 to 1..31,
		4 to 1..30,
		5 to 1..31,
		6 to 1..30,
		7 to 1..31,
		8 to 1..31,
		9 to 1..30,
		10 to 1..31,
		11 to 1..30,
		12 to 1..31,
	)

	override fun setLanguage(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "set_language") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			val embed = if (!accessService.fromManagerAtLeast(event, guildData)) {
				EmbedBuilder().access(event.member, I18n.of("msg_access", guildData))
			} else {
				val arguments = event.getOption("arguments")?.asString?.split(" ") ?: emptyList()

				if (arguments.size == 1) {
					try {
						val lang = Lang.of(arguments[0]) ?: throw Exception()

						guildData.lang = lang
						guildData.name = I18n.of("default_name", lang).s()
						guildData.preprompt = I18n.of("default_preprompt", lang).s()

						val bot = guild.getMemberById(event.jda.selfUser.idLong) ?: throw Exception()
						bot.modifyNickname(guildData.name).queue()

						val langName = I18n.of(lang.code, guildData)

						EmbedBuilder().success(
							event.member, I18n.of("set_language", guildData, langName)
						)
					} catch (_: Exception) {
						EmbedBuilder().error(event.member, I18n.of("msg_error_format", guildData))
					}
				} else {
					EmbedBuilder().error(event.member, I18n.of("msg_error_arg", guildData))
				}
			}
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

			val embed = if (!accessService.fromManagerAtLeast(event, guildData)) {
				EmbedBuilder().access(event.member, I18n.of("msg_access", guildData))
			} else {
				val arguments = event.getOption("arguments")?.asString?.split(" ") ?: emptyList()

				if (arguments.size == 3) {
					try {
						val memberId = arguments[0].toLong()
						val month = if (arguments[1].toInt() in 1..12) arguments[1].toInt() else throw Exception()
						val range = ranges[month] ?: throw Exception()
						val day = if (arguments[2].toInt() in range) arguments[2].toInt() else throw Exception()

						guild.getMemberById(memberId) ?: throw Exception()

						guildData.birthdays.add(GuildData.Birthday(memberId, GuildData.Date(day, month)))

						val numericDate = "%02d.%02d".format(day, month)
						val date = "${I18n.of(Month.of(month).name.lowercase(), guildData, day)} ($numericDate)"

						EmbedBuilder().success(
							event.member, I18n.of("add_birthday", guildData, memberId, date)
						)
					} catch (_: Exception) {
						EmbedBuilder().error(event.member, I18n.of("msg_error_format", guildData))
					}
				} else {
					EmbedBuilder().error(event.member, I18n.of("msg_error_arg", guildData))
				}
			}
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

			val embed = if (!accessService.fromManagerAtLeast(event, guildData)) {
				EmbedBuilder().access(event.member, I18n.of("msg_access", guildData))
			} else {
				val arguments = event.getOption("arguments")?.asString?.split(" ") ?: emptyList()

				if (arguments.isEmpty()) {
					guildData.birthdays.clear()

					EmbedBuilder().success(event.member, I18n.of("clear_birthdays", guildData))
				} else {
					if (arguments.size == 1) {
						try {
							val memberId = arguments[0].toLong()

							if (!guildData.birthdays.removeIf { it.memberId == memberId }) {
								throw Exception()
							}

							EmbedBuilder().success(
								event.member, I18n.of("clear_birthdays_single", guildData, memberId)
							)
						} catch (_: Exception) {
							EmbedBuilder().error(event.member, I18n.of("msg_error_format", guildData))
						}
					} else {
						EmbedBuilder().error(event.member, I18n.of("msg_error_arg", guildData))
					}
				}
			}
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

			val embed = if (!accessService.fromManagerAtLeast(event, guildData)) {
				EmbedBuilder().access(event.member, I18n.of("msg_access", guildData))
			} else {
				val arguments = event.getOption("arguments")?.asString?.split(" ") ?: emptyList()

				if (arguments.size == 1) {
					try {
						val roleId = arguments[0].toLong()

						guild.getRoleById(roleId) ?: throw Exception()

						guildData.managerRoleIds.add(roleId)

						EmbedBuilder().success(
							event.member, I18n.of("add_manager_role", guildData, roleId)
						)
					} catch (_: Exception) {
						EmbedBuilder().error(event.member, I18n.of("msg_error_format", guildData))
					}
				} else {
					EmbedBuilder().error(event.member, I18n.of("msg_error_arg", guildData))
				}
			}
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

			val embed = if (!accessService.fromManagerAtLeast(event, guildData)) {
				EmbedBuilder().access(event.member, I18n.of("msg_access", guildData))
			} else {
				val arguments = event.getOption("arguments")?.asString?.split(" ") ?: emptyList()

				if (arguments.isEmpty()) {
					guildData.managerRoleIds.clear()

					EmbedBuilder().success(event.member, I18n.of("clear_manager_roles", guildData))
				} else {
					if (arguments.size == 1) {
						try {
							val roleId = arguments[0].toLong()

							if (!guildData.managerRoleIds.removeIf { it == roleId }) {
								throw Exception()
							}

							EmbedBuilder().success(
								event.member, I18n.of("clear_manager_roles_single", guildData, roleId)
							)
						} catch (_: Exception) {
							EmbedBuilder().error(event.member, I18n.of("msg_error_format", guildData))
						}
					} else {
						EmbedBuilder().error(event.member, I18n.of("msg_error_arg", guildData))
					}
				}
			}
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

			val embed = if (!accessService.fromManagerAtLeast(event, guildData)) {
				EmbedBuilder().access(event.member, I18n.of("msg_access", guildData))
			} else {
				val arguments = event.getOption("arguments")?.asString?.split(" ") ?: emptyList()

				if (arguments.size == 1) {
					try {
						val channelId = arguments[0].toLong()

						guild.getTextChannelById(
							channelId
						) ?: guild.getThreadChannelById(
							channelId
						) ?: throw Exception()

						guildData.excludedChannelIds.add(channelId)

						EmbedBuilder().success(
							event.member, I18n.of("add_excluded_channel", guildData, channelId)
						)
					} catch (_: Exception) {
						EmbedBuilder().error(event.member, I18n.of("msg_error_format", guildData))
					}
				} else {
					EmbedBuilder().error(event.member, I18n.of("msg_error_arg", guildData))
				}
			}
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

			val embed = if (!accessService.fromManagerAtLeast(event, guildData)) {
				EmbedBuilder().access(event.member, I18n.of("msg_access", guildData))
			} else {
				val arguments = event.getOption("arguments")?.asString?.split(" ") ?: emptyList()

				if (arguments.isEmpty()) {
					guildData.excludedChannelIds.clear()

					EmbedBuilder().success(event.member, I18n.of("clear_excluded_channels", guildData))
				} else {
					if (arguments.size == 1) {
						try {
							val channelId = arguments[0].toLong()

							if (!guildData.excludedChannelIds.removeIf { it == channelId }) {
								throw Exception()
							}

							EmbedBuilder().success(
								event.member,
								I18n.of("clear_excluded_channels_single", guildData, channelId)
							)
						} catch (_: Exception) {
							EmbedBuilder().error(event.member, I18n.of("msg_error_format", guildData))
						}
					} else {
						EmbedBuilder().error(event.member, I18n.of("msg_error_arg", guildData))
					}
				}
			}
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

			val embed = if (!accessService.fromManagerAtLeast(event, guildData)) {
				EmbedBuilder().access(event.member, I18n.of("msg_access", guildData))
			} else {
				val arguments = event.getOption("arguments")?.asString?.split(" ") ?: emptyList()

				if (arguments.size == 1) {
					try {
						val chance = arguments[0].toInt()

						if (chance !in 0..100) {
							throw Exception()
						}

						guildData.chanceMessage = chance

						EmbedBuilder().success(
							event.member, I18n.of("set_chance_message", guildData, chance)
						)
					} catch (_: Exception) {
						EmbedBuilder().error(event.member, I18n.of("msg_error_format", guildData))
					}
				} else {
					EmbedBuilder().error(event.member, I18n.of("msg_error_arg", guildData))
				}
			}
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

			val embed = if (!accessService.fromManagerAtLeast(event, guildData)) {
				EmbedBuilder().access(event.member, I18n.of("msg_access", guildData))
			} else {
				val arguments = event.getOption("arguments")?.asString?.split(" ") ?: emptyList()

				if (arguments.size == 1) {
					try {
						val chance = arguments[0].toInt()

						if (chance !in 0..100) {
							throw Exception()
						}

						guildData.chanceEmoji = chance

						EmbedBuilder().success(
							event.member, I18n.of("set_chance_emoji", guildData, chance)
						)
					} catch (_: Exception) {
						EmbedBuilder().error(event.member, I18n.of("msg_error_format", guildData))
					}
				} else {
					EmbedBuilder().error(event.member, I18n.of("msg_error_arg", guildData))
				}
			}
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

			val embed = if (!accessService.fromManagerAtLeast(event, guildData)) {
				EmbedBuilder().access(event.member, I18n.of("msg_access", guildData))
			} else {
				val arguments = event.getOption("arguments")?.asString?.split(" ") ?: emptyList()

				if (arguments.size == 1) {
					try {
						val chance = arguments[0].toInt()

						if (chance !in -1..100) {
							throw Exception()
						}

						guildData.chanceAI = chance

						EmbedBuilder().success(
							event.member, I18n.of("set_chance_ai", guildData, chance)
						)
					} catch (_: Exception) {
						EmbedBuilder().error(event.member, I18n.of("msg_error_format", guildData))
					}
				} else {
					EmbedBuilder().error(event.member, I18n.of("msg_error_arg", guildData))
				}
			}
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

			val embed = if (!accessService.fromManagerAtLeast(event, guildData)) {
				EmbedBuilder().access(event.member, I18n.of("msg_access", guildData))
			} else {
				try {
					val arguments = event.getOption("arguments")?.asString ?: throw Exception()
					val name = arguments.trim()

					if (name.isEmpty()) {
						throw Exception()
					}

					guildData.name = name

					val bot = guild.getMemberById(event.jda.selfUser.idLong) ?: throw Exception()
					bot.modifyNickname(name).queue()

					EmbedBuilder().success(event.member, I18n.of("set_name", guildData, name))
				} catch (_: Exception) {
					EmbedBuilder().error(event.member, I18n.of("msg_error_format", guildData))
				}
			}
			dataService.saveGuildData(guild, guildData)

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}

	@Suppress("StringFormatTrivial")
	override fun resetName(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "reset_name") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			val embed = if (!accessService.fromManagerAtLeast(event, guildData)) {
				EmbedBuilder().access(event.member, I18n.of("msg_access", guildData))
			} else {
				try {
					guildData.name = I18n.of("default_name", guildData).s()

					val bot = guild.getMemberById(event.jda.selfUser.idLong) ?: throw Exception()
					bot.modifyNickname(guildData.name).queue()

					EmbedBuilder().success(
						event.member, I18n.of("reset_name", guildData, guildData.name)
					)
				} catch (_: Exception) {
					EmbedBuilder().error(event.member, I18n.of("msg_error_format", guildData))
				}
			}
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

			val embed = if (!accessService.fromManagerAtLeast(event, guildData)) {
				EmbedBuilder().access(event.member, I18n.of("msg_access", guildData))
			} else {
				try {
					val arguments = event.getOption("arguments")?.asString ?: throw Exception()
					val prompt = arguments.trim()

					if (prompt.isEmpty()) {
						throw Exception()
					}

					guildData.preprompt = prompt

					EmbedBuilder().success(
						event.member, I18n.of("set_preprompt", guildData, prompt)
					)
				} catch (_: Exception) {
					EmbedBuilder().error(event.member, I18n.of("msg_error_format", guildData))
				}
			}
			dataService.saveGuildData(guild, guildData)

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}

	@Suppress("StringFormatTrivial")
	override fun resetPreprompt(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "reset_preprompt") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			val embed = if (!accessService.fromManagerAtLeast(event, guildData)) {
				EmbedBuilder().access(event.member, I18n.of("msg_access", guildData))
			} else {
				try {
					guildData.preprompt = I18n.of("default_preprompt", guildData).s()

					EmbedBuilder().success(
						event.member, I18n.of("reset_preprompt", guildData, guildData.preprompt)
					)
				} catch (_: Exception) {
					EmbedBuilder().error(event.member, I18n.of("msg_error_format", guildData))
				}
			}
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

			val embed = if (!accessService.fromManagerAtLeast(event, guildData)) {
				EmbedBuilder().access(event.member, I18n.of("msg_access", guildData))
			} else {
				dataService.wipeGuildData(guild)

				EmbedBuilder().success(event.member, I18n.of("wipe_data", guildData))
			}
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

			val embed = if (!accessService.fromManagerAtLeast(event, guildData)) {
				EmbedBuilder().access(event.member, I18n.of("msg_access", guildData))
			} else {
				dataService.wipeGuildBank(guild)

				EmbedBuilder().success(event.member, I18n.of("wipe_bank", guildData))
			}
			event.hook.sendMessageEmbeds(embed).queue()
		}
	}
}