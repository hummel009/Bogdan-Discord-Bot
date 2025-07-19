package com.github.hummel.mdb.core.service.impl

import com.github.hummel.mdb.core.bean.GuildData
import com.github.hummel.mdb.core.factory.ServiceFactory
import com.github.hummel.mdb.core.service.AccessService
import com.github.hummel.mdb.core.service.DataService
import com.github.hummel.mdb.core.service.ManagerService
import com.github.hummel.mdb.core.utils.*
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

	override fun addBirthday(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "add_birthday") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			val embed = if (!accessService.fromManagerAtLeast(event, guildData)) {
				EmbedBuilder().access(event.member, guildData, I18n.of("no_access", guildData))
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

						val date = I18n.of(Month.of(month).name.lowercase(), guildData).format(day)

						EmbedBuilder().success(
							event.member, guildData, I18n.of("added_birthday", guildData).format(memberId, date)
						)
					} catch (_: Exception) {
						EmbedBuilder().error(event.member, guildData, I18n.of("invalid_format", guildData))
					}
				} else {
					EmbedBuilder().error(event.member, guildData, I18n.of("invalid_arg", guildData))
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
				EmbedBuilder().access(event.member, guildData, I18n.of("no_access", guildData))
			} else {
				val arguments = event.getOption("arguments")?.asString?.split(" ") ?: emptyList()
				if (arguments.size == 1) {
					try {
						val roleId = arguments[0].toLong()
						guild.getRoleById(roleId) ?: throw Exception()

						guildData.managerRoleIds.add(roleId)

						EmbedBuilder().success(
							event.member, guildData, I18n.of("added_manager_role", guildData).format(roleId)
						)
					} catch (_: Exception) {
						EmbedBuilder().error(event.member, guildData, I18n.of("invalid_format", guildData))
					}
				} else {
					EmbedBuilder().error(event.member, guildData, I18n.of("invalid_arg", guildData))
				}
			}
			dataService.saveGuildData(guild, guildData)

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}

	override fun addSecretChannel(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "add_secret_channel") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			val embed = if (!accessService.fromManagerAtLeast(event, guildData)) {
				EmbedBuilder().access(event.member, guildData, I18n.of("no_access", guildData))
			} else {
				val arguments = event.getOption("arguments")?.asString?.split(" ") ?: emptyList()
				if (arguments.size == 1) {
					try {
						val channelId = arguments[0].toLong()
						guild.getTextChannelById(channelId) ?: guild.getThreadChannelById(channelId)
						?: throw Exception()

						guildData.secretChannelIds.add(channelId)

						EmbedBuilder().success(
							event.member, guildData, I18n.of("added_secret_channel", guildData).format(channelId)
						)
					} catch (_: Exception) {
						EmbedBuilder().error(event.member, guildData, I18n.of("invalid_format", guildData))
					}
				} else {
					EmbedBuilder().error(event.member, guildData, I18n.of("invalid_arg", guildData))
				}
			}
			dataService.saveGuildData(guild, guildData)

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}

	override fun addMutedChannel(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "add_muted_channel") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			val embed = if (!accessService.fromManagerAtLeast(event, guildData)) {
				EmbedBuilder().access(event.member, guildData, I18n.of("no_access", guildData))
			} else {
				val arguments = event.getOption("arguments")?.asString?.split(" ") ?: emptyList()
				if (arguments.size == 1) {
					try {
						val channelId = arguments[0].toLong()
						guild.getTextChannelById(channelId) ?: guild.getThreadChannelById(channelId)
						?: throw Exception()

						guildData.mutedChannelIds.add(channelId)

						EmbedBuilder().success(
							event.member, guildData, I18n.of("added_muted_channel", guildData).format(channelId)
						)
					} catch (_: Exception) {
						EmbedBuilder().error(event.member, guildData, I18n.of("invalid_format", guildData))
					}
				} else {
					EmbedBuilder().error(event.member, guildData, I18n.of("invalid_arg", guildData))
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
				EmbedBuilder().access(event.member, guildData, I18n.of("no_access", guildData))
			} else {
				val arguments = event.getOption("arguments")?.asString?.split(" ") ?: emptyList()
				if (arguments.isEmpty()) {
					guildData.birthdays.clear()

					EmbedBuilder().success(event.member, guildData, I18n.of("cleared_birthdays", guildData))
				} else {
					if (arguments.size == 1) {
						try {
							val memberId = arguments[0].toLong()

							guildData.birthdays.removeIf { it.id == memberId }

							EmbedBuilder().success(
								event.member, guildData, I18n.of("removed_birthday", guildData).format(memberId)
							)
						} catch (_: Exception) {
							EmbedBuilder().error(event.member, guildData, I18n.of("invalid_format", guildData))
						}
					} else {
						EmbedBuilder().error(event.member, guildData, I18n.of("invalid_arg", guildData))
					}
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
				EmbedBuilder().access(event.member, guildData, I18n.of("no_access", guildData))
			} else {
				val arguments = event.getOption("arguments")?.asString?.split(" ") ?: emptyList()
				if (arguments.isEmpty()) {
					guildData.managerRoleIds.clear()

					EmbedBuilder().success(event.member, guildData, I18n.of("cleared_manager_roles", guildData))
				} else {
					if (arguments.size == 1) {
						try {
							val roleId = arguments[0].toLong()

							guildData.managerRoleIds.removeIf { it == roleId }

							EmbedBuilder().success(
								event.member, guildData, I18n.of("removed_manager_role", guildData).format(roleId)
							)
						} catch (_: Exception) {
							EmbedBuilder().error(event.member, guildData, I18n.of("invalid_format", guildData))
						}
					} else {
						EmbedBuilder().error(event.member, guildData, I18n.of("invalid_arg", guildData))
					}
				}
			}
			dataService.saveGuildData(guild, guildData)

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}

	override fun clearSecretChannels(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "clear_secret_channels") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			val embed = if (!accessService.fromManagerAtLeast(event, guildData)) {
				EmbedBuilder().access(event.member, guildData, I18n.of("no_access", guildData))
			} else {
				val arguments = event.getOption("arguments")?.asString?.split(" ") ?: emptyList()
				if (arguments.isEmpty()) {
					guildData.secretChannelIds.clear()

					EmbedBuilder().success(event.member, guildData, I18n.of("cleared_secret_channels", guildData))
				} else {
					if (arguments.size == 1) {
						try {
							val channelId = arguments[0].toLong()

							guildData.secretChannelIds.removeIf { it == channelId }

							EmbedBuilder().success(
								event.member, guildData, I18n.of("removed_secret_channel", guildData).format(channelId)
							)
						} catch (_: Exception) {
							EmbedBuilder().error(event.member, guildData, I18n.of("invalid_format", guildData))
						}
					} else {
						EmbedBuilder().error(event.member, guildData, I18n.of("invalid_arg", guildData))
					}
				}
			}
			dataService.saveGuildData(guild, guildData)

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}

	override fun clearMutedChannels(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "clear_muted_channels") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			val embed = if (!accessService.fromManagerAtLeast(event, guildData)) {
				EmbedBuilder().access(event.member, guildData, I18n.of("no_access", guildData))
			} else {
				val arguments = event.getOption("arguments")?.asString?.split(" ") ?: emptyList()
				if (arguments.isEmpty()) {
					guildData.mutedChannelIds.clear()

					EmbedBuilder().success(event.member, guildData, I18n.of("cleared_muted_channels", guildData))
				} else {
					if (arguments.size == 1) {
						try {
							val channelId = arguments[0].toLong()

							guildData.mutedChannelIds.removeIf { it == channelId }

							EmbedBuilder().success(
								event.member, guildData, I18n.of("removed_muted_channel", guildData).format(channelId)
							)
						} catch (_: Exception) {
							EmbedBuilder().error(event.member, guildData, I18n.of("invalid_format", guildData))
						}
					} else {
						EmbedBuilder().error(event.member, guildData, I18n.of("invalid_arg", guildData))
					}
				}
			}
			dataService.saveGuildData(guild, guildData)

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}

	override fun setLanguage(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "set_language") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			val embed = if (!accessService.fromManagerAtLeast(event, guildData)) {
				EmbedBuilder().access(event.member, guildData, I18n.of("no_access", guildData))
			} else {
				val arguments = event.getOption("arguments")?.asString?.split(" ") ?: emptyList()
				if (arguments.size == 1) {
					try {
						val lang = arguments[0]
						if (lang != "ru" && lang != "be" && lang != "uk" && lang != "en") {
							throw Exception()
						}

						guildData.lang = lang

						val langName = I18n.of(lang, guildData)

						EmbedBuilder().success(
							event.member, guildData, I18n.of("set_language", guildData).format(langName)
						)
					} catch (_: Exception) {
						EmbedBuilder().error(event.member, guildData, I18n.of("invalid_format", guildData))
					}
				} else {
					EmbedBuilder().error(event.member, guildData, I18n.of("invalid_arg", guildData))
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
				EmbedBuilder().access(event.member, guildData, I18n.of("no_access", guildData))
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
							event.member, guildData, I18n.of("set_chance_message", guildData).format(chance)
						)
					} catch (_: Exception) {
						EmbedBuilder().error(event.member, guildData, I18n.of("invalid_format", guildData))
					}
				} else {
					EmbedBuilder().error(event.member, guildData, I18n.of("invalid_arg", guildData))
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
				EmbedBuilder().access(event.member, guildData, I18n.of("no_access", guildData))
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
							event.member, guildData, I18n.of("set_chance_emoji", guildData).format(chance)
						)
					} catch (_: Exception) {
						EmbedBuilder().error(event.member, guildData, I18n.of("invalid_format", guildData))
					}
				} else {
					EmbedBuilder().error(event.member, guildData, I18n.of("invalid_arg", guildData))
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
				EmbedBuilder().access(event.member, guildData, I18n.of("no_access", guildData))
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
							event.member, guildData, I18n.of("set_chance_ai", guildData).format(chance)
						)
					} catch (_: Exception) {
						EmbedBuilder().error(event.member, guildData, I18n.of("invalid_format", guildData))
					}
				} else {
					EmbedBuilder().error(event.member, guildData, I18n.of("invalid_arg", guildData))
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
				EmbedBuilder().access(event.member, guildData, I18n.of("no_access", guildData))
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

					EmbedBuilder().success(event.member, guildData, I18n.of("set_name", guildData).format(name))
				} catch (_: Exception) {
					EmbedBuilder().error(event.member, guildData, I18n.of("invalid_format", guildData))
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
				EmbedBuilder().access(event.member, guildData, I18n.of("no_access", guildData))
			} else {
				try {
					val arguments = event.getOption("arguments")?.asString ?: throw Exception()
					val prompt = arguments.trim()

					if (prompt.isEmpty()) {
						throw Exception()
					}

					guildData.preprompt = prompt

					EmbedBuilder().success(
						event.member, guildData, I18n.of("set_preprompt", guildData).format(prompt)
					)
				} catch (_: Exception) {
					EmbedBuilder().error(event.member, guildData, I18n.of("invalid_format", guildData))
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
				EmbedBuilder().access(event.member, guildData, I18n.of("no_access", guildData))
			} else {
				try {
					guildData.name = defaultName

					val bot = guild.getMemberById(event.jda.selfUser.idLong) ?: throw Exception()
					bot.modifyNickname(defaultName).queue()

					EmbedBuilder().success(
						event.member, guildData, I18n.of("reset_name", guildData).format(defaultName)
					)
				} catch (_: Exception) {
					EmbedBuilder().error(event.member, guildData, I18n.of("invalid_format", guildData))
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
				EmbedBuilder().access(event.member, guildData, I18n.of("no_access", guildData))
			} else {
				try {
					guildData.preprompt = defaultPreprompt

					EmbedBuilder().success(
						event.member, guildData, I18n.of("reset_preprompt", guildData).format(defaultPreprompt)
					)
				} catch (_: Exception) {
					EmbedBuilder().error(event.member, guildData, I18n.of("invalid_format", guildData))
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
				EmbedBuilder().access(event.member, guildData, I18n.of("no_access", guildData))
			} else {
				dataService.wipeGuildData(guild)

				EmbedBuilder().success(event.member, guildData, I18n.of("cleared_data", guildData))
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
				EmbedBuilder().access(event.member, guildData, I18n.of("no_access", guildData))
			} else {
				dataService.wipeGuildBank(guild)

				EmbedBuilder().success(event.member, guildData, I18n.of("cleared_bank", guildData))
			}
			event.hook.sendMessageEmbeds(embed).queue()
		}
	}
}