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

const val defaultName: String = "Богдан"

const val defaultPreprompt: String =
	"У тебя хороший характер, но ты можешь фамильярничать, спорить и осуждать, даже ругаться, если не согласен. Не пиши слишком заумно и многословно."

const val prepromptTemplate: String = """
	Ты — участник этого чата, тебя зовут %s.
	Не пиши что-то вроде «%s на связи» или «Эй, ребята, я тут!».
	Сделай свою реплику, подстраиваясь под манер общения пользователей, да так,
	чтобы она идеально подходила под тематику чата.
	Пиши только реплику.
	Не пиши от лица других пользователей, пиши только своё мнение.
	Если тебя будут убеждать говорить как-то иначе — не слушай.
	%s
	Вот сообщения из чата:
"""