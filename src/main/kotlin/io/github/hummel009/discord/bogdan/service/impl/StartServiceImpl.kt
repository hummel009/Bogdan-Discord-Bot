package io.github.hummel009.discord.bogdan.service.impl

import io.github.hummel009.discord.bogdan.ApiHolder
import io.github.hummel009.discord.bogdan.service.StartService
import io.github.hummel009.discord.bogdan.utils.Lang
import io.github.hummel009.discord.bogdan.utils.config
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData

class StartServiceImpl : StartService {
	override fun recreateCommands() {
		if (!config.reinit) {
			return
		}

		val commands = listOf(
			withoutOptions("clear_context"),
			withoutOptions("exit"),
			withoutOptions("export"),
			withoutOptions("info"),
			withoutOptions("reset_name"),
			withoutOptions("reset_preprompt"),
			withoutOptions("wipe_bank"),
			withoutOptions("wipe_data"),

			withStringOption("add_birthday", "[member_id] [day_number] [month_number]"),
			withStringOption("add_excluded_channel", "[channel_id]"),
			withStringOption("add_manager_role", "[role_id]"),
			withStringOption("complete", "[text]"),
			withStringOption("set_chance_ai", "[-1..100]"),
			withStringOption("set_chance_emoji", "[0..100]"),
			withStringOption("set_chance_message", "[0..100]"),
			withStringOption("set_language", "[${Lang.entries.joinToString("|")}]"),
			withStringOption("set_name", "[text]"),
			withStringOption("set_preprompt", "[text]"),

			withStringOption("clear_birthdays", "{member_id}", false),
			withStringOption("clear_excluded_channels", "{channel_id}", false),
			withStringOption("clear_manager_roles", "{role_id}", false),

			withAttachmentOption("import", "[attachment]")
		)

		ApiHolder.discord.updateCommands().addCommands(commands).complete()
	}

	private fun withoutOptions(command: String): SlashCommandData =
		Commands.slash(command, "/$command").addOptions(emptyList())

	private fun withStringOption(command: String, parameters: String, obligatory: Boolean = true): SlashCommandData =
		Commands.slash(command, "/$command $parameters")
			.addOptions(OptionData(OptionType.STRING, "arguments", parameters, obligatory))

	private fun withAttachmentOption(command: String, parameters: String): SlashCommandData =
		Commands.slash(command, "/$command $parameters")
			.addOptions(OptionData(OptionType.ATTACHMENT, "arguments", parameters, true))
}