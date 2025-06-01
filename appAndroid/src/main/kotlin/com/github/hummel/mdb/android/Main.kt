package com.github.hummel.mdb.android

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.hummel.mdb.core.bean.BotData
import kotlin.system.exitProcess

class Main : ComponentActivity() {
	private val context: Context = this
	private lateinit var sharedPreferences: SharedPreferences

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
		var token: String by remember { mutableStateOf(getTokenFromPrefs()) }
		var ownerId: String by remember { mutableStateOf(getOwnerIdFromPrefs()) }

		Column(
			modifier = Modifier.fillMaxSize(),
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			TextField(value = token, onValueChange = {
				token = it
				saveTokenToPrefs(token)
			}, modifier = Modifier.fillMaxWidth().padding(16.dp), label = {
				Text("Token")
			})

			Spacer(modifier = Modifier.height(16.dp))

			TextField(value = ownerId, onValueChange = {
				ownerId = it
				saveOwnerIdToPrefs(ownerId)
			}, modifier = Modifier.fillMaxWidth().padding(16.dp), label = {
				Text("Owner ID")
			})

			Spacer(modifier = Modifier.height(16.dp))

			Row(
				modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceEvenly
			) {
				Button(
					onClick = {
						exitProcess(0)
					}, colors = ButtonDefaults.buttonColors(
						containerColor = Color(0xFFC94F4F), contentColor = Color(0xFFDFE1E5)
					)
				) {
					Text("Exit")
				}

				Button(
					onClick = {
						launchService(token, ownerId, context.filesDir.path, context)
					}, colors = ButtonDefaults.buttonColors(
						containerColor = Color(0xFF57965C), contentColor = Color(0xFFDFE1E5)
					)
				) {
					Text("Launch")
				}
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
fun launchService(token: String, ownerId: String, root: String, context: Any?) {
	context as Context
	BotData.token = token
	BotData.ownerId = ownerId
	BotData.root = root
	val serviceIntent = Intent(context, DiscordAdapter::class.java)
	context.startService(serviceIntent)
}