/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.easyui.widgets

import com.gridnine.jasmine.web.core.model.ui.*
import com.gridnine.jasmine.web.core.utils.HtmlUtilsJS
import com.gridnine.jasmine.web.core.utils.ReflectionFactoryJS
import com.gridnine.jasmine.web.easyui.jQuery
import com.gridnine.jasmine.web.easyui.utils.EasyUiViewBuilder
import kotlin.reflect.KClass

class EasyUiNavigatorWidget<VM:BaseVMEntityJS, VS:BaseVSEntityJS,VV:BaseVVEntityJS>(val uid:String,val description:NavigatorDescriptionJS) : NavigatorWidget<VM,VS,VV>() {


    private var initialized = false
    private val comboboxDiv  = jQuery("#${description.id}Select${uid}")
    private val panelDiv = jQuery("#${description.id}Panel${uid}")
    private var ignoreChangeEvent = false
    private val settings = arrayListOf<VS>()
    private val model = arrayListOf<VM>()
    private val views = linkedMapOf<String, BaseView<*,*,*>>()
    private var selectedUid:String? = null
    private val proxyFlag = linkedMapOf<String, Boolean>()
    private val buttonsHandler = if(description.buttonsHandler != null) ReflectionFactoryJS.get().getFactory(description.buttonsHandler!!).invoke()  as NavigatorButtonsHandler<VM,VS,VV> else null
    init {
        configure = {
           if(!initialized){
               val options = object {
                   val valueField = "id"
                   val textField = "caption"
                   val editable = false
                   val limitToList = true
                   val hasDownArrow =  true
                   val data = arrayOfNulls<SelectItemJS>(0)
                   val onChange = { newValue: String, _: String? ->
                       if(!ignoreChangeEvent){
                           select(newValue)
                       }
                   }
                   val icons = if(buttonsHandler != null) arrayOf(object{
                       val iconCls = "icon-add"
                       val handler = {_:dynamic ->
                           buttonsHandler.onAdd(this@EasyUiNavigatorWidget, model.find { it.uid ==selectedUid }!!)
                       }
                   }, object{
                       val iconCls = "icon-remove"
                       val handler = {_:dynamic ->
                           buttonsHandler.onDelete(this@EasyUiNavigatorWidget, model.find { it.uid ==selectedUid }!!)
                       }
                   }) else {
                       arrayOfNulls<Any>(0)
                   }
               }
               comboboxDiv.combobox(options)
               initialized = true
           }
            settings.clear()
            settings.addAll(it)
        }
        showValidation = {
            views.values.withIndex().forEach { (idx, view) ->
                view.showValidation(it[idx].asDynamic())
            }
        }
        readData ={
            ignoreChangeEvent = true
            model.clear()
            model.addAll(it)
            val items = ArrayList<SelectItemJS>()
            val selectedValue = comboboxDiv.combobox("getValue") as String?
            val newViews = linkedMapOf<String,BaseView<*,*,*>>()
            it.withIndex().forEach {(idx, vm) ->
                items.add(SelectItemJS(vm.uid, vm.getValue("caption") as String?))
                var view = views[vm.uid]
                if(view != null){
                    view.readData(vm.asDynamic())
                } else {
                    val fullPanelId  = "${description.id}Panel${vm.uid}"
                    val fullViewId = findFullViewId(description, vm::class)
                    val fullPaneHtml = HtmlUtilsJS.html {
                        div(id = fullPanelId, style = "width:100%;height:100%;display:none"){
                            EasyUiViewBuilder.generateHtml(fullViewId, "${fullPanelId}View", expandToParent = true, builder = this)
                        }
                    }.toString()
                    val pan = panelDiv
                    panelDiv.append(fullPaneHtml)
                    view = EasyUiViewBuilder.createView<BaseVMEntityJS,BaseVSEntityJS,BaseVVEntityJS, BaseView<BaseVMEntityJS,BaseVSEntityJS,BaseVVEntityJS>>(fullViewId, "${description.id}Panel${vm.uid}", true)
                    view!!.readData(vm.asDynamic())
                    proxyFlag[vm.uid!!] = true
                }
                newViews[vm.uid!!] = view

            }
            views.clear()
            views.putAll(newViews)
            comboboxDiv.combobox("loadData", items.toTypedArray())
            var mustReselect = true
            if(selectedValue != null){
                val selectedItem = items.find { it.id == selectedValue }
                if(selectedItem != null){
                    comboboxDiv.combobox("setValue", selectedValue)
                    mustReselect = false
                }
            }
            if(mustReselect && it.isNotEmpty()){
                select(it[0].uid!!)
            }
            ignoreChangeEvent = false
        }
        writeData ={
            val result = arrayListOf<VM>()
            views.entries.forEach {(uid, view) ->
                val mod = it.find { it.uid == uid }?:model.find { it.uid == uid }!!
                view.writeData(mod.asDynamic())
                result.add(mod)
            }
            it.clear()
            it.addAll(result)

        }
        add = {vm, vs, idx ->
            if(idx != null){
                model.add(idx, vm)
                settings.add(idx, vs)
            } else {
                model.add(vm)
                settings.add(vs)
            }
            ignoreChangeEvent = true;
            val items = ArrayList<SelectItemJS>()
            model.withIndex().forEach { (idx, vm) ->
                items.add(SelectItemJS(vm.uid, vm.getValue("caption") as String?))
            }
            comboboxDiv.combobox("loadData", items.toTypedArray())
            val fullPanelId  = "${description.id}Panel${vm.uid}"
            val fullViewId = findFullViewId(description, vm::class)
            val fullPaneHtml = HtmlUtilsJS.html {
                div(id = fullPanelId, style = "width:100%;height:100%;display:none"){
                    EasyUiViewBuilder.generateHtml(fullViewId, "${fullPanelId}View", expandToParent = true, builder = this)
                }
            }.toString()
            val pan = panelDiv
            panelDiv.append(fullPaneHtml)
            val view = EasyUiViewBuilder.createView<BaseVMEntityJS,BaseVSEntityJS,BaseVVEntityJS, BaseView<BaseVMEntityJS,BaseVSEntityJS,BaseVVEntityJS>>(fullViewId, "${description.id}Panel${vm.uid}", true)
            view.readData(vm.asDynamic())
            proxyFlag[vm.uid!!] = true
            views[vm.uid!!] = view
            select(vm.uid!!)
            ignoreChangeEvent = false
        }
        remove = {modelUid ->
            val value = model.find { it.uid == modelUid }
            if(value != null) {
                val idx = model.indexOf(value)
                model.removeAt(idx)
                settings.removeAt(idx)
                views.remove(modelUid)
            }
            ignoreChangeEvent = true;
            val items = ArrayList<SelectItemJS>()
            model.withIndex().forEach { (idx, vm) ->
                items.add(SelectItemJS(vm.uid, vm.getValue("caption") as String?))
            }
            comboboxDiv.combobox("loadData", items.toTypedArray())
            if(model.isEmpty()){
                panelDiv.empty()
            } else {
                select(model[0].uid!!)
            }
            ignoreChangeEvent = false
        }

        navigate = {uid ->
            val item = model.find { it.uid == uid }
            if(item == null){
                false
            } else {
                select(uid)
                true
            }
        }

        hasNavigationKey = { uid ->
            model.find { it.uid == uid } != null
        }

    }

    private fun findFullViewId(description: NavigatorDescriptionJS, kClass: KClass<out VM>): String {
        val className = ReflectionFactoryJS.get().getQualifiedClassName(kClass)
        return description.viewIds.find {
            val viewId = it
            val vm = UiMetaRegistryJS.get().views[viewId]!!.viewModel
            vm == className
        }!!
    }

    private fun select(modelUid:String) {
        comboboxDiv.combobox("setValue", modelUid)
        if(modelUid != selectedUid){
            if(selectedUid != null) {
                jQuery("#${description.id}Panel${selectedUid}").hide()
            }
            selectedUid = modelUid
            var fullView = views[modelUid]
            val fullModel = model.find { modelUid == it.uid }!!
            fullView!!.writeData(fullModel.asDynamic())
            val fullPanelId = "${description.id}Panel${modelUid}"
            jQuery("#$fullPanelId").show()
            if(proxyFlag[modelUid] != true){
                fullView!!.readData(fullModel.asDynamic())
            } else {
                val fullViewId = findFullViewId(description, fullModel::class)
                fullView =EasyUiViewBuilder.createView<BaseVMEntityJS,BaseVSEntityJS, BaseVVEntityJS, BaseView<BaseVMEntityJS,BaseVSEntityJS, BaseVVEntityJS>>(fullViewId, "${fullPanelId}View")
                fullView.configure(settings[model.indexOf(fullModel)].asDynamic())
                fullView.readData(fullModel.asDynamic())
                views[modelUid] = fullView
                proxyFlag[modelUid] = false
            }
        }
    }


}