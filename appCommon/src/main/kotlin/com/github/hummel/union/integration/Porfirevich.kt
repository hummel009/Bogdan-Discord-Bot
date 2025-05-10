package com.github.hummel.union.integration

import com.github.hummel.union.utils.gson
import org.apache.hc.client5.http.classic.methods.HttpPost
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.ContentType
import org.apache.hc.core5.http.io.entity.EntityUtils
import org.apache.hc.core5.http.io.entity.StringEntity

private val lock: Any = Any()

@Suppress("unused")
fun getPorfirevichResponse(
	prompt: String
): Pair<Pair<Int, String>, String?> {
	synchronized(lock) {
		val request = PorfirevichRequest(
			prompt = prompt
		)

		val payload = gson.toJson(request)

		return getResponse(payload, request.prompt)
	}
}

private fun getResponse(
	payload: String, append: String
): Pair<Pair<Int, String>, String?> = HttpClients.createDefault().use { client ->
	val request = HttpPost("https://api.porfirevich.com/generate/")

	request.entity = StringEntity(payload, ContentType.APPLICATION_JSON)

	client.execute(request) { response ->
		if (response.code in 200..299) {
			try {
				val entity = response.entity
				val jsonResponse = EntityUtils.toString(entity)

				val apiResponse = gson.fromJson(jsonResponse, PorfirevichResponse::class.java)

				response.code to response.reasonPhrase to (append + apiResponse.replies.random())
			} catch (e: Exception) {
				e.printStackTrace()

				422 to e.message to null
			}
		} else {
			response.code to response.reasonPhrase to null
		}
	}
}

private data class PorfirevichRequest(
	val prompt: String, val model: String = "xlarge", val length: Int = 100
)

private data class PorfirevichResponse(
	val replies: List<String>
)