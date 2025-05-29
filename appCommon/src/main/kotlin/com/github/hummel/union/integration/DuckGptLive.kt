package com.github.hummel.union.integration

import com.github.hummel.union.bean.InteractionResult
import com.github.hummel.union.utils.gson
import org.apache.hc.client5.http.classic.methods.HttpGet
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.io.entity.EntityUtils
import org.apache.hc.core5.net.URIBuilder

private val lock: Any = Any()

@Suppress("unused")
fun getDuckGptLiveInteractionResult(
	data: String
): InteractionResult {
	synchronized(lock) {
		val parameter = data

		return getResponse(parameter)
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
				val content = EntityUtils.toString(response.entity)

				val apiResponse = gson.fromJson(content, DuckGptLiveResponse::class.java)

				InteractionResult(apiResponse.response, null)
			} catch (e: Exception) {
				e.printStackTrace()

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