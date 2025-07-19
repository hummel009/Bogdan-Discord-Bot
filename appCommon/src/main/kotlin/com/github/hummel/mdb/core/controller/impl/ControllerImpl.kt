package com.github.hummel.mdb.core.controller.impl

import com.github.hummel.mdb.core.controller.Controller
import com.github.hummel.mdb.core.factory.ServiceFactory
import com.github.hummel.mdb.core.service.LoginService

class ControllerImpl : Controller {
	private val loginService: LoginService = ServiceFactory.loginService

	override fun onCreate() {
		loginService.loginBot()
	}
}