/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.server.core.build

import com.gridnine.jasmine.server.core.model.ui.NavigatorDescription
import java.io.File


object NavigatorWebEditorGenerator {
    fun generateEditor(description: NavigatorDescription, baseDir: File, projectName: String, generatedFiles: MutableList<File>) {
        val sb = StringBuilder()
        GenUtils.generateHeader(sb, description.id, projectName)
        GenUtils.classBuilder(sb, "class ${GenUtils.getSimpleClassName(description.id)}(parent: com.gridnine.jasmine.web.core.ui.WebComponent?):com.gridnine.jasmine.web.core.ui.widgets.NavigatorWidget<${description.id}VMJS,${description.id}VSJS,${description.id}VVJS>(parent,"){
            "widget ->"()
            """width = "100%""""()
            """height = "100%""""()
            """vmFactory = {${description.id}VMJS()}"""()
            description.variants.forEach { variant ->
                """factory(${variant.modelId}JS::class){${variant.viewId}(widget)}"""()
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