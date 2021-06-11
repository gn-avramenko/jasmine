/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.web.standard.workspaceEditor

import com.gridnine.jasmine.common.standard.model.rest.DynamicWorkspaceCriterionDTJS
import com.gridnine.jasmine.common.standard.model.rest.GetDynamicQueryConditionsRequestJS
import com.gridnine.jasmine.common.standard.model.rest.GetDynamicQueryHandlersRequestJS
import com.gridnine.jasmine.common.standard.model.rest.GetDynamicQueryPropertiesRequestJS
import com.gridnine.jasmine.web.core.common.RegistryJS
import com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS
import com.gridnine.jasmine.web.core.remote.WebPluginsHandler
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.WebNode
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.standard.StandardRestClient
import com.gridnine.jasmine.web.standard.widgets.RemoteGeneralSelectWidget
import com.gridnine.jasmine.web.standard.widgets.WebGeneralTableBoxWidgetCell
import com.gridnine.jasmine.web.standard.widgets.WebNodeProjectorWidget

@Suppress("UNCHECKED_CAST", "UNREACHABLE_CODE")
class WebDynamicCriterionHandler(private val listId: String, private val initData: DynamicWorkspaceCriterionDTJS?) : WebCriterionHandler<DynamicWorkspaceCriterionDTJS> {
    private val uuid = MiscUtilsJS.createUUID()

    private var lastEditor: WebNode? = null

    private lateinit var valueControl:WebNodeProjectorWidget
    private lateinit var  propertySelect:RemoteGeneralSelectWidget
    private lateinit var  conditionSelect:RemoteGeneralSelectWidget
    private lateinit var  handlerSelect:RemoteGeneralSelectWidget

    override fun getComponents(): MutableList<WebGeneralTableBoxWidgetCell> {

        val result = arrayListOf<WebGeneralTableBoxWidgetCell>()
        propertySelect = RemoteGeneralSelectWidget {
            width = "100%"
            showClearIcon = false
        }
        propertySelect.setProvider { ptr->
            StandardRestClient.standard_standard_getDynamicQueryProperties(GetDynamicQueryPropertiesRequestJS().apply {
                listId = this@WebDynamicCriterionHandler.listId
                pattern  = ptr
            }).items
        }
        initData?.property?.let {
            propertySelect.setValue(it)
        }
        result.add(WebGeneralTableBoxWidgetCell(propertySelect))
        conditionSelect = RemoteGeneralSelectWidget {
            width = "100%"
            showClearIcon = false
        }
        conditionSelect.setProvider { ptr ->
            val propId = propertySelect.getValue()?.id ?: return@setProvider emptyList()
            StandardRestClient.standard_standard_getDynamicQueryConditions(GetDynamicQueryConditionsRequestJS().apply {
                listId = this@WebDynamicCriterionHandler.listId
                propertyId = propId
                pattern= ptr
            }).items
        }
        initData?.condition?.let {
            conditionSelect.setValue(it)
        }
        result.add(WebGeneralTableBoxWidgetCell(conditionSelect))
        handlerSelect = RemoteGeneralSelectWidget {
            width = "100%"
            showClearIcon = false
        }
        handlerSelect.setProvider { ptr ->
            val propId = propertySelect.getValue()?.id ?: return@setProvider emptyList()
            val condId = conditionSelect.getValue()?.id ?: return@setProvider emptyList()
            StandardRestClient.standard_standard_getDynamicQueryHandlers(GetDynamicQueryHandlersRequestJS().apply {
                listId = this@WebDynamicCriterionHandler.listId
                propertyId = propId
                conditionId = condId
                pattern= ptr
            }).items
        }
        initData?.handler?.let {
            handlerSelect.setValue(it)
        }
        valueControl = WebNodeProjectorWidget{
            width = "100%"
        }
        initData?.value?.let {
            val className = ReflectionFactoryJS.get().getQualifiedClassName(it::class)
            val handler = RegistryJS.get().get(WebDynamicCriterionValueEditorHandler.TYPE, className)
            if(handler != null){
                lastEditor = handler.createEditor()
                handler.setValue(lastEditor!!, it)
                valueControl.addNode(className, lastEditor!!)
                valueControl.showNode(className)
            }
        }
        val valueGrid = WebUiLibraryAdapter.get().createGridContainer {
            width = "100%"
            noPadding = true
            column("200px")
            column("100%")
            row{
                cell(handlerSelect)
                cell(valueControl)
            }
        }
        result.add(WebGeneralTableBoxWidgetCell(valueGrid))
        propertySelect.setChangeListener {
            conditionSelect.setValue(null)
            handlerSelect.setValue(null)
            valueControl.clear()
        }
        conditionSelect.setChangeListener {
            handlerSelect.setValue(null)
            valueControl.clear()
        }
        handlerSelect.setChangeListener {
            lastEditor = null
            valueControl.clear()
            if(it == null){
                return@setChangeListener
            }
            val rendererId = it.id.substringBefore("_")+"JS"
            WebPluginsHandler.get().ensureClassLoaded(rendererId)
            val handler = RegistryJS.get().get(WebDynamicCriterionValueEditorHandler.TYPE, rendererId)
            if(handler != null){
                lastEditor = handler.createEditor()
                valueControl.addNode(rendererId, lastEditor!!)
                valueControl.showNode(rendererId)
            }
        }
        return result
    }

    override fun getId(): String {
        return uuid
    }

    override fun getData(): DynamicWorkspaceCriterionDTJS? {
        val property = propertySelect.getValue()?:return null
        val condition = conditionSelect.getValue()?:return null
        val handler = handlerSelect.getValue()?:return null
        val result = DynamicWorkspaceCriterionDTJS()
        result.property = property
        result.condition =condition
        result.handler = handler
        val valueHandler = RegistryJS.get().get(WebDynamicCriterionValueEditorHandler.TYPE, handler.id.substringBefore("_")+"JS")
        if(valueHandler != null && lastEditor!= null){
            result.value = valueHandler.getValue(lastEditor!!)
        }
        return result

    }

}

