package com.github.hummel.mdb.core.bean

data class ServerData(
	val dataVer: Int,
	val serverId: String,
	val serverName: String,
	var chanceMessage: Int,
	var chanceEmoji: Int,
	var chanceAI: Int,
	var lang: String,
	val lastWish: Date,
	val secretChannels: MutableSet<Channel>,
	val mutedChannels: MutableSet<Channel>,
	val managers: MutableSet<Role>,
	val birthdays: MutableSet<Birthday>,
	var preprompt: String,
	var name: String
) {
	data class Date(var day: Int, var month: Int)

	data class Role(var id: Long)

	data class Channel(var id: Long)

	data class Birthday(var id: Long, var date: Date)
}