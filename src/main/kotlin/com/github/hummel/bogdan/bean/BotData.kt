package com.github.hummel.bogdan.bean

object BotData {
	lateinit var root: String
	lateinit var token: String
	lateinit var ownerId: String
	lateinit var exitFunction: () -> Unit

	val channelHistories: MutableMap<Long, MutableList<String>> = mutableMapOf(
		0L to mutableListOf(
			""
		)
	)
}