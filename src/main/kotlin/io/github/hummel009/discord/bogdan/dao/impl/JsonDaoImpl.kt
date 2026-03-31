package io.github.hummel009.discord.bogdan.dao.impl

import io.github.hummel009.discord.bogdan.dao.FileDao
import io.github.hummel009.discord.bogdan.dao.JsonDao
import io.github.hummel009.discord.bogdan.factory.DaoFactory
import io.github.hummel009.discord.bogdan.utils.gson

class JsonDaoImpl : JsonDao {
	private val fileDao: FileDao = DaoFactory.fileDao

	override fun <T> readFromFile(filePath: String, clazz: Class<T>): T? {
		val file = fileDao.getFile(filePath)

		if (!file.exists()) {
			return null
		}

		try {
			val json = String(fileDao.readFromFile(filePath))
			return gson.fromJson(json, clazz)
		} catch (_: Exception) {
			return null
		}
	}

	override fun <T> writeToFile(filePath: String, obj: T) {
		val file = fileDao.getFile(filePath)

		if (!file.exists()) {
			fileDao.createEmptyFile(filePath)
		}

		val json = gson.toJson(obj)
		fileDao.writeToFile(filePath, json.toByteArray())
	}
}