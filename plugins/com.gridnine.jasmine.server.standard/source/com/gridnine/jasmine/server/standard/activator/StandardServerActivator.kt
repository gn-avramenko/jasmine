/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.standard.activator

import com.gridnine.jasmine.server.core.app.Environment
import com.gridnine.jasmine.server.core.app.IPluginActivator
import com.gridnine.jasmine.server.standard.rest.ObjectEditorsRegistry
import java.util.*

class StandardServerActivator : IPluginActivator{
    override fun configure(config: Properties) {
        Environment.publish(ObjectEditorsRegistry())
    }
}