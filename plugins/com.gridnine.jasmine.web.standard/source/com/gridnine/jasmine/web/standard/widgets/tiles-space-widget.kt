/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("UNCHECKED_CAST", "unused")

package com.gridnine.jasmine.web.standard.widgets

import com.gridnine.jasmine.common.core.model.*
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.standard.editor.WebEditor
import com.gridnine.jasmine.web.standard.editor.WebEditorInterceptorsRegistry

@Suppress("LeakingThis")
abstract class TileSpaceWidget<VM:BaseVMJS, VS:BaseVSJS, VV:BaseVVJS> :WebEditor<VM,VS,VV>,BaseWebNodeWrapper<WebDivsContainer>(){

    private val configuration= TileSpaceWidgetConfiguration<VM>()
    private val mainPanelId = "mainPanel${MiscUtilsJS.createUUID()}"
    private val mainPanel: TilesSpaceMainPanel<VM, VS, VV>
    private val tilesEditors = hashMapOf<String, TileWidgetPanel>()
    init {
        configuration.apply(createInitializer())
        _node = WebUiLibraryAdapter.get().createDivsContainer{
            width = configuration.width
            height = configuration.height
        }
        mainPanel = TilesSpaceMainPanel({
            _node.show(it)
        }, configuration)
        configuration.tiles.forEach {tile ->
            val editor = TileWidgetPanel({
                _node.show(mainPanelId)
            }, tile)
            tilesEditors[tile.id] = editor
            _node.addDiv(tile.id, editor)
        }
        _node.addDiv(mainPanelId, mainPanel)
        _node.show(mainPanelId)
        WebEditorInterceptorsRegistry.get().getInterceptors(this)?.forEach {
            it.onInit(this)
        }
    }

    protected abstract fun createInitializer(): TileSpaceWidgetConfiguration<VM>.()->Unit

    override fun getData(): VM {
        val vm = configuration.vmFactory.invoke()

        tilesEditors.entries.forEach {
            vm.setValue(it.key, it.value.getData())
        }
        return vm
    }


    override fun readData(vm: VM, vs: VS?) {
        mainPanel.readData(vm,vs)
        tilesEditors.entries.forEach {
            it.value.readData(vm.getValue(it.key) as BaseVMJS, vs?.getValue(it.key) as BaseVSJS?)
        }
    }

    override fun setReadonly(value: Boolean) {
        tilesEditors.entries.forEach {
            it.value.setReadonly(value)
        }
    }

    override fun showValidation(vv: VV?) {
        tilesEditors.entries.forEach {
            it.value.showValidation(vv?.getValue(it.key) as BaseVVJS?)
        }
    }

    override fun navigate(id: String): Boolean {
        val result = tilesEditors.entries.find { it.value.navigate(id) }?.let { _node.show(it.key) } != null
        if(result){
            return true
        }
        return tilesEditors.entries.find { it.key == id }?.let { _node.show(it.key) } != null
    }
}

class TileSpaceWidgetConfiguration<VM:BaseVMJS>:BaseWidgetConfiguration(){
    var overviewConfig: TileSpaceWidgetOverviewConfiguration? = null
    val tiles = arrayListOf<TileWidgetConfiguration>()
    lateinit var vmFactory: ()->VM
    fun<T:WebEditor<*,*,*>> overview(title:String, editor:T):T{
        overviewConfig = TileSpaceWidgetOverviewConfiguration()
        overviewConfig!!.title = title
        overviewConfig!!.editor = editor
        return editor
    }
    fun<T:WebEditor<*,*,*>> tile(id:String, title:String, editor: T):T{
        val res = TileWidgetConfiguration()
        res.id = id
        res.title = title
        res.editor = editor
        tiles.add(res)
        return editor
    }

}

class TileSpaceWidgetOverviewConfiguration{
    lateinit var title:String
    lateinit var editor: WebEditor<*, *, *>
}

class TileWidgetConfiguration{
    lateinit var title:String
    lateinit var id:String
    lateinit var editor:WebEditor<*,*,*>
}

class TilesSpaceMainPanel<VM:BaseVMJS, VS:BaseVSJS, VV:BaseVVJS>(expandHadler:(String)->Unit, private val configuration: TileSpaceWidgetConfiguration<VM>): WebEditor<VM,VS,VV>, BaseWebNodeWrapper<WebGridLayoutContainer>(){

    init {
        _node = WebUiLibraryAdapter.get().createGridContainer{
            width = "100%"
            height = "100%"
            column("100%")
            configuration.overviewConfig?.let {
                row("auto") {
                    val overviewPanel = WebUiLibraryAdapter.get().createPanel{
                        width = "100%"
                        content = it.editor
                    }
                    it.editor.setReadonly(true)
                    overviewPanel.setTitle(it.title)
                    cell(overviewPanel)
                }
            }
            row("100%"){
                val tilesContainer = WebUiLibraryAdapter.get().createTilesContainer{
                    height = "100%"
                    width = "100%"
                    tileWidth = 200
                    configuration.tiles.forEach {
                        tile(it.id,it.title)
                    }
                }
                tilesContainer.setExpandHandler(expandHadler)
                cell(tilesContainer)
            }
        }

    }

    override fun readData(vm: VM, vs: VS?) {
        (configuration.overviewConfig!!.editor as  WebEditor<BaseVMJS,BaseVSJS,BaseVVJS>).readData(vm.getValue("overview") as BaseVMJS, vs?.getValue("overview") as BaseVSJS?)
    }

    override fun getData(): VM {
        return configuration.vmFactory.invoke()
    }

    override fun showValidation(vv: VV?) {
        //noops
    }

    override fun setReadonly(value: Boolean) {
        //noops
    }

    override fun navigate(id: String): Boolean {
        return false
    }

}

class TileWidgetPanel(collapseHandler:suspend ()->Unit, private val tileConfig: TileWidgetConfiguration):WebEditor<BaseVMJS,BaseVSJS,BaseVVJS>,BaseWebNodeWrapper<WebPanel>(){


    init {
        _node = WebUiLibraryAdapter.get().createPanel{
            width = "100%"
            height = "100%"
            content = tileConfig.editor
            tools.add(PanelToolConfiguration("close", "core:close"))
        }
        _node.setToolHandler{_, _ ->
            collapseHandler.invoke()
        }
        _node.setTitle(tileConfig.title)
    }

    override fun getData(): BaseVMJS {
        return tileConfig.editor.getData()
    }

    override fun readData(vm: BaseVMJS, vs: BaseVSJS?) {
        (tileConfig.editor as WebEditor<BaseVMJS,BaseVSJS,BaseVVJS>).readData(vm, vs)
    }

    override fun setReadonly(value: Boolean) {
        tileConfig.editor.setReadonly(value)
    }

    override fun showValidation(vv: BaseVVJS?) {
        (tileConfig.editor as WebEditor<BaseVMJS,BaseVSJS,BaseVVJS>).showValidation(vv)
    }

    override fun navigate(id: String): Boolean {
        return  (tileConfig.editor  as WebEditor<BaseVMJS,BaseVSJS,BaseVVJS>).navigate(id)
    }


}