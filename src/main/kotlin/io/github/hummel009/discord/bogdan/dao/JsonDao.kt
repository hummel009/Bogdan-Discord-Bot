package io.github.hummel009.discord.bogdan.dao

interface JsonDao {
	fun <T> readFromFile(filePath: String, clazz: Class<T>): T?
	fun <T> writeToFile(filePath: String, obj: T)
}