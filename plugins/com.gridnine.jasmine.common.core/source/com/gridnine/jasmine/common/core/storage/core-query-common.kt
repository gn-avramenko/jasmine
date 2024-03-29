/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.common.core.storage

import com.gridnine.jasmine.common.core.app.RegistryItem
import com.gridnine.jasmine.common.core.app.RegistryItemType
import com.gridnine.jasmine.common.core.model.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.reflect.KClass

private val  dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss:SSS")
private val  df = DateTimeFormatter.ofPattern("yyyy-MM-dd")

private fun value2String(value:Any?):String{
    return when(value){
        is Enum<*> ->{value.name}
        is LocalDate -> {
            df.format(value)}
        is LocalDateTime -> {
            dtf.format(value)}
        is ObjectReference<*> ->"\"${value.caption?:"${value.type.qualifiedName} ${value.uid}"}\""
        null -> {"null"}
        else -> "\"$value}\""
    }
}

abstract class SearchCriterion


data class SimpleCriterion constructor(val property:String, val operation: Operation, val value: Any) : SearchCriterion() {

    override fun toString(): String {
        return "$property $operation ${value2String(value)}"
    }

    enum class Operation {
        EQ,
        NE,
        LIKE,
        ILIKE,
        GT,
        LT,
        GE,
        LE,
        CONTAINS,
        ICONTAINS,
    }
}

data class CheckCriterion constructor(val property:String,val check: Check) : SearchCriterion() {

    enum class Check {
        IS_EMPTY,
        NOT_EMPTY,
        IS_NULL,
        IS_NOT_NULL,
    }

    override fun toString(): String {
        return "$property ${check.name}"
    }
}
data class BetweenCriterion constructor(val property:String,val lo:Any, val hi:Any ) : SearchCriterion() {

    override fun toString(): String {
        return "$property BETWEEN ${value2String(lo)} AND ${value2String(hi)}"
    }
}

data class NotBetweenCriterion constructor(val property:String,val lo:Any, val hi:Any ) : SearchCriterion() {

    override fun toString(): String {
        return "$property NOT BETWEEN ${value2String(lo)} AND ${value2String(hi)}"
    }

}

data class InCriterion constructor(val property:String,val objects:List<Any> ) : SearchCriterion() {

    override fun toString(): String {
        return "$property IN [${objects.joinToString(", ") { value2String(it) }}]"
    }
}

data class JunctionCriterion constructor(val disjunction:Boolean,val criterions:List<SearchCriterion> ) : SearchCriterion() {

    override fun toString(): String {
        return "(${criterions.joinToString(if(disjunction) " OR " else " AND "){ "($it)" }})"
    }
}

data class NotCriterion constructor(val criterion: SearchCriterion) : SearchCriterion() {

    override fun toString(): String {
        return "NOT ($criterion)"
    }
}

abstract class BaseQuery{
    var freeText: String? = null
    val criterions = arrayListOf<SearchCriterion>()
}

@SearchQueryDsl
open class CriterionsBuilder(private val criterions: MutableList<SearchCriterion>){

    fun<T> eq(property:T, value:Any) where T: PropertyNameSupport, T: EqualitySupport {
        criterions.add(SimpleCriterion(property.name, SimpleCriterion.Operation.EQ, value))
    }

    fun<T> ne(property:T, value:Any) where T: PropertyNameSupport, T: EqualitySupport {
        criterions.add(SimpleCriterion(property.name, SimpleCriterion.Operation.NE, value))
    }
    fun ne(property:String, value:Any)  {
        criterions.add(SimpleCriterion(property, SimpleCriterion.Operation.NE, value))
    }

    fun<T> like(property:T, value:Any) where T: PropertyNameSupport, T: StringOperationsSupport {
        criterions.add(SimpleCriterion(property.name, SimpleCriterion.Operation.LIKE, value))
    }
    fun<T> ilike(property:T, value:Any) where T: PropertyNameSupport, T: StringOperationsSupport {
        criterions.add(SimpleCriterion(property.name, SimpleCriterion.Operation.ILIKE, value))
    }

    fun<T> gt(property:T, value:Any) where T: PropertyNameSupport, T: ComparisonSupport {
        criterions.add(SimpleCriterion(property.name, SimpleCriterion.Operation.GT, value))
    }
    fun<T> ge(property:T, value:Any) where T: PropertyNameSupport, T: ComparisonSupport {
        criterions.add(SimpleCriterion(property.name, SimpleCriterion.Operation.GE, value))
    }
    fun<T> lt(property:T, value:Any) where T: PropertyNameSupport, T: ComparisonSupport {
        criterions.add(SimpleCriterion(property.name, SimpleCriterion.Operation.LT, value))
    }

    fun<T> le(property:T, value:Any) where T: PropertyNameSupport, T: ComparisonSupport {
        criterions.add(SimpleCriterion(property.name, SimpleCriterion.Operation.LE, value))
    }

    fun<T> contains(property:T, value:Any) where T: PropertyNameSupport, T: CollectionSupport {
        criterions.add(SimpleCriterion(property.name, SimpleCriterion.Operation.CONTAINS, value))
    }

    fun<T> icontains(property:T, value:Any) where T: PropertyNameSupport, T: CollectionSupport {
        criterions.add(SimpleCriterion(property.name, SimpleCriterion.Operation.ICONTAINS, value))
    }

    fun<T> isEmpty(property:T) where T: PropertyNameSupport, T: CollectionSupport {
        criterions.add(CheckCriterion(property.name, CheckCriterion.Check.IS_EMPTY))
    }

    fun<T> isNotEmpty(property:T) where T: PropertyNameSupport, T: CollectionSupport {
        criterions.add(CheckCriterion(property.name, CheckCriterion.Check.NOT_EMPTY))
    }

    fun<T> isNull(property:T) where T: PropertyNameSupport {
        criterions.add(CheckCriterion(property.name, CheckCriterion.Check.IS_NULL))
    }

    fun<T> isNotNull(property:T) where T: PropertyNameSupport {
        criterions.add(CheckCriterion(property.name, CheckCriterion.Check.IS_NOT_NULL))
    }

    fun<T,P:Any> between(property:T, lo:P, hi:P) where T: PropertyNameSupport, T: ComparisonSupport {
        criterions.add(BetweenCriterion(property.name, lo, hi))
    }

    fun<T,P:Any> notBetween(property:T, lo:P, hi:P) where T: PropertyNameSupport, T: ComparisonSupport {
        criterions.add(NotBetweenCriterion(property.name, lo, hi))
    }

    fun<T,P:Any> isIn(property:T, values:List<P>) where T: PropertyNameSupport, T: EqualitySupport {
        criterions.add(InCriterion(property.name, values))
    }

    fun and(crits:List<SearchCriterion>){
        criterions.add(JunctionCriterion(false, crits))
    }
    fun or(crits:List<SearchCriterion>){
        criterions.add(JunctionCriterion(true, crits))
    }

    fun not(crit: SearchCriterion){
        criterions.add(NotCriterion(crit))
    }

}

abstract class BaseDynamicCriterionValue :BaseIdentity()

class DynamicCriterion(val handlerId:String, val propertyId:String, val conditionId:String,  val value:BaseDynamicCriterionValue?):SearchCriterion()

interface DynamicCriterionHandler<T:BaseDynamicCriterionValue> : RegistryItem<DynamicCriterionHandler<*>> {
    fun isApplicable(listId:String, propertyId:String) : Boolean
    fun getConditionIds():Collection<String>
    fun getDisplayName():String
    fun getCriterion(listId:String, propertyId: String, conditionId: String, value:T?): SearchCriterion
    fun getRestCorrectionClass():KClass<*>?
    fun getRendererId():String
    override fun getType(): RegistryItemType<DynamicCriterionHandler<*>>{
        return TYPE
    }
    companion object{
        val TYPE = RegistryItemType<DynamicCriterionHandler<*>>("dynamic-criterion-handler")
    }
}

interface DynamicCriterionCondition: RegistryItem<DynamicCriterionCondition>{
    fun getDisplayName():String
    override fun getType(): RegistryItemType<DynamicCriterionCondition>{
        return TYPE
    }
    companion object{
        val TYPE = RegistryItemType<DynamicCriterionCondition>("dynamic-criterion")
    }
}

