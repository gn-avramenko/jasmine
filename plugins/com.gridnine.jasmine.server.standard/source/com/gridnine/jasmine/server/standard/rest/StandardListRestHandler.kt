/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UnsafeCastFromDynamic", "UNCHECKED_CAST")

package com.gridnine.jasmine.server.standard.rest

import com.gridnine.jasmine.common.core.meta.BaseIndexDescription
import com.gridnine.jasmine.common.core.meta.DatabaseCollectionType
import com.gridnine.jasmine.common.core.meta.DatabasePropertyType
import com.gridnine.jasmine.common.core.meta.DomainMetaRegistry
import com.gridnine.jasmine.common.core.model.BaseDocument
import com.gridnine.jasmine.common.core.model.BaseIdentity
import com.gridnine.jasmine.common.core.model.BaseIndex
import com.gridnine.jasmine.common.core.reflection.ReflectionFactory
import com.gridnine.jasmine.common.core.storage.*
import com.gridnine.jasmine.common.standard.model.domain.WorkspaceSimpleCriterionCondition
import com.gridnine.jasmine.common.standard.model.rest.*
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import java.time.LocalDate
import java.time.LocalDateTime

class StandardListRestHandler: RestHandler<GetListRequest, GetListResponse>{
    override fun service(request: GetListRequest, ctx: RestOperationContext): GetListResponse {
        val query = SearchQuery()
        query.preferredProperties.addAll(request.columns)
        query.freeText = request.freeText
        query.offset = (request.page!!-1)*request.rows!!
        query.limit = request.rows!!
        if(request.sortColumn != null){
            query.orders[request.sortColumn!!] = if(request.desc!!) SortOrder.DESC else SortOrder.ASC
        }
        val descr = DomainMetaRegistry.get().indexes[request.listId]
                ?:DomainMetaRegistry.get().assets[request.listId]?:throw IllegalArgumentException("no description found for ${request.listId}")
        query.criterions.addAll(toQueryCriterions(request.criterions, descr))
        query.criterions.addAll(toFiltersQueryCriterions(request.filters, descr))

        val rows:List<BaseIdentity> =  if(DomainMetaRegistry.get().assets.containsKey(request.listId)){
            Storage.get().searchAssets(ReflectionFactory.get().getClass(request.listId), query)
        } else {
            Storage.get().searchDocuments(ReflectionFactory.get().getClass<BaseIndex<BaseDocument>>(request.listId), query)
        }
        val projectionQuery = simpleProjectionQuery(count()){}
        projectionQuery.criterions.addAll(query.criterions)
        projectionQuery.freeText = request.freeText
        val totalCount:Long =  if(DomainMetaRegistry.get().assets.containsKey(request.listId)){
            Storage.get().searchAssets(ReflectionFactory.get().getClass(request.listId), projectionQuery)
        } else {
            Storage.get().searchDocuments(ReflectionFactory.get().getClass<BaseIndex<BaseDocument>>(request.listId), projectionQuery)
        }

        val result = GetListResponse()
        result.totalCount = totalCount
        result.items.addAll(rows)
        return result
    }

    private fun toQueryCriterions(criterions: List<BaseWorkspaceCriterionDT>, descr:BaseIndexDescription): List<SearchCriterion> {
        return criterions.flatMap { toCriterions(it, descr) }
    }

    private fun toCriterions(it: BaseWorkspaceCriterionDT, descr: BaseIndexDescription): List<SearchCriterion> {
        return when(it){
            is SimpleWorkspaceCriterionDT -> fromSimpleCriterion(it, descr)
            is AndWorkspaceCriterionDT ->{
                arrayListOf(JunctionCriterion(false, it.criterions.flatMap { toCriterions(it, descr) }))
            }
            is OrWorkspaceCriterionDT ->{
                arrayListOf(JunctionCriterion(true, it.criterions.flatMap { toCriterions(it, descr) }))
            }
            is NotWorkspaceCriterionDT ->{
                arrayListOf(NotCriterion(JunctionCriterion(false, it.criterions.flatMap { toCriterions(it, descr) })))
            }
            else -> throw IllegalArgumentException("unsupported criterion type $it")
        }
    }

    private fun fromSimpleCriterion(criterion: SimpleWorkspaceCriterionDT, descr: BaseIndexDescription):List<SearchCriterion> {
        val condition = criterion.condition?:return emptyList()
        val property = criterion.property?: return emptyList()
        val propertyDescr = descr.properties[property]
        val value = criterion.value
        if(propertyDescr != null){
            return when(propertyDescr.type){
                DatabasePropertyType.TEXT , DatabasePropertyType.STRING -> {

                    when(condition){
                        WorkspaceSimpleCriterionCondition.EQUALS -> {
                            val eValue = value as WorkspaceSimpleCriterionStringValuesDT??:return emptyList()
                            if(eValue.values.size>1)  arrayListOf(InCriterion(property, eValue.values)) else arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.EQ, eValue.values[0]))
                        }
                        WorkspaceSimpleCriterionCondition.NOT_EQUALS -> {
                            val eValue = value as WorkspaceSimpleCriterionStringValuesDT??:return emptyList()
                            if(eValue.values.size>1)  arrayListOf(NotCriterion(InCriterion(property, eValue.values))) else arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.NE, eValue.values[0]))
                        }
                        WorkspaceSimpleCriterionCondition.SET -> {arrayListOf(CheckCriterion(property, CheckCriterion.Check.IS_NOT_NULL))}
                        WorkspaceSimpleCriterionCondition.NOT_SET -> {arrayListOf(CheckCriterion(property, CheckCriterion.Check.IS_NULL))}
                        WorkspaceSimpleCriterionCondition.CONTAINS -> {
                            val eValue = value as WorkspaceSimpleCriterionStringValuesDT??:return emptyList()
                            if(eValue.values.size>1)  arrayListOf(JunctionCriterion(true, eValue.values.map { SimpleCriterion(property, SimpleCriterion.Operation.ILIKE, "%$it%") })) else arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.ILIKE, "%${eValue.values[0]}%"))
                        }
                        WorkspaceSimpleCriterionCondition.NOT_CONTAINS ->{
                            val eValue = value as WorkspaceSimpleCriterionStringValuesDT??:return emptyList()
                            if(eValue.values.size>1)  arrayListOf(NotCriterion(JunctionCriterion(true, eValue.values.map { SimpleCriterion(property, SimpleCriterion.Operation.ILIKE, "%$it%") }))) else arrayListOf(NotCriterion(SimpleCriterion(property, SimpleCriterion.Operation.ILIKE, "%${eValue.values[0]}%")))
                        }
                        else -> throw IllegalArgumentException("unsupported condition $condition")
                    }
                }
                DatabasePropertyType.LOCAL_DATE -> {
                    when(condition){
                        WorkspaceSimpleCriterionCondition.EQUALS -> {
                            val eValue = (value as WorkspaceSimpleCriterionDateValueDT?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.EQ, eValue))
                        }
                        WorkspaceSimpleCriterionCondition.GREATER_THAN -> {
                            val eValue = (value as WorkspaceSimpleCriterionDateValueDT?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.GT, eValue))
                        }
                        WorkspaceSimpleCriterionCondition.GREATER_THAN_OR_EQUALS -> {
                            val eValue = (value as WorkspaceSimpleCriterionDateValueDT?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.GE, eValue))
                        }
                        WorkspaceSimpleCriterionCondition.LESS_THAN_OR_EQUALS -> {
                            val eValue = (value as WorkspaceSimpleCriterionDateValueDT?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.LE, eValue))
                        }
                        WorkspaceSimpleCriterionCondition.LESS_THAN -> {
                            val eValue = (value as WorkspaceSimpleCriterionDateValueDT?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.LT, eValue))
                        }
                        WorkspaceSimpleCriterionCondition.SET -> {arrayListOf(CheckCriterion(property, CheckCriterion.Check.IS_NOT_NULL))}
                        WorkspaceSimpleCriterionCondition.NOT_SET -> {arrayListOf(CheckCriterion(property, CheckCriterion.Check.IS_NULL))}
                        else -> throw IllegalArgumentException("unsupported condition $condition")
                    }
                }
                DatabasePropertyType.LOCAL_DATE_TIME -> {
                    when(condition){
                        WorkspaceSimpleCriterionCondition.EQUALS -> {
                            val eValue = (value as WorkspaceSimpleCriterionDateTimeValueDT?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.EQ, eValue))
                        }
                        WorkspaceSimpleCriterionCondition.GREATER_THAN -> {
                            val eValue = (value as WorkspaceSimpleCriterionDateTimeValueDT?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.GT, eValue))
                        }
                        WorkspaceSimpleCriterionCondition.GREATER_THAN_OR_EQUALS -> {
                            val eValue = (value as WorkspaceSimpleCriterionDateTimeValueDT?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.GE, eValue))
                        }
                        WorkspaceSimpleCriterionCondition.LESS_THAN_OR_EQUALS -> {
                            val eValue = (value as WorkspaceSimpleCriterionDateTimeValueDT?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.LE, eValue))
                        }
                        WorkspaceSimpleCriterionCondition.LESS_THAN -> {
                            val eValue = (value as WorkspaceSimpleCriterionDateTimeValueDT?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.LT, eValue))
                        }
                        WorkspaceSimpleCriterionCondition.SET -> {arrayListOf(CheckCriterion(property, CheckCriterion.Check.IS_NOT_NULL))}
                        WorkspaceSimpleCriterionCondition.NOT_SET -> {arrayListOf(CheckCriterion(property, CheckCriterion.Check.IS_NULL))}
                        else -> throw IllegalArgumentException("unsupported condition $condition")
                    }
                }
                DatabasePropertyType.ENUM -> {

                    when(condition){
                        WorkspaceSimpleCriterionCondition.EQUALS -> {
                            val eValue = value as WorkspaceSimpleCriterionEnumValuesDT??:return emptyList()
                            if(eValue.values.size>1)  arrayListOf(InCriterion(property, eValue.values.map { ReflectionFactory.get().safeGetEnum(eValue.enumClassName!!.substringBeforeLast("JS"), it) } as List<Any>)) else arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.EQ, ReflectionFactory.get().safeGetEnum(eValue.enumClassName!!.substringBeforeLast("JS"),eValue.values[0]) as Any))
                        }
                        WorkspaceSimpleCriterionCondition.NOT_EQUALS -> {
                            val eValue = value as WorkspaceSimpleCriterionEnumValuesDT??:return emptyList()
                            if(eValue.values.size>1)  arrayListOf(NotCriterion(InCriterion(property, eValue.values.map { ReflectionFactory.get().safeGetEnum(eValue.enumClassName!!.substringBeforeLast("JS"), it) } as List<Any>))) else arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.NE, ReflectionFactory.get().safeGetEnum(eValue.enumClassName!!.substringBeforeLast("JS"),eValue.values[0]) as Any))
                        }
                        WorkspaceSimpleCriterionCondition.SET -> {arrayListOf(CheckCriterion(property, CheckCriterion.Check.IS_NOT_NULL))}
                        WorkspaceSimpleCriterionCondition.NOT_SET -> {arrayListOf(CheckCriterion(property, CheckCriterion.Check.IS_NULL))}
                        else -> throw IllegalArgumentException("unsupported condition $condition")
                    }
                }
                DatabasePropertyType.BOOLEAN -> {
                    when(condition){
                        WorkspaceSimpleCriterionCondition.NOT_SET -> {arrayListOf(CheckCriterion(property, CheckCriterion.Check.IS_NOT_NULL))}
                        else -> throw IllegalArgumentException("unsupported condition $condition")
                    }
                }
                DatabasePropertyType.ENTITY_REFERENCE -> {
                    when(condition){
                        WorkspaceSimpleCriterionCondition.EQUALS -> {
                            val eValue = value as WorkspaceSimpleCriterionEntityValuesDT??:return emptyList()
                            if(eValue.values.size>1)  arrayListOf(InCriterion(property, eValue.values)) else arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.EQ, eValue.values[0]))
                        }
                        WorkspaceSimpleCriterionCondition.NOT_EQUALS -> {
                            val eValue = value as WorkspaceSimpleCriterionEntityValuesDT??:return emptyList()
                            if(eValue.values.size>1)  arrayListOf(NotCriterion(InCriterion(property, eValue.values))) else arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.NE, eValue.values[0]))
                        }
                        WorkspaceSimpleCriterionCondition.SET -> {arrayListOf(CheckCriterion(property, CheckCriterion.Check.IS_NOT_NULL))}
                        WorkspaceSimpleCriterionCondition.NOT_SET -> {arrayListOf(CheckCriterion(property, CheckCriterion.Check.IS_NULL))}
                        else -> throw IllegalArgumentException("unsupported condition $condition")
                    }
                }
                DatabasePropertyType.LONG,DatabasePropertyType.INT -> {
                    when(condition){
                        WorkspaceSimpleCriterionCondition.EQUALS ->{
                            val eValue = (value as WorkspaceSimpleCriterionIntValueDT?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.EQ, eValue))
                        }
                        WorkspaceSimpleCriterionCondition.NOT_EQUALS ->{
                            val eValue = (value as WorkspaceSimpleCriterionIntValueDT?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.NE, eValue))
                        }
                        WorkspaceSimpleCriterionCondition.GREATER_THAN ->{
                            val eValue = (value as WorkspaceSimpleCriterionIntValueDT?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.GT, eValue))
                        }
                        WorkspaceSimpleCriterionCondition.GREATER_THAN_OR_EQUALS ->{
                            val eValue = (value as WorkspaceSimpleCriterionIntValueDT?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.GE, eValue))
                        }
                        WorkspaceSimpleCriterionCondition.LESS_THAN ->{
                            val eValue = (value as WorkspaceSimpleCriterionIntValueDT?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.LT, eValue))
                        }
                        WorkspaceSimpleCriterionCondition.LESS_THAN_OR_EQUALS ->{
                            val eValue = (value as WorkspaceSimpleCriterionIntValueDT?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.LE, eValue))
                        }
                        WorkspaceSimpleCriterionCondition.SET -> {arrayListOf(CheckCriterion(property, CheckCriterion.Check.IS_NOT_NULL))}
                        WorkspaceSimpleCriterionCondition.NOT_SET -> {arrayListOf(CheckCriterion(property, CheckCriterion.Check.IS_NULL))}
                        else ->throw IllegalArgumentException("unsupported condition $condition")
                    }
                }

                DatabasePropertyType.BIG_DECIMAL -> {
                    when(condition){
                        WorkspaceSimpleCriterionCondition.EQUALS ->{
                            val eValue = (value as WorkspaceSimpleCriterionFloatValueDT?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.EQ, eValue))
                        }
                        WorkspaceSimpleCriterionCondition.NOT_EQUALS ->{
                            val eValue = (value as WorkspaceSimpleCriterionFloatValueDT?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.NE, eValue))
                        }
                        WorkspaceSimpleCriterionCondition.GREATER_THAN ->{
                            val eValue = (value as WorkspaceSimpleCriterionFloatValueDT?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.GT, eValue))
                        }
                        WorkspaceSimpleCriterionCondition.GREATER_THAN_OR_EQUALS ->{
                            val eValue = (value as WorkspaceSimpleCriterionFloatValueDT?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.GE, eValue))
                        }
                        WorkspaceSimpleCriterionCondition.LESS_THAN ->{
                            val eValue = (value as WorkspaceSimpleCriterionFloatValueDT?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.LT, eValue))
                        }
                        WorkspaceSimpleCriterionCondition.LESS_THAN_OR_EQUALS ->{
                            val eValue = (value as WorkspaceSimpleCriterionFloatValueDT?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.LE, eValue))
                        }
                        WorkspaceSimpleCriterionCondition.SET -> {arrayListOf(CheckCriterion(property, CheckCriterion.Check.IS_NOT_NULL))}
                        WorkspaceSimpleCriterionCondition.NOT_SET -> {arrayListOf(CheckCriterion(property, CheckCriterion.Check.IS_NULL))}
                        else ->throw IllegalArgumentException("unsupported condition $condition")
                    }
                }
            }
        }
        val collectionDescr = descr.collections[property]?:throw IllegalArgumentException("no property or collection found for id $property")
        when(collectionDescr.elementType){
            DatabaseCollectionType.STRING -> TODO()
            DatabaseCollectionType.ENUM -> TODO()
            DatabaseCollectionType.ENTITY_REFERENCE -> TODO()
        }
    }

    private fun toFiltersQueryCriterions(filters: List<ListFilterDT>, descr:BaseIndexDescription): List<SearchCriterion> {
        return filters.flatMap { toCriterions(it, descr) }
    }

    private fun toCriterions(filter: ListFilterDT, descr: BaseIndexDescription): List<SearchCriterion> {
        val propertyDescription = descr.properties[filter.fieldId]
        if(propertyDescription != null){
            return when(propertyDescription.type){
                DatabasePropertyType.STRING, DatabasePropertyType.TEXT -> {
                    val eValue = filter.value as ListFilterStringValuesDT??:return emptyList()
                    if(eValue.values.size>1)  arrayListOf(JunctionCriterion(true, eValue.values.map { SimpleCriterion(filter.fieldId!!, SimpleCriterion.Operation.ILIKE, "%$it%") })) else arrayListOf(SimpleCriterion(filter.fieldId!!, SimpleCriterion.Operation.ILIKE, "%${eValue.values[0]}%"))
                }
                DatabasePropertyType.LOCAL_DATE -> {
                    val result = arrayListOf<SearchCriterion>()
                    val value = filter.value as ListFilterDateIntervalValueDT
                    if(value.startDate != null){
                        result.add(SimpleCriterion(filter.fieldId!!, SimpleCriterion.Operation.GE, value.startDate as LocalDate))
                    }
                    if(value.endDate != null){
                        result.add(SimpleCriterion(filter.fieldId!!, SimpleCriterion.Operation.LE, value.endDate as LocalDate))
                    }
                    result
                }
                DatabasePropertyType.LOCAL_DATE_TIME -> {
                    val result = arrayListOf<SearchCriterion>()
                    val value = filter.value as ListFilterDateTimeIntervalValueDT
                    if(value.startDate != null){
                        result.add(SimpleCriterion(filter.fieldId!!, SimpleCriterion.Operation.GE, value.startDate as LocalDateTime))
                    }
                    if(value.endDate != null){
                        result.add(SimpleCriterion(filter.fieldId!!, SimpleCriterion.Operation.LE, value.endDate as LocalDateTime))
                    }
                    result
                }
                DatabasePropertyType.ENUM -> {
                    val values = filter.value as ListFilterEnumValuesDT
                    if(values.values.size == 1){
                        arrayListOf(SimpleCriterion(filter.fieldId!!, SimpleCriterion.Operation.EQ,  ReflectionFactory.get().safeGetEnum(values.enumClassName!!.substringBeforeLast("JS"), values.values[0]) as Any))
                    } else {
                        arrayListOf(InCriterion(filter.fieldId!!,  values.values.map { ReflectionFactory.get().safeGetEnum(values.enumClassName!!.substringBeforeLast("JS"), it)} as List<Any>))
                    }
                }
                DatabasePropertyType.BOOLEAN -> {
                    val values = filter.value as ListFilterBooleanValuesDT?
                    if(values?.value == null){
                        arrayListOf(CheckCriterion(filter.fieldId!!, CheckCriterion.Check.IS_NULL))
                    } else {
                        arrayListOf(SimpleCriterion(filter.fieldId!!, SimpleCriterion.Operation.EQ, values.value!!))
                    }
                }
                DatabasePropertyType.ENTITY_REFERENCE -> {
                    val eValue = filter.value as ListFilterEntityValuesDT??:return emptyList()
                    if(eValue.values.size>1)  arrayListOf(InCriterion(filter.fieldId!!, eValue.values)) else arrayListOf(SimpleCriterion(filter.fieldId!!, SimpleCriterion.Operation.EQ, eValue.values[0]))
                }
                DatabasePropertyType.LONG,DatabasePropertyType.INT -> {
                    val result = arrayListOf<SearchCriterion>()
                    val value = filter.value as ListFilterIntIntervalValueDT
                    if(value.fromValue != null){
                        result.add(SimpleCriterion(filter.fieldId!!, SimpleCriterion.Operation.GE, value.fromValue!!))
                    }
                    if(value.toValue != null){
                        result.add(SimpleCriterion(filter.fieldId!!, SimpleCriterion.Operation.LE, value.toValue!! ))
                    }
                    result
                }
                DatabasePropertyType.BIG_DECIMAL -> {
                    val result = arrayListOf<SearchCriterion>()
                    val value = filter.value as ListFilterFloatIntervalValueDT
                    if(value.fromValue != null){
                        result.add(SimpleCriterion(filter.fieldId!!, SimpleCriterion.Operation.GE, value.fromValue!!))
                    }
                    if(value.toValue != null){
                        result.add(SimpleCriterion(filter.fieldId!!, SimpleCriterion.Operation.LE, value.toValue!!))
                    }
                    result
                }
            }
        }
        val collectionDescription = descr.collections[filter.fieldId]?: throw IllegalArgumentException("no property or collection found for id ${filter.fieldId}")
        return when(collectionDescription.elementType){
            DatabaseCollectionType.STRING -> TODO()
            DatabaseCollectionType.ENUM -> TODO()
            DatabaseCollectionType.ENTITY_REFERENCE -> TODO()
        }
    }

}