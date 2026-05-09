package io.github.hummel009.discord.bogdan.utils

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.MessageEmbed

fun EmbedBuilder.success(member: Member?, translation: I18n): MessageEmbed = apply {
	member ?: return@apply

	setAuthor(member.effectiveName, null, member.effectiveAvatarUrl)
	setTitle(I18n.of("title_success", translation.lang).s())
	setDescription(translation.s())
	setColor(0x00FF00)
}.build()

fun EmbedBuilder.access(member: Member?, translation: I18n): MessageEmbed = apply {
	member ?: return@apply

	setAuthor(member.effectiveName, null, member.effectiveAvatarUrl)
	setTitle(I18n.of("title_access", translation.lang).s())
	setDescription(translation.s())
	setColor(0xFFFF00)
}.build()

fun EmbedBuilder.error(member: Member?, translation: I18n): MessageEmbed = apply {
	member ?: return@apply

	setAuthor(member.effectiveName, null, member.effectiveAvatarUrl)
	setTitle(I18n.of("title_error", translation.lang).s())
	setDescription(translation.s())
	setColor(0xFF0000)
}.build()