package io.github.hummel009.discord.bogdan.factory

import io.github.hummel009.discord.bogdan.dao.FileDao
import io.github.hummel009.discord.bogdan.dao.JsonDao
import io.github.hummel009.discord.bogdan.dao.ZipDao
import io.github.hummel009.discord.bogdan.dao.impl.FileDaoImpl
import io.github.hummel009.discord.bogdan.dao.impl.JsonDaoImpl
import io.github.hummel009.discord.bogdan.dao.impl.ZipDaoImpl

@Suppress("unused", "RedundantSuppression")
object DaoFactory {
	val zipDao: ZipDao by lazy { ZipDaoImpl() }
	val jsonDao: JsonDao by lazy { JsonDaoImpl() }
	val fileDao: FileDao by lazy { FileDaoImpl() }
}