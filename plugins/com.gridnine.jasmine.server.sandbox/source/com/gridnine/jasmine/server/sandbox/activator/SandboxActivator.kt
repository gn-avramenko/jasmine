/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.server.sandbox.activator

import com.gridnine.jasmine.server.core.app.Environment
import com.gridnine.jasmine.server.core.app.IPluginActivator
import com.gridnine.jasmine.server.core.rest.KotlinFileDevFilter
import com.gridnine.jasmine.server.core.rest.NoCacheFilter
import com.gridnine.jasmine.server.core.storage.Storage
import com.gridnine.jasmine.server.core.storage.StorageRegistry
import com.gridnine.jasmine.server.core.storage.search.SearchQuery
import com.gridnine.jasmine.server.core.web.WebAppFilter
import com.gridnine.jasmine.server.core.web.WebApplication
import com.gridnine.jasmine.server.core.web.WebServerConfig
import com.gridnine.jasmine.server.sandbox.model.domain.*
import com.gridnine.jasmine.server.sandbox.rest.SandboxWorkspaceProvider
import com.gridnine.jasmine.server.sandbox.storage.SandboxComplexDocumentIndexHandler
import com.gridnine.jasmine.server.sandbox.storage.SandboxComplexDocumentVariantIndexHandler
import com.gridnine.jasmine.server.sandbox.storage.SandboxUserAccountIndexHandler
import com.gridnine.jasmine.server.standard.rest.WorkspaceProvider
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.math.roundToInt

class SandboxActivator : IPluginActivator {
    override fun configure(config: Properties) {
        StorageRegistry.get().register(SandboxComplexDocumentIndexHandler())
        StorageRegistry.get().register(SandboxComplexDocumentVariantIndexHandler())
        StorageRegistry.get().register(SandboxUserAccountIndexHandler())
        val easyuiApp = WebApplication("/sandbox/easyui", javaClass.classLoader.getResource("sb_easyui")
                ?: File("lib/sb_easyui.war").toURI().toURL(),
                javaClass.classLoader)
        WebServerConfig.get().addApplication(easyuiApp)
        val webCoreApp = WebApplication("/web-core", javaClass.classLoader.getResource("webapp-core")
                ?: File("lib/webapp-core.war").toURI().toURL(),
                javaClass.classLoader)
        WebServerConfig.get().addApplication(webCoreApp)
        val sandboxApp = WebApplication("/web-sandbox", javaClass.classLoader.getResource("web-sandbox")
                ?: File("lib/web-sandbox.war").toURI().toURL(),
                javaClass.classLoader)
        WebServerConfig.get().addApplication(sandboxApp)
        val easyuiWebapp = WebApplication("/web-easyui-libs", javaClass.classLoader.getResource("easyui-libs")
                ?:  File("lib/easyui-libs.war").toURI().toURL(),
                javaClass.classLoader)
        WebServerConfig.get().addApplication(easyuiWebapp)

        val easyuiScriptWebapp = WebApplication("/web-easyui-script", javaClass.classLoader.getResource("easyui-script")
                ?: File("lib/easyui-script.war").toURI().toURL(),
                javaClass.classLoader)
        WebServerConfig.get().addApplication(easyuiScriptWebapp)


        WebServerConfig.get().globalFilters.add(WebAppFilter("nocache", NoCacheFilter::class))
        WebServerConfig.get().globalFilters.add(WebAppFilter("dev-kt-files", KotlinFileDevFilter::class))
        Environment.publish(WorkspaceProvider::class, SandboxWorkspaceProvider())
    }

    override fun activate() {
        val size = Storage.get().searchDocuments(SandboxUserAccountIndex::class, SearchQuery()).size
        if (size == 0) {
            val adminAccount = SandboxUserAccount()
            adminAccount.name = "Jasmine Admin"
            adminAccount.login = "admin"
            adminAccount.password = "admin"
            Storage.get().saveDocument(adminAccount)
            for (n in 1..9) {
                val userAccount = SandboxUserAccount()
                userAccount.name = "User $n"
                userAccount.login = "user$n"
                userAccount.password = "user$n"
                Storage.get().saveDocument(userAccount)
            }

            for (n in 0..9) {
                val complexObject = SandboxComplexDocument()
                complexObject.booleanProperty = randomInt(1) == 1
                complexObject.dateProperty = LocalDate.of(2000 + randomInt(20), randomInt(11) + 1, randomInt(27) + 1)
                complexObject.dateTimeProperty = LocalDateTime.of(2000 + randomInt(20), randomInt(11) + 1, randomInt(27) + 1, randomInt(23), randomInt(59), randomInt(59))
                complexObject.entityRefProperty = Storage.get().findUniqueDocumentReference(SandboxUserAccountIndex::class, SandboxUserAccountIndex.login, "user${randomInt(10)}")
                complexObject.enumProperty = SandboxEnum.valueOf("ELEMENT_${randomInt(1) + 1}")
                complexObject.floatProperty = (randomInt(100).toDouble() / 10.toDouble()).toBigDecimal()
                complexObject.integerProperty = randomInt(100)
                complexObject.stringProperty = "string_${randomInt(10)}"
                for(m in 0..5){
                    val nestedObject = SandboxNestedDocument()
                    nestedObject.textColumn = "string_${randomInt(10)}"
                    nestedObject.entityRefColumn = Storage.get().findUniqueDocumentReference(SandboxUserAccountIndex::class, SandboxUserAccountIndex.login, "user${randomInt(10)}")
                    nestedObject.enumColumn = SandboxEnum.valueOf("ELEMENT_${randomInt(1) + 1}")
                    nestedObject.floatColumn = (randomInt(100).toDouble() / 10.toDouble()).toBigDecimal()
                    nestedObject.integerColumn =randomInt(100)
                    complexObject.entityCollection.add(nestedObject)
                }
                complexObject.nestedDocuments.add(createVariant1())
                complexObject.nestedDocuments.add(createVariant2())
                complexObject.nestedDocuments.add(createVariant1())
                complexObject.nestedDocuments.add(createVariant2())

                Storage.get().saveDocument(complexObject)
            }
        }
    }

    private fun createVariant1(): SandboxNavigatorVariant1 {
        val nestedObject = SandboxNavigatorVariant1()
        nestedObject.uid = UUID.randomUUID().toString()
        nestedObject.title = "string_${randomInt(10)}"
        nestedObject.intValue = randomInt(100)
        return nestedObject
    }
    private fun createVariant2(): SandboxNavigatorVariant2 {
        val nestedObject = SandboxNavigatorVariant2()
        nestedObject.uid = UUID.randomUUID().toString()
        nestedObject.title = "string_${randomInt(10)}"
        nestedObject.dateValue = LocalDate.of(2000 + randomInt(20), randomInt(11) + 1, randomInt(27) + 1)
        return nestedObject
    }

    private fun randomInt(max: Int): Int {
        return (max * Math.random()).roundToInt()
    }
}