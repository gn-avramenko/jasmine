/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.server.core.build

import com.gridnine.jasmine.common.core.build.GenUtils
import com.gridnine.jasmine.common.core.meta.L10nMetaRegistry
import com.gridnine.jasmine.common.core.meta.TileSpaceDescription
import com.gridnine.jasmine.server.core.ui.widgets.TileSpaceWidget
import com.gridnine.jasmine.server.core.ui.widgets.TileSpaceWidgetConfiguration
import java.io.File


internal object TileSpaceWidgetGenerator {
    fun generateEditor(description: TileSpaceDescription, baseDir: File, projectName: String, generatedFiles: MutableList<File>) {
        val sb = StringBuilder()
        GenUtils.generateHeader(sb, description.id, projectName)
        GenUtils.classBuilder(sb, "class ${GenUtils.getSimpleClassName(description.id)}:${TileSpaceWidget::class.qualifiedName}<${description.id}VM,${description.id}VS,${description.id}VV>()"){

            description.overviewDescription?.let {
                "lateinit var overviewEditor: ${it.viewId}"()
            }
            description.tiles.forEach {
                "lateinit var ${it.id}Editor: ${it.fullViewId}"()
            }

            "override fun createInitializer(): ${TileSpaceWidgetConfiguration::class.qualifiedName}<${description.id}VM>.() -> Unit"{
                "return"{
                    """width = "100%""""()
                    """height = "100%""""()
                    """vmFactory = {${description.id}VM()}"""()
                    description.overviewDescription?.let {
                        """overviewEditor = overview(${L10nMetaRegistry::class.qualifiedName}.get().bundles["${description.id}"]?.messages?.get("overview")?.getDisplayName()?:"overview", ${it.viewId}())"""()
                    }
                    description.tiles.forEach {
                        """${it.id}Editor = tile("${it.id}", ${L10nMetaRegistry::class.qualifiedName}.get().bundles["${description.id}"]?.messages?.get("${it.id}")?.getDisplayName()?:"${it.id}", ${it.fullViewId}())"""()
                    }
                }
            }
        }
        val file = File(baseDir, "source-gen/${GenUtils.getPackageName(description.id).replace(".", File.separator)}/${GenUtils.getSimpleClassName(description.id)}.kt")
        GenUtils.writeContent(file, sb)
        generatedFiles.add(file)
    }
}
