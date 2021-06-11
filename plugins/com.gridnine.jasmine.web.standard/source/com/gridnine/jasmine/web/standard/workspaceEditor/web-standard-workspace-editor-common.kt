/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.standard.workspaceEditor

import com.gridnine.jasmine.common.core.meta.DatabaseCollectionTypeJS
import com.gridnine.jasmine.common.core.meta.DatabasePropertyTypeJS
import com.gridnine.jasmine.common.standard.model.rest.BaseDynamicCriterionValueDTJS
import com.gridnine.jasmine.common.standard.model.rest.BaseWorkspaceCriterionDTJS
import com.gridnine.jasmine.common.standard.model.rest.BaseWorkspaceSimpleCriterionValueDTJS
import com.gridnine.jasmine.web.core.common.RegistryItemJS
import com.gridnine.jasmine.web.core.common.RegistryItemTypeJS
import com.gridnine.jasmine.web.core.ui.components.WebNode
import com.gridnine.jasmine.web.standard.widgets.WebGeneralTableBoxWidgetCell

class WebCriterionPropertyWrapper(val id: String, val text: String, val collection: Boolean, val propertyType: DatabasePropertyTypeJS?, val collectionType: DatabaseCollectionTypeJS?, val className: String?)

interface WebSimpleCriterionValueEditor<T:BaseWorkspaceSimpleCriterionValueDTJS>:WebNode{
    fun getType():WebSimpleCriterionValueType
    fun setValue(value: T?)
    fun getValue():T?
}

interface WebDynamicCriterionValueEditorHandler<T:BaseDynamicCriterionValueDTJS, W:WebNode>:RegistryItemJS<WebDynamicCriterionValueEditorHandler<BaseDynamicCriterionValueDTJS, WebNode>>{
    fun createEditor():W
    fun setValue(editor:W, value: T)
    fun getValue(editor:W):T
    override fun getType(): RegistryItemTypeJS<WebDynamicCriterionValueEditorHandler<BaseDynamicCriterionValueDTJS, WebNode>> = TYPE

    companion object{
        val TYPE = RegistryItemTypeJS<WebDynamicCriterionValueEditorHandler<BaseDynamicCriterionValueDTJS, WebNode>>("web-dynamic-criterion-value-editor-handler")
    }
}

interface WebCriterionHandler<T:BaseWorkspaceCriterionDTJS>{
    fun getComponents(): MutableList<WebGeneralTableBoxWidgetCell>
    fun getId(): String
    fun getData(): T?
}

enum class WebSimpleCriterionValueType{
    NULL,
    STRING_VALUES,
    ENUM_VALUES,
    INT_VALUE,
    LONG_VALUE,
    BIG_DECIMAL_VALUE,
    ENTITY_REFERENCE_VALUES,
    DATE_VALUE,
    DATE_INTERVAL,
    DATE_TIME_VALUE,
    DATE_TIME_INTERVAL
}
