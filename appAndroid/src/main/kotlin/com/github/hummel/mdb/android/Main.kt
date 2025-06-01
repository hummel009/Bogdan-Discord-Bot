package com.github.hummel.mdb.android

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.github.hummel.mdb.core.bean.BotData

class Main : ComponentActivity() {
	private lateinit var sharedPreferences: SharedPreferences

	private var token: String = ""
	private var ownerId: String = ""

	// DO NOT REMOVE
	private var context: ComponentActivity = this

	private val requestPermissionLauncher: ActivityResultLauncher<String?> = registerForActivityResult(
		ActivityResultContracts.RequestPermission()
	) { isGranted: Boolean ->
		if (isGranted) {
			launchWithData(token, ownerId, filesDir.path, context)
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		sharedPreferences = getSharedPreferences("mdb::preferences", MODE_PRIVATE)

		setContent {
			MaterialTheme(
				colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
			) {
				ComposableOnCreate()
			}
		}
	}

	@Composable
	@Suppress("FunctionName")
	fun ComposableOnCreate() {
		var tokenState by remember { mutableStateOf(getTokenFromPrefs()) }
		var ownerIdState by remember { mutableStateOf(getOwnerIdFromPrefs()) }

		token = tokenState
		ownerId = ownerIdState

		Column(
			modifier = Modifier.fillMaxSize(),
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			TextField(value = tokenState, onValueChange = {
				tokenState = it
				saveTokenToPrefs(it)
			}, modifier = Modifier.fillMaxWidth().padding(16.dp), label = {
				Text("Token")
			})

			Spacer(modifier = Modifier.height(16.dp))

			TextField(value = ownerIdState, onValueChange = {
				ownerIdState = it
				saveOwnerIdToPrefs(it)
			}, modifier = Modifier.fillMaxWidth().padding(16.dp), label = {
				Text("Owner ID")
			})

			Spacer(modifier = Modifier.height(16.dp))

			Row(
				modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceEvenly
			) {
				Button(
					onClick = {
						exitFunction(context)
					}, colors = ButtonDefaults.buttonColors(
						containerColor = Color(0xFFC94F4F), contentColor = Color(0xFFDFE1E5)
					)
				) {
					Text("Exit")
				}

				Button(
					onClick = {
						checkAndRequestNotificationPermission()
					}, colors = ButtonDefaults.buttonColors(
						containerColor = Color(0xFF57965C), contentColor = Color(0xFFDFE1E5)
					)
				) {
					Text("Launch")
				}
			}
		}
	}

	private fun checkAndRequestNotificationPermission() {
		when {
			ContextCompat.checkSelfPermission(
				context, Manifest.permission.POST_NOTIFICATIONS
			) == PackageManager.PERMISSION_GRANTED -> {
				launchWithData(token, ownerId, filesDir.path, context)
			}

			shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
				requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
			}

			else -> {
				requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
			}
		}
	}

	@Suppress("UseExpressionBody")
	private fun getOwnerIdFromPrefs(): String {
		return sharedPreferences.getString("OWNER_ID_KEY", "1186780521624244278") ?: "OWNER_ID"
	}

	private fun saveOwnerIdToPrefs(token: String) {
		with(sharedPreferences.edit()) {
			putString("OWNER_ID_KEY", token)
			apply()
		}
	}

	@Suppress("UseExpressionBody")
	private fun getTokenFromPrefs(): String {
		return sharedPreferences.getString("TOKEN_KEY", "TOKEN") ?: "TOKEN"
	}

	private fun saveTokenToPrefs(token: String) {
		with(sharedPreferences.edit()) {
			putString("TOKEN_KEY", token)
			apply()
		}
	}
}

@Suppress("RedundantSuppression", "unused")
fun launchWithData(token: String, ownerId: String, root: String, context: ComponentActivity) {
	BotData.token = token
	BotData.ownerId = ownerId
	BotData.root = root
	BotData.exitFunction = { exitFunction(context) }

	startFunction(context)
}

fun startFunction(context: ComponentActivity) {
	val serviceIntent = Intent(context, DiscordAdapter::class.java)
	context.startForegroundService(serviceIntent)
}

fun exitFunction(context: ComponentActivity) {
	val serviceIntent = Intent(context, DiscordAdapter::class.java)
	context.stopService(serviceIntent)
	context.finish()
}