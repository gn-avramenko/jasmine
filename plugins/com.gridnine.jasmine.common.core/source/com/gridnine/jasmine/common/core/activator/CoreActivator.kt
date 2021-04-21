/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.common.core.activator

import com.gridnine.jasmine.common.core.app.Environment
import com.gridnine.jasmine.common.core.app.IPluginActivator
import com.gridnine.jasmine.common.core.app.Registry
import com.gridnine.jasmine.common.core.lock.LockManager
import com.gridnine.jasmine.common.core.lock.StandardLockManager
import com.gridnine.jasmine.common.core.meta.*
import com.gridnine.jasmine.common.core.parser.CustomMetadataParser
import com.gridnine.jasmine.common.core.reflection.ReflectionFactory
import com.gridnine.jasmine.common.core.serialization.SerializationProvider
import java.util.*

class CoreActivator:IPluginActivator{

    override fun configure(config: Properties) {
        Environment.publish(Registry())
        Environment.publish(DomainMetaRegistry())
        Environment.publish(ReflectionFactory())
        Environment.publish(LockManager::class, StandardLockManager())
        Environment.publish(RestMetaRegistry())
        Environment.publish(CustomMetaRegistry())
        CustomMetadataParser.updateCustomMetaRegistry(CustomMetaRegistry.get(), "com/gridnine/jasmine/common/core/meta/core-custom.xml", javaClass.classLoader)
        Environment.publish(UiMetaRegistry())
        Environment.publish( L10nMetaRegistry())
        Environment.publish( MiscMetaRegistry())
        Environment.publish(SerializationProvider())

    }

    override fun activate(config: Properties) {
        updateL10nRegistryFromUiRegistry()
    }

    private fun updateL10nRegistryFromUiRegistry() {
        val registry = L10nMetaRegistry.get()
        UiMetaRegistry.get().views.values.forEach {vd->
            val bundle = L10nMessagesBundleDescription(vd.id)
            registry.bundles[bundle.id] = bundle
            when(vd.viewType){
                ViewType.GRID_CONTAINER ->{
                    vd as GridContainerDescription
                    vd.rows.forEach {row ->
                        row.cells.forEach{cell ->
                            val message = L10nMessageDescription(cell.id)
                            message.displayNames.putAll(cell.displayNames)
                            bundle.messages[cell.id] = message
                            val widget = cell.widget
                            if(widget is TableBoxWidgetDescription){
                                val bundle2 = L10nMessagesBundleDescription(widget.id)
                                registry.bundles[widget.id] = bundle2
                                widget.columns.forEach{column ->
                                    val message2 = L10nMessageDescription(column.id)
                                    message2.displayNames.putAll(column.displayNames)
                                    bundle2.messages[column.id] = message2
                                }
                            }
                        }
                    }
                }
                ViewType.TILE_SPACE ->{
                    vd as TileSpaceDescription
                    vd.overviewDescription?.let {
                        val message = L10nMessageDescription("overview")
                        message.displayNames.putAll(it.displayNames)
                        bundle.messages[it.id] = message
                    }
                    vd.tiles.forEach {
                        val message = L10nMessageDescription(it.id)
                        message.displayNames.putAll(it.displayNames)
                        bundle.messages[it.id] = message
                    }
                }
                else -> {}
            }
        }
    }

}