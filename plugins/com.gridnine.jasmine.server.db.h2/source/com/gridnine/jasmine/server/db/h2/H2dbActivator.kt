/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.db.h2

import com.gridnine.jasmine.server.core.app.Environment
import com.gridnine.jasmine.server.core.app.IPluginActivator
import com.gridnine.jasmine.server.core.storage.impl.jdbc.JdbcDialect
import java.io.File
import java.util.*
import javax.sql.DataSource


class H2dbActivator:IPluginActivator{

    override fun configure(config: Properties) {
        val file = File(Environment.rootFolder, "data/db")
        if (!file.exists()) {
            file.mkdirs()
        }
        Environment.publish(DataSource::class, H2DataSource.createDataSource("jdbc:h2:${file.absolutePath}/jasmine"))
        Environment.publish(JdbcDialect::class, H2dbDialect())
    }

}