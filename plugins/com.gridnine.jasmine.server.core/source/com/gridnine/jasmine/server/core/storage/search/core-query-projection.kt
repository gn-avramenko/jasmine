/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.server.core.storage.search


abstract class Projection


fun <T> max(property: T): Projection where T : ComparisonSupport, T : PropertyNameSupport {
    return SimpleProjection(ProjectionOperation.MAX, property.name)
}

fun <T> min(property: T): Projection where T : ComparisonSupport, T : PropertyNameSupport {
    return SimpleProjection(ProjectionOperation.MIN, property.name)
}

fun <T> avg(property: T): Projection where T : NumberOperationsSupport, T : PropertyNameSupport {
    return SimpleProjection(ProjectionOperation.AVG, property.name)
}

fun <T> sum(property: T): Projection where T : NumberOperationsSupport, T : PropertyNameSupport {
    return SimpleProjection(ProjectionOperation.SUM, property.name)
}

fun count(): Projection {
    return SimpleProjection(ProjectionOperation.COUNT, "*")
}

fun <T> property(property: T): Projection where T : PropertyNameSupport {
    return SimpleProjection(ProjectionOperation.PROPERTY, property.name)
}

fun <T> group(property: T): Projection where T : PropertyNameSupport {
    return SimpleProjection(ProjectionOperation.GROUP, property.name)
}

enum class ProjectionOperation {
    MAX,
    MIN,
    AVG,
    SUM,
    COUNT,
    PROPERTY,
    GROUP
}

class SimpleProjection internal constructor(val operation: ProjectionOperation, val property: String) : Projection() {
    override fun toString(): String {
        return when (operation) {
            ProjectionOperation.AVG -> "average($property)"
            ProjectionOperation.COUNT -> "count($property)"
            ProjectionOperation.MAX -> "max($property)"
            ProjectionOperation.MIN -> "min($property)"
            ProjectionOperation.SUM -> "sum($property)"
            ProjectionOperation.PROPERTY -> property
            ProjectionOperation.GROUP -> "group($property)"
        }
    }
}


class ProjectionWithAlias internal constructor(val operation: ProjectionOperation, val property: String, val alias: String) : Projection() {

    override fun toString(): String {
        return when (operation) {
            ProjectionOperation.AVG -> "average($property)"
            ProjectionOperation.COUNT -> "count($property)"
            ProjectionOperation.MAX -> "max($property)"
            ProjectionOperation.MIN -> " min($property)"
            ProjectionOperation.SUM -> "sum($property)"
            ProjectionOperation.PROPERTY -> property
            ProjectionOperation.GROUP -> "group($property)"
        } + " AS $alias"
    }

}


abstract class BaseProjectionQuery : BaseQuery() {
    override fun toString(): String {
        return if (criterions.isEmpty()) "" else " WHERE ${criterions.joinToString(" AND ")}}"
    }
}

class SimpleProjectionQuery(val projection: Projection) : BaseProjectionQuery() {

    override fun toString(): String {
        return "SELECT $projection${super.toString()}"
    }
}

class ProjectionQuery : BaseProjectionQuery() {

    val projections = arrayListOf<Projection>()

    override fun toString(): String {
        return "SELECT ${projections.joinToString(", ")}${super.toString()}"
    }
}

class ProjectionsBuilder(private val projections: MutableList<Projection>) {

    fun <T> max(property: T) where T : ComparisonSupport, T : PropertyNameSupport {
        projections.add(SimpleProjection(ProjectionOperation.MAX, property.name))
    }

    fun <T> min(property: T) where T : ComparisonSupport, T : PropertyNameSupport {
        projections.add(SimpleProjection(ProjectionOperation.MIN, property.name))
    }

    fun <T> avg(property: T) where T : NumberOperationsSupport, T : PropertyNameSupport {
        projections.add(SimpleProjection(ProjectionOperation.AVG, property.name))
    }

    fun <T> sum(property: T) where T : NumberOperationsSupport, T : PropertyNameSupport {
        projections.add(SimpleProjection(ProjectionOperation.SUM, property.name))
    }

    fun count() {
        projections.add(SimpleProjection(ProjectionOperation.COUNT, "*"))
    }

    fun <T> property(property: T) where T : PropertyNameSupport {
        projections.add(SimpleProjection(ProjectionOperation.PROPERTY, property.name))
    }

    fun <T> group(property: T) where T : PropertyNameSupport {
        projections.add(SimpleProjection(ProjectionOperation.GROUP, property.name))
    }

}

class SimpleProjectionQueryBuilder(private val query: SimpleProjectionQuery) {
    fun where(init: CriterionsBuilder.() -> Unit) {
        val res = CriterionsBuilder(query.criterions)
        res.init()
    }
}

class ProjectionQueryBuilder(val query: ProjectionQuery) {
    fun where(init: CriterionsBuilder.() -> Unit) {
        val res = CriterionsBuilder(query.criterions)
        res.init()
    }

    fun projections(init: ProjectionsBuilder.() -> Unit) {
        val res = ProjectionsBuilder(query.projections)
        res.init()
    }
}


fun simpleProjectionQuery(projection: Projection, init: SimpleProjectionQueryBuilder.() -> Unit): SimpleProjectionQuery {
    val result = SimpleProjectionQuery(projection)
    val query = SimpleProjectionQueryBuilder(result)
    query.init()
    return result
}

fun projectionQuery(init: ProjectionQueryBuilder.() -> Unit): ProjectionQuery {
    val result = ProjectionQuery()
    val query = ProjectionQueryBuilder(result)
    query.init()
    return result
}