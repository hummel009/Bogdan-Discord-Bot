package com.github.hummel.bogdan.bean

data class GuildData(
	val guildId: Long,
	val guildName: String,
	var chanceMessage: Int,
	var chanceEmoji: Int,
	var chanceAI: Int,
	var lang: String,
	val secretChannelIds: MutableSet<Long>,
	val mutedChannelIds: MutableSet<Long>,
	val managerRoleIds: MutableSet<Long>,
	val birthdays: MutableSet<Birthday>,
	val lastWish: Date,
	var name: String,
	var preprompt: String
) {
	data class Date(var day: Int, var month: Int)
	data class Birthday(var memberId: Long, var date: Date)
}