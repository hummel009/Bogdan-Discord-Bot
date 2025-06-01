package com.github.hummel.mdb.core.dao

interface ZipDao {
	fun unzipFile(filePath: String, folderPath: String)
	fun zipFolder(folderPath: String, filePath: String)
}