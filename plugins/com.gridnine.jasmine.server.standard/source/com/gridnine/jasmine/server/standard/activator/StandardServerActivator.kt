/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.standard.activator

import com.gridnine.jasmine.common.core.app.Environment
import com.gridnine.jasmine.common.core.app.IPluginActivator
import com.gridnine.jasmine.common.core.app.Registry
import com.gridnine.jasmine.server.core.ui.common.ObjectEditorsRegistry
import com.gridnine.jasmine.server.core.ui.common.ViewEditorInterceptorsRegistry
import com.gridnine.jasmine.server.standard.ui.mainframe.tools.AdditionalMenuButton
import com.gridnine.jasmine.server.standard.ui.mainframe.tools.DeleteListToolButton
import com.gridnine.jasmine.server.standard.ui.mainframe.tools.SaveObjectEditorButton
import com.gridnine.jasmine.server.standard.ui.mainframe.tools.ShowVersionsMenuItem
import com.gridnine.jasmine.server.standard.ui.mainframe.workspace.DateDynamicValueRendererFactory
import com.gridnine.jasmine.server.standard.ui.mainframe.workspace.DynamicCriterionEditorRegistry
import java.util.*

class StandardServerActivator : IPluginActivator{
    override fun configure(config: Properties) {
        Environment.publish(ObjectEditorsRegistry())
        Environment.publish(ViewEditorInterceptorsRegistry())
        Registry.get().register(SaveObjectEditorButton())
        Registry.get().register(AdditionalMenuButton())
        Registry.get().register(ShowVersionsMenuItem())
        Registry.get().register(DeleteListToolButton())
        Environment.publish(DynamicCriterionEditorRegistry())
        Registry.get().register(DateDynamicValueRendererFactory())
    }
}