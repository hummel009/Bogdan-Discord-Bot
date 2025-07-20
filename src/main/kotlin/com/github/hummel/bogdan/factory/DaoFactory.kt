package com.github.hummel.bogdan.factory

import com.github.hummel.bogdan.dao.FileDao
import com.github.hummel.bogdan.dao.JsonDao
import com.github.hummel.bogdan.dao.ZipDao
import com.github.hummel.bogdan.dao.impl.FileDaoImpl
import com.github.hummel.bogdan.dao.impl.JsonDaoImpl
import com.github.hummel.bogdan.dao.impl.ZipDaoImpl

@Suppress("unused", "RedundantSuppression")
object DaoFactory {
	val zipDao: ZipDao by lazy { ZipDaoImpl() }
	val jsonDao: JsonDao by lazy { JsonDaoImpl() }
	val fileDao: FileDao by lazy { FileDaoImpl() }
}