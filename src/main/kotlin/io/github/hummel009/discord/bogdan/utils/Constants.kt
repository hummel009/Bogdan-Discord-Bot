package io.github.hummel009.discord.bogdan.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.github.hummel009.discord.bogdan.Config
import java.io.File
import java.io.FileReader

val gson: Gson = GsonBuilder().setPrettyPrinting().create()

val input: File by lazy {
	File("input").apply {
		if (!exists()) {
			mkdirs()
		}
	}
}

val output: File by lazy {
	File("output").apply {
		if (!exists()) {
			mkdirs()
		}
	}
}

val config: Config by lazy {
	FileReader(File(input, "config.json")).use {
		gson.fromJson(it, Config::class.java)
	}
}