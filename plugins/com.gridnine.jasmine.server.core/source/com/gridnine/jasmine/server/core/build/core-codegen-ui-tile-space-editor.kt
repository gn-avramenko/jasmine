/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.server.core.build

import com.gridnine.jasmine.server.core.model.ui.TileSpaceDescription
import java.io.File


object TileSpaceWebEditorGenerator {
    fun generateEditor(description: TileSpaceDescription, baseDir: File, projectName: String, generatedFiles: MutableList<File>) {
        val sb = StringBuilder()
        GenUtils.generateHeader(sb, description.id, projectName)
        GenUtils.classBuilder(sb, "class ${GenUtils.getSimpleClassName(description.id)}(parent: com.gridnine.jasmine.web.core.ui.WebComponent?):com.gridnine.jasmine.web.core.ui.widgets.TileSpaceWidget<${description.id}VMJS,${description.id}VSJS,${description.id}VVJS>(parent,"){
            "widget ->"()
            """width = "100%""""()
            """height = "100%""""()
            """vmFactory = {${description.id}VMJS()}"""()
            description.overviewDescription?.let{
                "val overviewEditor = ${it.viewId}(widget)"()
                 """overview(com.gridnine.jasmine.server.core.model.l10n.L10nMetaRegistryJS.get().messages["${description.id}"]?.get("overview") ?: "overview", overviewEditor)"""()
            }
            description.tiles.forEach {
                "val ${it.id}Editor = ${it.fullViewId}(widget)"()
                """tile("${it.id}", com.gridnine.jasmine.server.core.model.l10n.L10nMetaRegistryJS.get().messages["${description.id}"]?.get("${it.id}") ?: "${it.id}", ${it.id}Editor)"""()
            }
            description.interceptors.forEach { interceptor ->
                "interceptors.add($interceptor())"()
            }
        }
        sb.append(")")
        val file = File(baseDir, "source-gen/${GenUtils.getPackageName(description.id).replace(".", File.separator)}/${GenUtils.getSimpleClassName(description.id)}.kt")
        GenUtils.writeContent(file, sb)
        generatedFiles.add(file)
    }
}
