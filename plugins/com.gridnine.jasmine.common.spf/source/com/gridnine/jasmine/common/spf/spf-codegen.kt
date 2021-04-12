/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.common.spf

import com.gridnine.jasmine.common.core.app.Environment
import com.gridnine.jasmine.common.core.build.CodeGeneratorTask
import com.gridnine.jasmine.common.core.reflection.ReflectionFactory
import com.gridnine.spf.meta.SpfPluginsRegistry
import java.io.File


class SpfCodeGen(private val registry: SpfPluginsRegistry, private val projectName:String, private val pluginsMap:Map<String,File>){
    fun generateCode() {
        Environment.publish(ReflectionFactory())
        CodeGeneratorTask.generate(pluginsMap,projectName,  SpfApplicationMetadataProvider(registry))
    }
}