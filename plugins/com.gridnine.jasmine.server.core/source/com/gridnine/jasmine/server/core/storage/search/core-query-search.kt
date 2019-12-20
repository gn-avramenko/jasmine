/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.core.storage.search


enum class SortOrder {
    ASC,
    DESC
}

interface SortableProperty

class SearchQuery :BaseQuery(){

    var freeText: String? = null

    val orders = linkedMapOf<String, SortOrder>()

    val preferredProperties = linkedSetOf<String>()

    var limit: Int = 200

    var offset: Int = 0

    override fun toString(): String {
        val buf = StringBuilder("SELECT ${if(preferredProperties.isEmpty()) "*" else preferredProperties.joinToString { ", "}}")

        if(criterions.isNotEmpty()) {
            buf.append(" WHERE ${criterions.joinToString(" AND") }")
        }
        if (limit > 0) {
            buf.append(" LIMIT $limit OFFSET $offset")
        }
        return buf.toString()
    }

}

class SearchCriterionsBuilder(private val query:SearchQuery):CriterionsBuilder(query.criterions){
    fun freeText(text:String?){
        query.freeText = text
    }
}
class SearchQueryBuilder(private val query: SearchQuery){
    fun limit(limit:Int){
        query.limit = limit
    }
    fun where(init: SearchCriterionsBuilder.() ->Unit){
        val res = SearchCriterionsBuilder(query)
        res.init()
    }
    fun<T> select(vararg properties:T) where T: PropertyNameSupport {
        properties.forEach { query.preferredProperties.add(it.name) }
    }
    fun<T> orderBy(prop:T, order:SortOrder) where T:PropertyNameSupport,T:SortableProperty{
        query.orders[prop.name] = order
    }

}
fun searchQuery(init: SearchQueryBuilder.() -> Unit): SearchQuery {
    val result = SearchQuery()
    val query = SearchQueryBuilder(result)
    query.init()
    return result
}