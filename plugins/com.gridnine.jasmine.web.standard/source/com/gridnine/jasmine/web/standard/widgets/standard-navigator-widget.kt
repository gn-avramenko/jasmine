/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("LeakingThis", "unused")

package com.gridnine.jasmine.web.standard.widgets

import com.gridnine.jasmine.common.core.model.*
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.BaseWebNodeWrapper
import com.gridnine.jasmine.web.core.ui.components.WebDivsContainer
import com.gridnine.jasmine.web.core.ui.components.WebGridLayoutContainer
import com.gridnine.jasmine.web.core.ui.components.WebLinkButton
import com.gridnine.jasmine.web.standard.editor.WebEditor
import com.gridnine.jasmine.web.standard.editor.WebEditorInterceptorsRegistry
import kotlin.reflect.KClass


@Suppress("UNCHECKED_CAST")
abstract class NavigatorWidget<VM: BaseVMJS, VS: BaseVSJS, VV: BaseVVJS> : WebEditor<VM, VS, VV>,BaseWebNodeWrapper<WebGridLayoutContainer>() {
    private val config = NavigatorWidgetConfiguration<VM>()
    private val addButton:WebLinkButton
    private val removeButton:WebLinkButton
    private val divsContainer:WebDivsContainer
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
        divsContainer = WebUiLibraryAdapter.get().createDivsContainer{
            width = "100%"
            height = "100%"
        }
        removeButton.setHandler {
            removeHandler?.let {handler ->
                select.getValue()?.id?.let{
                    handler.invoke(divsContainer.getDiv(it) as WebEditor<*, *, *>)
                }
            }
        }
        select.setChangeListener {selectItem ->
            selectItem?.let {
                divsContainer.show(it.id)
            }
        }
        _node = WebUiLibraryAdapter.get().createGridContainer{
            width = config.width
            height = config.height
            column("100%")
            column("auto")
            column("auto")
            row{
                cell(select)
                cell(addButton)
                cell(removeButton)
            }
            row ("100%"){
                cell(divsContainer, 3)
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
            divsContainer.removeDiv(id)
            select.setPossibleValues(possibleValues)
            if(possibleValues.isNotEmpty()){
                val pv = possibleValues[0]
                divsContainer.show(pv.id)
                select.setValue(pv)
            }
        }
    }

    fun<VM:BaseNavigatorVariantVMJS, VS:BaseNavigatorVariantVSJS> addTab(vm:VM, vs:VS?){
        val itemEditor = config.factories[vm::class]!!.invoke() as WebEditor<VM,VS,VV>
        divsContainer.addDiv(vm.uid, itemEditor)
        itemEditor.readData(vm, vs)
        val newValue = SelectItemJS(vm.uid, vm.title)
        possibleValues.add(newValue)
        divsContainer.show(vm.uid)
        select.setPossibleValues(possibleValues)
        select.setValue(newValue)
    }

    override fun getData(): VM {
        val result = config.vmFactory.invoke()
        possibleValues.forEach {
            val model = (divsContainer.getDiv(it.id) as WebEditor<*,*,*>).getData()
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
            val itemEditor = (divsContainer.getDiv(itemVM.uid)?: run{
                val editor = config.factories[itemVM::class]!!.invoke()
                divsContainer.addDiv(itemVM.uid, editor)
                editor
            }) as WebEditor<BaseNavigatorVariantVMJS,BaseNavigatorVariantVSJS,BaseNavigatorVariantVVJS>
            itemEditor.readData(itemVM, itemVS)
            selectValues.add(SelectItemJS(itemVM.uid, itemVM.title))
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
            (divsContainer.getDiv(it.id) as WebEditor<BaseNavigatorVariantVMJS,BaseNavigatorVariantVSJS,BaseNavigatorVariantVVJS>).setReadonly(value)
        }
    }

    override fun showValidation(vv: VV?) {
        val vsColls = vv?.getCollection("values") as List<BaseNavigatorVariantVVJS>?
        possibleValues.withIndex().forEach { (index, item) ->
            (divsContainer.getDiv(item.id)  as WebEditor<BaseNavigatorVariantVMJS,BaseNavigatorVariantVSJS,BaseNavigatorVariantVVJS>).showValidation(vsColls?.get(index))
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
class NavigatorWidgetConfiguration<VM:BaseVMJS>:BaseWidgetConfiguration() {
    lateinit var vmFactory:()->VM
    val factories = hashMapOf<KClass<*>, ()->WebEditor<*,*,*>>()
}