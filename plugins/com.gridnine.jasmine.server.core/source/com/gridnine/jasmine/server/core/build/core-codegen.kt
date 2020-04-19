/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.server.core.build

import com.gridnine.jasmine.server.core.app.IApplicationMetadataProvider
import java.io.File

object JasmineCodeGen{
    fun generateSources(metadataProvider: IApplicationMetadataProvider, projectName:String, pluginsLocations: Map<String, File>){
        val generatedFiles = linkedMapOf<String, MutableList<File>>()
        DomainServerGenerator.generateDomainClasses(metadataProvider, generatedFiles, pluginsLocations, projectName)
        RestServerGenerator.generateServerRest(metadataProvider, generatedFiles, pluginsLocations, projectName)
        generatedFiles.forEach { (t, u) ->
            GenUtils.deleteFiles(File(pluginsLocations[t], "source-gen"), u)
        }
    }
}