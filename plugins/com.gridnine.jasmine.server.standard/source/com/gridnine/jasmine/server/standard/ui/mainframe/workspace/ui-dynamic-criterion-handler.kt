/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("UNCHECKED_CAST")

package com.gridnine.jasmine.server.standard.ui.mainframe.workspace

import com.gridnine.jasmine.common.core.app.PublishableWrapper
import com.gridnine.jasmine.common.core.app.Registry
import com.gridnine.jasmine.common.core.meta.*
import com.gridnine.jasmine.common.core.model.SelectItem
import com.gridnine.jasmine.common.core.storage.BaseDynamicCriterionValue
import com.gridnine.jasmine.common.core.storage.DynamicCriterionCondition
import com.gridnine.jasmine.common.core.storage.DynamicCriterionHandler
import com.gridnine.jasmine.common.core.utils.TextUtils
import com.gridnine.jasmine.common.standard.model.domain.*
import com.gridnine.jasmine.server.core.ui.components.*
import com.gridnine.jasmine.server.core.ui.widgets.GeneralSelectBoxValueWidget
import java.util.concurrent.ConcurrentHashMap

@Suppress("UNCHECKED_CAST", "UNREACHABLE_CODE")
class DynamicCriterionUiHandler(private val listId: String, private val initData: DynamicWorkspaceCriterion?) : UiCriterionHandler<DynamicWorkspaceCriterion> {
    private val uuid = TextUtils.generateUid()

    private val valueDivId= "valueDiv$uuid"
    private lateinit var valueControl:DivsContainer
    private lateinit var  propertySelect:GeneralSelectBoxValueWidget
    private lateinit var  conditionSelect:GeneralSelectBoxValueWidget
    private lateinit var  handlerSelect:GeneralSelectBoxValueWidget
    private var lastEditor:DynamicCriterionValueRenderer<*>? = null

    override fun getComponents(): MutableList<TableCell> {
        val result = arrayListOf<TableCell>()
        propertySelect = GeneralSelectBoxValueWidget {
            width = "100%"
            showClearIcon = false
            showAllPossibleValues = true
        }
        val possibleProperties = DynamicCriterionEditorRegistry.get().getProperties(listId)
        propertySelect.setPossibleValues(possibleProperties)
        result.add(TableCell(propertySelect))
        conditionSelect = GeneralSelectBoxValueWidget {
            width = "100%"
            showClearIcon = false
            showAllPossibleValues = true
        }
        result.add(TableCell(conditionSelect))
        val compaundEditor = UiLibraryAdapter.get().createGridLayoutContainer {
            width = "100%"
            noPadding = true
            columns.add(GridLayoutColumnConfiguration("auto"))
            columns.add(GridLayoutColumnConfiguration("100%"))
        }
        result.add(TableCell(compaundEditor))
        handlerSelect = GeneralSelectBoxValueWidget {
            width = "100px"
            showClearIcon = false
            showAllPossibleValues = true
        }
        compaundEditor.addRow()
        compaundEditor.addCell(GridLayoutCell(handlerSelect))
        valueControl = UiLibraryAdapter.get().createDivsContainer{
            width = "100%"
        }
        compaundEditor.addCell(GridLayoutCell(valueControl))
        initData?.propertyId?.let { property ->
            val prop = possibleProperties.find { it.id == property }
            prop?.let {
                propertySelect.setValue(it)
                val conditions = DynamicCriterionEditorRegistry.get().getConditions(listId, prop.id)
                conditionSelect.setPossibleValues(conditions)
                val cond = conditions.find { cond -> initData.conditionId == cond.id}
                cond?.let {
                    conditionSelect.setValue(cond)
                    val handlers = DynamicCriterionEditorRegistry.get().getHandlers(listId, prop.id, cond.id)
                    handlerSelect.setPossibleValues(handlers)
                    val hand = handlers.find { handlerItem -> initData.handlerId == handlerItem.id }
                    hand?.let {
                        handlerSelect.setValue(hand)
                        setEditor(hand.id, initData.value)
                    }
                }
            }
        }
        propertySelect.setChangeListener { prop ->
            val property = possibleProperties.find { it.id == prop?.id }
            val possibleConditions = if(prop != null) DynamicCriterionEditorRegistry.get().getConditions(listId, prop.id) else emptyList()
            conditionSelect.setPossibleValues(possibleConditions)
            val condition = if(possibleConditions.isNotEmpty()) possibleConditions[0] else null
            conditionSelect.setValue(condition)
            val possibleHandlers = if(property != null && condition != null) DynamicCriterionEditorRegistry.get().getHandlers(listId, property.id, condition.id) else  emptyList()
            handlerSelect.setPossibleValues(possibleHandlers)
            val handler = if(possibleHandlers.isNotEmpty()) possibleHandlers[0] else null
            handlerSelect.setValue(handler)
            setEditor(handler?.id, null)
        }
        conditionSelect.setChangeListener {
            val property = propertySelect.getValue()!!
            val possibleHandlers = if(it != null) DynamicCriterionEditorRegistry.get().getHandlers(listId, property.id, it.id) else  emptyList()
            handlerSelect.setPossibleValues(possibleHandlers)
            val handler = if(possibleHandlers.isNotEmpty()) possibleHandlers[0] else null
            handlerSelect.setValue(handler)
            setEditor(handler?.id, null)
        }
        handlerSelect.setChangeListener {
            setEditor(it?.id, null)
        }
        return result
    }

    private fun setEditor(handlerId: String?, value: BaseDynamicCriterionValue?) {
        val div = valueControl.getDiv(valueDivId)
        if (div != null) {
            valueControl.removeDiv(valueDivId)
        }
        lastEditor = null
        val rendererFactory = handlerId?.let { Registry.get().get(DynamicCriterionHandler.TYPE, it)!!.getRendererId() }?.
            let { Registry.get().get(DynamicCriterionValueRendererFactory.TYPE, it) }?:return
        lastEditor = rendererFactory.createRenderer()
        valueControl.addDiv(valueDivId, lastEditor!!)
        valueControl.show(valueDivId)
        (lastEditor as DynamicCriterionValueRenderer<BaseDynamicCriterionValue>).setValue(value)
    }

    override fun getId(): String {
        return  uuid
    }

    override fun getData(): DynamicWorkspaceCriterion? {
        val handlerId = handlerSelect.getValue()?.id?:return null
        val editorValue = lastEditor?.getValue()?:return null
        val result = DynamicWorkspaceCriterion()
        result.propertyId = propertySelect.getValue()!!.id
        result.conditionId = conditionSelect.getValue()!!.id
        result.handlerId = handlerId
        result.value = editorValue
        return result
    }


}

class DynamicCriterionConditionData(val condition:DynamicCriterionCondition, val handlers:MutableList<DynamicCriterionHandler<BaseDynamicCriterionValue>>)
class DynamicCriterionPropertyData(val propertyDescription: BaseModelElementDescription, val conditions:MutableList<DynamicCriterionConditionData>)


class DynamicCriterionEditorRegistry{

    private val lists = ConcurrentHashMap<String, List<DynamicCriterionPropertyData>>()

    fun getProperties(listId:String): List<SelectItem>{
        return lists.getOrPut(listId){
            getListData(listId)
        }.map {
           SelectItem(it.propertyDescription.id, it.propertyDescription.getDisplayName()!!)
        }.sortedBy { it.text }
    }

    fun getConditions(listId:String, propertyId:String): List<SelectItem>{
        return lists.getOrPut(listId){
            getListData(listId)
        }.find { it.propertyDescription.id == propertyId }!!.conditions.map {  SelectItem(it.condition.getId(), it.condition.getDisplayName()) }.sortedBy { it.text }
    }

    fun getHandlers(listId:String, propertyId:String, conditionId:String):List<SelectItem>{
        return lists.getOrPut(listId){
            getListData(listId)
        }.find { it.propertyDescription.id == propertyId }!!.conditions.find { it.condition.getId() == conditionId }!!
                .handlers.map { SelectItem(it.getId(),it.getDisplayName()) }.sortedBy { it.text }
    }

    private fun getListData(listId: String): List<DynamicCriterionPropertyData>{
        val result = arrayListOf<DynamicCriterionPropertyData>()
        val descr = DomainMetaRegistry.get().indexes[listId]?:DomainMetaRegistry.get().assets[listId]?:return result
        Registry.get().allOf(DynamicCriterionHandler.TYPE).forEach {handler ->
            descr.properties.keys.forEach { propertyId ->
                if(handler.isApplicable(listId, propertyId)){
                    var property = result.find { it.propertyDescription.id == propertyId}
                    if(property == null){
                        property = DynamicCriterionPropertyData(descr.properties[propertyId]!!, arrayListOf())
                        result.add(property)
                    }
                    handler.getConditionIds().forEach {conditionId ->
                        var condition = property.conditions.find { it.condition.getId() == conditionId }
                        if(condition == null){
                            condition = DynamicCriterionConditionData(Registry.get().get(DynamicCriterionCondition.TYPE, conditionId)!!, arrayListOf())
                            property.conditions.add(condition)
                        }
                        condition.handlers.find { it.getId() == handler.getId() }?:condition.handlers.add(handler as DynamicCriterionHandler<BaseDynamicCriterionValue>)
                    }
                }
            }
        }
        return result
    }


    companion object {
        private val wrapper = PublishableWrapper(DynamicCriterionEditorRegistry::class)
        fun get() = wrapper.get()
    }
}

