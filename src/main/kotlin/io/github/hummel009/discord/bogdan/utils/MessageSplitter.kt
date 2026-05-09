package io.github.hummel009.discord.bogdan.utils

private const val MAX_LENGTH: Int = 1999

fun String.split(): List<String> {
	if (length <= MAX_LENGTH) {
		return listOf(this)
	}

	val parts = mutableListOf<String>()
	var remaining = this

	while (remaining.length > MAX_LENGTH) {
		val splitIndex = findSplitIndex(remaining, MAX_LENGTH)
		parts.add(remaining.take(splitIndex))
		remaining = remaining.substring(splitIndex).trimStart()
	}

	if (remaining.isNotEmpty()) {
		parts.add(remaining)
	}

	return parts
}

private fun findSplitIndex(text: String, maxLength: Int): Int {
	val textToCheck = text.take(maxLength)

	val lastParagraph = textToCheck.lastIndexOf("\n\n")
	if (lastParagraph > 0 && lastParagraph < maxLength - 10) {
		return lastParagraph + 2
	}

	val lastDotSpace = textToCheck.lastIndexOf(". ")
	if (lastDotSpace > 0 && lastDotSpace < maxLength - 5) {
		return lastDotSpace + 2
	}

	val punctuationPattern = "[!?;:] ".toRegex()
	val match = punctuationPattern.findAll(textToCheck).lastOrNull { it.range.last < maxLength - 5 }
	if (match != null) {
		return match.range.last + 1
	}

	val lastSpace = textToCheck.lastIndexOf(' ')
	if (lastSpace > 0 && lastSpace < maxLength - 5) {
		return lastSpace + 1
	}

	return maxLength
}