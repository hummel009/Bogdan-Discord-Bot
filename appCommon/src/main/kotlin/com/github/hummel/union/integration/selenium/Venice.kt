package com.github.hummel.union.integration.selenium

import com.github.hummel.union.bean.InteractionResult
import com.github.hummel.union.utils.executeRemoteWebDriver
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

private val lock: Any = Any()

@Suppress("unused")
fun getVeniceInteractionResult(
	data: String
): InteractionResult {
	synchronized(lock) {
		val prompt = data.replace("\r", " ").replace("\n", " ").replace("  ", "").trim()
		val interactionResult = executeRemoteWebDriver { driver -> getSimulationResult(driver, prompt) }
		interactionResult ?: return InteractionResult(null, "NullPointerException")

		return interactionResult
	}
}

private fun getSimulationResult(driver: WebDriver, prompt: String): InteractionResult {
	return try {
		driver.get("https://venice.ai/chat")

		val wait = WebDriverWait(driver, Duration.ofSeconds(30))

		val textarea = wait.until(
			ExpectedConditions.elementToBeClickable(
				By.xpath("//textarea[@placeholder='Ask a question privately...']")
			)
		)
		textarea.clear()
		textarea.sendKeys(prompt)

		val submitButton = wait.until(
			ExpectedConditions.elementToBeClickable(
				By.xpath("//button[@aria-label='Submit chat' and @type='submit']")
			)
		)
		submitButton.click()

		val latestMessageDiv = wait.until(
			ExpectedConditions.visibilityOfElementLocated(
				By.xpath("//div[@data-latest-message='true']")
			)
		)

		wait.until(
			ExpectedConditions.visibilityOfNestedElementsLocatedBy(
				latestMessageDiv,
				By.xpath(".//div[@role='group' and contains(@class, 'chakra-button__group')]")
			)
		)

		val responseParagraphs = latestMessageDiv.findElements(
			By.xpath(".//div[contains(@class,'prose') and contains(@class,'flex-1')]//p[contains(@class, 'chakra-text')]")
		)

		val fullResponse = responseParagraphs.joinToString(separator = "\n\n") { it.text.trim() }

		InteractionResult(fullResponse, null)
	} catch (e: Exception) {
		e.printStackTrace()

		InteractionResult(null, "${e.javaClass.getSimpleName()}")
	}
}