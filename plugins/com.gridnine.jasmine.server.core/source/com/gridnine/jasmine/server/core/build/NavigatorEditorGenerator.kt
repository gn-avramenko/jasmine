/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.server.core.build

import com.gridnine.jasmine.common.core.build.GenUtils
import com.gridnine.jasmine.common.core.meta.NavigatorDescription
import com.gridnine.jasmine.server.core.ui.widgets.NavigatorWidget
import com.gridnine.jasmine.server.core.ui.widgets.NavigatorWidgetConfiguration
import java.io.File


internal object NavigatorEditorGenerator {
    fun generateEditor(description: NavigatorDescription, baseDir: File, projectName: String, generatedFiles: MutableList<File>) {
        val sb = StringBuilder()
        GenUtils.generateHeader(sb, description.id, projectName)
        GenUtils.classBuilder(sb, "class ${GenUtils.getSimpleClassName(description.id)}:${NavigatorWidget::class.qualifiedName}<${description.id}VM,${description.id}VS,${description.id}VV>()"){
            "override fun createInitializer(): ${NavigatorWidgetConfiguration::class.qualifiedName}<${description.id}VM>.() -> Unit"{
                "return"{
                    """width = "100%""""()
                    """height = "100%""""()
                    """vmFactory = {${description.id}VM()}"""()
                    description.variants.forEach {
                        """factories[${it.modelId}::class.qualifiedName!!] = {${it.viewId}() }"""()
                    }
                }
            }
        }
        val file = File(baseDir, "source-gen/${GenUtils.getPackageName(description.id).replace(".", File.separator)}/${GenUtils.getSimpleClassName(description.id)}.kt")
        GenUtils.writeContent(file, sb)
        generatedFiles.add(file)
    }
}