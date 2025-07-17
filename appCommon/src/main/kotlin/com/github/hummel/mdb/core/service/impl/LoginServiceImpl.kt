package com.github.hummel.mdb.core.service.impl

import com.github.hummel.mdb.core.bean.BotData
import com.github.hummel.mdb.core.controller.impl.DiscordControllerImpl
import com.github.hummel.mdb.core.service.LoginService
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag

class LoginServiceImpl : LoginService {
	override fun loginBot(impl: DiscordControllerImpl) {
		impl.api = JDABuilder.createDefault(BotData.token).apply {
			enableIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
			enableCache(CacheFlag.entries)
			setMemberCachePolicy(MemberCachePolicy.ALL)
		}.build().awaitReady()
	}

	override fun deleteCommands(impl: DiscordControllerImpl) {
		val commands = impl.api.retrieveCommands().complete()

		commands.forEach {
			impl.api.deleteCommandById(it.id).complete()

			println("${it.id}/${commands.size} was deleted.")
		}
	}

	override fun registerCommands(impl: DiscordControllerImpl) {
		fun String.cmd(description: String, options: List<OptionData>) =
			Commands.slash(this, description).addOptions(options)

		val commands = listOf(
			"clear_context".cmd("/clear_context", empty()),
			"complete".cmd("/complete [text]", string()),
			"info".cmd("/info", empty()),

			"add_birthday".cmd("/add_birthday [member_id] [month_number] [day_number]", string()),
			"add_manager".cmd("/add_manager [role_id]", string()),
			"add_secret_channel".cmd("/add_secret_channel [channel_id]", string()),
			"add_muted_channel".cmd("/add_muted_channel [channel_id]", string()),

			"clear_birthdays".cmd("/clear_birthdays {member_id}", string(false)),
			"clear_managers".cmd("/clear_managers {role_id}", string(false)),
			"clear_secret_channels".cmd("/clear_secret_channels {channel_id}", string(false)),
			"clear_muted_channels".cmd("/clear_muted_channels {channel_id}", string(false)),

			"clear_bank".cmd("/clear_messages", empty()),
			"clear_data".cmd("/clear_data", empty()),

			"set_chance_message".cmd("/set_chance_message [0..100]", string()),
			"set_chance_emoji".cmd("/set_chance_emoji [0..100]", string()),
			"set_chance_ai".cmd("/set_chance_ai [-1..100]", string()),

			"set_language".cmd("/set_language [ru/be/uk/en]", string()),

			"set_preprompt".cmd("/set_preprompt [text]", string()),
			"reset_preprompt".cmd("/reset_preprompt", empty()),

			"set_name".cmd("/set_name [text]", string()),
			"reset_name".cmd("/reset_name", empty()),

			"import".cmd("/import", attachment()),
			"export".cmd("/export", empty()),
			"exit".cmd("/exit", empty())
		)
		impl.api.updateCommands().addCommands(commands).complete()
	}

	private fun empty(): List<OptionData> = emptyList()

	private fun string(obligatory: Boolean = true): List<OptionData> = listOf(
		OptionData(OptionType.STRING, "Arguments", "The list of arguments", obligatory)
	)

	private fun attachment(): List<OptionData> = listOf(
		OptionData(OptionType.ATTACHMENT, "Arguments", "The list of arguments", true)
	)
}