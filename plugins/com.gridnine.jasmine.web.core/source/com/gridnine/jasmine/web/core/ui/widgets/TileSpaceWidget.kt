/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.widgets

import com.gridnine.jasmine.server.core.model.ui.BaseVMJS
import com.gridnine.jasmine.server.core.model.ui.BaseVSJS
import com.gridnine.jasmine.server.core.model.ui.BaseVVJS
import com.gridnine.jasmine.web.core.ui.UiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.WebEditor
import com.gridnine.jasmine.web.core.ui.components.WebGridLayoutCell
import com.gridnine.jasmine.web.core.ui.components.WebGridLayoutContainer

open class TileSpaceWidget<VM:BaseVMJS, VS:BaseVSJS, VV:BaseVVJS>(aParent:WebComponent?, configure:TileSpaceWidgetConfiguration<VM>.(widget:TileSpaceWidget<VM, VS, VV>)->Unit) :WebEditor<VM,VS,VV>{
    private val parent = aParent
    private val configuration = TileSpaceWidgetConfiguration<VM>()
    private val delegate:WebGridLayoutContainer
    init {
        configuration.configure(this)
        delegate  = UiLibraryAdapter.get().createGridLayoutContainer(this){
            width = configuration.width
            height = configuration.height
        }
        delegate.defineColumn("100%")
        delegate.addRow()
        val overviewLabel = UiLibraryAdapter.get().createLabel(this)
        overviewLabel.setText("Overview")
        delegate.addCell(WebGridLayoutCell(overviewLabel))
        delegate.addRow("100%")
        val tileLabel = UiLibraryAdapter.get().createLabel(delegate)
        tileLabel.setText("Tiles")
        delegate.addCell(WebGridLayoutCell(tileLabel))
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

    override fun getData(): VM {
        val vm = configuration.vmFactory.invoke()
        return vm
    }

    override fun readData(vm: VM, vs: VS) {
        //noops
    }

    override fun setReadonly(value: Boolean) {
        //noops
    }

    override fun showValidation(validation: VV) {
        //noops
    }

}


class TileSpaceWidgetConfiguration<VM:BaseVMJS>{
    var width:String? = null
    var height:String? = null
    var overviewConfig:TileSpaceOverviewConfiguration? = null
    lateinit var vmFactory: ()->VM
    fun overview(title:String, editor:WebEditor<*,*,*>){
        overviewConfig = TileSpaceOverviewConfiguration()
        overviewConfig!!.title = title
        overviewConfig!!.editor = editor
    }
}

class TileSpaceOverviewConfiguration{
    lateinit var title:String
    lateinit var editor:WebEditor<*,*,*>
}