/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.server.core.build

import com.gridnine.jasmine.common.core.build.CodeGenerator
import com.gridnine.jasmine.common.core.meta.*
import com.gridnine.jasmine.common.core.parser.UiMetadataParser
import java.io.File


@Suppress("unused")
class UiServerGenerator: CodeGenerator {

    override fun generate(destPlugin: File, sources: List<Pair<File, String?>>, projectName: String, generatedFiles: MutableList<File>, context: MutableMap<String, Any>) {
        val registry = UiMetaRegistry()
        sources.forEach { metaFile -> UiMetadataParser.updateUiMetaRegistry(registry, metaFile.first) }
        registry.views.values.forEach {
            when(it){
                is GridContainerDescription ->GridContainerGenerator.generateEditor(it,destPlugin, projectName, generatedFiles )
                is NavigatorDescription ->NavigatorEditorGenerator.generateEditor(it,destPlugin, projectName, generatedFiles )
                is TileSpaceDescription ->TileSpaceWidgetGenerator.generateEditor(it,destPlugin, projectName, generatedFiles )
            }
        }
    }
}