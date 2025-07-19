package com.github.hummel.mdb.core.dao.impl

import com.github.hummel.mdb.core.dao.FileDao
import com.github.hummel.mdb.core.dao.JsonDao
import com.github.hummel.mdb.core.factory.DaoFactory
import com.github.hummel.mdb.core.utils.gson

private const val notExist: String = "File doesn't exist!"

class JsonDaoImpl : JsonDao {
	private val fileDao: FileDao = DaoFactory.fileDao

	override fun <T> readFromFile(filePath: String, clazz: Class<T>): T? {
		val file = fileDao.getFile(filePath)
		if (file.exists()) {
			try {
				val json = String(fileDao.readFromFile(filePath))
				return gson.fromJson(json, clazz)
			} catch (_: Exception) {
				return null
			}
		}
		return null
	}

	override fun <T> writeToFile(filePath: String, obj: T) {
		val file = fileDao.getFile(filePath)
		if (file.exists()) {
			val json = gson.toJson(obj)
			fileDao.writeToFile(filePath, json.toByteArray())
		} else {
			throw Exception(notExist)
		}
	}
}