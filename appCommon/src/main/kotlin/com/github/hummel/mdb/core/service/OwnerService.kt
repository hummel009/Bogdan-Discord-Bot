package com.github.hummel.mdb.core.service

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

interface OwnerService {
	fun import(event: SlashCommandInteractionEvent)
	fun export(event: SlashCommandInteractionEvent)
	fun exit(event: SlashCommandInteractionEvent)
}