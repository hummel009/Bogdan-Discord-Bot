package com.github.hummel.mdb.core.service

import com.github.hummel.mdb.core.bean.ServerData
import org.javacord.api.interaction.SlashCommandInteraction

interface AccessService {
	fun fromManagerAtLeast(sc: SlashCommandInteraction, serverData: ServerData): Boolean
	fun fromOwnerAtLeast(sc: SlashCommandInteraction): Boolean
}