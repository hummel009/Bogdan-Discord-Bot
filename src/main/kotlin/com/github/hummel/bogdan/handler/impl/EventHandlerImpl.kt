package com.github.hummel.bogdan.handler.impl

import com.github.hummel.bogdan.factory.ServiceFactory
import com.github.hummel.bogdan.service.BotService
import com.github.hummel.bogdan.service.ManagerService
import com.github.hummel.bogdan.service.MemberService
import com.github.hummel.bogdan.service.OwnerService
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class EventHandlerImpl : ListenerAdapter() {
	private val memberService: MemberService = ServiceFactory.memberService
	private val managerService: ManagerService = ServiceFactory.managerService
	private val ownerService: OwnerService = ServiceFactory.ownerService
	private val botService: BotService = ServiceFactory.botService

	override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
		memberService.info(event)
		memberService.complete(event)
		memberService.clearContext(event)

		managerService.addBirthday(event)
		managerService.addManagerRole(event)
		managerService.addSecretChannel(event)
		managerService.addMutedChannel(event)

		managerService.clearBirthdays(event)
		managerService.clearManagerRoles(event)
		managerService.clearSecretChannels(event)
		managerService.clearMutedChannels(event)

		managerService.setChanceMessage(event)
		managerService.setChanceEmoji(event)
		managerService.setChanceAI(event)

		managerService.setLanguage(event)

		managerService.setName(event)
		managerService.setPreprompt(event)
		managerService.resetName(event)
		managerService.resetPreprompt(event)

		managerService.wipeBank(event)
		managerService.wipeData(event)

		ownerService.import(event)
		ownerService.export(event)
		ownerService.exit(event)
	}

	override fun onMessageReceived(event: MessageReceivedEvent) {
		botService.saveMessage(event)
		botService.sendRandomMessage(event)
		botService.sendBirthdayMessage(event)
		botService.addRandomEmoji(event)
	}
}