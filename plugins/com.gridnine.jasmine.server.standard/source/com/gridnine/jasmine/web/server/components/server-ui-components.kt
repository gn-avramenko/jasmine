/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.components

import com.gridnine.jasmine.server.core.app.Disposable
import com.gridnine.jasmine.server.core.app.PublishableWrapper
import com.gridnine.jasmine.server.core.model.common.BaseIntrospectableObject
import com.gridnine.jasmine.server.core.model.ui.BaseVM
import com.gridnine.jasmine.server.core.model.ui.BaseVS
import com.gridnine.jasmine.server.core.model.ui.BaseVV
import kotlin.reflect.KClass


interface ServerUiNode{
    fun getParent(): ServerUiNode?
}

interface ServerUiNodeWrapper<T:ServerUiNode>: ServerUiNode{
    fun getNode():T
}

abstract class BaseServerUiNodeWrapper<T:ServerUiNode>:ServerUiNodeWrapper<T>{
    private var _parent:ServerUiNode? =  null
    protected lateinit var _node:T

    fun setParent(parent:ServerUiNode){
        this._parent = parent
    }

    override fun getParent(): ServerUiNode? {
        return _parent
    }

    override fun getNode(): T {
        return _node
    }
}


enum class ServerUiComponentHorizontalAlignment {
    LEFT,
    RIGHT,
    CENTER
}

enum class ServerUiNotificationType {
    INFO,
    ERROR
}

interface ServerUiLibraryAdapter{
    fun redirect(relativeUrl:String)
    fun createBorderLayout(config:ServerUiBorderContainerConfiguration):ServerUiBorderContainer
    fun createLabel(config:ServerUiLabelConfiguration):ServerUiLabel
    fun createAccordionContainer(config:ServerUiAccordionContainerConfiguration):ServerUiAccordionContainer
    fun createTabboxContainer(config:ServerUiTabboxConfiguration):ServerUiTabbox
    fun createGridLayoutContainer(config:ServerUiGridLayoutContainerConfiguration):ServerUiGridLayoutContainer
    fun<E:BaseIntrospectableObject> createDataGrid(config:ServerUiDataGridComponentConfiguration):ServerUiDataGridComponent<E>
    fun createTextBox(config:ServerUiTextBoxConfiguration):ServerUiTextBox
    fun createPasswordBox(config:ServerUiPasswordBoxConfiguration):ServerUiPasswordBox
    fun createLinkButton(config:ServerUiLinkButtonConfiguration):ServerUiLinkButton
    fun createDateBox(config:ServerUiDateBoxConfiguration):ServerUiDateBox
    fun createDateTimeBox(config:ServerUiDateTimeBoxConfiguration):ServerUiDateTimeBox
    fun createNumberBox(config:ServerUiNumberBoxConfiguration):ServerUiNumberBox
    fun createSelect(config:ServerUiSelectConfiguration): ServerUiSelect
    fun<W> showDialog(config:ServerUiDialogConfiguration<W>):ServerUiDialog<W>  where W:ServerUiNode
    fun createMenuButton(config:ServerUiMenuButtonConfiguration):ServerUiMenuButton
    fun createPanel(config:ServerUiPanelConfiguration):ServerUiPanel
    fun createTilesContainer(config:ServerUiTilesContainerConfiguration):ServerUiTilesContainer
    fun createDivsContainer(config:ServerUiDivsContainerConfiguration):ServerUiDivsContainer
    fun createBooleanBox(config:ServerUiBooleanBoxConfiguration):ServerUiBooleanBox
    fun createTableBox(config:ServerUiTableConfiguration):ServerUiTable
    fun createTree(config:ServerUiTreeConfiguration):ServerUiTree
    fun showContextMenu(items:List<ServerUiContextMenuItem>, pageX:Int, pageY:Int)
    fun showNotification(message:String,  type:ServerUiNotificationType, timeout:Int)
    fun findRootComponent():ServerUiNode

    companion object{
        private val wrapper = PublishableWrapper(ServerUiLibraryAdapter::class)
        fun get() = wrapper.get()
    }
}

interface ServerUiViewEditor<VM:BaseVM, VS:BaseVS, VV:BaseVV>:ServerUiNode{
    fun setData(vm:VM, vs:VS?)
    fun getData():VM
    fun showValidation(validation:VV?)
    fun setReadonly(value:Boolean)
    fun navigate(key:String):Boolean
}

interface ServerUiEditorInterceptor<E:ServerUiViewEditor<*,*,*>>{
    fun onInit(editor:E){}
    fun getEditorClass():KClass<E>
    fun getPriority():Double{
        return 0.0;
    }
}

class ServerUiEditorInterceptorsRegistry: Disposable {

    private val registry = hashMapOf<String, List<ServerUiEditorInterceptor<*>>>()

    fun<E:ServerUiViewEditor<*,*,*>> register(item: ServerUiEditorInterceptor<E>){
        val items = registry.getOrPut(item.getEditorClass().qualifiedName!!, { arrayListOf() }) as MutableList
        items.add(item)
        items.sortBy { item.getPriority() }
    }

    fun <E:ServerUiViewEditor<*,*,*>> getInterceptors(item : E) = registry[item::class.qualifiedName] as List<ServerUiEditorInterceptor<E>>?

    override fun dispose() {
        wrapper.dispose()
    }
    companion object {
        private val wrapper = PublishableWrapper(ServerUiEditorInterceptorsRegistry::class)
        fun get() = wrapper.get()
    }
}