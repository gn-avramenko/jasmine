/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.spf

import com.gridnine.jasmine.server.core.build.JasmineCodeGen
import com.gridnine.spf.meta.SpfPluginsRegistry
import java.io.File


class SpfCodeGen(private val registry: SpfPluginsRegistry, private val projectName:String, private val pluginsMap:Map<String,File>){
    fun generateCode() {
        JasmineCodeGen.generateSources(SpfApplicationMetadataProvider(registry), projectName, pluginsMap)
    }
}