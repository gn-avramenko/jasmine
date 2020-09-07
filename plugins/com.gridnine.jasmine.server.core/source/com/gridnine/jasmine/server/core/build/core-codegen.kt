/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.server.core.build

import com.gridnine.jasmine.server.core.app.Environment
import com.gridnine.jasmine.server.core.app.IApplicationMetadataProvider
import com.gridnine.jasmine.server.core.model.domain.DomainMetaRegistry
import java.io.File

object JasmineCodeGen{
    fun generateSources(metadataProvider: IApplicationMetadataProvider, projectName:String, pluginsLocations: Map<String, File>){
        val generatedFiles = linkedMapOf<String, MutableList<File>>()
        val totalRegistry = DomainServerGenerator.generateDomainClasses(metadataProvider, generatedFiles, pluginsLocations, projectName)
        Environment.publish(DomainMetaRegistry::class, totalRegistry)
        RestServerGenerator.generateServerRest(metadataProvider, generatedFiles, pluginsLocations, projectName)
        DomainWebGenerator.generateDomainClasses(metadataProvider, generatedFiles, pluginsLocations, projectName, totalRegistry)
        RestWebGenerator.generateRestClasses(metadataProvider, generatedFiles, pluginsLocations, projectName)
        L10nWebGenerator.generateWebClasses(metadataProvider, generatedFiles, pluginsLocations, projectName)
        generatedFiles.forEach { (t, u) ->
            GenUtils.deleteFiles(File(pluginsLocations[t], "source-gen"), u)
        }
    }
}