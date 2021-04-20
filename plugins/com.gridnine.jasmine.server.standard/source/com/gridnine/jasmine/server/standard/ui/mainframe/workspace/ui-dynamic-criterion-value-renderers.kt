/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("UNCHECKED_CAST")

package com.gridnine.jasmine.server.standard.ui.mainframe.workspace

import com.gridnine.jasmine.common.core.app.RegistryItem
import com.gridnine.jasmine.common.core.app.RegistryItemType
import com.gridnine.jasmine.common.core.storage.BaseDynamicCriterionValue
import com.gridnine.jasmine.common.core.utils.TextUtils
import com.gridnine.jasmine.common.standard.model.domain.DynamicCriterionDateValue
import com.gridnine.jasmine.common.standard.model.domain.DynamicCriterionDateValueType
import com.gridnine.jasmine.common.standard.model.domain.StandardDynamicCriterionValueRendererType
import com.gridnine.jasmine.common.standard.model.l10n.StandardL10nMessagesFactory
import com.gridnine.jasmine.server.core.ui.common.BaseNodeWrapper
import com.gridnine.jasmine.server.core.ui.common.UiNode
import com.gridnine.jasmine.server.core.ui.components.GridLayoutCell
import com.gridnine.jasmine.server.core.ui.components.GridLayoutColumnConfiguration
import com.gridnine.jasmine.server.core.ui.components.GridLayoutContainer
import com.gridnine.jasmine.server.core.ui.components.UiLibraryAdapter
import com.gridnine.jasmine.server.core.ui.widgets.EnumBoxValueWidget
import com.gridnine.jasmine.server.core.ui.widgets.IntBoxWidget

interface DynamicCriterionValueRendererFactory<R: DynamicCriterionValueRenderer<*>> : RegistryItem<DynamicCriterionValueRendererFactory<*>> {

    override fun getType(): RegistryItemType<DynamicCriterionValueRendererFactory<*>> {
        return TYPE
    }

    fun createRenderer():R

    companion object{
        val TYPE = RegistryItemType<DynamicCriterionValueRendererFactory<*>>("dynamic-criterion-value-renderer-factory")
    }
}

interface DynamicCriterionValueRenderer<T: BaseDynamicCriterionValue> : UiNode {

    fun getValue():T?

    fun setValue(value:T?)
}

class DateDynamicValueRenderer : DynamicCriterionValueRenderer<DynamicCriterionDateValue>,BaseNodeWrapper<GridLayoutContainer>(){
    private val valueTypeWidget:EnumBoxValueWidget<DynamicCriterionDateValueType>
    private val correctionWidget:IntBoxWidget
    private var uid:String? = null
    init {
        _node = UiLibraryAdapter.get().createGridLayoutContainer {
            width = "100%"
            noPadding = true
            columns.add(GridLayoutColumnConfiguration("auto"))
            columns.add(GridLayoutColumnConfiguration("100px"))
            columns.add(GridLayoutColumnConfiguration("100%"))
        }
        _node.addRow()
        val label = UiLibraryAdapter.get().createLabel {}
        label.setText(" ${StandardL10nMessagesFactory.with_correction()} ")
        _node.addCell(GridLayoutCell(label))
        correctionWidget = IntBoxWidget {
            nullable = false
            width = "100%"
        }
        _node.addCell(GridLayoutCell(correctionWidget))
        valueTypeWidget = EnumBoxValueWidget {
            allowNull = false
            enumClass = DynamicCriterionDateValueType::class
            width = "100%"
            showAllPossibleValues = true
        }
        _node.addCell(GridLayoutCell(valueTypeWidget))
    }
    override fun getValue(): DynamicCriterionDateValue? {
        val correctionType = valueTypeWidget.getValue()?:return null
        val valueType = correctionWidget.getValue()?:return null
        return DynamicCriterionDateValue().let{
            it.uid = uid?:TextUtils.generateUid()
            it.correction = valueType
            it.valueType = correctionType
            it
        }
    }

    override fun setValue(value: DynamicCriterionDateValue?) {
        uid = value?.uid
        valueTypeWidget.setValue(value?.valueType?:DynamicCriterionDateValueType.DAYS)
        correctionWidget.setValue(value?.correction?:0)
    }
}

class DateDynamicValueRendererFactory:DynamicCriterionValueRendererFactory<DateDynamicValueRenderer>{
    override fun createRenderer(): DateDynamicValueRenderer {
        return DateDynamicValueRenderer()
    }

    override fun getId(): String {
        return StandardDynamicCriterionValueRendererType.DATE.name
    }

}