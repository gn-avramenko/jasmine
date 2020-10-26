/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.mainframe

import com.gridnine.jasmine.server.core.model.domain.ObjectReferenceJS
import com.gridnine.jasmine.server.core.model.ui.BaseVMJS
import com.gridnine.jasmine.server.core.model.ui.BaseVSJS
import com.gridnine.jasmine.server.core.model.ui.BaseVVJS
import com.gridnine.jasmine.server.standard.model.rest.GetEditorDataRequestJS
import com.gridnine.jasmine.server.standard.model.rest.GetEditorDataResponseJS
import com.gridnine.jasmine.web.core.CoreWebMessagesJS
import com.gridnine.jasmine.web.core.StandardRestClient
import com.gridnine.jasmine.web.core.ui.*
import com.gridnine.jasmine.web.core.ui.components.*
import kotlin.js.Promise

class ObjectEditorTabHandler:MainFrameTabHandler<ObjectReferenceJS, GetEditorDataResponseJS>{
    override fun getTabId(obj: ObjectReferenceJS): String {
        return "${obj.type}||${obj.uid}"
    }

    override fun loadData(obj: ObjectReferenceJS): Promise<GetEditorDataResponseJS> {
        val request = GetEditorDataRequestJS()
        request.objectId = obj.type
        request.objectUid = obj.uid
      return StandardRestClient.standard_standard_getEditorData(request)
    }

    override fun createTabData(obj: ObjectReferenceJS, data: GetEditorDataResponseJS, parent: WebComponent, callback: MainFrameTabCallback): MainFrameTabData {
        return MainFrameTabData(data.title, ObjectEditor(parent, obj, data))
    }
}

class ObjectEditor(private val par:WebComponent, private val obj:ObjectReferenceJS, private val data:GetEditorDataResponseJS,
                                       private val delegate:WebBorderContainer =  UiLibraryAdapter.get().createBorderLayout(par){
    fit=true
}):WebComponent by delegate{
    val viewButton:WebLinkButton
    val editButton:WebLinkButton
    init {
        val handler = ClientRegistry.get().get(ObjectHandler.TYPE, obj.type)!!
        val cont = handler.createWebEditor(delegate) as WebEditor<BaseVMJS, BaseVSJS,BaseVVJS>
        delegate.setCenterRegion(WebBorderContainer.region {
            content = cont
        })
        val toolBar = UiLibraryAdapter.get().createGridLayoutContainer(delegate){
            width = "100%"
        }
        toolBar.defineColumn("100%")
        toolBar.defineColumn("auto")
        toolBar.defineColumn("auto")
        toolBar.addRow()
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
            cont.setReadonly(false)
            viewButton.setVisible(true)
            editButton.setVisible(false)
        }
        viewButton.setHandler {
            cont.setReadonly(true)
            viewButton.setVisible(false)
            editButton.setVisible(true)
        }
        cont.readData(data.viewModel, data.viewSettings)
        cont.setReadonly(true)

    }
}