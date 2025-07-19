package com.github.hummel.mdb.core.controller.impl

import com.github.hummel.mdb.core.controller.Controller
import com.github.hummel.mdb.core.factory.ServiceFactory

class ControllerImpl : Controller {
	override fun onCreate() {
		val loginService = ServiceFactory.loginService
		loginService.loginBot()
		//loginService.recreateCommands()
	}
}