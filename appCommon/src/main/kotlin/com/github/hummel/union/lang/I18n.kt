package com.github.hummel.union.lang

import com.github.hummel.union.bean.ServerData
import com.github.hummel.union.utils.gson

object I18n {
	@Suppress("UNCHECKED_CAST")
	fun of(key: String, serverData: ServerData): String {
		val instance = when (serverData.lang) {
			"ru" -> LangRuRu
			"be" -> LangBeBy
			"uk" -> LangUkUa
			"en" -> LangEnUs
			else -> throw Exception()
		}

		val translation = gson.fromJson(instance.getJson(), Map::class.java) as Map<String, String>

		translation[key]?.let { return it }

		return "Invalid translation key!"
	}
}