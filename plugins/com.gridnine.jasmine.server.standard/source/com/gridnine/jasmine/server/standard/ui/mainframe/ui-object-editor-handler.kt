/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("UNCHECKED_CAST")

package com.gridnine.jasmine.server.standard.ui.mainframe

import com.gridnine.jasmine.common.core.app.Registry
import com.gridnine.jasmine.common.core.app.RegistryItem
import com.gridnine.jasmine.common.core.app.RegistryItemType
import com.gridnine.jasmine.common.core.model.BaseVM
import com.gridnine.jasmine.common.core.model.BaseVS
import com.gridnine.jasmine.common.core.model.BaseVV
import com.gridnine.jasmine.common.core.model.ObjectReference
import com.gridnine.jasmine.server.core.ui.common.*
import com.gridnine.jasmine.server.core.ui.components.*
import kotlin.reflect.KClass

interface UiObjectHandler: RegistryItem<UiObjectHandler> {

    fun createEditor():ViewEditor<*,*,*>

    override fun getType(): RegistryItemType<UiObjectHandler> {
        return TYPE
    }

    companion object{
        val TYPE = RegistryItemType<UiObjectHandler>("object-handlers")
    }
}

abstract class BaseUiObjectHandler(private val cls: KClass<*>):UiObjectHandler{

    override fun getId(): String {
        return cls.java.name
    }
}

class ObjectEditorTabHandlerData(val obj: ObjectReference<*>, val navigationKey:String?)

class ObjectEditorTabHandler : MainFrameTabHandler<ObjectEditorTabHandlerData>{
    override fun getTabId(obj: ObjectEditorTabHandlerData): String {
        return "${obj.obj.type.qualifiedName}||${obj.obj.uid}"
    }

    override fun createTabData(obj:ObjectEditorTabHandlerData, callback: MainFrameTabCallback): MainFrameTabData {
        val bundle = UiEditorHelper.getReadDataBundle(obj.obj.type.java.name, obj.obj.uid)
        val handler = Registry.get().get(UiObjectHandler.TYPE, obj.obj.type)!!
        val editor = ObjectEditor(obj.obj, bundle.vm,bundle.vs, handler.createEditor(), true,obj.navigationKey, callback)
        return MainFrameTabData(bundle.title, editor)
    }

}

interface ObjectEditorButton<VM:BaseVM, W:ViewEditor<VM,*,*>>: RegistryItem<ObjectEditorButton<BaseVM, ViewEditor<BaseVM, *, *>>>,HasWeight{
    fun isApplicable(vm:VM, editor: ObjectEditor<W>):Boolean
    fun onClick(value: ObjectEditor<W>)
    fun getDisplayName():String
    override fun getType(): RegistryItemType<ObjectEditorButton<BaseVM, ViewEditor<BaseVM, *, *>>> {
        return TYPE
    }
    companion object{
        val TYPE =RegistryItemType<ObjectEditorButton<BaseVM, ViewEditor<BaseVM, *, *>>>("editor-button-handlers")
    }
}


interface ObjectEditorMenuItem<VM:BaseVM, W:ViewEditor<VM,*,*>>: RegistryItem<ObjectEditorMenuItem<BaseVM, ViewEditor<BaseVM, *, *>>>,HasWeight{
    fun isApplicable(vm:VM, editor: ObjectEditor<W>):Boolean
    fun onClick(value: ObjectEditor<W>)
    fun getDisplayName():String
    fun getMenuButtonId():String
    override fun getType(): RegistryItemType<ObjectEditorMenuItem<BaseVM, ViewEditor<BaseVM, *, *>>> {
        return TYPE
    }
    companion object{
        val TYPE = RegistryItemType<ObjectEditorMenuItem<BaseVM, ViewEditor<BaseVM, *, *>>>("editor-menu-item-handlers")
    }
}

class ObjectEditor<V:ViewEditor<*,*,*>>(val reference: ObjectReference<*>, vm:BaseVM, vs:BaseVS, val rootEditor:V, var readOnly:Boolean, navigationKey:String?, private val callback: MainFrameTabCallback):BaseNodeWrapper<BorderContainer>(), EventsSubscriber{

    init {
        _node = UiLibraryAdapter.get().createBorderLayout {
            width = "100%"
            height = "100%"
        }
        updateTools(vm)
        _node.setCenterRegion{
            content = rootEditor
        }
        (rootEditor as ViewEditor<BaseVM,BaseVS,BaseVV>).setData(vm, vs)
        if(navigationKey != null) {
            rootEditor.navigate(navigationKey)
        }
        rootEditor.setReadonly(readOnly)
    }

    fun updateTools(vm: BaseVM) {
        val buttons = Registry.get().allOf(ObjectEditorButton.TYPE).filter { it.isApplicable(vm, this as ObjectEditor<ViewEditor<BaseVM, *, *>>) }.toMutableList()
        val menuButtons = Registry.get().allOf(ObjectEditorMenuItem.TYPE).filter {  it.isApplicable(vm, this as ObjectEditor<ViewEditor<BaseVM, *, *>>)  }
        val lst = arrayListOf<HasWeight>()
        lst.addAll(buttons)
        lst.addAll(menuButtons.map { it.getMenuButtonId() }.distinct().map { Registry.get().get(MainFrameMenuButton.TYPE, it) as HasWeight})
        lst.sortBy { it.getWeight() }
        val buttonsGrid = UiLibraryAdapter.get().createGridLayoutContainer {
            lst.forEach { _ ->
                columns.add(GridLayoutColumnConfiguration("auto"))
            }
            columns.add(GridLayoutColumnConfiguration("100%"))
            columns.add(GridLayoutColumnConfiguration("auto"))
        }
        buttonsGrid.addRow()
        lst.forEach { buttonConfig ->
            if(buttonConfig is ObjectEditorButton<*,*>){
                buttonConfig as ObjectEditorButton<BaseVM, ViewEditor<BaseVM, *,*>>
                val button = UiLibraryAdapter.get().createLinkButton{
                    title = buttonConfig.getDisplayName()
                }
                button.setHandler {
                    buttonConfig.onClick(this@ObjectEditor as ObjectEditor<ViewEditor<BaseVM, *, *>>)
                }
                buttonsGrid.addCell(GridLayoutCell(button))
            }
            if(buttonConfig is MainFrameMenuButton){
                val menuButton = UiLibraryAdapter.get().createMenuButton{
                    title = buttonConfig.getDisplayName()
                    val buttons2 = menuButtons.filter { button -> button.getMenuButtonId() == buttonConfig.getId() }.sortedBy { it.getWeight() }
                    buttons2.forEach { button2 ->
                        items.add(MenuButtonStandardItem( button2.getDisplayName(), null, false) {
                            button2.onClick(this@ObjectEditor as ObjectEditor<ViewEditor<BaseVM, *, *>>)
                        })
                    }
                }
                buttonsGrid.addCell(GridLayoutCell(menuButton))
            }
        }
        buttonsGrid.addCell(GridLayoutCell(null, 1))
        if(readOnly){
            val editButton = UiLibraryAdapter.get().createLinkButton{
                title = "Редактировать"
            }
            editButton.setHandler {
                readOnly = false
                rootEditor.setReadonly(false)
                updateTools(vm)
            }
            buttonsGrid.addCell(GridLayoutCell(editButton))
        } else {
            val viewButton = UiLibraryAdapter.get().createLinkButton{
                title = "Просмотр"
            }
            viewButton.setHandler {
                readOnly = true
                rootEditor.setReadonly(true)
                updateTools(vm)
            }
            buttonsGrid.addCell(GridLayoutCell(viewButton))
        }
        _node.setNorthRegion{
            showSplitLine = false
            showBorder = false
            collapsible = false
            width = "100%"
            content = buttonsGrid
        }
    }

    fun updateTitle(title: String?) {
        callback.setTitle(title?:"???")
    }

    override fun receiveEvent(event: Any) {
        if(event is ObjectModificationEvent){
            val bundle = UiEditorHelper.getReadDataBundle(reference.type.java.name, reference.uid)
            (rootEditor as ViewEditor<BaseVM,BaseVS,BaseVV>).setData(bundle.vm, bundle.vs)
            updateTools(bundle.vm)
            updateTitle(bundle.title)
            return
        }
        if(event is ObjectDeleteEvent){
            callback.close()
            return
        }
    }

}