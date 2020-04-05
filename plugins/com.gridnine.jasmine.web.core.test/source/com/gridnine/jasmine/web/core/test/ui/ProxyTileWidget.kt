/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.core.test.ui

import com.gridnine.jasmine.web.core.model.ui.*
import com.gridnine.jasmine.web.core.utils.ReflectionFactoryJS

class ProxyTileWidget<VC : BaseView<*, *, *>, VF : BaseView<*, *, *>>(private val uid: String, private val description: TileDescriptionJS) : TileWidget<VC, VF>() {

    private lateinit var fullModel: BaseVMEntityJS
    private lateinit var fullSettings: BaseVMEntityJS

    private lateinit var compactModel: BaseVMEntityJS
    private lateinit var compactSettings: BaseVMEntityJS


    init {

        compactView = TestViewBuilder.createView<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS, BaseView<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS>>(description.compactViewId, "${description.id}-compact-${uid}") as VC

        fullView = TestViewBuilder.createView<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS, BaseView<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS>>(description.fullViewId, "${description.id}-full-${uid}") as VF
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
            fullView.readData(tileData.fullData.asDynamic())
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
                        fullView.navigate(navKey)
                        return@navigate true;
                    }

                }
            }
            return@navigate false;

        }
    }


    private fun createVM(compactViewId: String): Any {
        val viewDescr = UiMetaRegistryJS.get().views[compactViewId]
                ?: throw IllegalArgumentException("unable to find description for view ${compactViewId}")
        return ReflectionFactoryJS.get().getFactory(viewDescr.viewModel)() as BaseVMEntityJS
    }


}