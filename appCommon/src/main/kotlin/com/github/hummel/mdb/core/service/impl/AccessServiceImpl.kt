package com.github.hummel.mdb.core.service.impl

import com.github.hummel.mdb.core.bean.BotData
import com.github.hummel.mdb.core.bean.ServerData
import com.github.hummel.mdb.core.service.AccessService
import org.javacord.api.interaction.SlashCommandInteraction

class AccessServiceImpl : AccessService {
	override fun fromManagerAtLeast(sc: SlashCommandInteraction, serverData: ServerData): Boolean {
		val server = sc.server.get()
		val user = sc.user
		return user.getRoles(server).any { role ->
			serverData.managers.any { it.id == role.id }
		} || user.id == BotData.ownerId.toLong() || user.isBotOwnerOrTeamMember || server.isAdmin(user)
	}

	override fun fromOwnerAtLeast(sc: SlashCommandInteraction): Boolean {
		val user = sc.user
		return user.id == BotData.ownerId.toLong() || user.isBotOwnerOrTeamMember
	}
}