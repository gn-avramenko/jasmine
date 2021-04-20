/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.storage

import com.gridnine.jasmine.common.core.app.Registry
import com.gridnine.jasmine.common.core.storage.*
import java.util.concurrent.ConcurrentHashMap

@Suppress("UNCHECKED_CAST")
class DynamicCriterionsUpdater {

    private val criterionHandlersCache = ConcurrentHashMap<String, DynamicCriterionHandler<*>>()

    fun<T:BaseQuery> updateQuery(listId:String, query: T):T{
        updateQueryInternal(listId, query.criterions)
        return query
    }

    private fun getCriterionInternal(listId: String, crit: SearchCriterion):SearchCriterion? {
        if(crit is DynamicCriterion){
            val key = "${listId}||${crit.propertyId}||${crit.conditionId}"
            val handler = criterionHandlersCache.computeIfAbsent(key){
                Registry.get().allOf(DynamicCriterionHandler.TYPE).find { dch -> dch.isApplicable(listId, crit.propertyId) && dch.getConditionIds().contains(crit.conditionId) }!!
            } as DynamicCriterionHandler<BaseDynamicCriterionValue>
            return handler.getCriterion(listId, crit.propertyId, crit.conditionId, crit.value)
        }
        if(crit is JunctionCriterion){
            val subcrits = ArrayList(crit.criterions)
            if(updateQueryInternal(listId, subcrits)){
                return JunctionCriterion(crit.disjunction, subcrits)
            }
        }
        if(crit is NotCriterion){
           val modifiedCrit = getCriterionInternal(listId, crit.criterion)
            if(modifiedCrit != null){
                return NotCriterion(modifiedCrit)
            }
        }
        return null
    }
    private fun updateQueryInternal(listId: String, criterions: ArrayList<SearchCriterion>):Boolean {
        var modified = false
        val updatedCriterions = hashMapOf<SearchCriterion, SearchCriterion>()
        criterions.forEach {crit ->
            val modifiedCrit = getCriterionInternal(listId, crit)
            if(modifiedCrit != null){
                updatedCriterions[crit] = modifiedCrit
                modified = true
            }
        }
        updatedCriterions.forEach{ (sourceCrit, destCrit) ->
            val idx = criterions.indexOf(sourceCrit)
            criterions[idx] = destCrit
        }
        return modified
    }
}
