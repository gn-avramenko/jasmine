/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.widgets

import com.gridnine.jasmine.server.core.model.common.SelectItem
import com.gridnine.jasmine.server.core.model.ui.*
import com.gridnine.jasmine.web.server.components.*

abstract class ServerUiNavigatorWidget<VM: BaseVM, VS: BaseVS, VV: BaseVV>() : ServerUiViewEditor<VM, VS, VV>,BaseServerUiNodeWrapper<ServerUiGridLayoutContainer>() {
    private val config = ServerUiNavigatorWidgetConfiguration<VM>()
    private val addButton:ServerUiLinkButton
    private val removeButton:ServerUiLinkButton
    private val divsContainer:ServerUiDivsContainer
    private val select:ServerUiGeneralSelectValueWidget
    private val possibleValues = arrayListOf<SelectItem>()
    private var addHandler:(() ->Unit)? = null
    private var removeHandler:((ServerUiViewEditor<*,*,*>) ->Unit)? = null

    init {
        createInitializer().apply { config }
        _node = ServerUiLibraryAdapter.get().createGridLayoutContainer(ServerUiGridLayoutContainerConfiguration{
            width = config.width
            height = config.height
            columns.add(ServerUiGridLayoutColumnConfiguration("100%"))
            columns.add(ServerUiGridLayoutColumnConfiguration("auto"))
            columns.add(ServerUiGridLayoutColumnConfiguration("auto"))
        })

        _node.addRow()
        select = ServerUiGeneralSelectValueWidget{
            width = "100%"
            showClearIcon = false
            showAllPossibleValues = true
        }
        _node.addCell(ServerUiGridLayoutCell(select))
        addButton= ServerUiLibraryAdapter.get().createLinkButton(ServerUiLinkButtonConfiguration{
            iconClass = "z-icon-plus"
        })
        addButton.setHandler {
            addHandler?.invoke()
        }
        _node.addCell(ServerUiGridLayoutCell(addButton))
        removeButton= ServerUiLibraryAdapter.get().createLinkButton(ServerUiLinkButtonConfiguration{
            iconClass = "z-icon-minus"
        })
        divsContainer = ServerUiLibraryAdapter.get().createDivsContainer(ServerUiDivsContainerConfiguration{
            width = "100%"
            height = "100%"
        })
        removeButton.setHandler {
            removeHandler?.let {handler ->
                select.getValue()?.id?.let{
                    handler.invoke(divsContainer.getDiv(it) as ServerUiViewEditor<*, *, *>)
                }
            }
        }
        _node.addCell(ServerUiGridLayoutCell(removeButton))
        _node.addRow("100%")

        _node.addCell(ServerUiGridLayoutCell(divsContainer, 3))
        select.changeListener = {selectItem ->
            selectItem?.let {
                divsContainer.show(it.id)
            }
        }
        config.interceptors.forEach {
            (it as ServerUiEditorInterceptor<ServerUiNavigatorWidget<VM,VS,VV>>).onInit(this)
        }
    }

    protected abstract fun createInitializer(): ServerUiNavigatorWidgetConfiguration<VM>.()->Unit

    fun removeTab(id:String){
        possibleValues.find { it.id == id }?.let{
            possibleValues.remove(it)
            divsContainer.removeDiv(id)
            select.setPossibleValues(possibleValues)
            if(possibleValues.isNotEmpty()){
                val pv = possibleValues[0]
                divsContainer.show(pv.id)
                select.setValue(pv)
            }
        }
    }

    fun<VM:BaseNavigatorVariantVM, VS:BaseNavigatorVariantVS> addTab(vm:VM, vs:VS?){
        val itemEditor = config.factories[vm::class.qualifiedName]!!.invoke() as ServerUiViewEditor<VM,VS,VV>
        divsContainer.addDiv(vm.uid, itemEditor)
        itemEditor.setData(vm, vs)
        val newValue = SelectItem(vm.uid, vm.title)
        possibleValues.add(newValue)
        divsContainer.show(vm.uid)
        select.setPossibleValues(possibleValues)
        select.setValue(newValue)
    }

    override fun getData(): VM {
        val result = config.vmFactory.invoke()
        possibleValues.forEach {
            val model = (divsContainer.getDiv(it.id) as ServerUiViewEditor<*,*,*>).getData()
            result.getCollection("values").add(model)
        }
        return result
    }

    fun setRemoveHandler(value:((ServerUiViewEditor<*,*,*>) ->Unit)? ){
        removeHandler = value
    }
    fun setAddHandler(value:(() ->Unit)? ){
        addHandler = value
    }


    override fun setData(vm: VM, vs: VS?) {
        val vsColls = vs?.getCollection("values") as Collection<BaseNavigatorVariantVS>?
        val selectValues = arrayListOf<SelectItem>()
        val actualUids = hashSetOf<String>()
        vm.getCollection("values").forEach {
            val itemVM = it as BaseNavigatorVariantVM
            val itemVS = vsColls?.find { it.uid == itemVM.uid} as BaseNavigatorVariantVS?
            val itemEditor = (divsContainer.getDiv(itemVM.uid)?: run{
                val editor = config.factories[itemVM::class.qualifiedName]!!.invoke()
                divsContainer.addDiv(itemVM.uid, editor)
                editor
            }) as ServerUiViewEditor<BaseNavigatorVariantVM,BaseNavigatorVariantVS,BaseNavigatorVariantVV>
            itemEditor.setData(itemVM, itemVS)
            selectValues.add(SelectItem(itemVM.uid, itemVM.title))
            actualUids.add(itemVM.uid)
        }
        val possibleUids = possibleValues.map { it.id }
        possibleUids.forEach {
            if(!actualUids.contains(it)){
                divsContainer.removeDiv(it)
            }
        }
        possibleValues.clear()
        possibleValues.addAll(selectValues)
        val previouslySelectedValue = select.getValue()
        val navigateValue = if(previouslySelectedValue != null && possibleValues.contains(previouslySelectedValue)) previouslySelectedValue
        else if(possibleValues.isNotEmpty()) possibleValues[0]
        else null
        navigateValue?.let { divsContainer.show(it.id) }
        select.setPossibleValues(possibleValues)
        select.setValue(navigateValue)
    }

    override fun setReadonly(value: Boolean) {
        addButton.setEnabled(!value)
        removeButton.setEnabled(!value)
        possibleValues.forEach {
            (divsContainer.getDiv(it.id) as ServerUiViewEditor<BaseNavigatorVariantVM,BaseNavigatorVariantVS,BaseNavigatorVariantVV>).setReadonly(value)
        }
    }

    override fun showValidation(validation: VV?) {
        val vsColls = validation?.getCollection("values") as List<BaseNavigatorVariantVV>?
        possibleValues.withIndex().forEach { (index, item) ->
            (divsContainer.getDiv(item.id)  as ServerUiViewEditor<BaseNavigatorVariantVM,BaseNavigatorVariantVS,BaseNavigatorVariantVV>).showValidation(vsColls?.get(index))
        }
    }

    override fun navigate(id: String): Boolean {
        return possibleValues.find { it.id == id }?.let {
            divsContainer.show(id)
            select.setValue(it)
            it
        } != null
    }
}
class ServerUiNavigatorWidgetConfiguration<VM:BaseVM> {
    var width:String? = null
    var height:String? = null
    val interceptors = arrayListOf<ServerUiEditorInterceptor<*>>()
    lateinit var vmFactory:()->VM
    val factories = hashMapOf<String, ()->ServerUiViewEditor<*,*,*>>()
}