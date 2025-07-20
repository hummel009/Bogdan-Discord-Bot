package com.github.hummel.mdb.dao

import java.io.File

interface FileDao {
	fun createEmptyFile(filePath: String)
	fun createEmptyFolder(folderPath: String)
	fun removeFile(filePath: String)
	fun removeFolder(folderPath: String)
	fun getFile(filePath: String): File
	fun getFolder(folderPath: String): File
	fun readFromFile(filePath: String): ByteArray
	fun writeToFile(filePath: String, byteArray: ByteArray)
	fun appendToFile(filePath: String, byteArray: ByteArray)
}