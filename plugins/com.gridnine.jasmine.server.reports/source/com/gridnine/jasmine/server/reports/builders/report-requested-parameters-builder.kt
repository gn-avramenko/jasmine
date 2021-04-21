/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.reports.builders

import com.gridnine.jasmine.common.reports.model.misc.ReportRequestedParameterDescription
import com.gridnine.jasmine.common.reports.model.misc.ReportRequestedParameterType

object ReportRequestedParametersBuilder {

    fun build(configure: ReportRequestedParametersData.()->Unit):List<ReportRequestedParameterDescription>{
        val data = ReportRequestedParametersData()
        data.configure()
        return data.parameters
    }
}

class  ReportRequestedParametersData{
    val parameters = arrayListOf<ReportRequestedParameterDescription>()
    fun parameter(param:Enum<*>, type:ReportRequestedParameterType, objectClassName:String? = null){
        parameters.add(ReportRequestedParameterDescription().let {
            it.id = param.name
            it.name = param.toString()
            it.type = type
            it.objectClassName = objectClassName
            it
        })
    }
}