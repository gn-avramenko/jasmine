/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.reports.activator

import com.gridnine.jasmine.common.core.app.IPluginActivator
import com.gridnine.jasmine.common.core.app.Registry
import com.gridnine.jasmine.common.core.meta.MiscMetaRegistry
import com.gridnine.jasmine.common.core.parser.MiscMetadataParser
import com.gridnine.jasmine.common.core.storage.SearchQuery
import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.common.core.utils.AuthUtils
import com.gridnine.jasmine.common.reports.model.domain.ReportDescription
import com.gridnine.jasmine.common.reports.model.domain.ReportDescriptionIndex
import com.gridnine.jasmine.server.core.storage.StorageRegistry
import com.gridnine.jasmine.server.reports.model.ServerReportHandler
import com.gridnine.jasmine.server.reports.storage.ReportDescriptionIndexHandler
import com.gridnine.jasmine.server.reports.ui.ReportDescriptionUiListItemHandler
import java.util.*

class ServerReportsActivator : IPluginActivator{
    override fun configure(config: Properties) {
        StorageRegistry.get().register(ReportDescriptionIndexHandler())
        MiscMetadataParser.updateMiscMetaRegistry(MiscMetaRegistry.get(), "com/gridnine/jasmine/server/reports/model/reports-server-model-misc.xml", javaClass.classLoader)
        Registry.get().register(ReportDescriptionUiListItemHandler())
    }

    override fun activate(config: Properties) {
        AuthUtils.setCurrentUser("system")
        val codeDescriptions = Registry.get().allOf(ServerReportHandler.TYPE).toMutableList()
        if(codeDescriptions.isEmpty()){
            return
        }
        Storage.get().searchDocuments(ReportDescriptionIndex::class, SearchQuery()).forEach {
            val description = codeDescriptions.find { cd -> cd.getId() == it.id }
            if(description == null || description.getName() != it.name){
                Storage.get().deleteDocument(it.document!!)
                return@forEach
            }
            codeDescriptions.remove(description)
        }
        codeDescriptions.forEach {
            val descr = ReportDescription()
            descr.id = it.getId()
            descr.name = it.getName()
            Storage.get().saveDocument(descr)
        }
    }
}