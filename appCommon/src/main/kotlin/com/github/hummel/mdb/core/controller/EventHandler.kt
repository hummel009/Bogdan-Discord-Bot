package com.github.hummel.mdb.core.controller

interface EventHandler {
	fun onCreate()
	fun onStartCommand()
}