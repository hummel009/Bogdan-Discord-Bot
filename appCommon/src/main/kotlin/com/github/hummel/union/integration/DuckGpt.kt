package com.github.hummel.union.integration

import com.github.hummel.union.integration.DuckGptRequest.DuckMessage
import com.github.hummel.union.utils.getRandomUserAgent
import com.github.hummel.union.utils.gson
import org.apache.hc.client5.http.classic.methods.HttpGet
import org.apache.hc.client5.http.classic.methods.HttpPost
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.ContentType
import org.apache.hc.core5.http.io.entity.EntityUtils
import org.apache.hc.core5.http.io.entity.StringEntity
import java.net.URI

@Deprecated(message = "Replaced with DuckGgptLive")
fun getDuckGptResponse(prompt: String): String? {
	val request = DuckGptRequest(
		"gpt-4o-mini", listOf(
			DuckMessage("user", prompt)
		)
	)

	val payload = gson.toJson(request)

	val userAgent = getRandomUserAgent()
	val feVersion = getXFeVersion() ?: return null
	val (vqd, _) = getXVqdHash()

	return getResponse(userAgent, feVersion, vqd ?: return null, payload)
}

private fun getResponse(
	userAgent: String, feVersion: String, vqd: String, payload: String
): String? = HttpClients.createDefault().use { client ->
	try {
		val url = URI("https://duckduckgo.com/duckchat/v1/chat")

		val request = HttpPost(url)
		request.setHeader("Accept", "text/event-stream")
		request.setHeader("Accept-Encoding", "gzip, deflate, br, zstd")
		request.setHeader("Accept-Language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7,uk;q=0.6,be;q=0.5")
		request.setHeader("Cookie", "dcm=3; dcs=1")
		request.setHeader("Dnt", "1")
		request.setHeader("Origin", "https://duckduckgo.com")
		request.setHeader("Priority", "u=1, i")
		request.setHeader("Referer", "https://duckduckgo.com/")
		request.setHeader("Sec-Fetch-Dest", "empty")
		request.setHeader("Sec-Fetch-Mode", "cors")
		request.setHeader("Sec-Fetch-Site", "same-origin")

		request.addHeader("User-Agent", userAgent)
		request.addHeader("X-Fe-Version", feVersion)
		request.addHeader("X-Vqd-4", vqd)
		request.addHeader("X-Vqd-Hash-1", "")

		request.entity = StringEntity(payload, ContentType.APPLICATION_JSON)

		client.execute(request) { response ->
			if (response.code in 200..299) {
				val content = EntityUtils.toString(response.entity)

				val apiResponse = content.split("data: ").filter {
					it.isNotEmpty()
				}.takeWhile {
					!it.contains("[DONE]")
				}.mapNotNull {
					gson.fromJson(it, DuckGptResponse::class.java)
				}

				apiResponse.joinToString("") { it.message }
			} else {
				null
			}
		}
	} catch (e: Exception) {
		e.printStackTrace()
		null
	}
}

private fun getXVqdHash(): Pair<String?, String?> {
	return HttpClients.createDefault().use { client ->
		try {
			val url = URI("https://duckduckgo.com/duckchat/v1/status")

			val request = HttpGet(url)
			request.setHeader("Accept", "*/*")
			request.setHeader("Accept-Encoding", "gzip, deflate, br, zstd")
			request.setHeader("Accept-Language", "ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7,uk;q=0.6,be;q=0.5")
			request.setHeader("Cache-Control", "no-store")
			request.setHeader("Cookie", "dcm=3")
			request.setHeader("Dnt", "1")
			request.setHeader("Priority", "u=1, i")
			request.setHeader("Referer", "https://duckduckgo.com/")
			request.setHeader("Sec-Fetch-Dest", "empty")
			request.setHeader("Sec-Fetch-Mode", "cors")
			request.setHeader("Sec-Fetch-Site", "same-origin")

			request.addHeader("X-Vqd-Accept", "1")

			client.execute(request) { response ->
				if (response.code in 200..299) {
					val headers = response.headers

					headers.find {
						it.name.lowercase() == "X-Vqd-4".lowercase()
					}?.value to headers.find {
						it.name.lowercase() == "X-Vqd-Hash-1".lowercase()
					}?.value
				} else {
					null to null
				}
			}
		} catch (e: Exception) {
			e.printStackTrace()
			null to null
		}
	}
}

private fun getXFeVersion(): String? {
	return HttpClients.createDefault().use { client ->
		try {
			val url = URI("https://duckduckgo.com/?q=DuckDuckGo+AI+Chat&ia=chat&duckai=1")

			val request = HttpGet(url)

			client.execute(request) { response ->
				if (response.code in 200..299) {
					val content = EntityUtils.toString(response.entity)

					val regexVersion = "__DDG_BE_VERSION__=\"([^\"]+)\"".toRegex()
					val regexChatHash = "__DDG_FE_CHAT_HASH__=\"([^\"]+)\"".toRegex()

					val version = regexVersion.find(content)?.groups[1]?.value
					val chatHash = regexChatHash.find(content)?.groups[1]?.value

					"$version-$chatHash"
				} else {
					null
				}
			}
		} catch (e: Exception) {
			e.printStackTrace()
			null
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