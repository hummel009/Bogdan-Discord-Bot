package io.github.hummel009.discord.bogdan.utils

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel

fun Guild.getMessageChannelById(id: Long): GuildMessageChannel? = getTextChannelById(id) ?: getThreadChannelById(id)