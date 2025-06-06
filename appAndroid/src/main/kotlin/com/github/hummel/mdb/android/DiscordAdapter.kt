package com.github.hummel.mdb.android

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import androidx.core.app.NotificationCompat
import com.github.hummel.mdb.core.controller.DiscordController
import com.github.hummel.mdb.core.controller.impl.DiscordControllerImpl

class DiscordAdapter : Service() {
	private lateinit var wakeLock: WakeLock
	private val controller: DiscordController = DiscordControllerImpl()

	override fun onCreate() {
		wakeLock = (getSystemService(POWER_SERVICE) as PowerManager).run {
			newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "mdb::wake_lock")
		}
		controller.onCreate()
	}

	@SuppressLint("WakelockTimeout")
	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		wakeLock.acquire()

		val channelId = "mdb::channel_id"
		val channelName = "mdb::channel_name"
		val notification = NotificationCompat.Builder(this, channelId).run {
			priority = NotificationCompat.PRIORITY_MAX
			setOngoing(true)
		}.build()
		val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
		val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
		notificationManager.createNotificationChannel(channel)

		startForeground(1, notification)

		controller.onStartCommand()

		return START_STICKY
	}

	override fun onDestroy() {
		stopForeground(STOP_FOREGROUND_REMOVE)

		wakeLock.release()
	}

	override fun onBind(intent: Intent?): IBinder? = null
}