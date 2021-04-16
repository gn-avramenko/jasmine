/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.standard.ui.mainframe.workspace

import com.gridnine.jasmine.common.core.meta.DatabaseCollectionType
import com.gridnine.jasmine.common.core.meta.DatabasePropertyType
import com.gridnine.jasmine.common.standard.model.domain.BaseWorkspaceCriterion
import com.gridnine.jasmine.common.standard.model.domain.BaseWorkspaceSimpleCriterionValue
import com.gridnine.jasmine.server.core.ui.common.UiNode
import com.gridnine.jasmine.server.core.ui.components.TableCell

interface WorkspaceElementEditorHandler<E: UiNode,M:Any>{
    fun getId():String
    fun createEditor(): E
    fun setData(editor:E, data:M)
    fun getData(editor:E):M
    fun getName(data:M):String
    fun validate(editor:E):Boolean
}

class CriterionPropertyWrapper(val id: String, val text: String, val collection: Boolean, val propertyType: DatabasePropertyType?, val collectionType: DatabaseCollectionType?, val className: String?)

interface SimpleCriterionValueEditor<T:BaseWorkspaceSimpleCriterionValue>:UiNode{
    fun getType():SimpleCriterionValueType
    fun setValue(value: T?)
    fun getValue():T?
}

interface UiCriterionHandler<T:BaseWorkspaceCriterion>{
    fun getComponents(): MutableList<TableCell>
    fun getId(): String
    fun getData(): T?
}

enum class SimpleCriterionValueType{
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
