/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("UNCHECKED_CAST")

package com.gridnine.jasmine.server.core.ui.widgets

import com.gridnine.jasmine.common.core.model.BaseVM
import com.gridnine.jasmine.common.core.model.BaseVS
import com.gridnine.jasmine.common.core.model.BaseVV
import com.gridnine.jasmine.common.core.utils.TextUtils
import com.gridnine.jasmine.server.core.ui.common.BaseNodeWrapper
import com.gridnine.jasmine.server.core.ui.common.BaseWidgetConfiguration
import com.gridnine.jasmine.server.core.ui.common.ViewEditor
import com.gridnine.jasmine.server.core.ui.common.ViewEditorInterceptorsRegistry
import com.gridnine.jasmine.server.core.ui.components.*

@Suppress("LeakingThis")
abstract class TileSpaceWidget<VM:BaseVM, VS:BaseVS, VV:BaseVV> :ViewEditor<VM,VS,VV>,BaseNodeWrapper<DivsContainer>(){

    private val configuration= TileSpaceWidgetConfiguration<VM>()
    private val mainPanelId = "mainPanel${TextUtils.generateUid()}"
    private val mainPanel: TilesSpaceMainPanel<VM, VS, VV>
    private val tilesEditors = hashMapOf<String, TileWidgetPanel>()
    init {
        configuration.apply(createInitializer())
        _node = UiLibraryAdapter.get().createDivsContainer{
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
        ViewEditorInterceptorsRegistry.get().getInterceptors(this)?.forEach {
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

    override fun setData(vm: VM, vs: VS?) {
        mainPanel.setData(vm,vs)
        tilesEditors.entries.forEach {
            it.value.setData(vm.getValue(it.key) as BaseVM, vs?.getValue(it.key) as BaseVS?)
        }
    }

    override fun setReadonly(value: Boolean) {
        tilesEditors.entries.forEach {
            it.value.setReadonly(value)
        }
    }

    override fun showValidation(validation: VV?) {
        tilesEditors.entries.forEach {
            it.value.showValidation(validation?.getValue(it.key) as BaseVV?)
        }
    }

    override fun navigate(id: String): Boolean {
        return tilesEditors.entries.find { it.value.navigate(id) }?.let { _node.show(it.key) } != null
    }
}

class TileSpaceWidgetConfiguration<VM:BaseVM>:BaseWidgetConfiguration(){
    var overviewConfig: TileSpaceWidgetOverviewConfiguration? = null
    val tiles = arrayListOf<TileWidgetConfiguration>()
    lateinit var vmFactory: ()->VM
    fun<T:ViewEditor<*,*,*>> overview(title:String, editor:T):T{
        overviewConfig = TileSpaceWidgetOverviewConfiguration()
        overviewConfig!!.title = title
        overviewConfig!!.editor = editor
        return editor
    }
    fun<T:ViewEditor<*,*,*>> tile(id:String, title:String, editor: T):T{
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
    lateinit var editor: ViewEditor<*, *, *>
}

class TileWidgetConfiguration{
    lateinit var title:String
    lateinit var id:String
    lateinit var editor:ViewEditor<*,*,*>
}

class TilesSpaceMainPanel<VM:BaseVM, VS:BaseVS, VV:BaseVV>(expandHadler:(String)->Unit, private val configuration: TileSpaceWidgetConfiguration<VM>): ViewEditor<VM,VS,VV>, BaseNodeWrapper<GridLayoutContainer>(){

    init {
        _node = UiLibraryAdapter.get().createGridLayoutContainer{
            width = "100%"
            height = "100%"
            columns.add(GridLayoutColumnConfiguration("100%"))
        }

        configuration.overviewConfig?.let {
            _node.addRow()
            val overviewPanel = UiLibraryAdapter.get().createPanel{
                width = "100%"
            }
            overviewPanel.setContent(it.editor)
            it.editor.setReadonly(true)
            overviewPanel.setTitle(it.title)
            _node.addCell(GridLayoutCell(overviewPanel))
        }
        _node.addRow("100%")

        val tilesContainer = UiLibraryAdapter.get().createTilesContainer{
            height = "100%"
            width = "100%"
            tileWidth = "200px"
        }
        tilesContainer.setTiles(configuration.tiles.map {
            TileConfiguration(it.id,it.title)
        })
        tilesContainer.setExpandHandler(expandHadler)
        _node.addCell(GridLayoutCell(tilesContainer))
    }

    override fun setData(vm: VM, vs: VS?) {
        (configuration.overviewConfig!!.editor as  ViewEditor<BaseVM,BaseVS,BaseVV>).setData(vm.getValue("overview") as BaseVM, vs?.getValue("overview") as BaseVS?)
    }

    override fun getData(): VM {
        return configuration.vmFactory.invoke()
    }

    override fun showValidation(validation: VV?) {
        //noops
    }

    override fun setReadonly(value: Boolean) {
        //noops
    }

    override fun navigate(id: String): Boolean {
        return false
    }

}

class TileWidgetPanel(collapseHandler:()->Unit, private val tileConfig: TileWidgetConfiguration):ViewEditor<BaseVM,BaseVS,BaseVV>,BaseNodeWrapper<Panel>(){


    init {
        _node = UiLibraryAdapter.get().createPanel{
            width = "100%"
            height = "100%"
            minimizable = true
        }
        _node.setContent(tileConfig.editor)
        _node.setMinimizeHandler(collapseHandler)
        _node.setTitle(tileConfig.title)
    }

    override fun getData(): BaseVM {
        return tileConfig.editor.getData()
    }

    override fun setData(vm: BaseVM, vs: BaseVS?) {
        (tileConfig.editor as ViewEditor<BaseVM,BaseVS,BaseVV>).setData(vm, vs)
    }

    override fun setReadonly(value: Boolean) {
        tileConfig.editor.setReadonly(value)
    }

    override fun showValidation(validation: BaseVV?) {
        (tileConfig.editor as ViewEditor<BaseVM,BaseVS,BaseVV>).showValidation(validation)
    }

    override fun navigate(id: String): Boolean {
        return  (tileConfig.editor  as ViewEditor<BaseVM,BaseVS,BaseVV>).navigate(id)
    }


}