/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.server.core.build

import com.gridnine.jasmine.server.core.app.IApplicationMetadataProvider
import com.gridnine.jasmine.server.core.model.common.BaseIdentity
import com.gridnine.jasmine.server.core.model.common.Xeption
import com.gridnine.jasmine.server.core.model.domain.*
import com.gridnine.jasmine.server.core.model.ui.UiMetaRegistry
import com.gridnine.jasmine.server.core.model.ui.UiMetadataParser
import java.io.File


internal object UiServerGenerator {

    fun generateUiClasses(metadataProvider: IApplicationMetadataProvider, generatedFiles: MutableMap<String, MutableList<File>>, pluginsLocation:Map<String,File>, projectName: String){
        val uiData = linkedMapOf<String, MutableList<File>>()
        metadataProvider.getExtensions("ui-metadata").forEach {
            uiData.getOrPut(it.getParameters("dest-server-plugin-id").first(), { arrayListOf() })
                    .add(File(pluginsLocation[it.plugin.pluginId], it.getParameters("relative-path").first()))
            Unit
        }
        uiData.entries.forEach { (key, value) ->
            val registry = UiMetaRegistry()
            value.forEach { metaFile -> UiMetadataParser.updateUiMetaRegistry(registry, metaFile) }
            val classesData = arrayListOf<BaseGenData>()
            registry.enums.values.forEach {
                val enumClassData = GenEnumData(it.id)
                it.items.values.forEach { ei ->
                    enumClassData.enumItems.add(ei.id)
                }
                classesData.add(enumClassData)
            }
            GenUtils.generateClasses(classesData, pluginsLocation[key]?: throw Xeption.forDeveloper("unable to find basedir of plugin $key"), projectName,  generatedFiles.getOrPut(key, { arrayListOf()}))
        }
    }


}