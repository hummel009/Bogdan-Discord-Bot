package com.github.hummel.bogdan

import com.github.hummel.bogdan.bean.BotData
import com.github.hummel.bogdan.factory.ServiceFactory
import com.github.hummel.bogdan.utils.gson
import java.io.File
import java.io.FileReader
import java.io.FileWriter

data class Config(
	val token: String, val ownerId: String, val reinit: Boolean?
)

fun main() {
	try {
		val file = File("config.json")
		if (file.exists()) {
			FileReader(file).use {
				val config = gson.fromJson(it, Config::class.java)

				launchWithData(config, "files")
			}
		} else {
			requestUserInput()
		}
	} catch (_: Exception) {
		requestUserInput()
	}
}

fun requestUserInput() {
	print("Enter the Token: ")
	val token = readln()

	print("Enter the Owner ID: ")
	val ownerId = readln()

	print("Reinit? Type true/false: ")
	val reinit = readln()

	val config = Config(token, ownerId, reinit.toBoolean())
	try {
		val file = File("config.json")
		FileWriter(file).use {
			gson.toJson(config, it)
		}
	} catch (e: Exception) {
		e.printStackTrace()
	}

	launchWithData(config, "files")
}

@Suppress("UNUSED_PARAMETER")
fun launchWithData(config: Config, root: String) {
	BotData.token = config.token
	BotData.ownerId = config.ownerId
	BotData.root = root

	val loginService = ServiceFactory.loginService
	loginService.loginBot(config.reinit ?: false)
}