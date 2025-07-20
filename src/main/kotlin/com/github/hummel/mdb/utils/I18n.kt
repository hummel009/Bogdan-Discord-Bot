package com.github.hummel.mdb.utils

import com.github.hummel.mdb.bean.GuildData
import java.io.InputStreamReader

object I18n {
	private val cache: MutableMap<String, Map<String, String>> = mutableMapOf()

	@Suppress("UNCHECKED_CAST")
	fun of(key: String, guildData: GuildData): String {
		val lang = guildData.lang
		val translations = cache.getOrPut(lang) {
			val langFileName = when (lang) {
				"ru" -> "ru_ru.json"
				"be" -> "be_by.json"
				"uk" -> "uk_ua.json"
				"en" -> "en_us.json"
				else -> throw Exception("Unsupported language: $lang")
			}

			val inputStream = this::class.java.classLoader.getResourceAsStream("assets/lang/$langFileName")!!

			InputStreamReader(inputStream, Charsets.UTF_8).use {
				gson.fromJson(it, Map::class.java) as Map<String, String>
			}
		}

		return translations[key] ?: "Invalid translation key!"
	}
}
