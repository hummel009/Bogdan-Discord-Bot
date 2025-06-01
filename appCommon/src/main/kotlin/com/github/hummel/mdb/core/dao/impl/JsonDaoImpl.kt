package com.github.hummel.mdb.core.dao.impl

import com.github.hummel.mdb.core.dao.FileDao
import com.github.hummel.mdb.core.dao.JsonDao
import com.github.hummel.mdb.core.factory.DaoFactory
import com.github.hummel.mdb.core.utils.gson

class JsonDaoImpl : JsonDao {
	private val fileDao: FileDao = DaoFactory.fileDao

	override fun <D> readFromJson(filePath: String, clazz: Class<D>): D? {
		return try {
			val json = fileDao.readFromFile(filePath)
			gson.fromJson(String(json), clazz)
		} catch (_: Exception) {
			null
		}
	}

	override fun <D> writeToJson(filePath: String, serverData: D) {
		val json = gson.toJson(serverData)
		fileDao.createFile(filePath)
		fileDao.writeToFile(filePath, json.toByteArray())
	}
}