package com.github.hummel.mdb.integration

import com.github.hummel.mdb.bean.InteractionResult
import com.github.hummel.mdb.utils.getRandomUserAgent
import com.github.hummel.mdb.utils.gson
import com.github.hummel.mdb.utils.setHeaders
import org.apache.hc.client5.http.classic.methods.HttpGet
import org.apache.hc.client5.http.classic.methods.HttpPost
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.ContentType
import org.apache.hc.core5.http.io.entity.EntityUtils
import org.apache.hc.core5.http.io.entity.StringEntity
import java.net.URI

private val lock: Any = Any()

@Suppress("unused")
private fun main() {
	val input = "Привет, как дела?"
	val result = getDuckGptInteractionResult(input)

	if (result.error != null) {
		println("Ошибка: ${result.error}")
	} else {
		println("Ответ: ${result.data}")
	}
}

fun getDuckGptInteractionResult(
	input: String
): InteractionResult {
	synchronized(lock) {
		val payload = gson.toJson(
			DuckGptRequest(
				"gpt-4o-mini", listOf(
					DuckGptRequest.DuckMessage("user", input)
				)
			)
		)

		val (dataXfev, errorXfev) = getXFeVersion()
		dataXfev ?: return InteractionResult(null, errorXfev)

		val (dataXvqd, errorXvqd) = getXVqd()
		dataXvqd ?: return InteractionResult(null, errorXvqd)

		val (data, error) = getResponse(payload, dataXfev, dataXvqd)
		data ?: return InteractionResult(null, error)

		return InteractionResult(data, null)
	}
}

private fun getResponse(
	payload: String, xfev: String, xvqd: String
): InteractionResult = HttpClients.createDefault().use { client ->
	val url = URI("https://duckduckgo.com/duckchat/v1/chat")

	val request = HttpPost(url)

	request.setHeaders(
		"Accept" to "text/event-stream",
		"Accept-Encoding" to "gzip, deflate, br, zstd",
		"Accept-Language" to "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7,uk;q=0.6,be;q=0.5",
		"Cookie" to "dcm=3; dcs=1",
		"Dnt" to "1",
		"Origin" to "https://duckduckgo.com",
		"Priority" to "u=1, i",
		"Referer" to "https://duckduckgo.com/",
		"Sec-Fetch-Dest" to "empty",
		"Sec-Fetch-Mode" to "cors",
		"Sec-Fetch-Site" to "same-origin",
		"User-Agent" to getRandomUserAgent(),
		"X-Fe-Version" to xfev,
		"X-Vqd-4" to xvqd,
		"X-Vqd-Hash-1" to ""
	)

	request.entity = StringEntity(payload, ContentType.APPLICATION_JSON)

	client.execute(request) { response ->
		if (response.code in 200..299) {
			try {
				val entity = response.entity
				val jsonResponse = EntityUtils.toString(entity)

				val apiResponse = jsonResponse.split("data: ").filter {
					it.isNotEmpty()
				}.takeWhile {
					!it.contains("[DONE]")
				}.mapNotNull {
					gson.fromJson(it, DuckGptResponse::class.java)
				}

				InteractionResult(apiResponse.joinToString("") { it.message }, null)
			} catch (e: Exception) {
				InteractionResult(null, "${e.javaClass.getSimpleName()}")
			}
		} else {
			InteractionResult(null, "${response.reasonPhrase} [${response.code}]")
		}
	}
}

private fun getXVqd(
): InteractionResult = HttpClients.createDefault().use { client ->
	val url = URI("https://duckduckgo.com/duckchat/v1/status")

	val request = HttpGet(url)

	request.setHeaders(
		"accept" to "*/*",
		"accept-encoding" to "gzip, deflate, br, zstd",
		"accept-language" to "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7,uk;q=0.6,be;q=0.5",
		"cache-control" to "no-store",
		"cookie" to "dcm=3; dcs=1",
		"dnt" to "1",
		"priority" to "u=1, i",
		"referer" to "https://duckduckgo.com/",
		"sec-ch-ua" to "\"Not)A;Brand\";v=\"8\", \"Chromium\";v=\"138\", \"Microsoft Edge\";v=\"138\"",
		"sec-ch-ua-mobile" to "?0",
		"sec-ch-ua-platform" to "\"Windows\"",
		"sec-fetch-dest" to "empty",
		"sec-fetch-mode" to "cors",
		"sec-fetch-site" to "same-origin",
		"user-agent" to getRandomUserAgent(),
		"x-vqd-accept" to "1"
	)

	client.execute(request) { response ->
		if (response.code in 200..299) {
			try {
				val headers = response.headers

				InteractionResult(headers.find {
					it.name.lowercase() == "X-Vqd-4".lowercase()
				}?.value, null)
			} catch (e: Exception) {
				InteractionResult(null, "${e.javaClass.getSimpleName()}")
			}
		} else {
			InteractionResult(null, "${response.reasonPhrase} [${response.code}]")
		}
	}
}

private fun getXFeVersion(
): InteractionResult {
	return HttpClients.createDefault().use { client ->
		val url = URI("https://duckduckgo.com/?q=DuckDuckGo+AI+Chat&ia=chat&duckai=1")

		val request = HttpGet(url)

		client.execute(request) { response ->
			if (response.code in 200..299) {
				try {
					val entity = response.entity
					val jsonResponse = EntityUtils.toString(entity)

					val regexVersion = "__DDG_BE_VERSION__=\"([^\"]+)\"".toRegex()
					val regexChatHash = "__DDG_FE_CHAT_HASH__=\"([^\"]+)\"".toRegex()

					val version = regexVersion.find(jsonResponse)?.groups[1]?.value
					val chatHash = regexChatHash.find(jsonResponse)?.groups[1]?.value

					InteractionResult("$version-$chatHash", null)
				} catch (e: Exception) {
					InteractionResult(null, "${e.javaClass.getSimpleName()}")
				}
			} else {
				InteractionResult(null, "${response.reasonPhrase} [${response.code}]")
			}
		}
	}
}

private data class DuckGptRequest(
	val model: String, val messages: List<DuckMessage>
) {
	data class DuckMessage(
		val role: String, val content: String
	)
}

private data class DuckGptResponse(
	val role: String,
	val message: String,
	val created: Long,
	val id: String,
	val action: String,
	val model: String,
)