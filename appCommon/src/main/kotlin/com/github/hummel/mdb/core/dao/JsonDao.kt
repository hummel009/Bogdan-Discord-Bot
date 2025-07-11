package com.github.hummel.mdb.core.dao

interface JsonDao {
	fun <T> readFromJson(filePath: String, clazz: Class<T>): T?
	fun <T> writeToJson(filePath: String, obj: T)
}