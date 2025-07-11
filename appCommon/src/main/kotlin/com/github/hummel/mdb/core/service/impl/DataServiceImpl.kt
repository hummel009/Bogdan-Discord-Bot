package com.github.hummel.mdb.core.service.impl

import com.github.hummel.mdb.core.bean.GuildData
import com.github.hummel.mdb.core.dao.FileDao
import com.github.hummel.mdb.core.dao.JsonDao
import com.github.hummel.mdb.core.dao.ZipDao
import com.github.hummel.mdb.core.factory.DaoFactory
import com.github.hummel.mdb.core.service.DataService
import com.github.hummel.mdb.core.utils.defaultName
import com.github.hummel.mdb.core.utils.defaultPreprompt
import com.github.hummel.mdb.core.utils.version
import net.dv8tion.jda.api.entities.Guild
import java.time.LocalDate

class DataServiceImpl : DataService {
	private val fileDao: FileDao = DaoFactory.fileDao
	private val jsonDao: JsonDao = DaoFactory.jsonDao
	private val zipDao: ZipDao = DaoFactory.zipDao

	override fun loadGuildData(guild: Guild): GuildData {
		val folderName = guild.id
		val filePath = "guilds/$folderName/data.json"

		return jsonDao.readFromJson(filePath, GuildData::class.java) ?: initAndGet(guild)
	}

	override fun saveGuildData(guild: Guild, guildData: GuildData) {
		val folderName = guild.id
		val filePath = "guilds/$folderName/data.json"

		jsonDao.writeToJson(filePath, guildData)
	}

	override fun saveMessage(guild: Guild, message: String) {
		fun encodeMessage(message: String) = message.map { it.code }.joinToString(" ")

		val folderName = guild.id
		val filePath = "guilds/$folderName/bank.bin"

		fileDao.appendToFile(filePath, encodeMessage(message).toByteArray())
		fileDao.appendToFile(filePath, "\r\n".toByteArray())
	}

	override fun getMessage(guild: Guild): String? {
		fun decodeMessage(encoded: String) = encoded.split(" ").map { it.toInt() }.map { it.toChar() }.joinToString("")

		val folderName = guild.id
		val filePath = "guilds/$folderName/bank.bin"

		val messages = String(fileDao.readFromFile(filePath)).lines()

		if (messages.isEmpty()) {
			return null
		}

		return decodeMessage(messages.random())
	}

	override fun wipeGuildBank(guild: Guild) {
		val folderName = guild.id
		val filePath = "guilds/$folderName/bank.bin"

		fileDao.removeFile(filePath)
		fileDao.createEmptyFile(filePath)
	}

	override fun wipeGuildData(guild: Guild) {
		val folderName = guild.id
		val filePath = "guilds/$folderName/data.json"

		fileDao.removeFile(filePath)
		fileDao.createEmptyFile(filePath)
	}

	override fun importBotData(byteArray: ByteArray) {
		val targetFolderPath = "guilds"
		val importFolderPath = "import"
		val importFilePath = "import/bot.zip"

		fileDao.createEmptyFolder(importFolderPath)
		fileDao.createEmptyFile(importFilePath)
		fileDao.writeToFile(importFilePath, byteArray)

		fileDao.removeFolder(targetFolderPath)
		fileDao.createEmptyFolder(targetFolderPath)

		zipDao.unzipFileToFolder(importFilePath, targetFolderPath)

		fileDao.removeFile(importFilePath)
		fileDao.removeFolder(importFolderPath)
	}

	override fun exportBotData(): ByteArray {
		val targetFolderPath = "guilds"
		val exportFolderPath = "export"
		val exportFilePath = "export/bot.zip"

		fileDao.createEmptyFolder(exportFolderPath)
		zipDao.zipFolderToFile(targetFolderPath, exportFilePath)

		val file = fileDao.readFromFile(exportFilePath)

		fileDao.removeFile(exportFilePath)
		fileDao.removeFolder(exportFolderPath)

		return file
	}

	private fun initAndGet(guild: Guild): GuildData {
		val folderName = guild.id
		val serverPath = "guilds/$folderName"
		val bankPath = "guilds/$folderName/bank.bin"
		val dataPath = "guilds/$folderName/data.json"

		fileDao.createEmptyFolder(serverPath)
		fileDao.createEmptyFile(dataPath)
		fileDao.createEmptyFile(bankPath)

		val guildId = guild.id
		val guildName = guild.name
		val chanceMessage = 10
		val chanceEmoji = 1
		val chanceAI = 20
		val lang = "ru"
		val yesterday = LocalDate.now().minusDays(1)
		val lastWish = GuildData.Date(yesterday.dayOfMonth, yesterday.monthValue)

		val guildData = GuildData(
			dataVer = version,
			guildId = guildId,
			guildName = guildName,
			chanceMessage = chanceMessage,
			chanceEmoji = chanceEmoji,
			chanceAI = chanceAI,
			lang = lang,
			lastWish = lastWish,
			secretChannels = mutableSetOf(),
			mutedChannels = mutableSetOf(),
			managers = mutableSetOf(),
			birthdays = mutableSetOf(),
			preprompt = defaultPreprompt,
			name = defaultName
		)

		jsonDao.writeToJson(dataPath, guildData)

		return guildData
	}
}