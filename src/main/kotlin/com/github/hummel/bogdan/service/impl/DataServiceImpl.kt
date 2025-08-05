package com.github.hummel.bogdan.service.impl

import com.github.hummel.bogdan.bean.GuildData
import com.github.hummel.bogdan.dao.FileDao
import com.github.hummel.bogdan.dao.JsonDao
import com.github.hummel.bogdan.dao.ZipDao
import com.github.hummel.bogdan.factory.DaoFactory
import com.github.hummel.bogdan.service.DataService
import com.github.hummel.bogdan.utils.decode
import com.github.hummel.bogdan.utils.defaultName
import com.github.hummel.bogdan.utils.defaultPreprompt
import com.github.hummel.bogdan.utils.encode
import net.dv8tion.jda.api.entities.Guild
import java.time.LocalDate

class DataServiceImpl : DataService {
	private val fileDao: FileDao = DaoFactory.fileDao
	private val jsonDao: JsonDao = DaoFactory.jsonDao
	private val zipDao: ZipDao = DaoFactory.zipDao

	override fun loadGuildData(guild: Guild): GuildData {
		val folderName = guild.id
		val filePath = "guilds/$folderName/data.json"

		return jsonDao.readFromFile(filePath, GuildData::class.java) ?: initAndGetGuildData(guild)
	}

	override fun saveGuildData(guild: Guild, guildData: GuildData) {
		val folderName = guild.id
		val filePath = "guilds/$folderName/data.json"

		jsonDao.writeToFile(filePath, guildData)
	}

	override fun wipeGuildBank(guild: Guild) {
		val folderName = guild.id
		val filePath = "guilds/$folderName/bank.txt"

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
		val folderName = guild.id
		val filePath = "guilds/$folderName/bank.txt"

		val bank = String(fileDao.readFromFile(filePath))

		if (bank.isEmpty()) {
			return null
		}

		val messages = bank.lines()

		return messages.random().decode()
	}

	override fun saveMessage(guild: Guild, message: String) {
		val folderName = guild.id
		val filePath = "guilds/$folderName/bank.txt"

		val bank = String(fileDao.readFromFile(filePath))

		val messages = bank.lines().distinct().filterNot {
			it.isEmpty()
		}.toMutableList()

		messages.add(message.encode())

		if (messages.size > 10000) {
			messages.removeAt(0)
		}

		fileDao.writeToFile(filePath, messages.joinToString(separator = "\n").toByteArray())
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

	private fun initAndGetGuildData(guild: Guild): GuildData {
		val yesterday = LocalDate.now().minusDays(1)
		val lastWish = GuildData.Date(yesterday.dayOfMonth, yesterday.monthValue)

		return GuildData(
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
	}
}