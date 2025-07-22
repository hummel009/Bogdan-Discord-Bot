package com.github.hummel.bogdan.dao.impl

import com.github.hummel.bogdan.bean.BotData
import com.github.hummel.bogdan.dao.FileDao
import java.io.File

private const val notExist: String = "File doesn't exist!"

class FileDaoImpl : FileDao {
	override fun createEmptyFile(filePath: String) {
		val file = getFile(filePath)
		if (!file.exists()) {
			file.createNewFile()
		}
	}

	override fun createEmptyFolder(folderPath: String) {
		val folder = getFolder(folderPath)
		if (!folder.exists()) {
			folder.mkdirs()
		}
	}

	override fun removeFile(filePath: String) {
		val file = getFile(filePath)
		if (file.exists()) {
			file.delete()
		}
	}

	override fun removeFolder(folderPath: String) {
		val folder = getFolder(folderPath)
		if (folder.exists()) {
			folder.deleteRecursively()
		}
	}

	override fun getFile(filePath: String): File = File(BotData.root, filePath)

	override fun getFolder(folderPath: String): File = File(BotData.root, folderPath)

	override fun readFromFile(filePath: String): ByteArray {
		val file = getFile(filePath)
		if (!file.exists()) {
			throw Exception(notExist)
		}
		return file.readBytes()
	}

	override fun writeToFile(filePath: String, byteArray: ByteArray) {
		val file = getFile(filePath)
		if (!file.exists()) {
			throw Exception(notExist)
		}
		file.writeBytes(byteArray)
	}

	override fun appendToFile(filePath: String, byteArray: ByteArray) {
		val file = getFile(filePath)
		if (!file.exists()) {
			throw Exception(notExist)
		}
		file.appendBytes(byteArray)
	}
}