package io.github.hummel009.discord.bogdan.dao.impl

import io.github.hummel009.discord.bogdan.dao.FileDao
import io.github.hummel009.discord.bogdan.dao.ZipDao
import io.github.hummel009.discord.bogdan.factory.DaoFactory
import net.lingala.zip4j.ZipFile

class ZipDaoImpl : ZipDao {
	private val fileDao: FileDao = DaoFactory.fileDao

	override fun unzipFileToFolder(filePath: String, folderPath: String) {
		val file = fileDao.getFile(filePath)
		val folder = fileDao.getFolder(folderPath)

		ZipFile(file.path).extractAll(folder.path)
	}

	override fun zipFolderToFile(folderPath: String, filePath: String) {
		val file = fileDao.getFile(filePath)

		ZipFile(file.path).compressAll(folderPath)
	}

	private fun ZipFile.compressAll(folderPath: String) {
		val folder = fileDao.getFolder(folderPath)
		folder.listFiles()?.forEach {
			if (it.isDirectory) {
				addFolder(it)
			} else {
				addFile(it)
			}
		}
	}
}