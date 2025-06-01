package com.github.hummel.mdb.core.bean

object BotData {
	lateinit var token: String
	lateinit var ownerId: String
	lateinit var root: String
	lateinit var exitFunction: () -> Unit

	val channelHistories: MutableMap<Long, MutableList<String>> = mutableMapOf(
		0L to mutableListOf(
			""
		)
	)
}