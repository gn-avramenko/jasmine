/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("LeakingThis", "unused")

package com.gridnine.jasmine.web.standard.widgets

import com.gridnine.jasmine.common.core.model.*
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.BaseWebNodeWrapper
import com.gridnine.jasmine.web.core.ui.components.WebLinkButton
import com.gridnine.jasmine.web.standard.editor.WebEditor
import com.gridnine.jasmine.web.standard.editor.WebEditorInterceptorsRegistry
import kotlin.reflect.KClass


@Suppress("UNCHECKED_CAST")
abstract class NavigatorWidget<VM: BaseVMJS, VS: BaseVSJS, VV: BaseVVJS> : WebEditor<VM, VS, VV>,BaseWebNodeWrapper<WebGridLayoutWidget>() {
    private val config = NavigatorWidgetConfiguration<VM>()
    private val addButton:WebLinkButton
    private val removeButton:WebLinkButton
    private val nodeProjector:WebNodeProjectorWidget
    private val select: GeneralSelectWidget
    private val possibleValues = arrayListOf<SelectItemJS>()
    private var addHandler:(() ->Unit)? = null
    private var removeHandler:((WebEditor<*,*,*>) ->Unit)? = null

    init {
        config.apply(createInitializer())
        select = GeneralSelectWidget {
            width = "100%"
            showClearIcon = false
        }
        addButton= WebUiLibraryAdapter.get().createLinkButton{
            icon = "core:plus"
        }
        removeButton= WebUiLibraryAdapter.get().createLinkButton{
            icon = "core:minus"
        }
        nodeProjector = WebNodeProjectorWidget{
            width = "100%"
            height = "100%"
        }
        removeButton.setHandler {
            removeHandler?.let {handler ->
                select.getValue()?.id?.let{
                    handler.invoke(nodeProjector.getNode(it) as WebEditor<*, *, *>)
                }
            }
        }
        select.setChangeListener {selectItem ->
            selectItem?.let {
                nodeProjector.showNode(it.id)
            }
        }
        _node = WebGridLayoutWidget{
            width = config.width
            height = config.height
            noPadding = true
        }.also {
            it.setColumnsWidths("1fr","auto","auto")
            it.addRow(select,addButton,removeButton)
            it.addRow("100%", arrayListOf(WebGridLayoutWidgetCell(nodeProjector, 3)))
        }
        addButton.setHandler {
            addHandler?.let {handler ->
                handler.invoke()
            }
        }
        WebEditorInterceptorsRegistry.get().getInterceptors(this)?.forEach {
            it.onInit(this)
        }
    }

    protected abstract fun createInitializer(): NavigatorWidgetConfiguration<VM>.()->Unit

    fun removeTab(id:String){
        possibleValues.find { it.id == id }?.let{
            possibleValues.remove(it)
            nodeProjector.removeNode(id)
            select.setPossibleValues(possibleValues)
            if(possibleValues.isNotEmpty()){
                val pv = possibleValues[0]
                nodeProjector.showNode(pv.id)
                select.setValue(pv)
            }
        }
    }

    fun<VM:BaseNavigatorVariantVMJS, VS:BaseNavigatorVariantVSJS> addTab(vm:VM, vs:VS?){
        val itemEditor = config.factories[vm::class]!!.invoke() as WebEditor<VM,VS,VV>
        nodeProjector.addNode(vm.uid, itemEditor)
        itemEditor.readData(vm, vs)
        val newValue = SelectItemJS(vm.uid, vm.title)
        possibleValues.add(newValue)
        nodeProjector.showNode(vm.uid)
        select.setPossibleValues(possibleValues)
        select.setValue(newValue)
    }

    override fun getData(): VM {
        val result = config.vmFactory.invoke()
        possibleValues.forEach {
            val model = (nodeProjector.getNode(it.id) as WebEditor<*,*,*>).getData()
            result.getCollection("values").add(model)
        }
        return result
    }

    fun setRemoveHandler(value:((WebEditor<*,*,*>) ->Unit)? ){
        removeHandler = value
    }
    fun setAddHandler(value:(() ->Unit)? ){
        addHandler = value
    }


    override fun readData(vm: VM, vs: VS?) {
        val vsColls = vs?.getCollection("values") as Collection<BaseNavigatorVariantVSJS>?
        val selectValues = arrayListOf<SelectItemJS>()
        val actualUids = hashSetOf<String>()
        vm.getCollection("values").forEach { item ->
            val itemVM = item as BaseNavigatorVariantVMJS
            val itemVS = vsColls?.find { it.uid == itemVM.uid}
            val itemEditor = (nodeProjector.getNode(itemVM.uid)?: run{
                val editor = config.factories[itemVM::class]!!.invoke()
                nodeProjector.addNode(itemVM.uid, editor)
                editor
            }) as WebEditor<BaseNavigatorVariantVMJS,BaseNavigatorVariantVSJS,BaseNavigatorVariantVVJS>
            itemEditor.readData(itemVM, itemVS)
            selectValues.add(SelectItemJS(itemVM.uid, itemVM.title))
            actualUids.add(itemVM.uid)
        }
        val possibleUids = possibleValues.map { it.id }
        possibleUids.forEach {
            if(!actualUids.contains(it)){
                nodeProjector.removeNode(it)
            }
        }
        possibleValues.clear()
        possibleValues.addAll(selectValues)
        val previouslySelectedValue = select.getValue()
        val navigateValue = if(previouslySelectedValue != null && possibleValues.contains(previouslySelectedValue)) previouslySelectedValue
        else if(possibleValues.isNotEmpty()) possibleValues[0]
        else null
        navigateValue?.let { nodeProjector.showNode(it.id) }
        select.setPossibleValues(possibleValues)
        select.setValue(navigateValue)
    }

    override fun setReadonly(value: Boolean) {
        addButton.setEnabled(!value)
        removeButton.setEnabled(!value)
        possibleValues.forEach {
            (nodeProjector.getNode(it.id) as WebEditor<BaseNavigatorVariantVMJS,BaseNavigatorVariantVSJS,BaseNavigatorVariantVVJS>).setReadonly(value)
        }
    }

    override fun showValidation(vv: VV?) {
        val vsColls = vv?.getCollection("values") as List<BaseNavigatorVariantVVJS>?
        possibleValues.withIndex().forEach { (index, item) ->
            (nodeProjector.getNode(item.id)  as WebEditor<BaseNavigatorVariantVMJS,BaseNavigatorVariantVSJS,BaseNavigatorVariantVVJS>).showValidation(vsColls?.get(index))
        }
    }

    override fun navigate(id: String): Boolean {
        return possibleValues.find { it.id == id }?.let {
            nodeProjector.showNode(id)
            select.setValue(it)
            it
        } != null
    }
}
class NavigatorWidgetConfiguration<VM:BaseVMJS>:BaseWidgetConfiguration() {
    lateinit var vmFactory:()->VM
    val factories = hashMapOf<KClass<*>, ()->WebEditor<*,*,*>>()
}