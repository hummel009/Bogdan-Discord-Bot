package com.github.hummel.mdb.core.service

import com.github.hummel.mdb.core.bean.GuildData
import net.dv8tion.jda.api.entities.Guild

interface DataService {
	fun loadGuildData(guild: Guild): GuildData
	fun saveGuildData(guild: Guild, guildData: GuildData)
	fun saveMessage(guild: Guild, message: String)
	fun getMessage(guild: Guild): String?
	fun wipeGuildBank(guild: Guild)
	fun wipeGuildData(guild: Guild)
	fun exportBotData(): ByteArray
	fun importBotData(byteArray: ByteArray)
}