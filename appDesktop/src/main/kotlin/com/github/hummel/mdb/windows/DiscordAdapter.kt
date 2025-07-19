package com.github.hummel.mdb.windows

import com.github.hummel.mdb.core.controller.Controller
import com.github.hummel.mdb.core.controller.impl.ControllerImpl

class DiscordAdapter {
	private val controller: Controller = ControllerImpl()

	fun launch() {
		controller.onCreate()
	}
}