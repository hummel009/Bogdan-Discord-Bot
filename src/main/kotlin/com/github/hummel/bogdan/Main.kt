package com.github.hummel.bogdan

import com.github.hummel.bogdan.bean.BotData
import com.github.hummel.bogdan.factory.ServiceFactory
import com.github.hummel.bogdan.utils.gson
import java.io.File
import java.io.FileReader
import java.io.FileWriter

data class Config(
	val discordToken: String, val ownerId: String, val gptToken: String, val reinit: Boolean
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
	print("Enter the Discord Token: ")
	val discordToken = readln()

	print("Enter the Owner ID: ")
	val ownerId = readln()

	print("Enter the GPT Token: ")
	val gptToken = readln()

	print("Reinit? Type true/false: ")
	val reinit = readln()

	val config = Config(discordToken, ownerId, gptToken, reinit.toBoolean())
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

fun launchWithData(config: Config, root: String) {
	BotData.discordToken = config.discordToken
	BotData.gptToken = config.gptToken
	BotData.ownerId = config.ownerId
	BotData.root = root

	val loginService = ServiceFactory.loginService
	loginService.loginBot(config.reinit)
}