/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("LeakingThis")

package com.gridnine.jasmine.server.core.ui.widgets

import com.gridnine.jasmine.common.core.model.*
import com.gridnine.jasmine.server.core.ui.common.BaseNodeWrapper
import com.gridnine.jasmine.server.core.ui.common.BaseWidgetConfiguration
import com.gridnine.jasmine.server.core.ui.common.ViewEditor
import com.gridnine.jasmine.server.core.ui.common.ViewEditorInterceptorsRegistry
import com.gridnine.jasmine.server.core.ui.components.*


@Suppress("UNCHECKED_CAST")
abstract class NavigatorWidget<VM: BaseVM, VS: BaseVS, VV: BaseVV> : ViewEditor<VM, VS, VV>,BaseNodeWrapper<GridLayoutContainer>() {
    private val config = NavigatorWidgetConfiguration<VM>()
    private val addButton:LinkButton
    private val removeButton:LinkButton
    private val divsContainer:DivsContainer
    private val select: GeneralSelectBoxValueWidget
    private val possibleValues = arrayListOf<SelectItem>()
    private var addHandler:(() ->Unit)? = null
    private var removeHandler:((ViewEditor<*,*,*>) ->Unit)? = null

    init {
        config.apply(createInitializer())
        _node = UiLibraryAdapter.get().createGridLayoutContainer{
            width = config.width
            height = config.height
            columns.add(GridLayoutColumnConfiguration("100%"))
            columns.add(GridLayoutColumnConfiguration("auto"))
            columns.add(GridLayoutColumnConfiguration("auto"))
        }

        _node.addRow()
        select = GeneralSelectBoxValueWidget{
            width = "100%"
            showClearIcon = false
            showAllPossibleValues = true
        }
        _node.addCell(GridLayoutCell(select))
        addButton= UiLibraryAdapter.get().createLinkButton{
            iconClass = "z-icon-plus"
        }
        addButton.setHandler {
            addHandler?.invoke()
        }
        _node.addCell(GridLayoutCell(addButton))
        removeButton= UiLibraryAdapter.get().createLinkButton{
            iconClass = "z-icon-minus"
        }
        divsContainer = UiLibraryAdapter.get().createDivsContainer{
            width = "100%"
            height = "100%"
        }
        removeButton.setHandler {
            removeHandler?.let {handler ->
                select.getValue()?.id?.let{
                    handler.invoke(divsContainer.getDiv(it) as ViewEditor<*, *, *>)
                }
            }
        }
        _node.addCell(GridLayoutCell(removeButton))
        _node.addRow("100%")

        _node.addCell(GridLayoutCell(divsContainer, 3))
        select.setChangeListener {selectItem ->
            selectItem?.let {
                divsContainer.show(it.id)
            }
        }
        ViewEditorInterceptorsRegistry.get().getInterceptors(this)?.forEach {
            it.onInit(this)
        }
    }

    protected abstract fun createInitializer(): NavigatorWidgetConfiguration<VM>.()->Unit

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
        val itemEditor = config.factories[vm::class.qualifiedName]!!.invoke() as ViewEditor<VM,VS,VV>
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
            val model = (divsContainer.getDiv(it.id) as ViewEditor<*,*,*>).getData()
            result.getCollection("values").add(model)
        }
        return result
    }

    fun setRemoveHandler(value:((ViewEditor<*,*,*>) ->Unit)? ){
        removeHandler = value
    }
    fun setAddHandler(value:(() ->Unit)? ){
        addHandler = value
    }


    override fun setData(vm: VM, vs: VS?) {
        val vsColls = vs?.getCollection("values") as Collection<BaseNavigatorVariantVS>?
        val selectValues = arrayListOf<SelectItem>()
        val actualUids = hashSetOf<String>()
        vm.getCollection("values").forEach { item ->
            val itemVM = item as BaseNavigatorVariantVM
            val itemVS = vsColls?.find { it.uid == itemVM.uid}
            val itemEditor = (divsContainer.getDiv(itemVM.uid)?: run{
                val editor = config.factories[itemVM::class.qualifiedName]!!.invoke()
                divsContainer.addDiv(itemVM.uid, editor)
                editor
            }) as ViewEditor<BaseNavigatorVariantVM,BaseNavigatorVariantVS,BaseNavigatorVariantVV>
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
            (divsContainer.getDiv(it.id) as ViewEditor<BaseNavigatorVariantVM,BaseNavigatorVariantVS,BaseNavigatorVariantVV>).setReadonly(value)
        }
    }

    override fun showValidation(validation: VV?) {
        val vsColls = validation?.getCollection("values") as List<BaseNavigatorVariantVV>?
        possibleValues.withIndex().forEach { (index, item) ->
            (divsContainer.getDiv(item.id)  as ViewEditor<BaseNavigatorVariantVM,BaseNavigatorVariantVS,BaseNavigatorVariantVV>).showValidation(vsColls?.get(index))
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
class NavigatorWidgetConfiguration<VM:BaseVM>:BaseWidgetConfiguration() {
    lateinit var vmFactory:()->VM
    val factories = hashMapOf<String, ()->ViewEditor<*,*,*>>()
}