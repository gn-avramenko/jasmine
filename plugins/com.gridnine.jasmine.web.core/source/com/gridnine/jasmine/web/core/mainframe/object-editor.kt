/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.mainframe

import com.gridnine.jasmine.server.core.model.ui.BaseVMJS
import com.gridnine.jasmine.server.core.model.ui.BaseVSJS
import com.gridnine.jasmine.server.core.model.ui.BaseVVJS
import com.gridnine.jasmine.server.standard.model.rest.GetEditorDataRequestJS
import com.gridnine.jasmine.server.standard.model.rest.GetEditorDataResponseJS
import com.gridnine.jasmine.web.core.CoreWebMessagesJS
import com.gridnine.jasmine.web.core.StandardRestClient
import com.gridnine.jasmine.web.core.application.EnvironmentJS
import com.gridnine.jasmine.web.core.ui.*
import com.gridnine.jasmine.web.core.ui.components.*
import kotlin.browser.window
import kotlin.js.Promise

class ObjectEditorTabHandler(private val forEdit:Boolean, private val navigationKey:String?):MainFrameTabHandler<ObjectEditorTabData, GetEditorDataResponseJS>{
    override fun getTabId(obj: ObjectEditorTabData): String {
        return "${obj.objectType}||${obj.objectUid}"
    }

    override fun loadData(obj: ObjectEditorTabData): Promise<GetEditorDataResponseJS> {
        val request = GetEditorDataRequestJS()
        request.objectId = obj.objectType
        request.objectUid = obj.objectUid
      return StandardRestClient.standard_standard_getEditorData(request)
    }

    override fun createTabData(obj: ObjectEditorTabData, data: GetEditorDataResponseJS, parent: WebComponent, callback: MainFrameTabCallback): MainFrameTabData {
        return MainFrameTabData(data.title, ObjectEditor<WebEditor<BaseVMJS,BaseVSJS,BaseVVJS>>(parent, obj, data, data.title, forEdit, navigationKey, callback))
    }
}

class ObjectEditorTabData(val objectType:String, var objectUid:String?)

class ObjectEditor<W:WebEditor<*,*,*>>(aParent: WebComponent, val obj: ObjectEditorTabData, data: GetEditorDataResponseJS, initTitle:String, forEdit:Boolean, private val navigationKey:String?, private val callback: MainFrameTabCallback):WebComponent,WebPopupContainer,EventsSubscriber{
    private val delegate:WebBorderContainer
    private val viewButton:WebLinkButton
    private val editButton:WebLinkButton
    private val parent:WebComponent = aParent
    val rootWebEditor:W
    private val editorButtonsMap = hashMapOf<WebLinkButton, ObjectEditorButton<WebEditor<*,*,*>>>()
    private val menuItemsMap = hashMapOf<WebMenuButton, MutableMap<String, ObjectEditorMenuItem<WebEditor<*,*,*>>>>()
    var readOnly:Boolean = true
    var title = "???"

    init {
        title= initTitle
        delegate = UiLibraryAdapter.get().createBorderLayout(this){
            fit=true
        }
        val handler = ClientRegistry.get().get(ObjectHandler.TYPE, obj.objectType)!!
        rootWebEditor = handler.createWebEditor(delegate) as W

        delegate.setCenterRegion(WebBorderContainer.region {
            content = rootWebEditor
        })
        val toolBar = UiLibraryAdapter.get().createGridLayoutContainer(delegate){
            width = "100%"
        }
        ObjectsHandlersCache.get().getObjectEditorButtonHandlers(obj.objectType).forEach {
            toolBar.defineColumn("auto")
        }
        toolBar.defineColumn("100%")
        toolBar.defineColumn("auto")
        toolBar.defineColumn("auto")
        toolBar.addRow()
        ObjectsHandlersCache.get().getObjectEditorButtonHandlers(obj.objectType).forEach {ti ->
            if(ti is ObjectEditorButton<*>){
                val oeb = ti as ObjectEditorButton<WebEditor<*, *, *>>
                val button = UiLibraryAdapter.get().createLinkButton(toolBar){
                    title = oeb.getDisplayName()
                    icon  = oeb.getIcon()
                }
                editorButtonsMap[button] = oeb
                button.setHandler {
                    ti.onClick(ObjectEditor@this as ObjectEditor<WebEditor<*, *, *>>)
                }
                toolBar.addCell(WebGridLayoutCell(button))
            } else {
                val mb = ti as MenuButton
                val menuButton = UiLibraryAdapter.get().createMenuButton(toolBar){
                    title = mb.getDisplayName()

                    ObjectsHandlersCache.get().getObjectEditorMenuItems(obj.objectType, mb.getId()).forEach { mi ->
                        items.add(WebMenuItemConfiguration(mi.getId()){
                            title = mi.getDisplayName()
                        })
                    }
                }
                val itemsMap = hashMapOf<String, ObjectEditorMenuItem<WebEditor<*, *, *>>>()
                menuItemsMap[menuButton] = itemsMap
                ObjectsHandlersCache.get().getObjectEditorMenuItems(obj.objectType, mb.getId()).forEach { mi ->
                    menuButton.setHandler(mi.getId()) {
                        mi.onClick(ObjectEditor@this as ObjectEditor<WebEditor<*, *, *>>)
                    }
                    itemsMap[mi.getId()] = mi
                }
                toolBar.addCell(WebGridLayoutCell(menuButton))
            }
        }

        toolBar.addCell(WebGridLayoutCell(null))
        viewButton = UiLibraryAdapter.get().createLinkButton(toolBar){
            title = CoreWebMessagesJS.view
        }

        viewButton.setVisible(forEdit)
        toolBar.addCell(WebGridLayoutCell(viewButton))
        editButton = UiLibraryAdapter.get().createLinkButton(toolBar){
            title = CoreWebMessagesJS.edit
        }
        editButton.setVisible(!forEdit)

        toolBar.addCell(WebGridLayoutCell(editButton))
        delegate.setNorthRegion(WebBorderContainer.region {
            content = toolBar
        })
        editButton.setHandler {
            rootWebEditor.setReadonly(false)
            viewButton.setVisible(true)
            editButton.setVisible(false)
            readOnly = false
            updateButtonsState()
        }
        viewButton.setHandler {
            rootWebEditor.setReadonly(true)
            viewButton.setVisible(false)
            editButton.setVisible(true)
            readOnly = true
            updateButtonsState()
        }
        (rootWebEditor as WebEditor<BaseVMJS, BaseVSJS,BaseVVJS>).readData(data.viewModel, data.viewSettings)
        readOnly = !forEdit
        rootWebEditor.setReadonly(!forEdit)
        navigationKey?.let {rootWebEditor.navigate(it)}
        updateButtonsState()
    }

    fun updateButtonsState() {
        editorButtonsMap.entries.forEach { it.key.setEnabled(it.value.isEnabled(ObjectEditor@this as ObjectEditor<WebEditor<*, *, *>>)) }
        menuItemsMap.entries.forEach { mbEntry ->
            mbEntry.value.entries.forEach {miEntry ->
                mbEntry.key.setEnabled(miEntry.key, miEntry.value.isEnabled(ObjectEditor@this as ObjectEditor<WebEditor<*, *, *>>))
        } }
    }

    override fun getParent(): WebComponent? {
        return parent
    }

    override fun getChildren(): List<WebComponent> {
        return arrayListOf(delegate)
    }

    override fun getHtml(): String {
        return delegate.getHtml()
    }

    override fun decorate() {
        delegate.decorate()
    }

    override fun destroy() {
        delegate.destroy()
    }

    fun updateTitle(value:String){
        title = value
        callback.setTitle(value)
    }

    override fun getId(): String {
        return delegate.getId()
    }

    override fun receiveEvent(event: Any) {
        if(event is ObjectModificationEvent){
            if(event.objectType == obj.objectType && event.objectUid == obj.objectUid){
                val request = GetEditorDataRequestJS()
                request.objectId = obj.objectType
                request.objectUid = obj.objectUid
                StandardRestClient.standard_standard_getEditorData(request).then {
                    (rootWebEditor as WebEditor<BaseVMJS, BaseVSJS,BaseVVJS>).readData(it.viewModel, it.viewSettings)
                    callback.setTitle(it.title)
                }
            }
        }
        if(event is ObjectDeleteEvent){
            if(event.objectType == obj.objectType && event.objectUid == obj.objectUid){
                callback.close()
            }
        }
    }
}

class ObjectsHandlersCache{

    fun getObjectEditorButtonHandlers(objectId:String):List<Any>{
        if(!objectEditorButtonHandlersCache.containsKey(objectId)){
            updateObjectsButtonsCaches(objectId)
        }
        return objectEditorButtonHandlersCache[objectId]!!
    }

    private fun updateObjectsButtonsCaches(objectId: String) {
        val list1 = ClientRegistry.get().allOf(ObjectEditorButton.TYPE).filter { it.isApplicable(objectId) }
        val list2 = ClientRegistry.get().allOf(ObjectEditorMenuItem.TYPE).filter { it.isApplicable(objectId) }
        val editorButtons = arrayListOf<HasWeight>()
        editorButtons.addAll(list1)
        val list3 = list2.mapNotNull { ClientRegistry.get().get(MenuButton.TYPE, it.getMenuButtonId()) }.distinct()
        editorButtons.addAll(list3)
        editorButtons.sortBy { it.getWeight() }
        objectEditorButtonHandlersCache[objectId] = editorButtons
        objectEditorMenuItemsCache[objectId] = hashMapOf()
        list3.forEach {mb ->
            val list = arrayListOf<ObjectEditorMenuItem<WebEditor<*,*,*>>>()
            list.addAll(list2.filter { it.getMenuButtonId() == mb.getId() })
            list.sortBy { it.getWeight() }
            objectEditorMenuItemsCache[objectId]!![mb.getId()] = list
        }
    }

    fun getObjectEditorMenuItems(objectId:String, buttonId:String): List<ObjectEditorMenuItem<WebEditor<*,*,*>>>{
        if(!objectEditorMenuItemsCache.containsKey(objectId)){
            updateObjectsButtonsCaches(objectId)
        }
        return objectEditorMenuItemsCache[objectId]!![buttonId]!!
    }

    companion object{
        private val objectEditorButtonHandlersCache = hashMapOf<String, List<HasWeight>>()
        private val objectEditorMenuItemsCache = hashMapOf<String, MutableMap<String, List<ObjectEditorMenuItem<WebEditor<*,*,*>>>>>()
        fun get() = EnvironmentJS.getPublished(ObjectsHandlersCache::class)
    }
}