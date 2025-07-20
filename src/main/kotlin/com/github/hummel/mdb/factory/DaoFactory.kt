package com.github.hummel.mdb.factory

import com.github.hummel.mdb.dao.FileDao
import com.github.hummel.mdb.dao.JsonDao
import com.github.hummel.mdb.dao.ZipDao
import com.github.hummel.mdb.dao.impl.FileDaoImpl
import com.github.hummel.mdb.dao.impl.JsonDaoImpl
import com.github.hummel.mdb.dao.impl.ZipDaoImpl

@Suppress("unused", "RedundantSuppression")
object DaoFactory {
	val zipDao: ZipDao by lazy { ZipDaoImpl() }
	val jsonDao: JsonDao by lazy { JsonDaoImpl() }
	val fileDao: FileDao by lazy { FileDaoImpl() }
}