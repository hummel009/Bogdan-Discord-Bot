package com.github.hummel.mdb.integration

import com.github.hummel.mdb.bean.InteractionResult
import com.github.hummel.mdb.utils.gson
import org.apache.hc.client5.http.classic.methods.HttpGet
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.io.entity.EntityUtils
import org.apache.hc.core5.net.URIBuilder

private val lock: Any = Any()

@Suppress("unused")
private fun main() {
	val input = "Привет, как дела?"
	val result = getDuckGptLiveInteractionResult(input)

	if (result.error != null) {
		println("Ошибка: ${result.error}")
	} else {
		println("Ответ: ${result.data}")
	}
}

fun getDuckGptLiveInteractionResult(
	input: String
): InteractionResult {
	synchronized(lock) {
		val parameter = input

		val (data, error) = getResponse(parameter)
		data ?: return InteractionResult(null, error)

		return InteractionResult(data, null)
	}
}

private fun getResponse(
	parameter: String
): InteractionResult = HttpClients.createDefault().use { client ->
	val url = URIBuilder("https://duck.gpt-api.workers.dev/chat/").apply {
		addParameter("prompt", parameter)
	}.build().toString()

	val request = HttpGet(url)

	client.execute(request) { response ->
		if (response.code in 200..299) {
			try {
				val entity = response.entity
				val jsonResponse = EntityUtils.toString(entity)

				val apiResponse = gson.fromJson(jsonResponse, DuckGptLiveResponse::class.java)

				InteractionResult(apiResponse.response, null)
			} catch (e: Exception) {
				InteractionResult(null, "${e.javaClass.getSimpleName()}")
			}
		} else {
			InteractionResult(null, "${response.reasonPhrase} [${response.code}]")
		}
	}
}

private data class DuckGptLiveResponse(
	val action: String, val status: Int, val response: String, val model: String
)