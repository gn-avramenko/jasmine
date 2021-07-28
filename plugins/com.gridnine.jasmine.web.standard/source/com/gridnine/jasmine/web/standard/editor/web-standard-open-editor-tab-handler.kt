/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("UNCHECKED_CAST", "unused", "UNUSED_PARAMETER")

package com.gridnine.jasmine.web.standard.editor

import com.gridnine.jasmine.common.core.model.BaseVMJS
import com.gridnine.jasmine.common.core.model.BaseVSJS
import com.gridnine.jasmine.common.core.model.BaseVVJS
import com.gridnine.jasmine.common.standard.model.rest.GetEditorDataRequestJS
import com.gridnine.jasmine.common.standard.model.rest.GetEditorDataResponseJS
import com.gridnine.jasmine.web.core.common.RegistryJS
import com.gridnine.jasmine.web.core.remote.launch
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.standard.StandardRestClient
import com.gridnine.jasmine.web.standard.mainframe.*
import com.gridnine.jasmine.web.standard.widgets.WebGridLayoutWidget

data class OpenObjectData(val type: String, var uid: String, val navigationKey: String?, val forEdit:Boolean = false)

class ObjectEditorMainFrameTabHandler:MainFrameTabHandler<OpenObjectData>{
    override fun getTabId(obj: OpenObjectData): String {
        return "${obj.type}||${obj.uid}"
    }

    override suspend fun createTabData(obj: OpenObjectData, callback: MainFrameTabCallback): MainFrameTabData {
        val request = GetEditorDataRequestJS()
        request.objectId = obj.type
        request.objectUid = obj.uid
        val response = StandardRestClient.standard_standard_getEditorData(request)
        val handler = RegistryJS.get().get(ObjectEditorHandler.TYPE, obj.type)!!
        val actionsGroup = WebActionsHandler.get().getActionsFor(handler.getActionsGroupId())
        val displayHandlers = hashMapOf<String, ObjectEditorActionDisplayHandler<WebEditor<*,*,*>>>()
        processGroup(actionsGroup, displayHandlers)
        return MainFrameTabData(response.title, ObjectEditorImpl(obj, response, actionsGroup, displayHandlers, callback))
    }

    private suspend fun processGroup(actionsGroup: ActionsGroupWrapper, displayHandlers: HashMap<String, ObjectEditorActionDisplayHandler<WebEditor<*, *, *>>>) {
        actionsGroup.actions.forEach {
            if(it is ActionWrapper){
                it.getDisplayHandler<ObjectEditorActionDisplayHandler<WebEditor<*, *, *>>>()?.let {dh ->
                    displayHandlers[it.id] = dh
                }
            } else {
                it as ActionsGroupWrapper
                processGroup(it, displayHandlers)
            }
        }
    }

    override fun getId(): String {
        return OpenObjectData::class.simpleName!!
    }

}

class ObjectEditorImpl<W : WebEditor<*, *, *>>(private val obj: OpenObjectData, initData: GetEditorDataResponseJS, actions: ActionsGroupWrapper, displayHandlers: Map<String, ObjectEditorActionDisplayHandler<W>>,  private val callback: MainFrameTabCallback) : BaseWebNodeWrapper<WebBorderContainer>(), EventsSubscriber, ObjectEditor<W> {
    private val viewButton: WebLinkButton
    private val editButton: WebLinkButton
    private val rootWebEditor: W
    private val editorButtonsMap = hashMapOf<WebLinkButton, ObjectEditorActionDisplayHandler<W>>()
    private val menuItemsMap = hashMapOf<WebMenuButton, MutableMap<String, ObjectEditorActionDisplayHandler<W>>>()
    private var readOnly: Boolean = !obj.forEdit
    var title = "???"

    init {
        title = initData.title
        _node = WebUiLibraryAdapter.get().createBorderContainer {
            fit = true
        }
        val handler = RegistryJS.get().get(ObjectEditorHandler.TYPE, obj.type)!!
        rootWebEditor = handler.createEditor() as W

        _node.setCenterRegion {
            content = rootWebEditor
        }
        viewButton = WebUiLibraryAdapter.get().createLinkButton {
            title = "Просмотр"
        }
        viewButton.setVisible(!readOnly)
        editButton = WebUiLibraryAdapter.get().createLinkButton {
            title = "Редактировать"
        }
        editButton.setVisible(readOnly)
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
        val toolBar = WebGridLayoutWidget {
            width = "100%"
        }.also {
            val widths = actions.actions.map { "auto" }.toMutableList().also { lst ->
                lst.add("100%")
                lst.add("auto")
                lst.add("auto")
            }
            it.setColumnsWidths(widths)
            val cells = actions.actions.map { wrapper ->
                when (wrapper) {
                    is ActionWrapper -> {
                        val toolButton = WebUiLibraryAdapter.get().createLinkButton {
                            title = wrapper.displayName
                        }
                        toolButton.setHandler {
                            wrapper.getActionHandler<ObjectEditorTool<W>>().invoke(this@ObjectEditorImpl)
                        }
                        displayHandlers[wrapper.id]?.let {handler ->
                            editorButtonsMap[toolButton] = handler
                        }
                        toolButton
                    }
                    is ActionsGroupWrapper -> {
                        val menuButton = WebUiLibraryAdapter.get().createMenuButton {
                            title = wrapper.displayName
                            wrapper.actions.forEach { action ->
                                if (action is ActionWrapper) {
                                    elements.add(StandardMenuItem().apply {
                                        id = action.id
                                        title = action.displayName
                                    })
                                }
                            }
                        }
                        menuItemsMap[menuButton] = hashMapOf()
                        wrapper.actions.forEach { action ->
                            if (action is ActionWrapper) {
                                menuButton.setHandler(action.id) {
                                    action.getActionHandler<ObjectEditorTool<W>>().invoke(this@ObjectEditorImpl)
                                }
                                displayHandlers[action.id]?.let { displayHandler ->
                                    menuItemsMap[menuButton]!!.put(action.id, displayHandler)
                                }
                            }
                        }
                        menuButton
                    }
                    else -> null
                }
            }.toMutableList().also { lst ->
                lst.add(null)
                lst.add(viewButton)
                lst.add(editButton)
            }
            it.addRow(cells)
        }
        _node.setNorthRegion {
            content = toolBar
        }
        (rootWebEditor as WebEditor<BaseVMJS,BaseVSJS,BaseVVJS>).readData(initData.viewModel, initData.viewSettings)
        rootWebEditor.setReadonly(readOnly)
        obj.navigationKey?.let {rootWebEditor.navigate(it)}
        updateButtonsState()
    }

    override fun updateButtonsState() {
        editorButtonsMap.entries.forEach { it.key.setVisible(it.value.isVisible(this@ObjectEditorImpl )) }
        editorButtonsMap.entries.forEach { it.key.setEnabled(it.value.isEnabled(this@ObjectEditorImpl )) }
        menuItemsMap.entries.forEach { mbEntry ->
            mbEntry.value.entries.forEach { miEntry ->
                mbEntry.key.setEnabled(miEntry.key, miEntry.value.isEnabled(this@ObjectEditorImpl))
            }
        }
    }

    override fun updateTitle(title: String) {
        this.title = title
        callback.setTitle(title)
    }


    override fun receiveEvent(event: Any) {
        if (event is ObjectModificationEvent) {
            if (event.objectType == obj.type && event.objectUid == obj.uid) {
                val request = GetEditorDataRequestJS()
                request.objectId = obj.type
                request.objectUid = obj.uid
                launch {
                    val response = StandardRestClient.standard_standard_getEditorData(request)
                    (rootWebEditor as WebEditor<BaseVMJS, BaseVSJS, BaseVVJS>).readData(response.viewModel, response.viewSettings)
                    updateButtonsState()
                    callback.setTitle(response.title)
                }
            }
        }
        if (event is ObjectDeleteEvent) {
            if (event.objectType == obj.type && event.objectUid == obj.uid) {
                callback.close()
            }
        }
    }

    override fun getEditor(): W {
        return rootWebEditor
    }

    override fun isReadonly(): Boolean {
        return readOnly
    }

    override val objectType: String
        get() = obj.type
    override var objectUid: String
        get() = obj.uid
        set(value) {obj.uid = value}

    override fun getTitle(): String {
        return title
    }
}