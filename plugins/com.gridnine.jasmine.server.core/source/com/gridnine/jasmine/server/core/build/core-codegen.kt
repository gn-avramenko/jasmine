/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.server.core.build

import com.gridnine.jasmine.server.core.app.IApplicationMetadataProvider
import java.io.File

object JasmineCodeGen{
    fun generateSources(projectDir:File, metadataProvider: IApplicationMetadataProvider, projectName:String){
        val generatedFiles = linkedMapOf<String, MutableList<File>>()
        DomainServerGenerator.generateDomainClasses(projectDir, metadataProvider, generatedFiles, projectName)
        UiServerGenerator.generateUiClasses(projectDir, metadataProvider, generatedFiles, projectName)
        RestServerGenerator.generateServerRest(projectDir, metadataProvider, generatedFiles, projectName)
        generatedFiles.forEach { (t, u) ->
            GenUtils.deleteFiles(File(projectDir, "plugins/$t/source-gen"), u)
        }
    }
}