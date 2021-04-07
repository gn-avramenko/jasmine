/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.server.core.build

import com.gridnine.jasmine.server.core.model.ui.TileSpaceDescription
import java.io.File


object ServerUiTileSpaceWebEditorGenerator {
    fun generateEditor(description: TileSpaceDescription, baseDir: File, projectName: String, generatedFiles: MutableList<File>) {
        val sb = StringBuilder()
        GenUtils.generateHeader(sb, description.id, projectName)
        GenUtils.classBuilder(sb, "class ${GenUtils.getSimpleClassName(description.id)}:com.gridnine.jasmine.web.server.widgets.ServerUiTileSpaceWidget<${description.id}VM,${description.id}VS,${description.id}VV>()"){

            description.overviewDescription?.let {
                "lateinit var overviewEditor: ${it.viewId}"()
            }
            description.tiles.forEach {
                "lateinit var ${it.id}Editor: ${it.fullViewId}"()
            }

            "override fun createInitializer(): com.gridnine.jasmine.web.server.widgets.ServerUiTileSpaceWidgetConfiguration<${description.id}VM>.() -> Unit"{
                "return"{
                    """width = "100%""""()
                    """height = "100%""""()
                    """vmFactory = {${description.id}VM()}"""()
                    description.overviewDescription?.let {
                        """overviewEditor = overview(com.gridnine.jasmine.server.core.model.l10n.L10nMetaRegistry.get().webMessages["${description.id}"]?.messages?.get("overview")?.getDisplayName()?:"overview", ${it.viewId}())"""()
                    }
                    description.tiles.forEach {
                        """${it.id}Editor = tile("${it.id}", com.gridnine.jasmine.server.core.model.l10n.L10nMetaRegistry.get().webMessages["${description.id}"]?.messages?.get("${it.id}")?.getDisplayName()?:"${it.id}", ${it.fullViewId}())"""()
                    }
                }
            }
        }
        val file = File(baseDir, "source-gen/${GenUtils.getPackageName(description.id).replace(".", File.separator)}/${GenUtils.getSimpleClassName(description.id)}.kt")
        GenUtils.writeContent(file, sb)
        generatedFiles.add(file)
    }
}
