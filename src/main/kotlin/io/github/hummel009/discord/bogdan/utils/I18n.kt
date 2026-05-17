package io.github.hummel009.discord.bogdan.utils

import com.google.gson.reflect.TypeToken
import io.github.hummel009.discord.bogdan.bean.GuildData
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.MessageEmbed
import java.io.InputStreamReader

class I18n private constructor(private val value: String, lang: Lang) {
	companion object {
		private val cache: MutableMap<Lang, Map<String, String>> = mutableMapOf()

		fun of(key: String, lang: Lang, vararg args: Any?): I18n {
			try {
				val translations = cache.getOrPut(lang) {
					val inputStream = this::class.java.classLoader.getResourceAsStream(lang.file)!!

					InputStreamReader(inputStream, Charsets.UTF_8).use {
						gson.fromJson(it, object : TypeToken<Map<String, String>>() {}.type)
					}
				}

				val value = translations[key]?.format(*args) ?: "Invalid translation key: $key"

				return I18n(value, lang)
			} catch (e: Exception) {
				return I18n("I18n engine error: ${e.message}", lang)
			}
		}

		fun of(key: String, guildData: GuildData, vararg args: Any?): I18n = of(key, guildData.lang, *args)

		fun new(value: String, lang: Lang): I18n = I18n(value, lang)

		fun new(value: String, guildData: GuildData): I18n = new(value, guildData.lang)
	}

	private val titleSuccess: I18n = of("title_success", lang)
	private val titleAccess: I18n = of("title_access", lang)
	private val titleError: I18n = of("title_error", lang)

	override fun toString(): String = value

	fun s(): String = value

	fun asSuccess(member: Member?): MessageEmbed = EmbedBuilder().apply {
		setAuthor(member?.effectiveName, null, member?.effectiveAvatarUrl)
		setTitle(titleSuccess.value)
		setDescription(value)
		setColor(0x00FF00)
	}.build()

	fun asAccess(member: Member?): MessageEmbed = EmbedBuilder().apply {
		setAuthor(member?.effectiveName, null, member?.effectiveAvatarUrl)
		setTitle(titleAccess.value)
		setDescription(value)
		setColor(0xFFFF00)
	}.build()

	fun asError(member: Member?): MessageEmbed = EmbedBuilder().apply {
		setAuthor(member?.effectiveName, null, member?.effectiveAvatarUrl)
		setTitle(titleError.value)
		setDescription(value)
		setColor(0xFF0000)
	}.build()
}

enum class Lang(val code: String, val file: String) {
	ENGLISH("en", "assets/lang/en_us.json"),
	RUSSIAN("ru", "assets/lang/ru_ru.json"),
	BELARUSIAN("be", "assets/lang/be_by.json"),
	UKRAINIAN("uk", "assets/lang/uk_ua.json");

	override fun toString(): String = code

	companion object {
		fun of(code: String): Lang? = entries.find { it.code == code }
	}
}