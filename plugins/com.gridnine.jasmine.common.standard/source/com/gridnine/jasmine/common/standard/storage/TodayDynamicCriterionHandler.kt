/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.common.standard.storage

import com.gridnine.jasmine.common.core.meta.DatabasePropertyType
import com.gridnine.jasmine.common.core.meta.DomainMetaRegistry
import com.gridnine.jasmine.common.core.model.Xeption
import com.gridnine.jasmine.common.core.storage.BetweenCriterion
import com.gridnine.jasmine.common.core.storage.DynamicCriterionHandler
import com.gridnine.jasmine.common.core.storage.SearchCriterion
import com.gridnine.jasmine.common.core.storage.SimpleCriterion
import com.gridnine.jasmine.common.standard.model.domain.DynamicCriterionDateValue
import com.gridnine.jasmine.common.standard.model.domain.StandardDynamicCriterionCondition
import com.gridnine.jasmine.common.standard.model.domain.StandardDynamicCriterionHandlerType
import com.gridnine.jasmine.common.standard.model.domain.StandardDynamicCriterionValueRendererType
import com.gridnine.jasmine.common.standard.model.rest.DynamicCriterionDateValueDT
import java.time.LocalDate
import kotlin.reflect.KClass

class TodayDynamicCriterionHandler : DynamicCriterionHandler<DynamicCriterionDateValue>{
    override fun isApplicable(listId: String, propertyId: String): Boolean {
        val descr = DomainMetaRegistry.get().indexes[listId]?:DomainMetaRegistry.get().assets[listId]?:return false
        val propType = descr.properties[propertyId]?.type?:return false
        return propType ==  DatabasePropertyType.LOCAL_DATE ||propType ==  DatabasePropertyType.LOCAL_DATE_TIME
    }

    override fun getConditionIds(): Collection<String> {
       return  arrayListOf(StandardDynamicCriterionCondition.EQUALS.name,StandardDynamicCriterionCondition.GREATER_THAN.name,
               StandardDynamicCriterionCondition.GREATER_THAN_OR_EQUALS.name,StandardDynamicCriterionCondition.LESS_THAN.name,
               StandardDynamicCriterionCondition.LESS_THAN_OR_EQUALS.name)
    }

    override fun getDisplayName(): String {
        return StandardDynamicCriterionHandlerType.TODAY.toString()
    }

    override fun getCriterion(listId: String, propertyId: String, conditionId: String, value: DynamicCriterionDateValue?): SearchCriterion {

        val descr = DomainMetaRegistry.get().indexes[listId]?:DomainMetaRegistry.get().assets[listId]!!
        val propType = descr.properties[propertyId]?.type!!
        return when (propType){
            DatabasePropertyType.LOCAL_DATE ->{
                val correctedValue = LocalDate.now().plusDays(value!!.correction.toLong())
                when (conditionId){
                    StandardDynamicCriterionCondition.EQUALS.name ->{
                        SimpleCriterion(propertyId, SimpleCriterion.Operation.EQ, correctedValue)
                    }
                    StandardDynamicCriterionCondition.GREATER_THAN_OR_EQUALS.name ->{
                        SimpleCriterion(propertyId, SimpleCriterion.Operation.GE, correctedValue)
                    }
                    StandardDynamicCriterionCondition.GREATER_THAN.name ->{
                        SimpleCriterion(propertyId, SimpleCriterion.Operation.GT, correctedValue)
                    }
                    StandardDynamicCriterionCondition.LESS_THAN_OR_EQUALS.name ->{
                        SimpleCriterion(propertyId, SimpleCriterion.Operation.LE, correctedValue)
                    }
                    StandardDynamicCriterionCondition.LESS_THAN.name ->{
                        SimpleCriterion(propertyId, SimpleCriterion.Operation.LT, correctedValue)
                    }
                    else -> throw Xeption.forDeveloper("unsupported date condition id $conditionId")
                }
            }
            DatabasePropertyType.LOCAL_DATE_TIME ->{
                val correctedValue = LocalDate.now().plusDays(value!!.correction.toLong())
                val lv = correctedValue.atStartOfDay()
                val hv = correctedValue.atTime(23,59,59,999999)
                when (conditionId){
                    StandardDynamicCriterionCondition.EQUALS.name ->{
                        BetweenCriterion(propertyId, lv, hv)
                    }
                    StandardDynamicCriterionCondition.GREATER_THAN_OR_EQUALS.name ->{
                        SimpleCriterion(propertyId, SimpleCriterion.Operation.GE, lv)
                    }
                    StandardDynamicCriterionCondition.GREATER_THAN.name ->{
                        SimpleCriterion(propertyId, SimpleCriterion.Operation.GT, hv)
                    }
                    StandardDynamicCriterionCondition.LESS_THAN_OR_EQUALS.name ->{
                        SimpleCriterion(propertyId, SimpleCriterion.Operation.LE, hv)
                    }
                    StandardDynamicCriterionCondition.LESS_THAN.name ->{
                        SimpleCriterion(propertyId, SimpleCriterion.Operation.LT, lv)
                    }
                    else -> throw Xeption.forDeveloper("unsupported date condition id $conditionId")
                }
            }
            else ->throw Xeption.forDeveloper("unsupported property type $propType")

        }
    }

    override fun getRendererId(): String {
        return StandardDynamicCriterionValueRendererType.DATE.name
    }

    override fun getId(): String {
        return StandardDynamicCriterionHandlerType.TODAY.name
    }

    override fun getRestCorrectionClass(): KClass<*> {
        return DynamicCriterionDateValueDT::class
    }

}