package com.github.hummel.mdb.core.factory

import com.github.hummel.mdb.core.service.*
import com.github.hummel.mdb.core.service.impl.*

@Suppress("unused", "RedundantSuppression")
object ServiceFactory {
	val loginService: LoginService by lazy { LoginServiceImpl() }
	val botService: BotService by lazy { BotServiceImpl() }
	val userService: UserService by lazy { UserServiceImpl() }
	val managerService: ManagerService by lazy { ManagerServiceImpl() }
	val ownerService: OwnerService by lazy { OwnerServiceImpl() }
	val dataService: DataService by lazy { DataServiceImpl() }
	val accessService: AccessService by lazy { AccessServiceImpl() }
}