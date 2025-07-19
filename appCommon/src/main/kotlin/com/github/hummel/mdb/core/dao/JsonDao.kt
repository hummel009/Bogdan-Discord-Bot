package com.github.hummel.mdb.core.dao

interface JsonDao {
	fun <T> readFromFile(filePath: String, clazz: Class<T>): T?
	fun <T> writeToFile(filePath: String, obj: T)
}