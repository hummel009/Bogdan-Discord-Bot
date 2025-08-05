package com.github.hummel.bogdan.service

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

interface ManagerService {
	fun setLanguage(event: SlashCommandInteractionEvent)

	fun addBirthday(event: SlashCommandInteractionEvent)
	fun clearBirthdays(event: SlashCommandInteractionEvent)

	fun addManagerRole(event: SlashCommandInteractionEvent)
	fun clearManagerRoles(event: SlashCommandInteractionEvent)

	fun addExcludedChannel(event: SlashCommandInteractionEvent)
	fun clearExcludedChannels(event: SlashCommandInteractionEvent)

	fun setChanceMessage(event: SlashCommandInteractionEvent)
	fun setChanceEmoji(event: SlashCommandInteractionEvent)
	fun setChanceAI(event: SlashCommandInteractionEvent)

	fun setName(event: SlashCommandInteractionEvent)
	fun resetName(event: SlashCommandInteractionEvent)

	fun setPreprompt(event: SlashCommandInteractionEvent)
	fun resetPreprompt(event: SlashCommandInteractionEvent)

	fun wipeData(event: SlashCommandInteractionEvent)
	fun wipeBank(event: SlashCommandInteractionEvent)
}