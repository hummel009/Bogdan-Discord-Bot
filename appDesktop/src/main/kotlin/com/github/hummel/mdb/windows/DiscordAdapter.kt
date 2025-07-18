package com.github.hummel.mdb.windows

import com.github.hummel.mdb.core.controller.EventHandler
import com.github.hummel.mdb.core.controller.impl.EventHandlerImpl

class DiscordAdapter {
	private val controller: EventHandler = EventHandlerImpl()

	fun launch() {
		controller.onCreate()
		controller.onStartCommand()
	}
}