package com.github.hummel.bogdan.service.impl

import com.github.hummel.bogdan.bean.GuildData
import com.github.hummel.bogdan.dao.FileDao
import com.github.hummel.bogdan.dao.JsonDao
import com.github.hummel.bogdan.dao.ZipDao
import com.github.hummel.bogdan.factory.DaoFactory
import com.github.hummel.bogdan.service.DataService
import com.github.hummel.bogdan.utils.defaultName
import com.github.hummel.bogdan.utils.defaultPreprompt
import net.dv8tion.jda.api.entities.Guild
import java.time.LocalDate

class DataServiceImpl : DataService {
	private val fileDao: FileDao = DaoFactory.fileDao
	private val jsonDao: JsonDao = DaoFactory.jsonDao
	private val zipDao: ZipDao = DaoFactory.zipDao

	override fun loadGuildData(guild: Guild): GuildData {
		val folderName = guild.id
		val filePath = "guilds/$folderName/data.json"

		return jsonDao.readFromFile(filePath, GuildData::class.java) ?: initAndGet(guild)
	}

	override fun saveGuildData(guild: Guild, guildData: GuildData) {
		val folderName = guild.id
		val filePath = "guilds/$folderName/data.json"

		jsonDao.writeToFile(filePath, guildData)
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

	override fun saveMessage(guild: Guild, message: String) {
		fun encodeMessage(message: String) = message.map { it.code }.joinToString(" ")

		val folderName = guild.id
		val filePath = "guilds/$folderName/bank.bin"

		fileDao.appendToFile(filePath, encodeMessage(message).toByteArray())
		fileDao.appendToFile(filePath, "\r\n".toByteArray())
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

	private fun initAndGet(guild: Guild): GuildData {
		val folderName = guild.id
		val serverPath = "guilds/$folderName"
		val bankPath = "guilds/$folderName/bank.bin"
		val dataPath = "guilds/$folderName/data.json"

		fileDao.createEmptyFolder(serverPath)
		fileDao.createEmptyFile(dataPath)
		fileDao.createEmptyFile(bankPath)

		val yesterday = LocalDate.now().minusDays(1)
		val lastWish = GuildData.Date(yesterday.dayOfMonth, yesterday.monthValue)

		val guildData = GuildData(
			guildId = guild.idLong,
			guildName = guild.name,
			lang = "ru",
			chanceMessage = 10,
			chanceEmoji = 1,
			chanceAI = 0,
			managerRoleIds = mutableSetOf(),
			excludedChannelIds = mutableSetOf(),
			birthdays = mutableSetOf(),
			lastWish = lastWish,
			name = defaultName,
			preprompt = defaultPreprompt
		)

		jsonDao.writeToFile(dataPath, guildData)

		return guildData
	}
}