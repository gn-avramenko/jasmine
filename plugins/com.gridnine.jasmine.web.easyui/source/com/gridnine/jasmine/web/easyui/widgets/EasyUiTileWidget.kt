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

class EasyUiTileWidget<VC: BaseView<*, *, *>, VF: BaseView<*, *, *>>(uid: String, description: TileDescriptionJS) : TileWidget<VC,VF>() {

    private lateinit var fullModel:BaseVMEntityJS
    private lateinit var fullSettings:BaseVMEntityJS


    init {
        val div = jQuery("#${description.id}${uid}")

        div.panel(object{
            val title = description.displayName
            val tools = arrayOf(object{
                val iconCls = "icon-add"
                val handler = handler@{
                    jQuery("#mainPane${uid}").hide()
                    val fullPaneId = "${description.id}Full${uid}"
                    var fullPaneDiv = jQuery("#$fullPaneId")
                    if(fullPaneDiv.length >0){
                        fullPaneDiv.show()
                        return@handler
                    }
                    val fullPaneHtml = HtmlUtilsJS.html {
                        div(id = fullPaneId, style = "width:100%;height:100%"){
                            div(id = "${fullPaneId}Panel"){
                                EasyUiViewBuilder.generateHtml(description.fullViewId, "${fullPaneId}View", expandToParent = true, builder = this)
                            }
                        }
                    }.toString()
                    jQuery("#contentPane${uid}").append(fullPaneHtml)
                    jQuery("#${fullPaneId}Panel").panel(object {
                        val fit = true
                        val title = description.displayName
                        val tools = arrayOf(object {
                            val iconCls = "icon-remove"
                            val handler = {
                                jQuery("#$fullPaneId").hide()
                                jQuery("#mainPane${uid}").show()
                            }
                        })
                    })
                    val proxyFullView = fullView
                    proxyFullView.writeData(fullModel.asDynamic())
                    fullView =EasyUiViewBuilder.createView<BaseVMEntityJS,BaseVSEntityJS, BaseVVEntityJS, BaseView<BaseVMEntityJS,BaseVSEntityJS, BaseVVEntityJS>>(description.fullViewId, "${fullPaneId}View") as VF
                    fullView.configure(fullSettings.asDynamic())
                    fullView.readData(fullModel.asDynamic())
                }
            })
        })
        compactView = EasyUiViewBuilder.createView<BaseVMEntityJS,BaseVSEntityJS,BaseVVEntityJS, BaseView<BaseVMEntityJS,BaseVSEntityJS,BaseVVEntityJS>>(description.compactViewId, "${description.id}-compact-${uid}") as VC

        fullView = EasyUiViewBuilder.createView<BaseVMEntityJS,BaseVSEntityJS,BaseVVEntityJS, BaseView<BaseVMEntityJS,BaseVSEntityJS,BaseVVEntityJS>>(description.fullViewId, "${description.id}-full-${uid}", true) as VF

        configure = {tileData ->
            compactView.configure(tileData.compactData.asDynamic())
            fullView.configure(tileData.fullData.asDynamic())
            fullSettings = tileData.fullData.asDynamic()
        }

        setData ={tileData ->
            compactView.readData(tileData.compactData.asDynamic())
            fullView.readData(tileData.fullData.asDynamic())
            fullModel = tileData.fullData.asDynamic()
        }

        getData ={
            val result = TileDataJS<Any,Any>()
            result.compactData =  createVM(description.compactViewId)
            compactView.writeData(result.compactData.asDynamic())
            result.fullData =   createVM(description.fullViewId)
            fullView.writeData(result.fullData.asDynamic())
            result
        }

        showValidation ={tileData ->
            compactView.showValidation(tileData.compactData.asDynamic())
            fullView.showValidation(tileData.fullData.asDynamic())
        }
    }

    private fun createVM(compactViewId: String): Any {
        val viewDescr = UiMetaRegistryJS.get().views[compactViewId]?:throw IllegalArgumentException("unable to find description for view ${compactViewId}")
        return ReflectionFactoryJS.get().getFactory(viewDescr.viewModel)() as BaseVMEntityJS
    }


}