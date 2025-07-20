package com.github.hummel.mdb.core.factory

import com.github.hummel.mdb.core.dao.FileDao
import com.github.hummel.mdb.core.dao.JsonDao
import com.github.hummel.mdb.core.dao.ZipDao
import com.github.hummel.mdb.core.dao.impl.FileDaoImpl
import com.github.hummel.mdb.core.dao.impl.JsonDaoImpl
import com.github.hummel.mdb.core.dao.impl.ZipDaoImpl

@Suppress("unused", "RedundantSuppression")
object DaoFactory {
	val zipDao: ZipDao by lazy { ZipDaoImpl() }
	val jsonDao: JsonDao by lazy { JsonDaoImpl() }
	val fileDao: FileDao by lazy { FileDaoImpl() }
}