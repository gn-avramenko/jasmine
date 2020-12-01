/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.mainframe.workspaceEditor

import com.gridnine.jasmine.server.standard.model.domain.WorkspaceJS
import com.gridnine.jasmine.server.standard.model.rest.GetWorkspaceRequestJS
import com.gridnine.jasmine.server.standard.model.rest.GetWorkspaceResponseJS
import com.gridnine.jasmine.web.core.CoreWebMessagesJS
import com.gridnine.jasmine.web.core.StandardRestClient
import com.gridnine.jasmine.web.core.mainframe.MainFrameTabCallback
import com.gridnine.jasmine.web.core.mainframe.MainFrameTabData
import com.gridnine.jasmine.web.core.mainframe.MainFrameTabHandler
import com.gridnine.jasmine.web.core.ui.UiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.WebPopupContainer
import com.gridnine.jasmine.web.core.ui.components.WebBorderContainer
import com.gridnine.jasmine.web.core.ui.components.WebGridLayoutCell
import com.gridnine.jasmine.web.core.ui.components.WebLinkButton
import com.gridnine.jasmine.web.core.ui.components.WebTreeNode
import kotlin.js.Promise


class WorkspaceEditorTabHandler: MainFrameTabHandler<Unit, GetWorkspaceResponseJS> {
    override fun getTabId(obj: Unit): String {
        return "workspaceEditor"
    }

    override fun loadData(obj: Unit): Promise<GetWorkspaceResponseJS> {
        val request = GetWorkspaceRequestJS()
        return StandardRestClient.standard_standard_getWorkspace(request)
    }

    override fun createTabData(obj: Unit, data: GetWorkspaceResponseJS, parent: WebComponent, callback: MainFrameTabCallback): MainFrameTabData {
        return MainFrameTabData(CoreWebMessagesJS.Workspace, WorkspaceEditor(parent, data.workspace))
    }
}

class WorkspaceEditor(private val parent: WebComponent, private val workspace:WorkspaceJS): WebComponent, WebPopupContainer {
    private val delegate: WebBorderContainer
    private val saveButton: WebLinkButton

    init {
        delegate = UiLibraryAdapter.get().createBorderLayout(this){
            fit=true
        }
        val centerContent = UiLibraryAdapter.get().createLabel(delegate)
        centerContent.setText("Center content")

        delegate.setCenterRegion(WebBorderContainer.region {
            content = centerContent
            showBorder = true
        })
        val toolBar = UiLibraryAdapter.get().createGridLayoutContainer(delegate){
            width = "100%"
        }
        saveButton = UiLibraryAdapter.get().createLinkButton(toolBar){
            title = CoreWebMessagesJS.save
        }
        toolBar.defineColumn("auto")
        toolBar.defineColumn("100%")
        toolBar.addRow()
        toolBar.addCell(WebGridLayoutCell(saveButton))
        toolBar.addCell(WebGridLayoutCell(null))
        delegate.setNorthRegion(WebBorderContainer.region {
            content = toolBar
        })
        val tree = UiLibraryAdapter.get().createTree(delegate){
            fit = true
            enableDnd = true
            width = "100%"
            height = "100%"
        }
        delegate.setWestRegion(WebBorderContainer.region {
            content =tree
            width = 200
            showBorder = true
            collapsible = false
        })
        tree.setData(workspace.groups.map {
            val node = WebTreeNode(it.uid, it.displayName?:"???",it)
            node.children.addAll(it.items.map {
                val itemNode = WebTreeNode(it.uid, it.displayName?:"???",it)
                itemNode
            })
            node
        })
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


    override fun getId(): String {
        return delegate.getId()
    }
}

