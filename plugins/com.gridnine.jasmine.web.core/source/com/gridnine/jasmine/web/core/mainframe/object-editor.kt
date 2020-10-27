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
import kotlin.js.Promise

class ObjectEditorTabHandler:MainFrameTabHandler<ObjectEditorTabData, GetEditorDataResponseJS>{
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
        return MainFrameTabData(data.title, ObjectEditor<WebEditor<BaseVMJS,BaseVSJS,BaseVVJS>>(parent, obj, data,callback))
    }
}

class ObjectEditorTabData(val objectType:String, var objectUid:String?)

class ObjectEditor<W:WebEditor<*,*,*>>(aParent: WebComponent, val obj: ObjectEditorTabData, data: GetEditorDataResponseJS, private val callback: MainFrameTabCallback):WebComponent{
    private val delegate:WebBorderContainer
    private val viewButton:WebLinkButton
    private val editButton:WebLinkButton
    private val parent:WebComponent = aParent
    val rootWebEditor:W
    private val editorButtonsMap = hashMapOf<WebLinkButton, ObjectEditorButton<WebEditor<*,*,*>>>()
    var readOnly:Boolean = true

    init {
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
        ObjectsHandlersCache.get().getObjectEditorButtonHandlers(obj.objectType).forEach {oeb ->
            val button = UiLibraryAdapter.get().createLinkButton(toolBar){
                title = oeb.getDisplayName()
                icon  = oeb.getIcon()
            }
            editorButtonsMap[button] = oeb
            button.setHandler {
                oeb.onClick(ObjectEditor@this as ObjectEditor<WebEditor<*, *, *>>)
            }
            toolBar.addCell(WebGridLayoutCell(button))
        }
        toolBar.addCell(WebGridLayoutCell(null))
        viewButton = UiLibraryAdapter.get().createLinkButton(toolBar){
            title = CoreWebMessagesJS.view
        }

        viewButton.setVisible(false)
        toolBar.addCell(WebGridLayoutCell(viewButton))
        editButton = UiLibraryAdapter.get().createLinkButton(toolBar){
            title = CoreWebMessagesJS.edit
        }

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
        rootWebEditor.setReadonly(true)
        updateButtonsState()
    }

    fun updateButtonsState() {
        editorButtonsMap.entries.forEach { it.key.setEnabled(it.value.isEnabled(ObjectEditor@this as ObjectEditor<WebEditor<*, *, *>>)) }
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
        callback.setTitle(value)
    }
}

class ObjectsHandlersCache{

    fun getObjectEditorButtonHandlers(objectId:String):List<ObjectEditorButton<WebEditor<*,*,*>>>{
        return objectEditorButtonHandlersCache.getOrPut(objectId, {
            ClientRegistry.get().allOf(ObjectEditorButton.TYPE).filter { it.isApplicable(objectId) }.sortedBy { it.getWeight() }
        })
    }

    companion object{
        private val objectEditorButtonHandlersCache = hashMapOf<String, List<ObjectEditorButton<WebEditor<*,*,*>>>>()
        fun get() = EnvironmentJS.getPublished(ObjectsHandlersCache::class)
    }
}