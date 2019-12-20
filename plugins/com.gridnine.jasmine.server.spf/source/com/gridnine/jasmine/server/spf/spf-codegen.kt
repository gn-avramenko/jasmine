/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.spf

import com.gridnine.jasmine.server.core.build.JasmineCodeGen
import com.gridnine.spf.meta.SpfPluginsRegistry
import java.io.File


class SpfCodeGen(private val projectDir:File, private val projectName:String){
    fun generateCode() {
        val pluginsUrls = File(projectDir, "plugins").listFiles()?.map { file -> File(file, "plugin.xml").toURI().toURL() }?.toList()?: throw IllegalArgumentException("no plugins found in $projectDir")
        val registry = SpfPluginsRegistry()
        registry.initRegistry(pluginsUrls)
        JasmineCodeGen.generateSources(projectDir, SpfApplicationMetadataProvider(registry), projectName)
    }

}