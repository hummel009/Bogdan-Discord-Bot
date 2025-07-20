package com.github.hummel.mdb.core.integration

import com.github.hummel.mdb.core.bean.InteractionResult
import com.github.hummel.mdb.core.utils.gson
import org.apache.hc.client5.http.classic.methods.HttpPost
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.ContentType
import org.apache.hc.core5.http.io.entity.EntityUtils
import org.apache.hc.core5.http.io.entity.StringEntity

private val lock: Any = Any()

@Suppress("unused")
private fun main() {
	val input = "Привет, как дела?"
	val result = getPorfirevichInteractionResult(input)

	if (result.error != null) {
		println("Ошибка: ${result.error}")
	} else {
		println("Ответ: ${result.data}")
	}
}

fun getPorfirevichInteractionResult(
	input: String
): InteractionResult {
	synchronized(lock) {
		val payload = gson.toJson(
			PorfirevichRequest(
				prompt = input
			)
		)

		val (data, error) = getResponse(payload)
		data ?: return InteractionResult(null, error)

		return InteractionResult(input + data, null)
	}
}

private fun getResponse(
	payload: String
): InteractionResult = HttpClients.createDefault().use { client ->
	val request = HttpPost("https://api.porfirevich.com/generate/")

	request.entity = StringEntity(payload, ContentType.APPLICATION_JSON)

	client.execute(request) { response ->
		if (response.code in 200..299) {
			try {
				val entity = response.entity
				val jsonResponse = EntityUtils.toString(entity)

				val apiResponse = gson.fromJson(jsonResponse, PorfirevichResponse::class.java)

				InteractionResult(apiResponse.replies.random(), null)
			} catch (e: Exception) {
				InteractionResult(null, "${e.javaClass.getSimpleName()}")
			}
		} else {
			InteractionResult(null, "${response.reasonPhrase} [${response.code}]")
		}
	}
}

private data class PorfirevichRequest(
	val prompt: String, val model: String = "xlarge", val length: Int = 100
)

private data class PorfirevichResponse(
	val replies: List<String>
)