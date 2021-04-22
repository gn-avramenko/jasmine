/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.reports.model

import com.gridnine.jasmine.common.core.app.RegistryItem
import com.gridnine.jasmine.common.core.app.RegistryItemType
import com.gridnine.jasmine.common.reports.model.misc.BaseReportRequestedParameter
import com.gridnine.jasmine.common.reports.model.misc.ReportGenerationResult
import com.gridnine.jasmine.common.reports.model.misc.ReportRequestedParameterDescription

interface ServerReportHandler : RegistryItem<ServerReportHandler> {

    fun getName():String

    fun getRequestedParametersDescriptions():List<ReportRequestedParameterDescription>

    fun generateReport(parameters:List<BaseReportRequestedParameter>) : ReportGenerationResult

    fun validate(params: List<BaseReportRequestedParameter>):Map<String, String?>?

    override fun getType(): RegistryItemType<ServerReportHandler> {
        return TYPE
    }

    companion object{
        val TYPE = RegistryItemType<ServerReportHandler>("server-report-handlers")
    }
}

abstract class BaseSeverReportHandler(private val id:Enum<*>) : ServerReportHandler{
    override fun getId(): String {
        return id.name
    }

    override fun getName(): String {
        return id.toString()
    }
}