package com.github.hummel.bogdan.integration

import com.github.hummel.bogdan.bean.BotData
import com.github.hummel.bogdan.bean.InteractionResult
import com.github.hummel.bogdan.utils.gson
import org.apache.hc.client5.http.classic.methods.HttpPost
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.ContentType
import org.apache.hc.core5.http.io.entity.EntityUtils
import org.apache.hc.core5.http.io.entity.StringEntity

private val lock: Any = Any()

@Suppress("unused")
private fun main() {
	val input = "Привет, как дела?"
	val result = getGlobalSupportInteractionResult(input)

	if (result.error != null) {
		println("Ошибка: ${result.error}")
	} else {
		println("Ответ: ${result.data}")
	}
}

fun getGlobalSupportInteractionResult(
	input: String
): InteractionResult {
	synchronized(lock) {
		val payload = gson.toJson(
			GlobalSupportRequest(
				messages = listOf(
					GlobalMessage(role = "user", content = input)
				)
			)
		)

		val (data, error) = getResponse(payload)
		data ?: return InteractionResult(null, error)

		return InteractionResult(data, null)
	}
}

private fun getResponse(
	payload: String
): InteractionResult = HttpClients.createDefault().use { client ->
	val request = HttpPost("https://global.support.by/api/openai/v1/chat/completions")

	request.addHeader("Content-Type", "application/json")
	request.addHeader("Authorization", "Bearer ${BotData.gptToken}")

	request.entity = StringEntity(payload, ContentType.APPLICATION_JSON)

	client.execute(request) { response ->
		if (response.code in 200..299) {
			try {
				val entity = response.entity
				val jsonResponse = EntityUtils.toString(entity)

				val apiResponse = gson.fromJson(jsonResponse, GlobalAIResponse::class.java)
				val answer = apiResponse.choices.firstOrNull()?.message?.content

				InteractionResult(answer, null)
			} catch (e: Exception) {
				InteractionResult(null, "${e.javaClass.simpleName}")
			}
		} else {
			InteractionResult(null, "${response.reasonPhrase} [${response.code}]")
		}
	}
}

private data class GlobalSupportRequest(
	val model: String = "gpt-4o-mini",
	val messages: List<GlobalMessage>
)

private data class GlobalAIResponse(
	val choices: List<Choice>
) {
	data class Choice(
		val message: GlobalMessage
	)
}

private data class GlobalMessage(
	val role: String,
	val content: String
)