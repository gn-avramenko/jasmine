/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.test.storage

import com.gridnine.jasmine.server.core.app.Environment
import com.gridnine.jasmine.server.core.storage.Database
import com.gridnine.jasmine.server.core.storage.Storage
import com.gridnine.jasmine.server.core.storage.StorageRegistry
import com.gridnine.jasmine.server.core.storage.impl.StorageImpl
import com.gridnine.jasmine.server.core.storage.impl.jdbc.JdbcDatabase
import com.gridnine.jasmine.server.core.storage.impl.jdbc.JdbcDialect
import com.gridnine.jasmine.server.core.test.CoreTestBase
import com.gridnine.jasmine.server.db.h2.H2DataSource
import com.gridnine.jasmine.server.db.h2.H2dbDialect
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