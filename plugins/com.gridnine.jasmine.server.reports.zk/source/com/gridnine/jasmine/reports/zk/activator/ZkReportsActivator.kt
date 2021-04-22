/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.reports.zk.activator

import com.gridnine.jasmine.common.core.app.Environment
import com.gridnine.jasmine.common.core.app.IPluginActivator
import com.gridnine.jasmine.reports.zk.components.ZkReportsUiComponentsFactory
import com.gridnine.jasmine.server.reports.ui.ReportsUiComponentsFactory
import java.util.*

class ZkReportsActivator :IPluginActivator{
    override fun configure(config: Properties) {
        Environment.publish(ReportsUiComponentsFactory::class, ZkReportsUiComponentsFactory())
    }
}