package com.github.hummel.mdb.service

import com.github.hummel.mdb.bean.GuildData
import net.dv8tion.jda.api.entities.Guild

interface DataService {
	fun loadGuildData(guild: Guild): GuildData
	fun saveGuildData(guild: Guild, guildData: GuildData)

	fun wipeGuildBank(guild: Guild)
	fun wipeGuildData(guild: Guild)

	fun getMessage(guild: Guild): String?
	fun saveMessage(guild: Guild, message: String)

	fun exportBotData(): ByteArray
	fun importBotData(byteArray: ByteArray)
}