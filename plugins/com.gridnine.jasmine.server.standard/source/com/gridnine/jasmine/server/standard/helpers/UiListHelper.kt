package com.gridnine.jasmine.server.standard.helpers

import com.gridnine.jasmine.server.core.model.common.BaseIdentity
import com.gridnine.jasmine.server.core.model.domain.*
import com.gridnine.jasmine.server.core.reflection.ReflectionFactory
import com.gridnine.jasmine.server.core.storage.Storage
import com.gridnine.jasmine.server.core.storage.search.*
import com.gridnine.jasmine.server.core.storage.search.SortOrder
import com.gridnine.jasmine.server.standard.model.*
import com.gridnine.jasmine.server.standard.model.domain.*
import java.time.LocalDate
import java.time.LocalDateTime

object UiListHelper {
    fun search(listId:String, criterions: List<BaseWorkspaceCriterion>, filters:List<ListFilter>,columns: List<String>, freeText:String?, offset:Int, limit:Int,sortColumn: String?, sortDesc:Boolean): Pair<Int, List<BaseIdentity>> {
        val query = SearchQuery()
        query.preferredProperties.addAll(columns)
        query.freeText = freeText
        query.offset = offset
        query.limit = limit
        if(sortColumn != null){
            query.orders[sortColumn] = if(sortDesc) SortOrder.DESC else SortOrder.ASC
        }
        val descr = DomainMetaRegistry.get().indexes[listId]
                ?: DomainMetaRegistry.get().assets[listId]?:throw IllegalArgumentException("no description found for $listId")
        query.criterions.addAll(toQueryCriterions(criterions, descr))
        query.criterions.addAll(toFiltersQueryCriterions(filters, descr))

        val rows:List<BaseIdentity> =  if(DomainMetaRegistry.get().assets.containsKey(listId)){
            Storage.get().searchAssets(ReflectionFactory.get().getClass(listId), query)
        } else {
            Storage.get().searchDocuments(ReflectionFactory.get().getClass<BaseIndex<BaseDocument>>(listId), query)
        }
        val projectionQuery = simpleProjectionQuery(count()){}
        projectionQuery.criterions.addAll(query.criterions)
        projectionQuery.freeText = freeText
        val totalCount:Long =  if(DomainMetaRegistry.get().assets.containsKey(listId)){
            Storage.get().searchAssets(ReflectionFactory.get().getClass<BaseAsset>(listId), projectionQuery)
        } else {
            Storage.get().searchDocuments(ReflectionFactory.get().getClass<BaseIndex<BaseDocument>>(listId), projectionQuery)
        }

        return Pair(totalCount.toInt(), rows)
    }

    private fun toQueryCriterions(criterions: List<BaseWorkspaceCriterion>, descr: BaseIndexDescription): List<SearchCriterion> {
        return criterions.flatMap { toCriterions(it, descr) }
    }

    private fun toCriterions(it: BaseWorkspaceCriterion, descr: BaseIndexDescription): List<SearchCriterion> {
        return when(it){
            is SimpleWorkspaceCriterion -> fromSimpleCriterion(it, descr)
            is AndWorkspaceCriterion ->{
                it.criterions.flatMap { toCriterions(it, descr) }
            }
            is OrWorkspaceCriterion ->{
                arrayListOf(or(it.criterions.flatMap { toCriterions(it, descr) }))
            }
            is NotWorkspaceCriterion ->{
                arrayListOf(not(and(it.criterions.flatMap { toCriterions(it, descr) })))
            }
            else -> throw IllegalArgumentException("unsupported criterion type $it")
        }
    }

    private fun fromSimpleCriterion(criterion: SimpleWorkspaceCriterion, descr: BaseIndexDescription):List<SearchCriterion> {
        val condition = criterion.condition?:return emptyList()
        val property = criterion.property?: return emptyList()
        val propertyDescr = descr.properties[property]
        val value = criterion.value
        if(propertyDescr != null){
            return when(propertyDescr.type){
                DatabasePropertyType.TEXT , DatabasePropertyType.STRING -> {

                    when(condition){
                        WorkspaceSimpleCriterionCondition.EQUALS -> {
                            val eValue = value as WorkspaceSimpleCriterionStringValues??:return emptyList()
                            if(eValue.values.size>1)  arrayListOf(InCriterion(property, eValue.values)) else arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.EQ, eValue.values[0]))
                        }
                        WorkspaceSimpleCriterionCondition.NOT_EQUALS -> {
                            val eValue = value as WorkspaceSimpleCriterionStringValues??:return emptyList()
                            if(eValue.values.size>1)  arrayListOf(NotCriterion(InCriterion(property, eValue.values))) else arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.NE, eValue.values[0]))
                        }
                        WorkspaceSimpleCriterionCondition.SET -> {arrayListOf(CheckCriterion(property, CheckCriterion.Check.IS_NOT_NULL))}
                        WorkspaceSimpleCriterionCondition.NOT_SET -> {arrayListOf(CheckCriterion(property, CheckCriterion.Check.IS_NULL))}
                        WorkspaceSimpleCriterionCondition.CONTAINS -> {
                            val eValue = value as WorkspaceSimpleCriterionStringValues??:return emptyList()
                            if(eValue.values.size>1)  arrayListOf(or(eValue.values.map { SimpleCriterion(property, SimpleCriterion.Operation.ILIKE, "%$it%") })) else arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.ILIKE, "%${eValue.values[0]}%"))
                        }
                        WorkspaceSimpleCriterionCondition.NOT_CONTAINS ->{
                            val eValue = value as WorkspaceSimpleCriterionStringValues??:return emptyList()
                            if(eValue.values.size>1)  arrayListOf(not(or(eValue.values.map { SimpleCriterion(property, SimpleCriterion.Operation.ILIKE, "%$it%") }))) else arrayListOf(not(SimpleCriterion(property, SimpleCriterion.Operation.ILIKE, "%${eValue.values[0]}%")))
                        }
                        else -> throw IllegalArgumentException("unsupported condition $condition")
                    }
                }
                DatabasePropertyType.LOCAL_DATE -> {
                    when(condition){
                        WorkspaceSimpleCriterionCondition.EQUALS -> {
                            val eValue = (value as WorkspaceSimpleCriterionDateValue?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.EQ, eValue))
                        }
                        WorkspaceSimpleCriterionCondition.GREATER_THAN -> {
                            val eValue = (value as WorkspaceSimpleCriterionDateValue?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.GT, eValue))
                        }
                        WorkspaceSimpleCriterionCondition.GREATER_THAN_OR_EQUALS -> {
                            val eValue = (value as WorkspaceSimpleCriterionDateValue?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.GE, eValue))
                        }
                        WorkspaceSimpleCriterionCondition.LESS_THAN_OR_EQUALS -> {
                            val eValue = (value as WorkspaceSimpleCriterionDateValue?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.LE, eValue))
                        }
                        WorkspaceSimpleCriterionCondition.LESS_THAN -> {
                            val eValue = (value as WorkspaceSimpleCriterionDateValue?)?.value?:return emptyList()
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
                            val eValue = (value as WorkspaceSimpleCriterionDateTimeValue?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.EQ, eValue))
                        }
                        WorkspaceSimpleCriterionCondition.GREATER_THAN -> {
                            val eValue = (value as WorkspaceSimpleCriterionDateTimeValue?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.GT, eValue))
                        }
                        WorkspaceSimpleCriterionCondition.GREATER_THAN_OR_EQUALS -> {
                            val eValue = (value as WorkspaceSimpleCriterionDateTimeValue?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.GE, eValue))
                        }
                        WorkspaceSimpleCriterionCondition.LESS_THAN_OR_EQUALS -> {
                            val eValue = (value as WorkspaceSimpleCriterionDateTimeValue?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.LE, eValue))
                        }
                        WorkspaceSimpleCriterionCondition.LESS_THAN -> {
                            val eValue = (value as WorkspaceSimpleCriterionDateTimeValue?)?.value?:return emptyList()
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
                            val eValue = value as WorkspaceSimpleCriterionEnumValues??:return emptyList()
                            if(eValue.values.size>1)  arrayListOf(InCriterion(property, eValue.values.map { ReflectionFactory.get().safeGetEnum(eValue.enumClassName!!.substringBeforeLast("JS"), it) } as List<Any>)) else arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.EQ, ReflectionFactory.get().safeGetEnum(eValue.enumClassName!!.substringBeforeLast("JS"),eValue.values[0]) as Any))
                        }
                        WorkspaceSimpleCriterionCondition.NOT_EQUALS -> {
                            val eValue = value as WorkspaceSimpleCriterionEnumValues??:return emptyList()
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
                            val eValue = value as WorkspaceSimpleCriterionEntityValues??:return emptyList()
                            if(eValue.values.size>1)  arrayListOf(InCriterion(property, eValue.values)) else arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.EQ, eValue.values[0]))
                        }
                        WorkspaceSimpleCriterionCondition.NOT_EQUALS -> {
                            val eValue = value as WorkspaceSimpleCriterionEntityValues??:return emptyList()
                            if(eValue.values.size>1)  arrayListOf(NotCriterion(InCriterion(property, eValue.values))) else arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.NE, eValue.values[0]))
                        }
                        WorkspaceSimpleCriterionCondition.SET -> {arrayListOf(CheckCriterion(property, CheckCriterion.Check.IS_NOT_NULL))}
                        WorkspaceSimpleCriterionCondition.NOT_SET -> {arrayListOf(CheckCriterion(property, CheckCriterion.Check.IS_NULL))}
                        else -> throw IllegalArgumentException("unsupported condition $condition")
                    }
                }
                DatabasePropertyType.LONG, DatabasePropertyType.INT -> {
                    when(condition){
                        WorkspaceSimpleCriterionCondition.EQUALS ->{
                            val eValue = (value as WorkspaceSimpleCriterionIntValue?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.EQ, eValue))
                        }
                        WorkspaceSimpleCriterionCondition.NOT_EQUALS ->{
                            val eValue = (value as WorkspaceSimpleCriterionIntValue?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.NE, eValue))
                        }
                        WorkspaceSimpleCriterionCondition.GREATER_THAN ->{
                            val eValue = (value as WorkspaceSimpleCriterionIntValue?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.GT, eValue))
                        }
                        WorkspaceSimpleCriterionCondition.GREATER_THAN_OR_EQUALS ->{
                            val eValue = (value as WorkspaceSimpleCriterionIntValue?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.GE, eValue))
                        }
                        WorkspaceSimpleCriterionCondition.LESS_THAN ->{
                            val eValue = (value as WorkspaceSimpleCriterionIntValue?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.LT, eValue))
                        }
                        WorkspaceSimpleCriterionCondition.LESS_THAN_OR_EQUALS ->{
                            val eValue = (value as WorkspaceSimpleCriterionIntValue?)?.value?:return emptyList()
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
                            val eValue = (value as WorkspaceSimpleCriterionFloatValue?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.EQ, eValue))
                        }
                        WorkspaceSimpleCriterionCondition.NOT_EQUALS ->{
                            val eValue = (value as WorkspaceSimpleCriterionFloatValue?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.NE, eValue))
                        }
                        WorkspaceSimpleCriterionCondition.GREATER_THAN ->{
                            val eValue = (value as WorkspaceSimpleCriterionFloatValue?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.GT, eValue))
                        }
                        WorkspaceSimpleCriterionCondition.GREATER_THAN_OR_EQUALS ->{
                            val eValue = (value as WorkspaceSimpleCriterionFloatValue?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.GE, eValue))
                        }
                        WorkspaceSimpleCriterionCondition.LESS_THAN ->{
                            val eValue = (value as WorkspaceSimpleCriterionFloatValue?)?.value?:return emptyList()
                            arrayListOf(SimpleCriterion(property, SimpleCriterion.Operation.LT, eValue))
                        }
                        WorkspaceSimpleCriterionCondition.LESS_THAN_OR_EQUALS ->{
                            val eValue = (value as WorkspaceSimpleCriterionFloatValue?)?.value?:return emptyList()
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

    private fun toFiltersQueryCriterions(filters: List<ListFilter>, descr: BaseIndexDescription): List<SearchCriterion> {
        return filters.flatMap { toCriterions(it, descr) }
    }

    private fun toCriterions(filter: ListFilter, descr: BaseIndexDescription): List<SearchCriterion> {
        val propertyDescription = descr.properties[filter.fieldId]
        if(propertyDescription != null){
            return when(propertyDescription.type){
                DatabasePropertyType.STRING, DatabasePropertyType.TEXT -> {
                    val eValue = filter.value as ListFilterStringValues??:return emptyList()
                    if(eValue.values.size>1)  arrayListOf(or(eValue.values.map { SimpleCriterion(filter.fieldId!!, SimpleCriterion.Operation.ILIKE, "%$it%") })) else arrayListOf(SimpleCriterion(filter.fieldId!!, SimpleCriterion.Operation.ILIKE, "%${eValue.values[0]}%"))
                }
                DatabasePropertyType.LOCAL_DATE -> {
                    val result = arrayListOf<SearchCriterion>()
                    val value = filter.value as ListFilterDateIntervalValue
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
                    val value = filter.value as ListFilterDateTimeIntervalValue
                    if(value.startDate != null){
                        result.add(SimpleCriterion(filter.fieldId!!, SimpleCriterion.Operation.GE, value.startDate as LocalDateTime))
                    }
                    if(value.endDate != null){
                        result.add(SimpleCriterion(filter.fieldId!!, SimpleCriterion.Operation.LE, value.endDate as LocalDateTime))
                    }
                    result
                }
                DatabasePropertyType.ENUM -> {
                    val values = filter.value as ListFilterEnumValues
                    if(values.values.size == 1){
                        arrayListOf(SimpleCriterion(filter.fieldId!!, SimpleCriterion.Operation.EQ,  ReflectionFactory.get().safeGetEnum(values.enumClassName!!.substringBeforeLast("JS"), values.values[0]) as Any))
                    } else {
                        arrayListOf(InCriterion(filter.fieldId!!,  values.values.map { ReflectionFactory.get().safeGetEnum(values.enumClassName!!.substringBeforeLast("JS"), it)} as List<Any>))
                    }
                }
                DatabasePropertyType.BOOLEAN -> {
                    val values = filter.value as ListFilterBooleanValues?
                    if(values == null || values.value == null){
                        arrayListOf(CheckCriterion(filter.fieldId!!, CheckCriterion.Check.IS_NULL))
                    } else {
                        arrayListOf(SimpleCriterion(filter.fieldId!!, SimpleCriterion.Operation.EQ, values.value!!))
                    }
                }
                DatabasePropertyType.ENTITY_REFERENCE -> {
                    val eValue = filter.value as ListFilterEntityValues??:return emptyList()
                    if(eValue.values.size>1)  arrayListOf(InCriterion(filter.fieldId!!, eValue.values)) else arrayListOf(SimpleCriterion(filter.fieldId!!, SimpleCriterion.Operation.EQ, eValue.values[0]))
                }
                DatabasePropertyType.LONG, DatabasePropertyType.INT -> {
                    val result = arrayListOf<SearchCriterion>()
                    val value = filter.value as ListFilterIntIntervalValue
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
                    val value = filter.value as ListFilterFloatIntervalValue
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
        throw IllegalArgumentException("unsupported filter id ${filter.fieldId}")
    }
}