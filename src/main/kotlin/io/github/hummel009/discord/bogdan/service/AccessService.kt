package io.github.hummel009.discord.bogdan.service

import io.github.hummel009.discord.bogdan.bean.GuildData
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

interface AccessService {
	fun fromManagerAtLeast(event: SlashCommandInteractionEvent, guildData: GuildData): Boolean
	fun fromOwnerAtLeast(event: SlashCommandInteractionEvent): Boolean
}