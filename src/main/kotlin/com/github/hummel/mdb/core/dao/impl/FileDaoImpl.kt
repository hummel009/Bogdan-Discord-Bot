package com.github.hummel.mdb.core.dao.impl

import com.github.hummel.mdb.core.bean.BotData
import com.github.hummel.mdb.core.dao.FileDao
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

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
		var byteArray: ByteArray
		val file = getFile(filePath)
		if (file.exists()) {
			FileInputStream(file).use {
				byteArray = ByteArray(it.available())
				it.read(byteArray)
			}
		} else {
			throw Exception(notExist)
		}
		return byteArray
	}

	override fun writeToFile(filePath: String, byteArray: ByteArray) {
		val file = getFile(filePath)
		if (file.exists()) {
			FileOutputStream(file).use {
				it.write(byteArray)
			}
		} else {
			throw Exception(notExist)
		}
	}

	override fun appendToFile(filePath: String, byteArray: ByteArray) {
		val file = getFile(filePath)
		if (file.exists()) {
			FileOutputStream(file, true).use {
				it.write(byteArray)
			}
		} else {
			throw Exception(notExist)
		}
	}
}