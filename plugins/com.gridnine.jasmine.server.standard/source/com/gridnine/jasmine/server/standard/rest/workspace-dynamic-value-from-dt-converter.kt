/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.standard.rest

import com.gridnine.jasmine.common.standard.model.domain.DynamicCriterionDateValue
import com.gridnine.jasmine.common.standard.model.rest.DynamicCriterionDateValueDT

class DateWorkspaceFromDtConverter:WorkspaceDynamicValueFromDtConverter<DynamicCriterionDateValue, DynamicCriterionDateValueDT>{
    override fun convert(item: DynamicCriterionDateValueDT): DynamicCriterionDateValue {
        val res = DynamicCriterionDateValue()
        res.valueType = item.valueType
        res.correction = item.correction
        return res
    }

    override fun getId(): String {
        return DynamicCriterionDateValue::class.qualifiedName!!
    }

}
