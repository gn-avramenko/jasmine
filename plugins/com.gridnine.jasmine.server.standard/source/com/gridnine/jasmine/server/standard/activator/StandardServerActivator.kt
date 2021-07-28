/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.standard.activator

import com.gridnine.jasmine.common.core.app.Environment
import com.gridnine.jasmine.common.core.app.IPluginActivator
import com.gridnine.jasmine.common.core.app.Registry
import com.gridnine.jasmine.common.core.meta.*
import com.gridnine.jasmine.server.standard.helpers.ObjectEditorsRegistry
import com.gridnine.jasmine.server.standard.model.SequenceNumberGenerator
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
        Environment.publish(SequenceNumberGenerator())
    }

    override fun activate(config: Properties) {
        DomainMetaRegistry.get().indexes.values.forEach {
            updateAssociations(it)
            updateOptions(it)
        }
        DomainMetaRegistry.get().assets.values.forEach {
            updateAssociations(it)
            updateOptions(it)
        }
    }

    private fun updateOptions(it: BaseIndexDescription) {
        if(it.parameters["exclude-from-standard.list-ids"] == "true"){
            return
        }
        val group = UiMetaRegistry.get().optionsGroups["standard.list-ids"]!!
        val option = OptionDescription(it.id)
        option.displayNames.putAll(it.displayNames)
        group.options.add(option)
    }

    private fun updateAssociations(it: BaseIndexDescription) {
        WebPluginsAssociationsRegistry.get().associations["options-standard.list-ids-${it.id}"]=WebPluginsAssociationsRegistry.get().associations[it.id]!!
    }
}