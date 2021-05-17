/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.common.core.build

import com.gridnine.jasmine.common.core.meta.*
import com.gridnine.jasmine.common.core.model.Xeption
import java.io.File
import java.lang.StringBuilder


object WebNavigatorGenerator {
    fun generateEditor(description: NavigatorDescription, baseDir: File, projectName: String, generatedFiles: MutableList<File>) {
        val sb = StringBuilder()
        GenUtils.generateHeader(sb, description.id, projectName)
        GenUtils.classBuilder(sb, "class ${GenUtils.getSimpleClassName(description.id)}:com.gridnine.jasmine.web.standard.widgets.NavigatorWidget<${description.id}VMJS,${description.id}VSJS,${description.id}VVJS>()") {
            blankLine()
            blankLine()
            "override fun createInitializer(): com.gridnine.jasmine.web.standard.widgets.NavigatorWidgetConfiguration<${description.id}VMJS>.() -> Unit"{
                "return"{
                    """width = "100%""""()
                    """height = "100%""""()
                    "vmFactory = {${description.id}VMJS()}"()
                    description.variants.forEach { variant ->
                        "factories[${variant.modelId}JS::class] = {${variant.viewId}()}"()
                    }
                }
            }
        }
        val file = File(baseDir, "source-gen/${GenUtils.getPackageName(description.id).replace(".", File.separator)}/${GenUtils.getSimpleClassName(description.id)}.kt")
        GenUtils.writeContent(file, sb)
        generatedFiles.add(file)
    }
}