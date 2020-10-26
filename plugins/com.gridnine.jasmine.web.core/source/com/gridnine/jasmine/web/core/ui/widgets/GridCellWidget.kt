/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.widgets

import com.gridnine.jasmine.web.core.ui.UiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.WebGridLayoutCell
import com.gridnine.jasmine.web.core.ui.components.WebGridLayoutContainer
import com.gridnine.jasmine.web.core.ui.components.WebNumberBox

class GridCellWidget<W:WebComponent>(private val aParent:WebComponent, private val caption:String?, private val widgetFactory:(par:WebComponent)->W) : WebComponent {

    private val delegate: WebGridLayoutContainer
    private val parent:WebComponent = aParent
    private val children = arrayListOf<WebComponent>()

    val widget:W

    init {
        (parent.getChildren() as MutableList<WebComponent>).add(this)
        delegate = UiLibraryAdapter.get().createGridLayoutContainer(this){
            width = "100%"
        }
        widget = widgetFactory.invoke(this)
        val label = UiLibraryAdapter.get().createLabel(this)
        label.setText(caption)
        delegate.defineColumn("100%")
        delegate.addRow()
        delegate.addCell(WebGridLayoutCell(label))
        delegate.addRow("100%")
        delegate.addCell(WebGridLayoutCell(widget))
    }

    override fun getParent(): WebComponent? {
        return parent
    }

    override fun getChildren(): List<WebComponent> {
        return children
    }

    override fun getHtml(): String {
        return delegate.getHtml()
    }

    override fun decorate() {
        return delegate.decorate()
    }

    override fun destroy() {
        widget.destroy()
    }

}