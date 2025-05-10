package com.github.hummel.union.integration

import com.github.hummel.union.integration.DuckGptRequest.DuckMessage
import com.github.hummel.union.utils.getRandomUserAgent
import com.github.hummel.union.utils.gson
import com.github.hummel.union.utils.setHeaders
import org.apache.hc.client5.http.classic.methods.HttpGet
import org.apache.hc.client5.http.classic.methods.HttpPost
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.ContentType
import org.apache.hc.core5.http.io.entity.EntityUtils
import org.apache.hc.core5.http.io.entity.StringEntity
import java.net.URI

private val lock: Any = Any()

@Suppress("unused")
@Deprecated(message = "Replaced with DuckGptLive")
fun getDuckGptResponse(
	prompt: String
): Pair<Pair<Int, String>, String?> {
	synchronized(lock) {
		val request = DuckGptRequest(
			"gpt-4o-mini", listOf(
				DuckMessage("user", prompt)
			)
		)

		val payload = gson.toJson(request)

		val (statusFeVersion, feVersion) = getXFeVersion()
		feVersion ?: return statusFeVersion to null

		val (statusVqd, vqd) = getXVqd()
		vqd ?: return statusVqd to null

		return getResponse(payload, feVersion, vqd)
	}
}

private fun getResponse(
	payload: String, feVersion: String, vqd: String
): Pair<Pair<Int, String>, String?> = HttpClients.createDefault().use { client ->
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
		"X-Fe-Version" to feVersion,
		"X-Vqd-4" to vqd,
		"X-Vqd-Hash-1" to ""
	)

	request.entity = StringEntity(payload, ContentType.APPLICATION_JSON)

	client.execute(request) { response ->
		if (response.code in 200..299) {
			try {
				val content = EntityUtils.toString(response.entity)

				val apiResponse = content.split("data: ").filter {
					it.isNotEmpty()
				}.takeWhile {
					!it.contains("[DONE]")
				}.mapNotNull {
					gson.fromJson(it, DuckGptResponse::class.java)
				}

				response.code to response.reasonPhrase to apiResponse.joinToString("") { it.message }
			} catch (e: Exception) {
				e.printStackTrace()

				response.code to e.message to null
			}
		} else {
			response.code to response.reasonPhrase to null
		}
	}
}

private fun getXVqd(
): Pair<Pair<Int, String>, String?> {
	return HttpClients.createDefault().use { client ->
		val url = URI("https://duckduckgo.com/duckchat/v1/status")

		val request = HttpGet(url)

		request.setHeaders(
			"Accept" to "*/*",
			"Accept-Encoding" to "gzip, deflate, br, zstd",
			"Accept-Language" to "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7,uk;q=0.6,be;q=0.5",
			"Cache-Control" to "no-store",
			"Cookie" to "dcm=3",
			"Dnt" to "1",
			"Priority" to "u=1, i",
			"Referer" to "https://duckduckgo.com/",
			"Sec-Fetch-Dest" to "empty",
			"Sec-Fetch-Mode" to "cors",
			"Sec-Fetch-Site" to "same-origin",
			"X-Vqd-Accept" to "1"
		)

		client.execute(request) { response ->
			if (response.code in 200..299) {
				val headers = response.headers

				response.code to response.reasonPhrase to headers.find {
					it.name.lowercase() == "X-Vqd-4".lowercase()
				}?.value
			} else {
				response.code to response.reasonPhrase to null
			}
		}
	}
}

private fun getXFeVersion(
): Pair<Pair<Int, String>, String?> {
	return HttpClients.createDefault().use { client ->
		val url = URI("https://duckduckgo.com/?q=DuckDuckGo+AI+Chat&ia=chat&duckai=1")

		val request = HttpGet(url)

		client.execute(request) { response ->
			if (response.code in 200..299) {
				val content = EntityUtils.toString(response.entity)

				val regexVersion = "__DDG_BE_VERSION__=\"([^\"]+)\"".toRegex()
				val regexChatHash = "__DDG_FE_CHAT_HASH__=\"([^\"]+)\"".toRegex()

				val version = regexVersion.find(content)?.groups[1]?.value
				val chatHash = regexChatHash.find(content)?.groups[1]?.value

				response.code to response.reasonPhrase to "$version-$chatHash"
			} else {
				response.code to response.reasonPhrase to null
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