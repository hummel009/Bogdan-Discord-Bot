package com.github.hummel.union.service.impl

import com.github.hummel.union.bean.ServerData
import com.github.hummel.union.factory.ServiceFactory
import com.github.hummel.union.lang.I18n
import com.github.hummel.union.service.AccessService
import com.github.hummel.union.service.DataService
import com.github.hummel.union.service.ManagerService
import com.github.hummel.union.utils.*
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.event.interaction.InteractionCreateEvent
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

	override fun addBirthday(event: InteractionCreateEvent) {
		val sc = event.slashCommandInteraction.get()

		if (sc.fullCommandName == "add_birthday") {
			sc.respondLater().thenAccept {
				val server = sc.server.get()
				val serverData = dataService.loadServerData(server)

				val embed = if (!accessService.fromManagerAtLeast(sc, serverData)) {
					EmbedBuilder().access(sc.user, serverData, I18n.of("no_access", serverData))
				} else {
					val arguments = sc.arguments[0].stringValue.get().split(" ")
					if (arguments.size == 3) {
						try {
							val userId = arguments[0].toLong()
							val month = if (arguments[1].toInt() in 1..12) arguments[1].toInt() else throw Exception()
							val range = ranges[month] ?: throw Exception()
							val day = if (arguments[2].toInt() in range) arguments[2].toInt() else throw Exception()
							if (!server.getMemberById(userId).isPresent) {
								throw Exception()
							}
							serverData.birthdays.add(ServerData.Birthday(userId, ServerData.Date(day, month)))

							val date = I18n.of(Month.of(month).name.lowercase(), serverData).format(day)

							EmbedBuilder().success(
								sc.user, serverData, I18n.of("added_birthday", serverData).format(userId, date)
							)
						} catch (_: Exception) {
							EmbedBuilder().error(sc.user, serverData, I18n.of("invalid_format", serverData))
						}
					} else {
						EmbedBuilder().error(sc.user, serverData, I18n.of("invalid_arg", serverData))
					}
				}
				sc.createFollowupMessageBuilder().addEmbed(embed).send().get()

				dataService.saveServerData(server, serverData)
			}.get()
		}
	}

	override fun addManager(event: InteractionCreateEvent) {
		val sc = event.slashCommandInteraction.get()

		if (sc.fullCommandName == "add_manager") {
			sc.respondLater().thenAccept {
				val server = sc.server.get()
				val serverData = dataService.loadServerData(server)

				val embed = if (!accessService.fromManagerAtLeast(sc, serverData)) {
					EmbedBuilder().access(sc.user, serverData, I18n.of("no_access", serverData))
				} else {
					val arguments = sc.arguments[0].stringValue.get().split(" ")
					if (arguments.size == 1) {
						try {
							val roleId = arguments[0].toLong()
							if (!server.getRoleById(roleId).isPresent) {
								throw Exception()
							}
							serverData.managers.add(ServerData.Role(roleId))
							EmbedBuilder().success(
								sc.user, serverData, I18n.of("added_manager", serverData).format(roleId)
							)
						} catch (_: Exception) {
							EmbedBuilder().error(sc.user, serverData, I18n.of("invalid_format", serverData))
						}
					} else {
						EmbedBuilder().error(sc.user, serverData, I18n.of("invalid_arg", serverData))
					}
				}
				sc.createFollowupMessageBuilder().addEmbed(embed).send().get()

				dataService.saveServerData(server, serverData)
			}.get()
		}
	}

	override fun addSecretChannel(event: InteractionCreateEvent) {
		val sc = event.slashCommandInteraction.get()

		if (sc.fullCommandName == "add_secret_channel") {
			sc.respondLater().thenAccept {
				val server = sc.server.get()
				val serverData = dataService.loadServerData(server)

				val embed = if (!accessService.fromManagerAtLeast(sc, serverData)) {
					EmbedBuilder().access(sc.user, serverData, I18n.of("no_access", serverData))
				} else {
					val arguments = sc.arguments[0].stringValue.get().split(" ")
					if (arguments.size == 1) {
						try {
							val channelId = arguments[0].toLong()
							if (!server.getChannelById(channelId).isPresent) {
								throw Exception()
							}
							serverData.secretChannels.add(ServerData.Channel(channelId))
							EmbedBuilder().success(
								sc.user, serverData, I18n.of("added_secret_channel", serverData).format(channelId)
							)
						} catch (_: Exception) {
							EmbedBuilder().error(sc.user, serverData, I18n.of("invalid_format", serverData))
						}
					} else {
						EmbedBuilder().error(sc.user, serverData, I18n.of("invalid_arg", serverData))
					}
				}
				sc.createFollowupMessageBuilder().addEmbed(embed).send().get()

				dataService.saveServerData(server, serverData)
			}.get()
		}
	}

	override fun addMutedChannel(event: InteractionCreateEvent) {
		val sc = event.slashCommandInteraction.get()

		if (sc.fullCommandName == "add_muted_channel") {
			sc.respondLater().thenAccept {
				val server = sc.server.get()
				val serverData = dataService.loadServerData(server)

				val embed = if (!accessService.fromManagerAtLeast(sc, serverData)) {
					EmbedBuilder().access(sc.user, serverData, I18n.of("no_access", serverData))
				} else {
					val arguments = sc.arguments[0].stringValue.get().split(" ")
					if (arguments.size == 1) {
						try {
							val channelId = arguments[0].toLong()
							if (!server.getChannelById(channelId).isPresent) {
								throw Exception()
							}
							serverData.mutedChannels.add(ServerData.Channel(channelId))
							EmbedBuilder().success(
								sc.user, serverData, I18n.of("added_muted_channel", serverData).format(channelId)
							)
						} catch (_: Exception) {
							EmbedBuilder().error(sc.user, serverData, I18n.of("invalid_format", serverData))
						}
					} else {
						EmbedBuilder().error(sc.user, serverData, I18n.of("invalid_arg", serverData))
					}
				}
				sc.createFollowupMessageBuilder().addEmbed(embed).send().get()

				dataService.saveServerData(server, serverData)
			}.get()
		}
	}

	override fun clearBirthdays(event: InteractionCreateEvent) {
		val sc = event.slashCommandInteraction.get()

		if (sc.fullCommandName == "clear_birthdays") {
			sc.respondLater().thenAccept {
				val server = sc.server.get()
				val serverData = dataService.loadServerData(server)

				val embed = if (!accessService.fromManagerAtLeast(sc, serverData)) {
					EmbedBuilder().access(sc.user, serverData, I18n.of("no_access", serverData))
				} else {
					if (sc.arguments.isEmpty()) {
						serverData.birthdays.clear()
						EmbedBuilder().success(sc.user, serverData, I18n.of("cleared_birthdays", serverData))
					} else {
						val arguments = sc.arguments[0].stringValue.get().split(" ")
						if (arguments.size == 1) {
							try {
								val userId = arguments[0].toLong()
								serverData.birthdays.removeIf { it.id == userId }
								EmbedBuilder().success(
									sc.user, serverData, I18n.of("removed_birthday", serverData).format(userId)
								)
							} catch (_: Exception) {
								EmbedBuilder().error(sc.user, serverData, I18n.of("invalid_format", serverData))
							}
						} else {
							EmbedBuilder().error(sc.user, serverData, I18n.of("invalid_arg", serverData))
						}
					}
				}
				sc.createFollowupMessageBuilder().addEmbed(embed).send().get()

				dataService.saveServerData(server, serverData)
			}.get()
		}
	}

	override fun clearManagers(event: InteractionCreateEvent) {
		val sc = event.slashCommandInteraction.get()

		if (sc.fullCommandName == "clear_managers") {
			sc.respondLater().thenAccept {
				val server = sc.server.get()
				val serverData = dataService.loadServerData(server)

				val embed = if (!accessService.fromManagerAtLeast(sc, serverData)) {
					EmbedBuilder().access(sc.user, serverData, I18n.of("no_access", serverData))
				} else {
					if (sc.arguments.isEmpty()) {
						serverData.managers.clear()
						EmbedBuilder().success(sc.user, serverData, I18n.of("cleared_managers", serverData))
					} else {
						val arguments = sc.arguments[0].stringValue.get().split(" ")
						if (arguments.size == 1) {
							try {
								val roleId = arguments[0].toLong()
								serverData.managers.removeIf { it.id == roleId }
								EmbedBuilder().success(
									sc.user, serverData, I18n.of("removed_manager", serverData).format(roleId)
								)
							} catch (_: Exception) {
								EmbedBuilder().error(sc.user, serverData, I18n.of("invalid_format", serverData))
							}
						} else {
							EmbedBuilder().error(sc.user, serverData, I18n.of("invalid_arg", serverData))
						}
					}
				}
				sc.createFollowupMessageBuilder().addEmbed(embed).send().get()

				dataService.saveServerData(server, serverData)
			}.get()
		}
	}

	override fun clearSecretChannels(event: InteractionCreateEvent) {
		val sc = event.slashCommandInteraction.get()

		if (sc.fullCommandName == "clear_secret_channels") {
			sc.respondLater().thenAccept {
				val server = sc.server.get()
				val serverData = dataService.loadServerData(server)

				val embed = if (!accessService.fromManagerAtLeast(sc, serverData)) {
					EmbedBuilder().access(sc.user, serverData, I18n.of("no_access", serverData))
				} else {
					if (sc.arguments.isEmpty()) {
						serverData.secretChannels.clear()
						EmbedBuilder().success(sc.user, serverData, I18n.of("cleared_secret_channels", serverData))
					} else {
						val arguments = sc.arguments[0].stringValue.get().split(" ")
						if (arguments.size == 1) {
							try {
								val channelId = arguments[0].toLong()
								serverData.secretChannels.removeIf { it.id == channelId }
								EmbedBuilder().success(
									sc.user, serverData, I18n.of("removed_secret_channel", serverData).format(channelId)
								)
							} catch (_: Exception) {
								EmbedBuilder().error(sc.user, serverData, I18n.of("invalid_format", serverData))
							}
						} else {
							EmbedBuilder().error(sc.user, serverData, I18n.of("invalid_arg", serverData))
						}
					}
				}
				sc.createFollowupMessageBuilder().addEmbed(embed).send().get()

				dataService.saveServerData(server, serverData)
			}.get()
		}
	}

	override fun clearMutedChannels(event: InteractionCreateEvent) {
		val sc = event.slashCommandInteraction.get()

		if (sc.fullCommandName == "clear_muted_channels") {
			sc.respondLater().thenAccept {
				val server = sc.server.get()
				val serverData = dataService.loadServerData(server)

				val embed = if (!accessService.fromManagerAtLeast(sc, serverData)) {
					EmbedBuilder().access(sc.user, serverData, I18n.of("no_access", serverData))
				} else {
					if (sc.arguments.isEmpty()) {
						serverData.mutedChannels.clear()
						EmbedBuilder().success(sc.user, serverData, I18n.of("cleared_muted_channels", serverData))
					} else {
						val arguments = sc.arguments[0].stringValue.get().split(" ")
						if (arguments.size == 1) {
							try {
								val channelId = arguments[0].toLong()
								serverData.mutedChannels.removeIf { it.id == channelId }
								EmbedBuilder().success(
									sc.user, serverData, I18n.of("removed_muted_channel", serverData).format(channelId)
								)
							} catch (_: Exception) {
								EmbedBuilder().error(sc.user, serverData, I18n.of("invalid_format", serverData))
							}
						} else {
							EmbedBuilder().error(sc.user, serverData, I18n.of("invalid_arg", serverData))
						}
					}
				}
				sc.createFollowupMessageBuilder().addEmbed(embed).send().get()

				dataService.saveServerData(server, serverData)
			}.get()
		}
	}

	override fun clearBank(event: InteractionCreateEvent) {
		val sc = event.slashCommandInteraction.get()

		if (sc.fullCommandName == "clear_bank") {
			sc.respondLater().thenAccept {
				val server = sc.server.get()
				val serverData = dataService.loadServerData(server)

				val embed = if (!accessService.fromManagerAtLeast(sc, serverData)) {
					EmbedBuilder().access(sc.user, serverData, I18n.of("no_access", serverData))
				} else {
					dataService.wipeServerMessages(server)
					EmbedBuilder().success(sc.user, serverData, I18n.of("cleared_bank", serverData))
				}
				sc.createFollowupMessageBuilder().addEmbed(embed).send().get()
			}.get()
		}
	}

	override fun clearData(event: InteractionCreateEvent) {
		val sc = event.slashCommandInteraction.get()

		if (sc.fullCommandName == "clear_data") {
			sc.respondLater().thenAccept {
				val server = sc.server.get()
				val serverData = dataService.loadServerData(server)

				val embed = if (!accessService.fromManagerAtLeast(sc, serverData)) {
					EmbedBuilder().access(sc.user, serverData, I18n.of("no_access", serverData))
				} else {
					dataService.wipeServerData(server)
					EmbedBuilder().success(sc.user, serverData, I18n.of("cleared_data", serverData))
				}
				sc.createFollowupMessageBuilder().addEmbed(embed).send().get()
			}.get()
		}
	}

	override fun setLanguage(event: InteractionCreateEvent) {
		val sc = event.slashCommandInteraction.get()

		if (sc.fullCommandName == "set_language") {
			sc.respondLater().thenAccept {
				val server = sc.server.get()
				val serverData = dataService.loadServerData(server)

				val embed = if (!accessService.fromManagerAtLeast(sc, serverData)) {
					EmbedBuilder().access(sc.user, serverData, I18n.of("no_access", serverData))
				} else {
					val arguments = sc.arguments[0].stringValue.get().split(" ")
					if (arguments.size == 1) {
						try {
							val lang = arguments[0]
							if (lang != "ru" && lang != "be" && lang != "uk" && lang != "en") {
								throw Exception()
							}
							serverData.lang = lang
							val langName = I18n.of(lang, serverData)
							EmbedBuilder().success(
								sc.user, serverData, I18n.of("set_language", serverData).format(langName)
							)
						} catch (_: Exception) {
							EmbedBuilder().error(sc.user, serverData, I18n.of("invalid_arg", serverData))
						}
					} else {
						EmbedBuilder().error(sc.user, serverData, I18n.of("invalid_arg", serverData))
					}
				}
				sc.createFollowupMessageBuilder().addEmbed(embed).send().get()

				dataService.saveServerData(server, serverData)
			}.get()
		}
	}

	override fun setChanceMessage(event: InteractionCreateEvent) {
		val sc = event.slashCommandInteraction.get()

		if (sc.fullCommandName == "set_chance_message") {
			sc.respondLater().thenAccept {
				val server = sc.server.get()
				val serverData = dataService.loadServerData(server)

				val embed = if (!accessService.fromManagerAtLeast(sc, serverData)) {
					EmbedBuilder().access(sc.user, serverData, I18n.of("no_access", serverData))
				} else {
					val arguments = sc.arguments[0].stringValue.get().split(" ")
					if (arguments.size == 1) {
						try {
							val chance = arguments[0].toInt()
							if (chance !in 0..100) {
								throw Exception()
							}
							serverData.chanceMessage = chance
							EmbedBuilder().success(
								sc.user, serverData, I18n.of("set_chance_message", serverData).format(chance)
							)
						} catch (_: Exception) {
							EmbedBuilder().error(sc.user, serverData, I18n.of("invalid_format", serverData))
						}
					} else {
						EmbedBuilder().error(sc.user, serverData, I18n.of("invalid_arg", serverData))
					}
				}
				sc.createFollowupMessageBuilder().addEmbed(embed).send().get()

				dataService.saveServerData(server, serverData)
			}.get()
		}
	}

	override fun setChanceEmoji(event: InteractionCreateEvent) {
		val sc = event.slashCommandInteraction.get()

		if (sc.fullCommandName == "set_chance_emoji") {
			sc.respondLater().thenAccept {
				val server = sc.server.get()
				val serverData = dataService.loadServerData(server)

				val embed = if (!accessService.fromManagerAtLeast(sc, serverData)) {
					EmbedBuilder().access(sc.user, serverData, I18n.of("no_access", serverData))
				} else {
					val arguments = sc.arguments[0].stringValue.get().split(" ")
					if (arguments.size == 1) {
						try {
							val chance = arguments[0].toInt()
							if (chance !in 0..100) {
								throw Exception()
							}
							serverData.chanceEmoji = chance
							EmbedBuilder().success(
								sc.user, serverData, I18n.of("set_chance_emoji", serverData).format(chance)
							)
						} catch (_: Exception) {
							EmbedBuilder().error(sc.user, serverData, I18n.of("invalid_format", serverData))
						}
					} else {
						EmbedBuilder().error(sc.user, serverData, I18n.of("invalid_arg", serverData))
					}
				}
				sc.createFollowupMessageBuilder().addEmbed(embed).send().get()

				dataService.saveServerData(server, serverData)
			}.get()
		}
	}

	override fun setChanceAI(event: InteractionCreateEvent) {
		val sc = event.slashCommandInteraction.get()

		if (sc.fullCommandName == "set_chance_ai") {
			sc.respondLater().thenAccept {
				val server = sc.server.get()
				val serverData = dataService.loadServerData(server)

				val embed = if (!accessService.fromManagerAtLeast(sc, serverData)) {
					EmbedBuilder().access(sc.user, serverData, I18n.of("no_access", serverData))
				} else {
					val arguments = sc.arguments[0].stringValue.get().split(" ")
					if (arguments.size == 1) {
						try {
							val chance = arguments[0].toInt()
							if (chance !in 0..100) {
								throw Exception()
							}
							serverData.chanceAI = chance
							EmbedBuilder().success(
								sc.user, serverData, I18n.of("set_chance_ai", serverData).format(chance)
							)
						} catch (_: Exception) {
							EmbedBuilder().error(sc.user, serverData, I18n.of("invalid_format", serverData))
						}
					} else {
						EmbedBuilder().error(sc.user, serverData, I18n.of("invalid_arg", serverData))
					}
				}
				sc.createFollowupMessageBuilder().addEmbed(embed).send().get()

				dataService.saveServerData(server, serverData)
			}.get()
		}
	}

	override fun setPreprompt(event: InteractionCreateEvent) {
		val sc = event.slashCommandInteraction.get()

		if (sc.fullCommandName == "set_preprompt") {
			sc.respondLater().thenAccept {
				val server = sc.server.get()
				val serverData = dataService.loadServerData(server)

				val embed = if (!accessService.fromManagerAtLeast(sc, serverData)) {
					EmbedBuilder().access(sc.user, serverData, I18n.of("no_access", serverData))
				} else {
					val prompt = sc.arguments[0].stringValue.get()
					try {
						serverData.preprompt = prompt
						EmbedBuilder().success(
							sc.user, serverData, I18n.of("set_preprompt", serverData).format(prompt)
						)
					} catch (_: Exception) {
						EmbedBuilder().error(sc.user, serverData, I18n.of("invalid_arg", serverData))
					}
				}
				sc.createFollowupMessageBuilder().addEmbed(embed).send().get()

				dataService.saveServerData(server, serverData)
			}.get()
		}
	}

	@Suppress("StringFormatTrivial")
	override fun resetPreprompt(event: InteractionCreateEvent) {
		val sc = event.slashCommandInteraction.get()

		if (sc.fullCommandName == "reset_preprompt") {
			sc.respondLater().thenAccept {
				val server = sc.server.get()
				val serverData = dataService.loadServerData(server)

				val embed = if (!accessService.fromManagerAtLeast(sc, serverData)) {
					EmbedBuilder().access(sc.user, serverData, I18n.of("no_access", serverData))
				} else {
					try {
						serverData.preprompt = defaultPrompt
						EmbedBuilder().success(
							sc.user, serverData, I18n.of("reset_preprompt", serverData).format(defaultPrompt)
						)
					} catch (_: Exception) {
						EmbedBuilder().error(sc.user, serverData, I18n.of("invalid_arg", serverData))
					}
				}
				sc.createFollowupMessageBuilder().addEmbed(embed).send().get()

				dataService.saveServerData(server, serverData)
			}.get()
		}
	}

	override fun setName(event: InteractionCreateEvent) {
		val sc = event.slashCommandInteraction.get()

		if (sc.fullCommandName == "set_name") {
			sc.respondLater().thenAccept {
				val server = sc.server.get()
				val serverData = dataService.loadServerData(server)

				val embed = if (!accessService.fromManagerAtLeast(sc, serverData)) {
					EmbedBuilder().access(sc.user, serverData, I18n.of("no_access", serverData))
				} else {
					val name = sc.arguments[0].stringValue.get()
					try {
						serverData.name = name
						event.api.yourself.updateNickname(server, name)

						EmbedBuilder().success(sc.user, serverData, I18n.of("set_name", serverData).format(name))
					} catch (_: Exception) {
						EmbedBuilder().error(sc.user, serverData, I18n.of("invalid_arg", serverData))
					}
				}
				sc.createFollowupMessageBuilder().addEmbed(embed).send().get()

				dataService.saveServerData(server, serverData)
			}.get()
		}
	}

	@Suppress("StringFormatTrivial")
	override fun resetName(event: InteractionCreateEvent) {
		val sc = event.slashCommandInteraction.get()

		if (sc.fullCommandName == "reset_name") {
			sc.respondLater().thenAccept {
				val server = sc.server.get()
				val serverData = dataService.loadServerData(server)

				val embed = if (!accessService.fromManagerAtLeast(sc, serverData)) {
					EmbedBuilder().access(sc.user, serverData, I18n.of("no_access", serverData))
				} else {
					try {
						serverData.name = defaultName
						event.api.yourself.updateNickname(server, defaultName)

						EmbedBuilder().success(
							sc.user, serverData, I18n.of("reset_name", serverData).format(defaultName)
						)
					} catch (_: Exception) {
						EmbedBuilder().error(sc.user, serverData, I18n.of("invalid_arg", serverData))
					}
				}
				sc.createFollowupMessageBuilder().addEmbed(embed).send().get()

				dataService.saveServerData(server, serverData)
			}.get()
		}
	}
}