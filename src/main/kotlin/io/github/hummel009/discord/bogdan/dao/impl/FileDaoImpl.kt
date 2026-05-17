package io.github.hummel009.discord.bogdan.dao.impl

import io.github.hummel009.discord.bogdan.dao.FileDao
import io.github.hummel009.discord.bogdan.utils.output
import java.io.File

class FileDaoImpl : FileDao {
	override fun createEmptyFile(filePath: String) {
		val file = getFile(filePath)
		if (file.exists()) {
			require(file.delete())
		}
		createParentDirsWithDepth(file)
		file.createNewFile()
	}

	override fun createEmptyFolder(folderPath: String) {
		val folder = getFolder(folderPath)
		if (folder.exists()) {
			require(folder.deleteRecursively())
		}
		createParentDirsWithDepth(folder)
		folder.mkdirs()
	}

	override fun removeFile(filePath: String) {
		val file = getFile(filePath)
		if (file.exists()) {
			require(file.delete())
		}
	}

	override fun removeFolder(folderPath: String) {
		val folder = getFolder(folderPath)
		if (folder.exists()) {
			require(folder.deleteRecursively())
		}
	}

	override fun getFile(filePath: String): File = File(output, filePath)

	override fun getFolder(folderPath: String): File = File(output, folderPath)

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
			require(dir.mkdir())
		}
	}
}