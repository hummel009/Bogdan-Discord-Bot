package com.github.hummel.mdb.dao

interface ZipDao {
	fun unzipFileToFolder(filePath: String, folderPath: String)
	fun zipFolderToFile(folderPath: String, filePath: String)
}