package com.github.hummel.union.utils

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.RemoteWebDriver
import java.net.URI

private const val dockerAddress: String = "http://localhost:4444/wd/hub"

fun <WebDriverResponse> executeRemoteWebDriver(
	executeWebDriver: (WebDriver) -> WebDriverResponse
): WebDriverResponse? {
	var driver: WebDriver? = null

	return try {
		driver = RemoteWebDriver(URI.create(dockerAddress).toURL(), ChromeOptions())

		executeWebDriver(driver)
	} catch (e: Exception) {
		e.printStackTrace()

		null
	} finally {
		driver?.quit()
	}
}