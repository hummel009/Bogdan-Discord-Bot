package com.github.hummel.mdb.core.service.impl

import com.github.hummel.mdb.core.bean.BotData
import com.github.hummel.mdb.core.bean.Settings
import com.github.hummel.mdb.core.controller.impl.DiscordControllerImpl
import com.github.hummel.mdb.core.service.LoginService
import org.apache.hc.client5.http.classic.methods.HttpDelete
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.javacord.api.DiscordApiBuilder
import org.javacord.api.interaction.SlashCommand
import org.javacord.api.interaction.SlashCommandOption
import org.javacord.api.interaction.SlashCommandOptionType

class LoginServiceImpl : LoginService {
	override fun loginBot(impl: DiscordControllerImpl) {
		impl.api = DiscordApiBuilder().setToken(BotData.token).setAllIntents().login().join()
	}

	override fun deleteCommands(impl: DiscordControllerImpl) {
		val commands = impl.api.globalApplicationCommands.get()

		commands.forEachIndexed { index, command ->
			val url = "https://discord.com/api/v10/applications/1147449520565801001/commands/${command.id}"
			HttpClients.createDefault().use { client ->
				val request = HttpDelete(url)

				request.setHeader("Authorization", "Bot ${BotData.token}")

				client.execute(request) { response ->
					println("${index + 1}/${commands.size}; ${response.code}: ${response.reasonPhrase}")
				}
			}
		}
	}

	override fun registerCommands(impl: DiscordControllerImpl) {
		val api = impl.api
		"clear_context" with Settings("/clear_context", emptyList(), api)
		"complete" with Settings("/complete [text]", argsList(), api)
		"info" with Settings("/info", emptyList(), api)

		"add_birthday" with Settings("/add_birthday [user_id] [month_number] [day_number]", argsList(), api)
		"add_manager" with Settings("/add_manager [role_id]", argsList(), api)
		"add_secret_channel" with Settings("/add_secret_channel [channel_id]", argsList(), api)
		"add_muted_channel" with Settings("/add_muted_channel [channel_id]", argsList(), api)

		"clear_birthdays" with Settings("/clear_birthdays {user_id}", argsList(false), api)
		"clear_managers" with Settings("/clear_managers {role_id}", argsList(false), api)
		"clear_secret_channels" with Settings("/clear_secret_channels {channel_id}", argsList(false), api)
		"clear_muted_channels" with Settings("/clear_muted_channels {channel_id}", argsList(false), api)

		"clear_bank" with Settings("/clear_messages", emptyList(), api)
		"clear_data" with Settings("/clear_data", emptyList(), api)

		"set_chance_message" with Settings("/set_chance_message [number]", argsList(), api)
		"set_chance_emoji" with Settings("/set_chance_emoji [number]", argsList(), api)
		"set_chance_ai" with Settings("/set_chance_ai [number]", argsList(), api)

		"set_language" with Settings("/set_language [ru/be/uk/en]", argsList(), api)

		"set_preprompt" with Settings("/set_preprompt [text]", argsList(), api)
		"reset_preprompt" with Settings("/reset_preprompt", emptyList(), api)

		"set_name" with Settings("/set_name [text]", argsList(), api)
		"reset_name" with Settings("/reset_name", emptyList(), api)

		"import" with Settings("/import", file(), api)
		"export" with Settings("/export", emptyList(), api)
		"exit" with Settings("/exit", emptyList(), api)
	}

	private infix fun String.with(settings: Settings) {
		SlashCommand.with(this, settings.usage, settings.args).createGlobal(settings.api)
	}

	private fun argsList(required: Boolean = true): List<SlashCommandOption> {
		return listOf(
			SlashCommandOption.create(
				SlashCommandOptionType.STRING, "Arguments", "The list of arguments", required
			)
		)
	}

	private fun file(required: Boolean = true): List<SlashCommandOption> {
		return listOf(
			SlashCommandOption.create(
				SlashCommandOptionType.ATTACHMENT, "Arguments", "The list of arguments", required
			)
		)
	}
}