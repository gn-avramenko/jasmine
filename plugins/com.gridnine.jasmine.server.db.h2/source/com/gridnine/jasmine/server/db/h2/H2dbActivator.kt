/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.db.h2

import com.gridnine.jasmine.server.core.app.Environment
import com.gridnine.jasmine.server.core.app.IPluginActivator
import com.gridnine.jasmine.server.core.storage.Database
import com.gridnine.jasmine.server.core.storage.Storage
import com.gridnine.jasmine.server.core.storage.impl.StandardStorageImpl
import com.gridnine.jasmine.server.core.storage.impl.jdbc.JdbcAdapter
import com.gridnine.jasmine.server.core.storage.impl.jdbc.JdbcDialect
import java.io.File
import javax.sql.DataSource


class H2dbActivator:IPluginActivator{

    override fun activate() {
        val file = File(Environment.rootFolder, "data/db")
        if (!file.exists()) {
            file.mkdirs()
        }
        val dataSource = createH2DataSource("jdbc:h2:${file.absolutePath}/jasmine")
        //val dataSource = createH2DataSource("jdbc:h2:mem:optima")
        Environment.publish(DataSource::class, dataSource as DataSource)
        Environment.publish(H2DataSourceDisposable(dataSource))
        Environment.publish(JdbcDialect::class, H2dbDialect())
        Environment.publish(Database::class, JdbcAdapter())
        Environment.publish(Storage::class,StandardStorageImpl())
    }

}