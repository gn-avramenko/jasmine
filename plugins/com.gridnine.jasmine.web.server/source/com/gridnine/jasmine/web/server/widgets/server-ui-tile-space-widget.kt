/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.widgets

import com.gridnine.jasmine.server.core.model.ui.*
import com.gridnine.jasmine.web.server.components.*
import java.util.*

abstract class ServerUiTileSpaceWidget<VM:BaseVM, VS:BaseVS, VV:BaseVV>() :ServerUiViewEditor<VM,VS,VV>,BaseServerUiNodeWrapper<ServerUiDivsContainer>(){

    private val configuration= ServerUiTileSpaceWidgetConfiguration<VM>()
    private val mainPanelId = "mainPanel${UUID.randomUUID().toString()}"
    private val mainPanel:ServerUiTilesSpaceMainPanel<VM, VS, VV>
    private val tilesEditors = hashMapOf<String,ServerUiTileWidgetPanel>()
    init {
        createInitializer().apply { configuration }
        _node = ServerUiLibraryAdapter.get().createDivsContainer(ServerUiDivsContainerConfiguration{
            width = configuration.width
            height = configuration.height
        })
        mainPanel = ServerUiTilesSpaceMainPanel({
            _node.show(it)
        }, configuration)
        configuration.tiles.forEach {tile ->
            val editor = ServerUiTileWidgetPanel({
                _node.show(mainPanelId)
            }, tile)
            tilesEditors[tile.id] = editor
            _node.addDiv(tile.id, editor)
        }
        _node.addDiv(mainPanelId, mainPanel)
        _node.show(mainPanelId)

    }

    protected abstract fun createInitializer():ServerUiTileSpaceWidgetConfiguration<VM>.()->Unit

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

class ServerUiTileSpaceWidgetConfiguration<VM:BaseVM>(){
    constructor(config:ServerUiTileSpaceWidgetConfiguration<VM>.()->Unit):this(){
        config.invoke(this)
    }
    var width:String? = null
    var height:String? = null
    var overviewConfig:ServerUiTileSpaceWidgetOverviewConfiguration? = null
    val tiles = arrayListOf<ServerUiTileWidgetConfiguration>()
    lateinit var vmFactory: ()->VM
    fun<T:ServerUiViewEditor<*,*,*>> overview(title:String, editor:T):T{
        overviewConfig = ServerUiTileSpaceWidgetOverviewConfiguration()
        overviewConfig!!.title = title
        overviewConfig!!.editor = editor
        return editor
    }
    fun<T:ServerUiViewEditor<*,*,*>> tile(id:String, title:String, editor: T):T{
        val res = ServerUiTileWidgetConfiguration()
        res.id = id
        res.title = title
        res.editor = editor
        tiles.add(res)
        return editor
    }

}

class ServerUiTileSpaceWidgetOverviewConfiguration{
    lateinit var title:String
    lateinit var editor: ServerUiViewEditor<*, *, *>
}

class ServerUiTileWidgetConfiguration{
    lateinit var title:String
    lateinit var id:String
    lateinit var editor:ServerUiViewEditor<*,*,*>
}

class ServerUiTilesSpaceMainPanel<VM:BaseVM, VS:BaseVS, VV:BaseVV>(private val expandHadler:(String)->Unit, private val configuration: ServerUiTileSpaceWidgetConfiguration<VM>): ServerUiViewEditor<VM,VS,VV>, BaseServerUiNodeWrapper<ServerUiGridLayoutContainer>(){

    init {
        _node = ServerUiLibraryAdapter.get().createGridLayoutContainer(ServerUiGridLayoutContainerConfiguration(){
            width = "100%"
            height = "100%"
            columns.add(ServerUiGridLayoutColumnConfiguration("100%"))
        })

        configuration.overviewConfig?.let {
            _node.addRow()
            val overviewPanel = ServerUiLibraryAdapter.get().createPanel(ServerUiPanelConfiguration{
                width = "100%"
            })
            overviewPanel.setContent(it.editor)
            it.editor.setReadonly(true)
            overviewPanel.setTitle(it.title)
            _node.addCell(ServerUiGridLayoutCell(overviewPanel))
        }
        _node.addRow("100%")

        val tilesContainer = ServerUiLibraryAdapter.get().createTilesContainer(ServerUiTilesContainerConfiguration{
            height = "100%"
            width = "100%"
            tileWidth = "200px"
        })
        tilesContainer.setExpandHandler {

        }
        tilesContainer.setTiles(configuration.tiles.map {
            ServerUiTileConfiguration(it.id,it.title)
        })
        tilesContainer.setExpandHandler(expandHadler)
        _node.addCell(ServerUiGridLayoutCell(tilesContainer))
    }

    override fun setData(data: VM, settings: VS?) {
        (configuration.overviewConfig!!.editor as  ServerUiViewEditor<BaseVM,BaseVS,BaseVV>).setData(data.getValue("overview") as BaseVM, settings?.getValue("overview") as BaseVS)
    }

    override fun getData(): VM {
        val vm = configuration.vmFactory.invoke()
        return vm
    }

    override fun showValidation(validation: VV?) {
        //noops
    }

    override fun setReadonly(value: Boolean) {
        //noops
    }

    override fun navigate(key: String): Boolean {
        return false
    }

}

class ServerUiTileWidgetPanel(private val collapseHandler:()->Unit, private val tileConfig:ServerUiTileWidgetConfiguration):ServerUiViewEditor<BaseVM,BaseVS,BaseVV>,BaseServerUiNodeWrapper<ServerUiPanel>(){


    init {
        _node = ServerUiLibraryAdapter.get().createPanel(ServerUiPanelConfiguration{
            width = "100%"
            height = "100%"
            minimizable = true
        })
        _node.setContent(tileConfig.editor)
        _node.setMinimizeHandler(collapseHandler)
        _node.setTitle(tileConfig.title)
    }

    override fun getData(): BaseVM {
        return tileConfig.editor.getData()
    }

    override fun setData(vm: BaseVM, vs: BaseVS?) {
        (tileConfig.editor as ServerUiViewEditor<BaseVM,BaseVS,BaseVV>).setData(vm, vs)
    }

    override fun setReadonly(value: Boolean) {
        tileConfig.editor.setReadonly(value)
    }

    override fun showValidation(validation: BaseVV?) {
        (tileConfig.editor as ServerUiViewEditor<BaseVM,BaseVS,BaseVV>).showValidation(validation)
    }

    override fun navigate(id: String): Boolean {
        return  (tileConfig.editor  as ServerUiViewEditor<BaseVM,BaseVS,BaseVV>).navigate(id)
    }


}