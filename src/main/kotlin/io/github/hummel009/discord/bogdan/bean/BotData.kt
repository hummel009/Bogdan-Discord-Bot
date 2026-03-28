package io.github.hummel009.discord.bogdan.bean

object BotData {
	lateinit var root: String
	lateinit var discordToken: String
	lateinit var gptToken: String
	lateinit var ownerId: String

	val channelHistories: MutableMap<Long, MutableList<String>> = mutableMapOf(
		0L to mutableListOf(
			""
		)
	)
}