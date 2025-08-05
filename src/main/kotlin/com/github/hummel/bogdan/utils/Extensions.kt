package com.github.hummel.bogdan.utils

import com.github.hummel.bogdan.bean.GuildData
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.MessageEmbed
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase
import java.util.*

fun EmbedBuilder.success(member: Member?, guildData: GuildData, desc: String): MessageEmbed = apply {
	member ?: return@apply

	setAuthor(member.effectiveName, null, member.effectiveAvatarUrl)
	setTitle(I18n.of("title_success", guildData))
	setDescription(desc)
	setColor(0x00FF00)
}.build()

fun EmbedBuilder.access(member: Member?, guildData: GuildData, desc: String): MessageEmbed = apply {
	member ?: return@apply

	setAuthor(member.effectiveName, null, member.effectiveAvatarUrl)
	setTitle(I18n.of("title_access", guildData))
	setDescription(desc)
	setColor(0xFFFF00)
}.build()

fun EmbedBuilder.error(member: Member?, guildData: GuildData, desc: String): MessageEmbed = apply {
	member ?: return@apply

	setAuthor(member.effectiveName, null, member.effectiveAvatarUrl)
	setTitle(I18n.of("title_error", guildData))
	setDescription(desc)
	setColor(0xFF0000)
}.build()

fun String.build(name: String, preprompt: String): String =
	trimIndent().replace("\t", "").replace("\n", " ").format(name, name, preprompt) + "\n"


fun HttpUriRequestBase.setHeaders(vararg headers: Pair<String, String>) {
	headers.forEach { (name, value) ->
		setHeader(name, value)
	}
}

fun String.encode(): String {
	val bytes = toByteArray(Charsets.UTF_8)
	return Base64.getEncoder().encodeToString(bytes).reversed()
}

fun String.decode(): String {
	val bytes = Base64.getDecoder().decode(this)
	return bytes.toString(Charsets.UTF_8).reversed()
}