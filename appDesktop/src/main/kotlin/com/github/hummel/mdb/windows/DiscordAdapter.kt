package com.github.hummel.mdb.windows

import com.github.hummel.mdb.core.controller.DiscordController
import com.github.hummel.mdb.core.controller.impl.DiscordControllerImpl

class DiscordAdapter {
	private val controller: DiscordController = DiscordControllerImpl()

	fun launch() {
		controller.onCreate()
		controller.onStartCommand()
	}
}