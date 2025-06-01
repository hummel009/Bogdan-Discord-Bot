package com.github.hummel.mdb.windows

import com.github.hummel.mdb.core.bean.BotData
import com.google.gson.Gson
import java.io.File
import java.io.FileReader
import java.io.FileWriter

private val gson: Gson = Gson()

fun main() {
	val configFile = "start.json"

	try {
		val file = File(configFile)
		if (file.exists()) {
			FileReader(file).use { reader ->
				val config = gson.fromJson(reader, Config::class.java)
				if (config.token != null && config.ownerId != null) {
					launchService(config.token, config.ownerId, "files", null)
				} else {
					requestUserInput()
				}
			}
		} else {
			requestUserInput()
		}
	} catch (_: Exception) {
		requestUserInput()
	}
}

fun requestUserInput() {
	print("Введите token: ")
	val token = readln()

	print("Введите owner ID: ")
	val ownerId = readln()

	val config = Config(token, ownerId)
	val file = File("start.json")
	try {
		FileWriter(file).use { writer ->
			gson.toJson(config, writer)
		}
	} catch (e: Exception) {
		e.printStackTrace()
	}

	launchService(token, ownerId, "files", null)
}

@Suppress("UNUSED_PARAMETER", "RedundantSuppression", "unused")
fun launchService(token: String, ownerId: String, root: String, context: Any?) {
	BotData.token = token
	BotData.ownerId = ownerId
	BotData.root = root
	val adapter = DiscordAdapter()
	adapter.launch()
}

data class Config(
	val token: String?,
	val ownerId: String?
)