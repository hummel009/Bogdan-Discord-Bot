package io.github.hummel009.discord.bogdan.utils

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel

fun EmbedBuilder.success(member: Member?, i18n: I18n): MessageEmbed = apply {
	setAuthor(member?.effectiveName, null, member?.effectiveAvatarUrl)
	setTitle(I18n.of("title_success", i18n.lang).s())
	setDescription(i18n.s())
	setColor(0x00FF00)
}.build()

fun EmbedBuilder.access(member: Member?, i18n: I18n): MessageEmbed = apply {
	setAuthor(member?.effectiveName, null, member?.effectiveAvatarUrl)
	setTitle(I18n.of("title_access", i18n.lang).s())
	setDescription(i18n.s())
	setColor(0xFFFF00)
}.build()

fun EmbedBuilder.error(member: Member?, i18n: I18n): MessageEmbed = apply {
	setAuthor(member?.effectiveName, null, member?.effectiveAvatarUrl)
	setTitle(I18n.of("title_error", i18n.lang).s())
	setDescription(i18n.s())
	setColor(0xFF0000)
}.build()

fun Guild.getMessageChannelById(id: Long): GuildMessageChannel? = getTextChannelById(id) ?: getThreadChannelById(id)