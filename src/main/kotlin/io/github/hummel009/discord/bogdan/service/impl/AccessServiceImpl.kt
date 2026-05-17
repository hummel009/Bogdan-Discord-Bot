package io.github.hummel009.discord.bogdan.service.impl

import io.github.hummel009.discord.bogdan.bean.GuildData
import io.github.hummel009.discord.bogdan.service.AccessService
import io.github.hummel009.discord.bogdan.utils.I18n
import io.github.hummel009.discord.bogdan.utils.access
import io.github.hummel009.discord.bogdan.utils.config
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class AccessServiceImpl : AccessService {
	override fun managerAccessRestricted(event: SlashCommandInteractionEvent, guildData: GuildData): MessageEmbed? {
		val embed = EmbedBuilder().access(event.member, I18n.of("msg_access", guildData)).takeUnless {
			isManagerAccess(event, guildData) || isOwnerAccess(event)
		}

		embed?.let { event.hook.sendMessageEmbeds(it).queue() }

		return embed
	}

	override fun ownerAccessRestricted(event: SlashCommandInteractionEvent, guildData: GuildData): MessageEmbed? {
		val embed = EmbedBuilder().access(event.member, I18n.of("msg_access", guildData)).takeUnless {
			isOwnerAccess(event)
		}

		embed?.let { event.hook.sendMessageEmbeds(it).queue() }

		return embed
	}

	private fun isManagerAccess(event: SlashCommandInteractionEvent, guildData: GuildData): Boolean {
		val member = event.member ?: return false

		val isManager = member.roles.any { role ->
			guildData.managerRoleIds.any {
				it == role.idLong
			}
		}
		val isAdmin = member.hasPermission(Permission.ADMINISTRATOR)

		return isManager || isAdmin
	}

	private fun isOwnerAccess(event: SlashCommandInteractionEvent): Boolean {
		val member = event.member ?: return false

		val isOwner = member.idLong == config.ownerId.toLong()

		return isOwner
	}
}