package com.github.hummel.mdb.core.controller.impl

import com.github.hummel.mdb.core.controller.EventHandler
import com.github.hummel.mdb.core.factory.ServiceFactory
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class EventHandlerImpl : EventHandler {
	lateinit var api: JDA

	override fun onCreate() {
		val loginService = ServiceFactory.loginService
		loginService.loginBot(this)
		//loginService.deleteCommands(this)
		//loginService.registerCommands(this)
	}

	override fun onStartCommand() {
		val memberService = ServiceFactory.memberService
		val managerService = ServiceFactory.managerService
		val ownerService = ServiceFactory.ownerService
		val botService = ServiceFactory.botService

		api.addEventListener(object : ListenerAdapter() {
			override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
				memberService.info(event)
				memberService.complete(event)
				memberService.clearContext(event)

				managerService.addBirthday(event)
				managerService.addManager(event)
				managerService.addSecretChannel(event)
				managerService.addMutedChannel(event)
				managerService.clearBirthdays(event)
				managerService.clearManagers(event)
				managerService.clearSecretChannels(event)
				managerService.clearMutedChannels(event)
				managerService.setChanceMessage(event)
				managerService.setChanceEmoji(event)
				managerService.setChanceAI(event)
				managerService.setLanguage(event)
				managerService.setName(event)
				managerService.resetName(event)
				managerService.setPreprompt(event)
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
		})
	}
}