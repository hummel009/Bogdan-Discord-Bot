package com.github.hummel.union.integration

import com.github.hummel.union.utils.gson
import org.apache.hc.client5.http.classic.methods.HttpGet
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.io.entity.EntityUtils
import org.apache.hc.core5.net.URIBuilder

@Suppress("unused")
fun getDuckGptLiveResponse(
	prompt: String
): Pair<Pair<Int, String>, String?> {
	val parameter = prompt

	return getResponse(parameter)
}

private fun getResponse(
	parameter: String
): Pair<Pair<Int, String>, String?> = HttpClients.createDefault().use { client ->
	val url = URIBuilder("https://duck.gpt-api.workers.dev/chat/").apply {
		addParameter("prompt", parameter)
	}.build().toString()

	val request = HttpGet(url)

	client.execute(request) { response ->
		if (response.code in 200..299) {
			try {
				val content = EntityUtils.toString(response.entity)

				val apiResponse = gson.fromJson(content, DuckGptLiveResponse::class.java)

				response.code to response.reasonPhrase to apiResponse.response
			} catch (e: Exception) {
				e.printStackTrace()

				422 to e.message to null
			}
		} else {
			response.code to response.reasonPhrase to null
		}
	}
}

private data class DuckGptLiveResponse(
	val action: String, val status: Int, val response: String, val model: String
)