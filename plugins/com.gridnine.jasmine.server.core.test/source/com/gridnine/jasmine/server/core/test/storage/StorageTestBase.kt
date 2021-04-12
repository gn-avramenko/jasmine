/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.test.storage

import com.gridnine.jasmine.common.core.app.Environment
import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.common.core.test.CommonCoreTestBase
import com.gridnine.jasmine.server.core.storage.Database
import com.gridnine.jasmine.server.core.storage.StorageImpl
import com.gridnine.jasmine.server.core.storage.StorageRegistry
import com.gridnine.jasmine.server.core.storage.cache.CacheConfiguration
import com.gridnine.jasmine.server.core.storage.cache.CacheManager
import com.gridnine.jasmine.server.core.storage.cache.CacheStorageAdvice
import com.gridnine.jasmine.server.core.storage.cache.InvalidateCacheStorageInterceptor
import com.gridnine.jasmine.server.core.storage.cache.ehcache.EhCacheManager
import com.gridnine.jasmine.server.core.storage.jdbc.JdbcDatabase
import com.gridnine.jasmine.server.core.storage.jdbc.JdbcDialect
import com.gridnine.jasmine.server.db.h2.H2DataSource
import com.gridnine.jasmine.server.db.h2.H2dbDialect
import javax.sql.DataSource

abstract class StorageTestBase:CommonCoreTestBase(){

    override fun setUp() {
        super.setUp()
        startH2Database()
        publishDatabase()
        publishStorage()
        configureStorageRegistry()
        publishCache();
    }

    protected fun publishCache() {
        Environment.publish(CacheConfiguration())
        Environment.publish(CacheManager::class, EhCacheManager())
    }


    protected open fun configureStorageRegistry() {
        StorageRegistry.get().register(TestDocumentIndexHandler())
        val advice = CacheStorageAdvice(0.0)
        StorageRegistry.get().register(advice)
        StorageRegistry.get().register(InvalidateCacheStorageInterceptor(1.0, advice))
    }

    protected fun publishStorage() {
        Environment.publish(StorageRegistry())
        Environment.publish(Storage::class, StorageImpl())
    }

    protected fun publishDatabase(){
        Environment.publish(Database::class, JdbcDatabase())
    }

    protected fun startH2Database(){
        Environment.publish(DataSource::class, H2DataSource.createDataSource("jdbc:h2:mem:jasmine"))
        Environment.publish(JdbcDialect::class, H2dbDialect())
    }
}