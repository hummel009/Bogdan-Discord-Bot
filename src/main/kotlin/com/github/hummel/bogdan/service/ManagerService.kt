package com.github.hummel.bogdan.service

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

interface ManagerService {
	fun addBirthday(event: SlashCommandInteractionEvent)
	fun addManagerRole(event: SlashCommandInteractionEvent)
	fun addSecretChannel(event: SlashCommandInteractionEvent)
	fun addMutedChannel(event: SlashCommandInteractionEvent)

	fun clearBirthdays(event: SlashCommandInteractionEvent)
	fun clearManagerRoles(event: SlashCommandInteractionEvent)
	fun clearSecretChannels(event: SlashCommandInteractionEvent)
	fun clearMutedChannels(event: SlashCommandInteractionEvent)

	fun setLanguage(event: SlashCommandInteractionEvent)

	fun setChanceMessage(event: SlashCommandInteractionEvent)
	fun setChanceEmoji(event: SlashCommandInteractionEvent)
	fun setChanceAI(event: SlashCommandInteractionEvent)

	fun setName(event: SlashCommandInteractionEvent)
	fun setPreprompt(event: SlashCommandInteractionEvent)
	fun resetName(event: SlashCommandInteractionEvent)
	fun resetPreprompt(event: SlashCommandInteractionEvent)

	fun wipeData(event: SlashCommandInteractionEvent)
	fun wipeBank(event: SlashCommandInteractionEvent)
}