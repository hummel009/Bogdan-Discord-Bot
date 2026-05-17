package io.github.hummel009.discord.bogdan.service.impl

import io.github.hummel009.discord.bogdan.bean.GuildData
import io.github.hummel009.discord.bogdan.service.AccessService
import io.github.hummel009.discord.bogdan.utils.I18n
import io.github.hummel009.discord.bogdan.utils.access
import io.github.hummel009.discord.bogdan.utils.config
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class AccessServiceImpl : AccessService {
	override fun managerAccessRestricted(event: SlashCommandInteractionEvent, guildData: GuildData): MessageEmbed? {
		val embed = EmbedBuilder().access(event.member, I18n.of("msg_access", guildData)).takeUnless {
			val member = event.member ?: return@takeUnless false

			member.isGuildManager(guildData) || member.isGuildAdmin() || member.isGuildOwner() || member.isBotOwner()
		}

		embed?.let { event.hook.sendMessageEmbeds(it).queue() }

		return embed
	}

	override fun ownerAccessRestricted(event: SlashCommandInteractionEvent, guildData: GuildData): MessageEmbed? {
		val embed = EmbedBuilder().access(event.member, I18n.of("msg_access", guildData)).takeUnless {
			val member = event.member ?: return@takeUnless false

			member.isBotOwner()
		}

		embed?.let { event.hook.sendMessageEmbeds(it).queue() }

		return embed
	}

	private fun Member.isGuildManager(guildData: GuildData): Boolean = roles.any { userRole ->
		guildData.managerRoleIds.any { managerRole ->
			userRole.idLong == managerRole
		}
	}

	private fun Member.isGuildAdmin(): Boolean = hasPermission(Permission.ADMINISTRATOR)

	private fun Member.isGuildOwner(): Boolean = isOwner

	private fun Member.isBotOwner(): Boolean = idLong == config.ownerId.toLong()
}