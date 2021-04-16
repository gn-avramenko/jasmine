/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.common.core.activator

import com.gridnine.jasmine.common.core.app.Environment
import com.gridnine.jasmine.common.core.app.IApplicationMetadataProvider
import com.gridnine.jasmine.common.core.app.IPluginActivator
import com.gridnine.jasmine.common.core.app.Registry
import com.gridnine.jasmine.common.core.lock.LockManager
import com.gridnine.jasmine.common.core.lock.StandardLockManager
import com.gridnine.jasmine.common.core.meta.*
import com.gridnine.jasmine.common.core.parser.*
import com.gridnine.jasmine.common.core.reflection.ReflectionFactory
import com.gridnine.jasmine.common.core.serialization.SerializationProvider
import java.util.*

class CoreActivator:IPluginActivator{

    override fun configure(config: Properties) {
        Environment.publish(Registry())
        val result = DomainMetaRegistry()
        registerDomainMetadata(result)
        Environment.publish(ReflectionFactory())
        Environment.publish(LockManager::class, StandardLockManager())
        Environment.publish(result)
        val restRegistry = RestMetaRegistry()
        registerRestMetadata(restRegistry)
        Environment.publish(restRegistry)
        val customRegistry = CustomMetaRegistry()
        registerCustomMetadata(customRegistry)
        Environment.publish(customRegistry)
        val uiRegistry = UiMetaRegistry()
        registerUiMetadata(uiRegistry)
        Environment.publish(uiRegistry)
        val l10nMetaRegistry = L10nMetaRegistry()
        registerL10nMetadata(l10nMetaRegistry)
        Environment.publish(l10nMetaRegistry)

        Environment.publish(SerializationProvider())

    }

    private fun registerL10nMetadata(l10nMetaregistry: L10nMetaRegistry) {
        val extensions = IApplicationMetadataProvider.get()
                .getExtensions("l10n-messages")
        for (ext in extensions) {
            for (location in ext.getParameters("url")) {
                L10nMetadataParser.updateL10nMessages(l10nMetaregistry, location, ext.plugin.classLoader)
            }
        }
    }


    private fun registerRestMetadata(restRegistry: RestMetaRegistry) {
        val extensions = IApplicationMetadataProvider.get()
                .getExtensions("rest-metadata")
        for (ext in extensions) {
            for (location in ext.getParameters("url")) {
                RestMetadataParser.updateRestMetaRegistry(restRegistry, location, ext.plugin.classLoader)
            }
        }

    }

    private fun registerUiMetadata(uiRegistry: UiMetaRegistry) {
        val extensions = IApplicationMetadataProvider.get()
                .getExtensions("ui-metadata")
        for (ext in extensions) {
            for (location in ext.getParameters("url")) {
                UiMetadataParser.updateUiMetaRegistry(uiRegistry, location, ext.plugin.classLoader)
            }
        }

    }

    private fun registerDomainMetadata(result: DomainMetaRegistry) {
        val extensions = IApplicationMetadataProvider.get()
                .getExtensions("domain-metadata")
        for (ext in extensions) {
            for (location in ext.getParameters("url")) {
                DomainMetadataParser.updateDomainMetaRegistry(result, location, ext.plugin.classLoader)
            }
        }
    }


    private fun registerCustomMetadata(result: CustomMetaRegistry) {
        val extensions = IApplicationMetadataProvider.get()
                .getExtensions("custom-metadata")
        for (ext in extensions) {
            for (location in ext.getParameters("url")) {
                CustomMetadataParser.updateCustomMetaRegistry(result, location, ext.plugin.classLoader)
            }
        }
    }

}