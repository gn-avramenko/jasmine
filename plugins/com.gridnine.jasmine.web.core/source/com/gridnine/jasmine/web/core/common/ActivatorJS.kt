/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.web.core.common

interface ActivatorJS :RegistryItemJS<ActivatorJS>{
    suspend fun activate()

    override fun getType(): RegistryItemTypeJS<ActivatorJS> = TYPE

    companion object{
        val TYPE = RegistryItemTypeJS<ActivatorJS>("activator-js")
    }
}