package io.github.hummel009.discord.bogdan.utils

import com.google.gson.reflect.TypeToken
import io.github.hummel009.discord.bogdan.bean.GuildData
import java.io.InputStreamReader

class I18n private constructor(private val value: String, val lang: Lang) {
	companion object {
		private val cache: MutableMap<Lang, Map<String, String>> = mutableMapOf()

		fun of(key: String, lang: Lang, vararg args: Any?): I18n {
			val translations = cache.getOrPut(lang) {
				val inputStream = this::class.java.classLoader.getResourceAsStream("assets/lang/${lang.file}")!!

				InputStreamReader(inputStream, Charsets.UTF_8).use {
					gson.fromJson(it, object : TypeToken<Map<String, String>>() {}.type)
				}
			}

			val value = translations[key]?.format(*args) ?: "Invalid translation key: $key"

			return I18n(value, lang)
		}

		fun of(key: String, guildData: GuildData, vararg args: Any?): I18n = of(key, guildData.lang, *args)

		fun new(value: String, lang: Lang): I18n = I18n(value, lang)
	}

	fun s(): String = value

	override fun toString(): String = value
}

enum class Lang(val code: String, val file: String) {
	ENGLISH("en", "en_us.json"),
	RUSSIAN("ru", "ru_ru.json"),
	BELARUSIAN("be", "be_by.json"),
	UKRAINIAN("uk", "uk_ua.json");

	override fun toString(): String = code

	companion object {
		fun of(code: String): Lang? = entries.find { it.code == code }
	}
}