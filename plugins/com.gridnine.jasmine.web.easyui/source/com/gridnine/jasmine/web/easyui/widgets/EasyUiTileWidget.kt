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

class EasyUiTileWidget<VC : BaseView<*, *, *>, VF : BaseView<*, *, *>>(private val uid: String, private val description: TileDescriptionJS) : TileWidget<VC, VF>() {

    private lateinit var fullModel: BaseVMEntityJS
    private lateinit var fullSettings: BaseVMEntityJS

    private lateinit var compactModel: BaseVMEntityJS
    private lateinit var compactSettings: BaseVMEntityJS

    private var mustRereadFullData = false
    private var mustRereadCompactData = false
    private var maximized = false
    private var viewIsProxy = true

    init {
        val div = jQuery("#${description.id}${uid}")

        div.panel(object {
            val title = description.displayName
            val tools = arrayOf(object {
                val iconCls = "icon-add"
                val handler = handler@{
                    expand()

                }
            })
        })
        compactView = EasyUiViewBuilder.createView<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS, BaseView<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS>>(description.compactViewId, "${description.id}-compact-${uid}") as VC

        fullView = EasyUiViewBuilder.createView<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS, BaseView<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS>>(description.fullViewId, "${description.id}-full-${uid}", true) as VF
        compactView.parent = this
        fullView.parent = this
        configure = { tileData ->
            compactView.configure(tileData.compactData.asDynamic())
            fullView.configure(tileData.fullData.asDynamic())
            fullSettings = tileData.fullData.asDynamic()
            compactSettings = tileData.compactData.asDynamic()
        }

        setData = { tileData ->

            fullModel = tileData.fullData.asDynamic()
            compactModel = tileData.compactData.asDynamic()
            compactView.readData(tileData.compactData.asDynamic())
            if (!maximized) {
                mustRereadFullData = true
                if (viewIsProxy) {
                    fullView.readData(tileData.fullData.asDynamic())
                }
            } else {
                fullView.readData(tileData.fullData.asDynamic())
                mustRereadCompactData = true
            }
        }

        getData = {
            val result = TileDataJS<Any, Any>()
            result.compactData = createVM(description.compactViewId)
            compactView.writeData(result.compactData.asDynamic())
            result.fullData = createVM(description.fullViewId)
            fullView.writeData(result.fullData.asDynamic())
            result
        }

        showValidation = { tileData ->
            compactView.showValidation(tileData.compactData.asDynamic())
            fullView.showValidation(tileData.fullData.asDynamic())
        }

        navigate = navigate@{ navKey ->
            val descr = UiMetaRegistryJS.get().views[description.fullViewId]!! as StandardViewDescriptionJS
            descr.layout.widgets.values.forEach {
                if(it is NavigatorDescriptionJS){
                    val navigatorWidget = fullView.getValue(it.id) as NavigatorWidget<*,*,*>
                    if (navigatorWidget.hasNavigationKey(navKey)) {
                        expand()
                        fullView.navigate(navKey)
                        return@navigate true;
                    }

                }
            }
            return@navigate false;

        }
    }

    private fun expand() {
        jQuery("#mainPane${uid}").hide()
        val fullPaneId = "${description.id}Full${uid}"
        var fullPaneDiv = jQuery("#$fullPaneId")
        if (fullPaneDiv.length > 0) {
            fullPaneDiv.show()
            maximized = true
            if (mustRereadFullData) {
                fullView.configure(fullSettings.asDynamic())
                fullView.readData(fullModel.asDynamic())
                mustRereadFullData = false
            }
            return
        }
        val fullPaneHtml = HtmlUtilsJS.html {
            div(id = fullPaneId, style = "width:100%;height:100%") {
                div(id = "${fullPaneId}Panel") {
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
                    maximized = false
                    if(mustRereadCompactData){
                        compactView.configure(compactSettings.asDynamic())
                        compactView.readData(compactModel.asDynamic())
                        mustRereadCompactData = false
                    }
                }
            })
        })
        val proxyFullView = fullView
        proxyFullView.writeData(fullModel.asDynamic())
        fullView = EasyUiViewBuilder.createView<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS, BaseView<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS>>(description.fullViewId, "${fullPaneId}View") as VF
        fullView.configure(fullSettings.asDynamic())
        fullView.readData(fullModel.asDynamic())
        maximized = true
        viewIsProxy = false
    }

    private fun createVM(compactViewId: String): Any {
        val viewDescr = UiMetaRegistryJS.get().views[compactViewId]
                ?: throw IllegalArgumentException("unable to find description for view ${compactViewId}")
        return ReflectionFactoryJS.get().getFactory(viewDescr.viewModel)() as BaseVMEntityJS
    }


}