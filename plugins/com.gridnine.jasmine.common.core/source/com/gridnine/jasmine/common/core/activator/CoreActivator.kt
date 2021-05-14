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
import com.gridnine.jasmine.common.core.parser.WebMessagesMetadataParser
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
        Environment.publish(WebMessagesMetaRegistry())
        WebMessagesMetadataParser.updateWebMessages(WebMessagesMetaRegistry.get(), "com/gridnine/jasmine/common/core/meta/core-web-messages.xml", javaClass.classLoader)
        Environment.publish(WebPluginsAssociationsRegistry())
    }

    override fun activate(config: Properties) {
        updateWebMessagesRegistryFromUiRegistry()
    }

    private fun updateWebMessagesRegistryFromUiRegistry() {
        val webMessagesRegistry = WebMessagesMetaRegistry.get()
        val l10nMessagesRegistry = L10nMetaRegistry.get()
        UiMetaRegistry.get().views.values.forEach {vd->
            val wmBundle = WebMessagesBundleDescription(vd.id)
            webMessagesRegistry.bundles[wmBundle.id] = wmBundle
            val l10nBundle = L10nMessagesBundleDescription(vd.id)
            l10nMessagesRegistry.bundles[l10nBundle.id] = l10nBundle
            when(vd.viewType){
                ViewType.GRID_CONTAINER ->{
                    vd as GridContainerDescription
                    vd.rows.forEach {row ->
                        row.cells.forEach{cell ->
                            val l10nMessage = L10nMessageDescription(cell.id)
                            l10nMessage.displayNames.putAll(cell.displayNames)
                            l10nBundle.messages[cell.id] = l10nMessage
                            val wmMessage = WebMessageDescription(cell.id)
                            wmMessage.displayNames.putAll(cell.displayNames)
                            wmBundle.messages[cell.id] = wmMessage
                            val widget = cell.widget
                            if(widget is TableBoxWidgetDescription){
                                val bundle2 = WebMessagesBundleDescription(widget.id)
                                webMessagesRegistry.bundles[widget.id] = bundle2
                                val l10nBundle2 = L10nMessagesBundleDescription(widget.id)
                                l10nMessagesRegistry.bundles[widget.id] = l10nBundle2
                                widget.columns.forEach{column ->
                                    val message2 = WebMessageDescription(column.id)
                                    message2.displayNames.putAll(column.displayNames)
                                    bundle2.messages[column.id] = message2
                                    val l10nMessage2 = L10nMessageDescription(column.id)
                                    l10nMessage2.displayNames.putAll(column.displayNames)
                                    l10nBundle2.messages[column.id] = l10nMessage2
                                }
                            }
                        }
                    }
                }
                ViewType.TILE_SPACE ->{
                    vd as TileSpaceDescription
                    vd.overviewDescription?.let {
                        val message = WebMessageDescription("overview")
                        message.displayNames.putAll(it.displayNames)
                        wmBundle.messages[it.id] = message
                        val l10nMessageDescription = L10nMessageDescription("overview")
                        l10nMessageDescription.displayNames.putAll(it.displayNames)
                        l10nBundle.messages[it.id] = l10nMessageDescription
                    }
                    vd.tiles.forEach {
                        val message = WebMessageDescription(it.id)
                        message.displayNames.putAll(it.displayNames)
                        wmBundle.messages[it.id] = message
                        val l10nMessage = L10nMessageDescription(it.id)
                        l10nMessage.displayNames.putAll(it.displayNames)
                        l10nBundle.messages[it.id] = l10nMessage
                    }
                }
                else -> {}
            }
        }
    }

}