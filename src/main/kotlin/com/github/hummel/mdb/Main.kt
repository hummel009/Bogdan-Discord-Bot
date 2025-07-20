package com.github.hummel.mdb

import com.github.hummel.mdb.bean.BotData
import com.github.hummel.mdb.factory.ServiceFactory
import com.github.hummel.mdb.utils.gson
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import kotlin.system.exitProcess

data class Config(
	val token: String?, val ownerId: String?
)

fun main() {
	try {
		val file = File("config.json")
		if (file.exists()) {
			FileReader(file).use { reader ->
				val config = gson.fromJson(reader, Config::class.java)
				if (config.token != null && config.ownerId != null) {
					launchWithData(config.token, config.ownerId, "files")
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
	print("Enter the Token: ")
	val token = readln()

	print("Enter the OwnerID: ")
	val ownerId = readln()

	val config = Config(token, ownerId)
	try {
		val file = File("config.json")
		if (!file.exists()) {
			FileWriter(file).use { writer ->
				gson.toJson(config, writer)
			}
		}
	} catch (e: Exception) {
		e.printStackTrace()
	}

	launchWithData(token, ownerId, "files")
}

@Suppress("UNUSED_PARAMETER")
fun launchWithData(token: String, ownerId: String, root: String) {
	BotData.token = token
	BotData.ownerId = ownerId
	BotData.root = root
	BotData.exitFunction = { exitFunction() }

	startFunction()
}

fun startFunction() {
	val loginService = ServiceFactory.loginService
	loginService.loginBot(false)
}

fun exitFunction() {
	exitProcess(0)
}