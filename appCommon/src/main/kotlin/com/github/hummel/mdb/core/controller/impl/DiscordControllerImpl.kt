package com.github.hummel.mdb.core.controller.impl

import com.github.hummel.mdb.core.controller.DiscordController
import com.github.hummel.mdb.core.factory.ServiceFactory
import org.javacord.api.DiscordApi

class DiscordControllerImpl : DiscordController {
	lateinit var api: DiscordApi

	override fun onCreate() {
		val loginService = ServiceFactory.loginService
		loginService.loginBot(this)
		//loginService.deleteCommands(this)
		//loginService.registerCommands(this)
	}

	override fun onStartCommand() {
		val userService = ServiceFactory.userService
		val managerService = ServiceFactory.managerService
		val ownerService = ServiceFactory.ownerService
		val botService = ServiceFactory.botService

		api.addInteractionCreateListener {
			userService.clearContext(it)
			userService.complete(it)
			userService.info(it)

			managerService.addBirthday(it)
			managerService.addManager(it)
			managerService.addSecretChannel(it)
			managerService.addMutedChannel(it)
			managerService.clearBirthdays(it)
			managerService.clearManagers(it)
			managerService.clearSecretChannels(it)
			managerService.clearMutedChannels(it)
			managerService.clearBank(it)
			managerService.clearData(it)
			managerService.setLanguage(it)
			managerService.setChanceMessage(it)
			managerService.setChanceEmoji(it)
			managerService.setChanceAI(it)
			managerService.setPreprompt(it)
			managerService.resetPreprompt(it)
			managerService.setName(it)
			managerService.resetName(it)

			ownerService.import(it)
			ownerService.export(it)
			ownerService.exit(it)
		}

		api.addMessageCreateListener {
			botService.addRandomEmoji(it)
			botService.saveMessage(it)
			botService.sendBirthdayMessage(it)
			botService.sendRandomMessage(it)
		}
	}
}