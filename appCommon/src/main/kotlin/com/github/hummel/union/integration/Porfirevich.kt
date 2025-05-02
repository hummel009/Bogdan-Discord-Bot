package com.github.hummel.union.integration

import com.github.hummel.union.utils.gson
import org.apache.hc.client5.http.classic.methods.HttpPost
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.ContentType
import org.apache.hc.core5.http.io.entity.EntityUtils
import org.apache.hc.core5.http.io.entity.StringEntity

fun getPorfirevichResponse(prompt: String): String? {
	val request = PorfirevichRequest(
		prompt, "xlarge", 100
	)

	val payload = gson.toJson(request)

	return request.prompt + getResponse(payload)
}

private fun getResponse(payload: String): String? = HttpClients.createDefault().use { client ->
	try {
		val request = HttpPost("https://api.porfirevich.com/generate/")

		request.entity = StringEntity(payload, ContentType.APPLICATION_JSON)

		client.execute(request) { response ->
			if (response.code in 200..299) {
				val entity = response.entity
				val jsonResponse = EntityUtils.toString(entity)

				val apiResponse = gson.fromJson(jsonResponse, PorfirevichResponse::class.java)

				apiResponse.replies.random()
			} else {
				null
			}
		}
	} catch (e: Exception) {
		e.printStackTrace()

		null
	}
}

private data class PorfirevichRequest(
	val prompt: String,
	val model: String,
	val length: Int
)

private data class PorfirevichResponse(
	val replies: List<String>
)