/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.test.storage


import com.gridnine.jasmine.server.core.app.Environment
import com.gridnine.jasmine.server.core.storage.Database
import com.gridnine.jasmine.server.core.storage.Storage
import com.gridnine.jasmine.server.core.storage.StorageRegistry
import com.gridnine.jasmine.server.core.storage.cache.CacheAdvice
import com.gridnine.jasmine.server.core.storage.cache.CacheConfiguration
import com.gridnine.jasmine.server.core.storage.cache.ModelCacheConfigurator
import com.gridnine.jasmine.server.core.storage.cache.SimpleMapCacheStorage
import com.gridnine.jasmine.server.core.storage.impl.StandardStorageImpl
import com.gridnine.jasmine.server.core.storage.impl.jdbc.JdbcAdapter
import com.gridnine.jasmine.server.core.storage.impl.jdbc.JdbcDialect
import com.gridnine.jasmine.server.core.test.CoreTestBase
import com.gridnine.jasmine.server.db.h2.H2DataSourceDisposable
import com.gridnine.jasmine.server.db.h2.H2dbDialect
import com.gridnine.jasmine.server.db.h2.createH2DataSource
import javax.sql.DataSource

abstract class StorageTestBase:CoreTestBase(){

    override fun setUp() {
        super.setUp()
        startH2Database()
        publishDatabase()
        publishStorage()
        configureStorageRegisty()

    }



    protected fun configureStorageRegisty() {
        StorageRegistry.get().register(TestDocumentIndexHandler())
        registerCache()
    }

    protected fun registerCache() {
        val config = CacheConfiguration()
        Environment.publish(config)
        ModelCacheConfigurator.configure(config)
        StorageRegistry.get().register(CacheAdvice(1.0, SimpleMapCacheStorage()) )
    }
    protected fun publishStorage() {
        Environment.publish(StorageRegistry())
        Environment.publish(Storage::class, StandardStorageImpl())
    }

    protected fun publishDatabase(){
        Environment.publish(Database::class, JdbcAdapter())
    }

    protected fun startH2Database(){

        val dataSource = createH2DataSource("jdbc:h2:mem:optima")
        Environment.publish(DataSource::class, dataSource as DataSource)
        Environment.publish(H2DataSourceDisposable(dataSource))
        Environment.publish(JdbcDialect::class, H2dbDialect())
    }
}