/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.reports.model

import com.gridnine.jasmine.common.core.model.BaseIdentity
import com.gridnine.jasmine.common.core.model.ObjectReference
import com.gridnine.jasmine.common.reports.model.misc.BaseReportRequestedParameter
import com.gridnine.jasmine.common.reports.model.misc.ReportRequestedParameterDescription
import com.gridnine.jasmine.common.reports.model.misc.ReportRequestedParameterType
import java.time.LocalDate
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
abstract class BaseReportRequestedParameterHandler<T:Any>(private val id:Enum<*>):ReportRequestedParameterHandler<T>{
    override fun createParameterDescription(): ReportRequestedParameterDescription {
        return ReportRequestedParameterDescription().let{
            it.objectClassName = getObjectClassName()
            it.id = id.name
            it.name = id.toString()
            it.type = getReportRequestedParameterType()
            it
        }
    }

    fun getValue(params:List<BaseReportRequestedParameter>):T?{
        return params.find { it.id == id.name }?.getValue("value") as T?
    }

    fun getId() = id.name

    abstract fun getReportRequestedParameterType(): ReportRequestedParameterType

    open fun getObjectClassName(): String? {
        return null
    }
}

abstract class BaseDateReportRequestedParameterHandler(id:Enum<*>):BaseReportRequestedParameterHandler<LocalDate>(id){
    override fun getReportRequestedParameterType(): ReportRequestedParameterType {
        return ReportRequestedParameterType.LOCAL_DATE
    }
}

object StartDateReportRequestedParameterHandler:BaseDateReportRequestedParameterHandler(StandardReportRequestedParameterId.START_DATE)

object EndDateReportRequestedParameterHandler:BaseDateReportRequestedParameterHandler(StandardReportRequestedParameterId.END_DATE)

abstract class BaseObjectReferenceReportRequestedParameterHandler<T:BaseIdentity>(id:Enum<*>, private val cls:KClass<T>):BaseReportRequestedParameterHandler<ObjectReference<T>>(id){
    override fun getReportRequestedParameterType(): ReportRequestedParameterType {
        return ReportRequestedParameterType.OBJECT_REFERENCE
    }

    override fun getObjectClassName(): String? {
        return cls.qualifiedName
    }
}