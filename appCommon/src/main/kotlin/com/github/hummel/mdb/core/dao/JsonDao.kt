package com.github.hummel.mdb.core.dao

interface JsonDao {
	fun <D> readFromJson(filePath: String, clazz: Class<D>): D?
	fun <D> writeToJson(filePath: String, serverData: D)
}