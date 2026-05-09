package io.github.hummel009.discord.bogdan.utils

import com.google.gson.reflect.TypeToken
import io.github.hummel009.discord.bogdan.bean.GuildData
import java.io.InputStreamReader

class I18n(private val value: String, val lang: String) {
	companion object {
		private val cache: MutableMap<String, Map<String, String>> = mutableMapOf()

		fun of(key: String, lang: String, vararg args: Any?): I18n {
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
					gson.fromJson(it, object : TypeToken<Map<String, String>>() {}.type)
				}
			}

			val value = translations[key]?.format(*args) ?: "Invalid translation key: $key"

			return I18n(value, lang)
		}

		fun of(key: String, guildData: GuildData, vararg args: Any?): I18n = of(key, guildData.lang, *args)
	}

	fun s(): String = value

	override fun toString(): String = value
}
