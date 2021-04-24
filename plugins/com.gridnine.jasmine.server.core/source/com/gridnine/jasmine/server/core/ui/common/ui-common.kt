/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.ui.common

import com.gridnine.jasmine.common.core.app.Disposable
import com.gridnine.jasmine.common.core.app.PublishableWrapper
import com.gridnine.jasmine.common.core.app.RegistryItem
import com.gridnine.jasmine.common.core.app.RegistryItemType
import com.gridnine.jasmine.common.core.model.BaseVM
import com.gridnine.jasmine.common.core.model.BaseVS
import com.gridnine.jasmine.common.core.model.BaseVV
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.reflect.KClass

interface HasWeight{
    fun getWeight():Double
}

interface MainFrameMenuButton: RegistryItem<MainFrameMenuButton>, HasWeight {
    fun getIcon():String?
    fun getDisplayName():String
    override fun getType(): RegistryItemType<MainFrameMenuButton> {
        return TYPE
    }
    companion object{
        val TYPE = RegistryItemType<MainFrameMenuButton>("menu-buttons-handlers")
    }
}

interface EventsSubscriber{
    fun receiveEvent(event:Any)
}

data class ObjectModificationEvent(val objectType: String, val objectUid:String)

data class ObjectDeleteEvent(val objectType: String, val objectUid:String)

interface UiNode

interface NodeWrapper<T: UiNode>: UiNode {
    fun getNode():T
}

abstract class BaseNodeWrapper<T: UiNode>: NodeWrapper<T> {
    protected lateinit var _node:T

    override fun getNode(): T {
        return _node
    }
}


enum class ComponentHorizontalAlignment {
    LEFT,
    RIGHT,
    CENTER
}

enum class NotificationType {
    INFO,
    ERROR
}


interface ViewEditor<VM: BaseVM, VS: BaseVS, VV: BaseVV>: UiNode {
    fun setData(vm:VM, vs:VS?)
    fun getData():VM
    fun showValidation(validation:VV?)
    fun setReadonly(value:Boolean)
    fun navigate(id:String):Boolean
}

interface ViewEditorInterceptor<E: ViewEditor<*, *, *>>{
    fun onInit(editor:E){}
    fun getEditorClass(): KClass<E>
    fun getPriority():Double{
        return 0.0
    }
}

@Suppress("UNCHECKED_CAST")
class ViewEditorInterceptorsRegistry: Disposable {

    private val registry = hashMapOf<String, List<ViewEditorInterceptor<*>>>()

    fun<E: ViewEditor<*, *, *>> register(item: ViewEditorInterceptor<E>){
        val items = registry.getOrPut(item.getEditorClass().qualifiedName!!, { arrayListOf() }) as MutableList
        items.add(item)
        items.sortBy { item.getPriority() }
    }

    fun <E: ViewEditor<*, *, *>> getInterceptors(item : E) = registry[item::class.qualifiedName] as List<ViewEditorInterceptor<E>>?

    override fun dispose() {
        wrapper.dispose()
    }
    companion object {
        private val wrapper = PublishableWrapper(ViewEditorInterceptorsRegistry::class)
        fun get() = wrapper.get()
    }
}

abstract class BaseComponentConfiguration{
    var width:String? = null
    var height:String? = null
    var sClass:String? = null
}

abstract class BaseWidgetConfiguration:BaseComponentConfiguration()



object UiCommonUtils{

    private val dateTimeFormatter =  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    fun toString(value:Any?):String{
        return when(value){
            is LocalDateTime -> dateTimeFormatter.format(value)
            else -> value?.toString()?:""
        }
    }
}

enum class ContentType{
    TEXT{
        override fun getMimeType(): String {
            return "text/plain"
        }
    },
    EXCEL{
        override fun getMimeType(): String {
            return "application/vnd.ms-excel"
        }
    },
    PDF{
        override fun getMimeType(): String {
            return "application/pdf"
        }
    },
    XML{
        override fun getMimeType(): String {
            return "text/xml"
        }

    };
    abstract fun getMimeType():String
}