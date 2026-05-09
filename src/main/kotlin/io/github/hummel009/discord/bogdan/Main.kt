package io.github.hummel009.discord.bogdan

import io.github.hummel009.discord.bogdan.factory.ServiceFactory
import io.github.hummel009.discord.bogdan.utils.gson
import io.github.hummel009.discord.bogdan.utils.input
import java.io.File
import java.io.FileWriter

data class Config(
	val discordToken: String, val aiToken: String, val aiModel: String, val ownerId: String, val reinit: Boolean
)

fun main() {
	ensureConfigExists()

	ApiHolder.establishConnections()
}

fun ensureConfigExists() {
	val file = File(input, "config.json")
	if (file.exists()) {
		return
	}

	print("Enter the Discord Token: ")
	val discordToken = readln()

	print("Enter the AI Token: ")
	val aiToken = readln()

	print("Enter the AI Model: ")
	val aiModel = readln()

	print("Enter the Owner ID: ")
	val ownerId = readln()

	print("Reinit? Type true/false: ")
	val reinit = readln()

	FileWriter(file).use {
		gson.toJson(Config(discordToken, aiToken, aiModel, ownerId, reinit.toBoolean()), it)
	}
}