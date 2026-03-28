package io.github.hummel009.discord.bogdan.dao.impl

import io.github.hummel009.discord.bogdan.bean.BotData
import io.github.hummel009.discord.bogdan.dao.FileDao
import java.io.File

class FileDaoImpl : io.github.hummel009.discord.bogdan.dao.FileDao {
	override fun createEmptyFile(filePath: String) {
		val file = getFile(filePath)
		if (file.exists()) {
			file.delete()
		}
		createParentDirsWithDepth(file)
		file.createNewFile()
	}

	override fun createEmptyFolder(folderPath: String) {
		val folder = getFolder(folderPath)
		if (folder.exists()) {
			folder.deleteRecursively()
		}
		createParentDirsWithDepth(folder)
		folder.mkdirs()
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

	override fun getFile(filePath: String): File = File(_root_ide_package_.io.github.hummel009.discord.bogdan.bean.BotData.root, filePath)

	override fun getFolder(folderPath: String): File = File(_root_ide_package_.io.github.hummel009.discord.bogdan.bean.BotData.root, folderPath)

	override fun readFromFile(filePath: String): ByteArray {
		val file = getFile(filePath)
		if (!file.exists()) {
			return ByteArray(0)
		}
		return file.readBytes()
	}

	override fun writeToFile(filePath: String, byteArray: ByteArray) {
		val file = getFile(filePath)
		if (!file.exists()) {
			createEmptyFile(filePath)
		}
		file.writeBytes(byteArray)
	}

	private fun createParentDirsWithDepth(child: File, depth: Int = 3) {
		var current = child.parentFile
		val dirsToCreate = mutableListOf<File>()

		var count = 0
		while (current != null && count < depth) {
			if (!current.exists()) {
				dirsToCreate.add(current)
			}
			current = current.parentFile
			count++
		}

		for (dir in dirsToCreate.asReversed()) {
			dir.mkdir()
		}
	}
}