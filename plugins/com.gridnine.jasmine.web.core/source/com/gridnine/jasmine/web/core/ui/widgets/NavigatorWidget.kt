/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.widgets

import com.gridnine.jasmine.server.core.model.common.SelectItemJS
import com.gridnine.jasmine.server.core.model.ui.*
import com.gridnine.jasmine.web.core.ui.UiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.WebEditor
import com.gridnine.jasmine.web.core.ui.WebEditorInterceptor
import com.gridnine.jasmine.web.core.ui.components.WebDivsContainer
import com.gridnine.jasmine.web.core.ui.components.WebGridLayoutCell
import com.gridnine.jasmine.web.core.ui.components.WebGridLayoutContainer
import com.gridnine.jasmine.web.core.ui.components.WebLinkButton
import kotlin.reflect.KClass

open class NavigatorWidget<VM: BaseVMJS, VS: BaseVSJS, VV: BaseVVJS>(private val parent: WebComponent?, configure:NavigatorWidgetConfiguration<VM>.(widget:NavigatorWidget<VM, VS, VV>)->Unit) : WebEditor<VM, VS, VV> {
    private val delegate:WebGridLayoutContainer
    private val config = NavigatorWidgetConfiguration<VM>()
    private val addButton:WebLinkButton
    private val removeButton:WebLinkButton
    private val divsContainer:WebDivsContainer
    private val select:GeneralSelectWidget
    private val possibleValues = arrayListOf<SelectItemJS>()
    private var addHandler:(() ->Unit)? = null
    private var removeHandler:((WebEditor<*,*,*>) ->Unit)? = null
    init {
         config.configure(this)
         delegate = UiLibraryAdapter.get().createGridLayoutContainer(this){
             width = config.width
             height = config.height
         }
        delegate.defineColumn("100%")
        delegate.defineColumn("auto")
        delegate.defineColumn("auto")
        delegate.addRow()
        select = GeneralSelectWidget(delegate){
            width = "100%"
            showClearIcon = false
        }

        delegate.addCell(WebGridLayoutCell(select))
         addButton= UiLibraryAdapter.get().createLinkButton(delegate){
            icon = "core:plus"
        }
        addButton.setHandler {
        }
        delegate.addCell(WebGridLayoutCell(addButton))
        removeButton = UiLibraryAdapter.get().createLinkButton(delegate){
            icon = "core:minus"
        }

        delegate.addCell(WebGridLayoutCell(removeButton))
        delegate.addRow("100%")
        divsContainer = UiLibraryAdapter.get().createDivsContainer(delegate){
            width = "100%"
            height = "100%"
        }
        delegate.addCell(WebGridLayoutCell(divsContainer, 3))
        select.changeListener = {selectItem ->
            selectItem?.let {
                divsContainer.show(it.id)
            }
        }
        removeButton.setHandler {
            removeHandler?.let {handler ->
                select.getValue()?.id?.let{
                    handler.invoke(divsContainer.getDiv(it) as WebEditor<*, *, *>)
                }
            }
        }
        addButton.setHandler {
            addHandler?.let {handler ->
                select.getValue()?.id?.let{
                    handler.invoke()
                }
            }
        }
        config.interceptors.forEach {
            (it as WebEditorInterceptor<NavigatorWidget<VM,VS,VV>>).onInit(this)
        }
    }

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

    fun<VM:BaseNavigatorVariantVMJS, VS:BaseNavigatorVariantVSJS> addTab(vm:VM, vs:VS){
        val itemEditor = config.factories[vm::class]!!.invoke().unsafeCast<WebEditor<VM,VS,*>>()
        divsContainer.addDiv(vm.uid, itemEditor)
        itemEditor.readData(vm, vs)
        val newValue = SelectItemJS(vm.uid, vm.title)
        possibleValues.add(newValue)
        divsContainer.show(vm.uid)
        select.setPossibleValues(possibleValues)
        select.setValue(newValue)
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


    override fun readData(vm: VM, vs: VS) {
        val vsColls = vs.getCollection("values") as Collection<BaseNavigatorVariantVSJS>
        val selectValues = arrayListOf<SelectItemJS>()
        val actualUids = hashSetOf<String>()
        vm.getCollection("values").forEach {
            val itemVM = it as BaseNavigatorVariantVMJS
            val itemVS = vsColls.find { it.uid == itemVM.uid} as BaseNavigatorVariantVSJS
            val itemEditor = (divsContainer.getDiv(itemVM.uid)?: run{
                val editor = config.factories[itemVM::class]!!.invoke()
                divsContainer.addDiv(itemVM.uid, editor)
                editor
            }).unsafeCast<WebEditor<BaseNavigatorVariantVMJS,BaseNavigatorVariantVSJS,BaseNavigatorVariantVVJS>>()
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
            divsContainer.getDiv(it.id).unsafeCast<WebEditor<BaseNavigatorVariantVMJS,BaseNavigatorVariantVSJS,BaseNavigatorVariantVVJS>>().setReadonly(value)
        }
    }

    override fun showValidation(validation: VV) {
        val vsColls = validation.getCollection("values") as Collection<BaseNavigatorVariantVVJS>
        vsColls.forEach {
            divsContainer.getDiv(it.uid).unsafeCast<WebEditor<BaseNavigatorVariantVMJS,BaseNavigatorVariantVSJS,BaseNavigatorVariantVVJS>>().showValidation(it)
        }
    }

    override fun navigate(id: String): Boolean {
        return possibleValues.find { it.id == id }?.let {
            divsContainer.show(id)
            select.setValue(it)
        } != null
    }
}
class NavigatorWidgetConfiguration<VM:BaseVMJS> {
    var width:String? = null
    var height:String? = null
    val interceptors = arrayListOf<WebEditorInterceptor<*>>()
    lateinit var vmFactory:()->VM
    internal val factories = hashMapOf<KClass<*>, ()->WebEditor<*,*,*>>()
    fun<VM1:BaseNavigatorVariantVMJS, VS1:BaseNavigatorVariantVSJS, VV1:BaseNavigatorVariantVVJS> factory(cls:KClass<VM1>, fac:()->WebEditor<VM1,VS1,VV1>){
        factories[cls] = fac
    }
}