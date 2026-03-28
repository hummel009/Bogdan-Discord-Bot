package io.github.hummel009.discord.bogdan

import io.github.hummel009.discord.bogdan.bean.BotData
import io.github.hummel009.discord.bogdan.factory.ServiceFactory
import io.github.hummel009.discord.bogdan.utils.gson
import java.io.File
import java.io.FileReader
import java.io.FileWriter

data class Config(
	val discordToken: String, val gptToken: String, val ownerId: String, val reinit: Boolean
)

fun main() {
	try {
		val file = File("input/config.json")
		if (file.exists()) {
			FileReader(file).use {
				val config = _root_ide_package_.io.github.hummel009.discord.bogdan.utils.gson.fromJson(it, _root_ide_package_.io.github.hummel009.discord.bogdan.Config::class.java)

				_root_ide_package_.io.github.hummel009.discord.bogdan.launchWithData(config, "output")
			}
		} else {
			_root_ide_package_.io.github.hummel009.discord.bogdan.requestUserInput()
		}
	} catch (_: Exception) {
		_root_ide_package_.io.github.hummel009.discord.bogdan.requestUserInput()
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

	val config = _root_ide_package_.io.github.hummel009.discord.bogdan.Config(
		discordToken,
		ownerId,
		gptToken,
		reinit.toBoolean()
	)
	try {
		val file = File("input/config.json")
		FileWriter(file).use {
			_root_ide_package_.io.github.hummel009.discord.bogdan.utils.gson.toJson(config, it)
		}
	} catch (e: Exception) {
		e.printStackTrace()
	}

	_root_ide_package_.io.github.hummel009.discord.bogdan.launchWithData(config, "output")
}

fun launchWithData(config: io.github.hummel009.discord.bogdan.Config, root: String) {
	_root_ide_package_.io.github.hummel009.discord.bogdan.bean.BotData.discordToken = config.discordToken
	_root_ide_package_.io.github.hummel009.discord.bogdan.bean.BotData.gptToken = config.gptToken
	_root_ide_package_.io.github.hummel009.discord.bogdan.bean.BotData.ownerId = config.ownerId
	_root_ide_package_.io.github.hummel009.discord.bogdan.bean.BotData.root = root

	val loginService = _root_ide_package_.io.github.hummel009.discord.bogdan.factory.ServiceFactory.loginService
	loginService.loginBot(config.reinit)
}
