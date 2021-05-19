/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.standard.activator

import com.gridnine.jasmine.common.core.app.Environment
import com.gridnine.jasmine.common.core.app.IPluginActivator
import com.gridnine.jasmine.common.core.app.Registry
import com.gridnine.jasmine.server.standard.helpers.ObjectEditorsRegistry
import com.gridnine.jasmine.server.standard.rest.DateWorkspaceFromDtConverter
import com.gridnine.jasmine.server.standard.rest.DateWorkspaceToDtConverter
import com.gridnine.jasmine.server.standard.rest.WorkspaceListItemFromDTConverter
import com.gridnine.jasmine.server.standard.rest.WorkspaceListItemToDTConverter
import java.util.*

class StandardServerActivator : IPluginActivator{
    override fun configure(config: Properties) {
        Registry.get().register(WorkspaceListItemToDTConverter())
        Registry.get().register(DateWorkspaceToDtConverter())
        Registry.get().register(WorkspaceListItemFromDTConverter())
        Registry.get().register(DateWorkspaceFromDtConverter())
        Environment.publish(ObjectEditorsRegistry())

    }
}