/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("UNCHECKED_CAST")

package com.gridnine.jasmine.common.core.build

import com.gridnine.jasmine.common.core.meta.TileSpaceDescription
import java.io.File


object WebTileSpaceGenerator {
    fun generateEditor(description: TileSpaceDescription, baseDir: File, projectName: String, generatedFiles: MutableList<File>,context: MutableMap<String, Any>) {
        val webMapping = context[PluginAssociationsGenerator.WEB_MAP_KEY] as HashMap<String, String>
        webMapping["web-messages-${description.id}"] = baseDir.name
        val sb = StringBuilder()
        GenUtils.generateHeader(sb, description.id, projectName)
        GenUtils.classBuilder(sb, "class ${GenUtils.getSimpleClassName(description.id)}:com.gridnine.jasmine.web.standard.widgets.TileSpaceWidget<${description.id}VMJS,${description.id}VSJS,${description.id}VVJS>()") {
            blankLine()
            description.overviewDescription?.let {
                "lateinit var overviewEditor: ${it.viewId}"()
            }
            description.tiles.forEach {
                "lateinit var ${it.id}Editor: ${it.fullViewId}"()
            }
            blankLine()
            "override fun createInitializer(): com.gridnine.jasmine.web.standard.widgets.TileSpaceWidgetConfiguration<${description.id}VMJS>.() -> Unit"{
                "return"{
                    """width = "100%""""()
                    """height = "100%""""()
                    "vmFactory = { ${description.id}VMJS() }"()
                    description.overviewDescription?.let {
                        """overviewEditor = overview(com.gridnine.jasmine.common.core.meta.L10nMetaRegistryJS.get().messages["${description.id}"]?.get("overview")?:"overview", ${it.viewId}())"""()
                    }
                    description.tiles.forEach {
                        """${it.id}Editor = tile("${it.id}", com.gridnine.jasmine.common.core.meta.L10nMetaRegistryJS.get().messages["${description.id}"]?.get("${it.id}")?:"${it.id}", ${it.fullViewId}())"""()

                    }
                }
            }
        }
        val file = File(baseDir, "source-gen/${GenUtils.getPackageName(description.id).replace(".", File.separator)}/${GenUtils.getSimpleClassName(description.id)}.kt")
        GenUtils.writeContent(file, sb)
        generatedFiles.add(file)
    }
}