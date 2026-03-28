package io.github.hummel009.discord.bogdan.service

import com.github.hummel.bogdan.bean.GuildData
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

interface AccessService {
	fun fromManagerAtLeast(event: SlashCommandInteractionEvent, guildData: GuildData): Boolean
	fun fromOwnerAtLeast(event: SlashCommandInteractionEvent): Boolean
}