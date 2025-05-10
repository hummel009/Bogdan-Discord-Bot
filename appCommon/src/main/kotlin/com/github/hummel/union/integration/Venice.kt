package com.github.hummel.union.integration

import com.github.hummel.union.utils.getRandomUserAgent
import com.github.hummel.union.utils.gson
import com.github.hummel.union.utils.setHeaders
import org.apache.hc.client5.http.classic.methods.HttpPost
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.io.entity.EntityUtils
import org.apache.hc.core5.http.io.entity.StringEntity
import java.util.*

private val lock: Any = Any()

@Suppress("unused")
fun getVeniceResponse(
	prompt: String
): Pair<Pair<Int, String>, String?> {
	synchronized(lock) {
		val request = VeniceRequest(
			prompt = listOf(VeniceRequest.Prompt(content = prompt))
		)

		val payload = gson.toJson(request)

		return getResponse(payload)
	}
}

private fun getResponse(
	payload: String
): Pair<Pair<Int, String>, String?> = HttpClients.createDefault().use { client ->
	val request = HttpPost("https://outerface.venice.ai/api/inference/chat")

	request.setHeaders(
		"User-Agent" to getRandomUserAgent(),
		"Accept" to "*/*",
		"Accept-Language" to "en-US,en;q=0.9",
		"Content-Type" to "application/json",
		"Origin" to "https://venice.ai",
		"Referer" to "https://venice.ai/",
		"Sec-Ch-Ua" to "\"Microsoft Edge\";v=\"135\", \"Not-A.Brand\";v=\"8\", \"Chromium\";v=\"135\"",
		"Sec-Ch-Ua-Mobile" to "?0",
		"Sec-Ch-Ua-Platform" to "\"Windows\"",
		"Sec-Fetch-Dest" to "empty",
		"Sec-Fetch-Mode" to "cors",
		"Sec-Fetch-Site" to "same-site",
		"Priority" to "u=1, i",
		"Sec-Gpc" to "1",
		"X-Venice-Version" to "interface@20250424.065523+50bac27"
	)

	request.entity = StringEntity(payload)

	client.execute(request) { response ->
		if (response.code in 200..299) {
			try {
				val inputStream = response.entity.content
				val reader = inputStream.bufferedReader()
				val sb = StringBuilder()
				var line: String?
				while (reader.readLine().also { line = it } != null) {
					line?.takeIf { it.contains("\"kind\":\"content\"") }?.let {
						sb.append(
							it.split("\"content\":\"")[1].split("\"")[0].replace("\\n", "\n")
						)
					}
				}
				EntityUtils.consume(response.entity)

				response.code to response.reasonPhrase to sb.toString()
			} catch (e: Exception) {
				e.printStackTrace()

				422 to e.message to null
			}
		} else {
			response.code to response.reasonPhrase to null
		}
	}
}

private data class VeniceRequest(
	val requestId: String = UUID.randomUUID().toString().substring(0, 7),
	val modelId: String = "dolphin-3.0-mistral-24b",
	val prompt: List<Prompt>,
	val systemPrompt: String = "",
	val conversationType: String = "text",
	val temperature: Double = 0.8,
	val webEnabled: Boolean = true,
	val topP: Double = 0.9,
	val includeVeniceSystemPrompt: Boolean = true,
	val isCharacter: Boolean = false,
	val userId: String = "user_anon_${1000000000 + Random().nextInt(900000000)}",
	val isDefault: Boolean = true,
	val textToSpeech: TextToSpeech = TextToSpeech(),
	val clientProcessingTime: Int = 10 + Random().nextInt(40)
) {
	data class Prompt(
		val content: String, val role: String = "user"
	)

	data class TextToSpeech(
		val voiceId: String = "af_sky", val speed: Int = 1
	)
}