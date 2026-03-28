package io.github.hummel009.discord.bogdan.service.impl

import io.github.hummel009.discord.bogdan.bean.BotData
import io.github.hummel009.discord.bogdan.bean.GuildData
import io.github.hummel009.discord.bogdan.service.AccessService
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class AccessServiceImpl : AccessService {
	override fun fromManagerAtLeast(event: SlashCommandInteractionEvent, guildData: GuildData): Boolean {
		val member = event.member ?: return false

		val isManager = member.roles.any { role ->
			guildData.managerRoleIds.any {
				it == role.idLong
			}
		}
		val isOwner = member.idLong == BotData.ownerId.toLong()
		val isAdmin = member.hasPermission(Permission.ADMINISTRATOR)

		return isManager || isAdmin || isOwner
	}

	override fun fromOwnerAtLeast(event: SlashCommandInteractionEvent): Boolean {
		val member = event.member ?: return false

		val isOwner = member.idLong == BotData.ownerId.toLong()

		return isOwner
	}
}