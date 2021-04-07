/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.server.core.build

import com.gridnine.jasmine.server.core.model.ui.NavigatorDescription
import java.io.File


object ServerUiNavigatorWebEditorGenerator {
    fun generateEditor(description: NavigatorDescription, baseDir: File, projectName: String, generatedFiles: MutableList<File>) {
        val sb = StringBuilder()
        GenUtils.generateHeader(sb, description.id, projectName)
        GenUtils.classBuilder(sb, "class ${GenUtils.getSimpleClassName(description.id)}:com.gridnine.jasmine.web.server.widgets.ServerUiNavigatorWidget<${description.id}VM,${description.id}VS,${description.id}VV>()"){
            "override fun createInitializer(): com.gridnine.jasmine.web.server.widgets.ServerUiNavigatorWidgetConfiguration<DemoComplexDocumentNestedDocumentsEditorVM>.() -> Unit"{
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