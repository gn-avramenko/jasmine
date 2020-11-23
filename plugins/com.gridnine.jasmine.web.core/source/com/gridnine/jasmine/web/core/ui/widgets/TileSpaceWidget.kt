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
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS

open class TileSpaceWidget<VM:BaseVMJS, VS:BaseVSJS, VV:BaseVVJS>(aParent:WebComponent?, configure:TileSpaceWidgetConfiguration<VM>.(widget:TileSpaceWidget<VM, VS, VV>)->Unit) :WebEditor<VM,VS,VV>{
    private val parent = aParent
    private val configuration = TileSpaceWidgetConfiguration<VM>()
    private val delegate:WebDivsContainer
    private val mainPanelId = "mainPanel${MiscUtilsJS.createUUID()}"
    private val mainPanel:TilesSpaceMainPanel<VM, VS, VV>
    private val tilesEditors = hashMapOf<String,TilePanel>()
    init {
        configuration.configure(this)
        delegate  = UiLibraryAdapter.get().createDivsContainer(this){
            width = configuration.width
            height = configuration.height
        }
        mainPanel = TilesSpaceMainPanel(delegate, {delegate.show(it)}, configuration)
        configuration.tiles.forEach {tile ->
            val editor = TilePanel(delegate, {this.delegate.show(this.mainPanelId)}, tile)
            tilesEditors[tile.id] = editor
            delegate.addDiv(tile.id, editor)
        }
        delegate.addDiv(mainPanelId, mainPanel)
        delegate.show(mainPanelId)

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

        tilesEditors.entries.forEach {
            vm.setValue(it.key, it.value.getData())
        }
        return vm
    }

    override fun readData(vm: VM, vs: VS) {
        mainPanel.readData(vm,vs)
        tilesEditors.entries.forEach {
            it.value.readData(vm.getValue(it.key) as BaseVMJS, vs.getValue(it.key) as BaseVSJS)
        }
    }

    override fun setReadonly(value: Boolean) {
        tilesEditors.entries.forEach {
            it.value.setReadonly(value)
        }
    }

    override fun showValidation(validation: VV) {
        mainPanel.showValidation(validation)
        tilesEditors.entries.forEach {
            it.value.showValidation(validation.getValue(it.key) as BaseVVJS)
        }
    }

    override fun navigate(id: String): Boolean {
        return tilesEditors.entries.find { it.value.navigate(id) }?.let { delegate.show(it.key) } != null
    }
}

class TileSpaceWidgetConfiguration<VM:BaseVMJS>{
    var width:String? = null
    var height:String? = null
    var overviewConfig:TileSpaceOverviewConfiguration? = null
    val tiles = arrayListOf<TileConfiguration>()
    lateinit var vmFactory: ()->VM
    fun overview(title:String, editor:WebEditor<*,*,*>){
        overviewConfig = TileSpaceOverviewConfiguration()
        overviewConfig!!.title = title
        overviewConfig!!.editor = editor
    }
    fun tile(id:String, title:String, editor: WebEditor<*, *, *>){
        val res = TileConfiguration()
        res.id = id
        res.title = title
        res.editor = editor
        tiles.add(res)
    }

}

class TileSpaceOverviewConfiguration{
    lateinit var title:String
    lateinit var editor:WebEditor<*,*,*>
}

class TileConfiguration{
    lateinit var title:String
    lateinit var id:String
    lateinit var editor:WebEditor<*,*,*>
}
class TilePanel(private val parent:WebComponent, private val collapseHandler:()->Unit, private val tileConfig:TileConfiguration):WebEditor<BaseVMJS,BaseVSJS,BaseVVJS>{

    private val delegate:WebPanel = UiLibraryAdapter.get().createPanel(this){
        width = "100%"
        height = "100%"
        content = tileConfig.editor
        tools.add(PanelToolConfiguration("close", "core:close"))
    }

    init {
        delegate.setTitle(tileConfig.title)
        delegate.setToolHandler { _, _ ->
            collapseHandler.invoke()
        }
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

    override fun getData(): BaseVMJS {
        return tileConfig.editor.getData()
    }

    override fun readData(vm: BaseVMJS, vs: BaseVSJS) {
        (tileConfig.editor as WebEditor<BaseVMJS,BaseVSJS,BaseVVJS>).readData(vm, vs)
    }

    override fun setReadonly(value: Boolean) {
        tileConfig.editor.setReadonly(value)
    }

    override fun showValidation(validation: BaseVVJS) {
        (tileConfig.editor as WebEditor<BaseVMJS,BaseVSJS,BaseVVJS>).showValidation(validation)
    }

    override fun navigate(id: String): Boolean {
        return  (tileConfig.editor as WebEditor<BaseVMJS,BaseVSJS,BaseVVJS>).navigate(id)
    }


}
class TilesSpaceMainPanel<VM:BaseVMJS, VS:BaseVSJS, VV:BaseVVJS>(private val parent:WebComponent, private val expandHadler:(String)->Unit, private val configuration: TileSpaceWidgetConfiguration<VM>):WebEditor<VM,VS,VV>{
    private val delegate:WebGridLayoutContainer = UiLibraryAdapter.get().createGridLayoutContainer(this){
        width = "100%"
        height = "100%"
    }

    init {
        delegate.defineColumn("100%")
        configuration.overviewConfig?.let {
            delegate.addRow()
            val overviewPanel = UiLibraryAdapter.get().createPanel(this){
                width = "100%"
                content = it.editor
            }
            it.editor.setReadonly(true)
            overviewPanel.setTitle(it.title)
            delegate.addCell(WebGridLayoutCell(overviewPanel))
        }
        delegate.addRow("100%")
        val tilesContainer = UiLibraryAdapter.get().createTilesContainer(delegate){
            height = "100%"
            width = "100%"
            configuration.tiles.forEach { tileConfiguration ->
                tile(tileConfiguration.id, tileConfiguration.title)
            }
        }
        tilesContainer.setExpandHandler(expandHadler)
        delegate.addCell(WebGridLayoutCell(tilesContainer))
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
        configuration.overviewConfig?.let {
            (it.editor as WebEditor<BaseVMJS, BaseVSJS, BaseVVJS>).readData(vm.getValue("overview") as BaseVMJS, vs.getValue("overview") as BaseVSJS)
        }
    }

    override fun setReadonly(value: Boolean) {
        //noops
    }

    override fun showValidation(validation: VV) {
        //noops
    }

}