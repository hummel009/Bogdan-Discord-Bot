package com.github.hummel.mdb.core.service

import com.github.hummel.mdb.core.controller.impl.EventHandlerImpl

interface LoginService {
	fun loginBot(impl: EventHandlerImpl)
	fun deleteCommands(impl: EventHandlerImpl)
	fun registerCommands(impl: EventHandlerImpl)
}