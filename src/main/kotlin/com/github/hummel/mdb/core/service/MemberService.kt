package com.github.hummel.mdb.core.service

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

interface MemberService {
	fun info(event: SlashCommandInteractionEvent)
	fun complete(event: SlashCommandInteractionEvent)
	fun clearContext(event: SlashCommandInteractionEvent)
}