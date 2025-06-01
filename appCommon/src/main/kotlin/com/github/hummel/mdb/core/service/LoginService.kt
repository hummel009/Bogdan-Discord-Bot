package com.github.hummel.mdb.core.service

import com.github.hummel.mdb.core.controller.impl.DiscordControllerImpl

interface LoginService {
	fun loginBot(impl: DiscordControllerImpl)
	fun deleteCommands(impl: DiscordControllerImpl)
	fun registerCommands(impl: DiscordControllerImpl)
}