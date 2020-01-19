/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UnsafeCastFromDynamic", "UNCHECKED_CAST")

package com.gridnine.jasmine.server.standard.rest

import com.gridnine.jasmine.server.core.model.common.BaseEntity
import com.gridnine.jasmine.server.core.model.domain.*
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import com.gridnine.jasmine.server.core.storage.Storage
import com.gridnine.jasmine.server.core.storage.search.*
import com.gridnine.jasmine.server.core.utils.ReflectionUtils
import com.gridnine.jasmine.server.sandbox.model.domain.WorkspaceSimpleCriterionEnumValuesDT
import com.gridnine.jasmine.server.standard.model.rest.*
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
        query.criterions.addAll(toQueryCriterions(request.criterions, DomainMetaRegistry.get().indexes[request.listId]
                ?:DomainMetaRegistry.get().assets[request.listId]?:throw IllegalArgumentException("no description found for ${request.listId}")))
        query.criterions.addAll(toFiltersQueryCriterions(request.filters, DomainMetaRegistry.get().indexes[request.listId]
                ?:DomainMetaRegistry.get().assets[request.listId]?:throw IllegalArgumentException("no description found for ${request.listId}")))

        val rows:List<BaseEntity> =  if(DomainMetaRegistry.get().assets.containsKey(request.listId)){
            Storage.get().searchAssets(ReflectionUtils.getClass(request.listId), query)
        } else {
            Storage.get().searchDocuments(ReflectionUtils.getClass<BaseIndex<BaseDocument>>(request.listId), query)
        }
        val projectionQuery = simpleProjectionQuery(count()){}
        projectionQuery.criterions.addAll(query.criterions)
        projectionQuery.freeText = request.freeText
        val totalCount:Long =  if(DomainMetaRegistry.get().assets.containsKey(request.listId)){
            Storage.get().searchAssets(ReflectionUtils.getClass<BaseAsset>(request.listId), projectionQuery)
        } else {
            Storage.get().searchDocuments(ReflectionUtils.getClass<BaseIndex<BaseDocument>>(request.listId), projectionQuery)
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
                it.criterions.flatMap { toCriterions(it, descr) }
            }
            is OrWorkspaceCriterionDT ->{
                arrayListOf(or(it.criterions.flatMap { toCriterions(it, descr) }))
            }
            is NotWorkspaceCriterionDT ->{
                arrayListOf(not(and(it.criterions.flatMap { toCriterions(it, descr) })))
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
                        WorkspaceSimpleCriterionConditionDT.EQUALS -> {
                            val eValue = value as WorkspaceSimpleCriterionStringValuesDT??:return emptyList()
                            if(eValue.values.size>1)  arrayListOf(InCriterion(property, eValue.values)) else arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.EQ, eValue.values[0]))
                        }
                        WorkspaceSimpleCriterionConditionDT.NOT_EQUALS -> {
                            val eValue = value as WorkspaceSimpleCriterionStringValuesDT??:return emptyList()
                            if(eValue.values.size>1)  arrayListOf(NotCriterion(InCriterion(property, eValue.values))) else arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.NE, eValue.values[0]))
                        }
                        WorkspaceSimpleCriterionConditionDT.SET -> {arrayListOf(CheckCriterion(property, CheckCriterion.Check.IS_NOT_NULL))}
                        WorkspaceSimpleCriterionConditionDT.NOT_SET -> {arrayListOf(CheckCriterion(property, CheckCriterion.Check.IS_NULL))}
                        WorkspaceSimpleCriterionConditionDT.CONTAINS -> {
                            val eValue = value as WorkspaceSimpleCriterionStringValuesDT??:return emptyList()
                            if(eValue.values.size>1)  arrayListOf(or(eValue.values.map { SimpleCriterion(property, SimpleCriterion.Operation.ILIKE, "%$it%") })) else arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.ILIKE, "%${eValue.values[0]}%"))
                        }
                        WorkspaceSimpleCriterionConditionDT.NOT_CONTAINS ->{
                            val eValue = value as WorkspaceSimpleCriterionStringValuesDT??:return emptyList()
                            if(eValue.values.size>1)  arrayListOf(not(or(eValue.values.map { SimpleCriterion(property, SimpleCriterion.Operation.ILIKE, "%$it%") }))) else arrayListOf(not(SimpleCriterion(property, SimpleCriterion.Operation.ILIKE, "%${eValue.values[0]}%")))
                        }
                        else -> throw IllegalArgumentException("unsupported condition $condition")
                    }
                }
                DatabasePropertyType.LOCAL_DATE -> {
                    when(condition){
                        WorkspaceSimpleCriterionConditionDT.EQUALS -> {
                            val eValue = (value as WorkspaceSimpleCriterionDateValueDT?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.EQ, eValue))
                        }
                        WorkspaceSimpleCriterionConditionDT.GREATER_THAN -> {
                            val eValue = (value as WorkspaceSimpleCriterionDateValueDT?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.GT, eValue))
                        }
                        WorkspaceSimpleCriterionConditionDT.GREATER_THAN_OR_EQUALS -> {
                            val eValue = (value as WorkspaceSimpleCriterionDateValueDT?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.GE, eValue))
                        }
                        WorkspaceSimpleCriterionConditionDT.LESS_THAN_OR_EQUALS -> {
                            val eValue = (value as WorkspaceSimpleCriterionDateValueDT?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.LE, eValue))
                        }
                        WorkspaceSimpleCriterionConditionDT.LESS_THAN -> {
                            val eValue = (value as WorkspaceSimpleCriterionDateValueDT?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.LT, eValue))
                        }
                        WorkspaceSimpleCriterionConditionDT.SET -> {arrayListOf(CheckCriterion(property, CheckCriterion.Check.IS_NOT_NULL))}
                        WorkspaceSimpleCriterionConditionDT.NOT_SET -> {arrayListOf(CheckCriterion(property, CheckCriterion.Check.IS_NULL))}
                        else -> throw IllegalArgumentException("unsupported condition $condition")
                    }
                }
                DatabasePropertyType.LOCAL_DATE_TIME -> {
                    when(condition){
                        WorkspaceSimpleCriterionConditionDT.EQUALS -> {
                            val eValue = (value as WorkspaceSimpleCriterionDateTimeValueDT?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.EQ, eValue))
                        }
                        WorkspaceSimpleCriterionConditionDT.GREATER_THAN -> {
                            val eValue = (value as WorkspaceSimpleCriterionDateTimeValueDT?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.GT, eValue))
                        }
                        WorkspaceSimpleCriterionConditionDT.GREATER_THAN_OR_EQUALS -> {
                            val eValue = (value as WorkspaceSimpleCriterionDateTimeValueDT?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.GE, eValue))
                        }
                        WorkspaceSimpleCriterionConditionDT.LESS_THAN_OR_EQUALS -> {
                            val eValue = (value as WorkspaceSimpleCriterionDateTimeValueDT?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.LE, eValue))
                        }
                        WorkspaceSimpleCriterionConditionDT.LESS_THAN -> {
                            val eValue = (value as WorkspaceSimpleCriterionDateTimeValueDT?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.LT, eValue))
                        }
                        WorkspaceSimpleCriterionConditionDT.SET -> {arrayListOf(CheckCriterion(property, CheckCriterion.Check.IS_NOT_NULL))}
                        WorkspaceSimpleCriterionConditionDT.NOT_SET -> {arrayListOf(CheckCriterion(property, CheckCriterion.Check.IS_NULL))}
                        else -> throw IllegalArgumentException("unsupported condition $condition")
                    }
                }
                DatabasePropertyType.ENUM -> {

                    when(condition){
                        WorkspaceSimpleCriterionConditionDT.EQUALS -> {
                            val eValue = value as WorkspaceSimpleCriterionEnumValuesDT??:return emptyList()
                            if(eValue.values.size>1)  arrayListOf(InCriterion(property, eValue.values.map { ReflectionUtils.safeGetEnum(eValue.enumClassName!!.substringBeforeLast("JS"), it) } as List<Any>)) else arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.EQ, ReflectionUtils.safeGetEnum(eValue.enumClassName!!.substringBeforeLast("JS"),eValue.values[0]) as Any))
                        }
                        WorkspaceSimpleCriterionConditionDT.NOT_EQUALS -> {
                            val eValue = value as WorkspaceSimpleCriterionEnumValuesDT??:return emptyList()
                            if(eValue.values.size>1)  arrayListOf(NotCriterion(InCriterion(property, eValue.values.map { ReflectionUtils.safeGetEnum(eValue.enumClassName!!.substringBeforeLast("JS"), it) } as List<Any>))) else arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.NE, ReflectionUtils.safeGetEnum(eValue.enumClassName!!.substringBeforeLast("JS"),eValue.values[0]) as Any))
                        }
                        WorkspaceSimpleCriterionConditionDT.SET -> {arrayListOf(CheckCriterion(property, CheckCriterion.Check.IS_NOT_NULL))}
                        WorkspaceSimpleCriterionConditionDT.NOT_SET -> {arrayListOf(CheckCriterion(property, CheckCriterion.Check.IS_NULL))}
                        else -> throw IllegalArgumentException("unsupported condition $condition")
                    }
                }
                DatabasePropertyType.BOOLEAN -> {
                    when(condition){
                        WorkspaceSimpleCriterionConditionDT.FALSE -> {
                            arrayListOf(SimpleCriterion(property,  SimpleCriterion.Operation.EQ,false))
                        }
                        WorkspaceSimpleCriterionConditionDT.TRUE -> {
                            arrayListOf(SimpleCriterion(property,  SimpleCriterion.Operation.EQ,true))
                        }
                        WorkspaceSimpleCriterionConditionDT.NOT_SET -> {arrayListOf(CheckCriterion(property, CheckCriterion.Check.IS_NOT_NULL))}
                        else -> throw IllegalArgumentException("unsupported condition $condition")
                    }
                }
                DatabasePropertyType.ENTITY_REFERENCE -> {
                    when(condition){
                        WorkspaceSimpleCriterionConditionDT.EQUALS -> {
                            val eValue = value as WorkspaceSimpleCriterionEntityValuesDT??:return emptyList()
                            if(eValue.values.size>1)  arrayListOf(InCriterion(property, eValue.values)) else arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.EQ, eValue.values[0]))
                        }
                        WorkspaceSimpleCriterionConditionDT.NOT_EQUALS -> {
                            val eValue = value as WorkspaceSimpleCriterionEntityValuesDT??:return emptyList()
                            if(eValue.values.size>1)  arrayListOf(NotCriterion(InCriterion(property, eValue.values))) else arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.NE, eValue.values[0]))
                        }
                        WorkspaceSimpleCriterionConditionDT.SET -> {arrayListOf(CheckCriterion(property, CheckCriterion.Check.IS_NOT_NULL))}
                        WorkspaceSimpleCriterionConditionDT.NOT_SET -> {arrayListOf(CheckCriterion(property, CheckCriterion.Check.IS_NULL))}
                        else -> throw IllegalArgumentException("unsupported condition $condition")
                    }
                }
                DatabasePropertyType.LONG,DatabasePropertyType.INT -> {
                    when(condition){
                        WorkspaceSimpleCriterionConditionDT.EQUALS ->{
                            val eValue = (value as WorkspaceSimpleCriterionIntValueDT?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.EQ, eValue))
                        }
                        WorkspaceSimpleCriterionConditionDT.NOT_EQUALS ->{
                            val eValue = (value as WorkspaceSimpleCriterionIntValueDT?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.NE, eValue))
                        }
                        WorkspaceSimpleCriterionConditionDT.GREATER_THAN ->{
                            val eValue = (value as WorkspaceSimpleCriterionIntValueDT?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.GT, eValue))
                        }
                        WorkspaceSimpleCriterionConditionDT.GREATER_THAN_OR_EQUALS ->{
                            val eValue = (value as WorkspaceSimpleCriterionIntValueDT?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.GE, eValue))
                        }
                        WorkspaceSimpleCriterionConditionDT.LESS_THAN ->{
                            val eValue = (value as WorkspaceSimpleCriterionIntValueDT?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.LT, eValue))
                        }
                        WorkspaceSimpleCriterionConditionDT.LESS_THAN_OR_EQUALS ->{
                            val eValue = (value as WorkspaceSimpleCriterionIntValueDT?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.LE, eValue))
                        }
                        WorkspaceSimpleCriterionConditionDT.SET -> {arrayListOf(CheckCriterion(property, CheckCriterion.Check.IS_NOT_NULL))}
                        WorkspaceSimpleCriterionConditionDT.NOT_SET -> {arrayListOf(CheckCriterion(property, CheckCriterion.Check.IS_NULL))}
                        else ->throw IllegalArgumentException("unsupported condition $condition")
                    }
                }

                DatabasePropertyType.BIG_DECIMAL -> {
                    when(condition){
                        WorkspaceSimpleCriterionConditionDT.EQUALS ->{
                            val eValue = (value as WorkspaceSimpleCriterionFloatValueDT?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.EQ, eValue))
                        }
                        WorkspaceSimpleCriterionConditionDT.NOT_EQUALS ->{
                            val eValue = (value as WorkspaceSimpleCriterionFloatValueDT?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.NE, eValue))
                        }
                        WorkspaceSimpleCriterionConditionDT.GREATER_THAN ->{
                            val eValue = (value as WorkspaceSimpleCriterionFloatValueDT?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.GT, eValue))
                        }
                        WorkspaceSimpleCriterionConditionDT.GREATER_THAN_OR_EQUALS ->{
                            val eValue = (value as WorkspaceSimpleCriterionFloatValueDT?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.GE, eValue))
                        }
                        WorkspaceSimpleCriterionConditionDT.LESS_THAN ->{
                            val eValue = (value as WorkspaceSimpleCriterionFloatValueDT?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.LT, eValue))
                        }
                        WorkspaceSimpleCriterionConditionDT.LESS_THAN_OR_EQUALS ->{
                            val eValue = (value as WorkspaceSimpleCriterionFloatValueDT?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.LE, eValue))
                        }
                        WorkspaceSimpleCriterionConditionDT.SET -> {arrayListOf(CheckCriterion(property, CheckCriterion.Check.IS_NOT_NULL))}
                        WorkspaceSimpleCriterionConditionDT.NOT_SET -> {arrayListOf(CheckCriterion(property, CheckCriterion.Check.IS_NULL))}
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
                    if(eValue.values.size>1)  arrayListOf(or(eValue.values.map { SimpleCriterion(filter.fieldId!!, SimpleCriterion.Operation.ILIKE, "%$it%") })) else arrayListOf(SimpleCriterion(filter.fieldId!!, SimpleCriterion.Operation.ILIKE, "%${eValue.values[0]}%"))
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
                        arrayListOf(SimpleCriterion(filter.fieldId!!, SimpleCriterion.Operation.EQ,  ReflectionUtils.safeGetEnum(values.enumClassName!!.substringBeforeLast("JS"), values.values[0]) as Any))
                    } else {
                        arrayListOf(InCriterion(filter.fieldId!!,  values.values.map { ReflectionUtils.safeGetEnum(values.enumClassName!!.substringBeforeLast("JS"), it)} as List<Any>))
                    }
                }
                DatabasePropertyType.BOOLEAN -> {
                    val values = filter.value as ListFilterBooleanValuesDT
                    if(values.value == null){
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