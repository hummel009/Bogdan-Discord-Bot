package com.github.hummel.bogdan.service.impl

import com.github.hummel.bogdan.ApiHolder
import com.github.hummel.bogdan.bean.BotData
import com.github.hummel.bogdan.handler.EventHandler
import com.github.hummel.bogdan.service.LoginService
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag

class LoginServiceImpl : LoginService {
	override fun loginBot(reinit: Boolean) {
		ApiHolder.discord = JDABuilder.createDefault(BotData.discordToken).apply {
			enableIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
			enableCache(CacheFlag.entries)
			setMemberCachePolicy(MemberCachePolicy.ALL)
			addEventListeners(EventHandler)
		}.build().awaitReady()

		if (reinit) {
			recreateCommands()
		}
	}

	private fun recreateCommands() {
		fun String.cmd(description: String, options: List<OptionData>) =
			Commands.slash(this, description).addOptions(options)

		val commands = listOf(
			"info".cmd("/info", empty()),
			"complete".cmd("/complete [text]", string()),
			"clear_context".cmd("/clear_context", empty()),

			"add_birthday".cmd("/add_birthday [member_id] [month_number] [day_number]", string()),
			"add_manager_role".cmd("/add_manager_role [role_id]", string()),
			"add_excluded_channel".cmd("/add_excluded_channel [channel_id]", string()),

			"clear_birthdays".cmd("/clear_birthdays {member_id}", string(false)),
			"clear_manager_roles".cmd("/clear_manager_roles {role_id}", string(false)),
			"clear_excluded_channels".cmd("/clear_excluded_channels {channel_id}", string(false)),

			"set_chance_message".cmd("/set_chance_message [0..100]", string()),
			"set_chance_emoji".cmd("/set_chance_emoji [0..100]", string()),
			"set_chance_ai".cmd("/set_chance_ai [-1..100]", string()),

			"set_language".cmd("/set_language [ru/be/uk/en]", string()),

			"set_name".cmd("/set_name [text]", string()),
			"set_preprompt".cmd("/set_preprompt [text]", string()),
			"reset_name".cmd("/reset_name", empty()),
			"reset_preprompt".cmd("/reset_preprompt", empty()),

			"wipe_bank".cmd("/wipe_bank", empty()),
			"wipe_data".cmd("/wipe_data", empty()),

			"import".cmd("/import", attachment()),
			"export".cmd("/export", empty()),
			"exit".cmd("/exit", empty())
		)
		ApiHolder.discord.updateCommands().addCommands(commands).complete()
	}

	private fun empty(): List<OptionData> = emptyList()

	private fun string(obligatory: Boolean = true): List<OptionData> = listOf(
		OptionData(OptionType.STRING, "arguments", "The list of arguments", obligatory)
	)

	private fun attachment(): List<OptionData> = listOf(
		OptionData(OptionType.ATTACHMENT, "arguments", "The list of arguments", true)
	)
}