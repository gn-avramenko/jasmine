/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("UNCHECKED_CAST")

package com.gridnine.jasmine.web.standard.editor

import com.gridnine.jasmine.common.core.model.BaseVMJS
import com.gridnine.jasmine.common.core.model.BaseVSJS
import com.gridnine.jasmine.common.core.model.BaseVVJS
import com.gridnine.jasmine.common.core.model.ObjectReferenceJS
import com.gridnine.jasmine.web.core.common.EnvironmentJS
import com.gridnine.jasmine.web.core.common.RegistryItemJS
import com.gridnine.jasmine.web.core.common.RegistryItemTypeJS
import com.gridnine.jasmine.web.core.ui.components.WebNode
import kotlin.reflect.KClass

interface WebEditor<VM: BaseVMJS, VS: BaseVSJS, VV: BaseVVJS>: WebNode {
    fun navigate(id:String):Boolean{return false}
    fun getData():VM
    fun readData(vm:VM, vs:VS?)
    fun setReadonly(value:Boolean)
    fun showValidation(vv: VV?)
}

interface WebEditorInterceptor<E:WebEditor<*,*,*>>{
    fun onInit(editor:E){}
    fun getEditorClass(): KClass<E>
    fun getPriority():Double= 0.0
}


class WebEditorInterceptorsRegistry {

    private val registry = hashMapOf<KClass<*>, List<WebEditorInterceptor<*>>>()

    fun<E: WebEditor<*, *, *>> register(item: WebEditorInterceptor<E>){
        val items = registry.getOrPut(item.getEditorClass(), { arrayListOf() }) as MutableList
        items.add(item)
        items.sortBy { item.getPriority() }
    }

    fun <E: WebEditor<*, *, *>> getInterceptors(item : E) = registry[item::class] as List<WebEditorInterceptor<E>>?

    companion object {
        fun get() = EnvironmentJS.getPublished(WebEditorInterceptorsRegistry::class)
    }
}

interface ObjectEditorHandler:RegistryItemJS<ObjectEditorHandler>{

    fun createEditor():WebEditor<*,*,*>

    fun getActionsGroupId():String

    override fun getType(): RegistryItemTypeJS<ObjectEditorHandler> {
        return TYPE
    }
    companion object{
        val TYPE = RegistryItemTypeJS<ObjectEditorHandler>("object-editor-handlers")

    }
}

interface ObjectEditorActionDisplayHandler<E:WebEditor<*,*,*>>{
    fun isEnabled(editor:ObjectEditor<E>):Boolean
    fun isVisible(editor:ObjectEditor<E>):Boolean
}

interface ObjectEditor<E:WebEditor<*,*,*>>{
    fun getEditor():E
    fun updateTitle(title:String)
    fun updateButtonsState()
    fun isReadonly():Boolean
    val objectType:String
    var objectUid:String
    fun getTitle():String
}
interface ObjectEditorTool<E:WebEditor<*,*,*>> {
    suspend fun invoke(editor:ObjectEditor<E>)
}