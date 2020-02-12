/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.core.utils

import com.gridnine.jasmine.server.core.model.common.BaseEntity
import com.gridnine.jasmine.server.core.model.ui.BaseVMEntity
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.primaryConstructor

object UiUtils {
    fun<VM:BaseVMEntity, E:BaseEntity> writeCollection(editorList:List<VM>, modelList:MutableList<E>, cls:KClass<E>, func: (VM, E) ->Unit){
        val existingColl = ArrayList(modelList)
        modelList.clear()
        editorList.forEach { vm->
            val elm = existingColl.find { it.uid == vm.uid  }?:cls.createInstance()
            elm.uid = vm.uid!!
            func.invoke(vm,elm)
        }
    }
}