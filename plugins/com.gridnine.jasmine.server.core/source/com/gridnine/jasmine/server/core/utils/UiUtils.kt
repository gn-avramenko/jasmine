/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.core.utils

import com.gridnine.jasmine.server.core.model.common.BaseEntity
import com.gridnine.jasmine.server.core.model.domain.DomainMetaRegistry
import com.gridnine.jasmine.server.core.model.ui.BaseVMEntity
import com.gridnine.jasmine.server.core.model.ui.EntityAutocompleteConfiguration
import com.gridnine.jasmine.server.core.model.ui.UiMetaRegistry
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.primaryConstructor

object UiUtils {
    fun<VM:BaseVMEntity, E:BaseEntity> writeCollection(editorList:List<VM>, modelList:MutableList<E>, cls:KClass<E>, func: (VM, E) ->Unit){
        writeCollection(editorList, modelList, {cls.createInstance()}, func)
    }

    fun<VM:BaseVMEntity, E:BaseEntity> writeCollection(editorList:List<VM>, modelList:MutableList<E>, factory:(VM) -> E, func: (VM, E) ->Unit){
        val existingColl = ArrayList(modelList)
        modelList.clear()
        editorList.forEach { vm->
            val elm = existingColl.find { it.uid == vm.uid  }?:factory(vm)
            elm.uid = vm.uid!!
            func.invoke(vm,elm)
            modelList.add(elm)
        }
    }

    fun <E:BaseEntity> createStandardAutocompletetConfiguration(cls:KClass<E>):EntityAutocompleteConfiguration{
        val result = EntityAutocompleteConfiguration()
        result.limit = 10
        DomainMetaRegistry.get().indexes.values.filter { it.document  == cls.qualifiedName}.forEach {
            result.dataSources.add(UiMetaRegistry.get().autocompletes.values.find { ac -> ac.entity == it.id }?.id?: throw IllegalArgumentException("unable to find autocomplete description for ${it.id}"))
        }
        return result
    }
}