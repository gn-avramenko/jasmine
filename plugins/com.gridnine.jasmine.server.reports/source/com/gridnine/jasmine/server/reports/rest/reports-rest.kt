/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.reports.rest

import com.gridnine.jasmine.common.core.app.Registry
import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.common.core.utils.AuthUtils
import com.gridnine.jasmine.common.reports.model.domain.ReportDescription
import com.gridnine.jasmine.common.reports.model.domain.SavedReportParameters
import com.gridnine.jasmine.common.reports.model.misc.ReportRequestedParameterDescription
import com.gridnine.jasmine.common.reports.model.rest.*
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import com.gridnine.jasmine.server.reports.excel.ExcelGenerator
import com.gridnine.jasmine.server.reports.model.ServerReportHandler
import java.util.*


class GetReportParametersDescriptionsRestHandler:RestHandler<GetParametersDescriptionsRequest,GetParametersDescriptionsResponse>{
    override fun service(
        request: GetParametersDescriptionsRequest,
        ctx: RestOperationContext
    ): GetParametersDescriptionsResponse {
        val doc = Storage.get().loadDocument(ReportDescription::class, request.reportUid)!!
        val params = Registry.get().get(ServerReportHandler.TYPE, doc.id!!)!!.getRequestedParametersDescriptions().map {
            ReportRequestedParameterDescription().apply {
                id = it.id
                name = it.name
                type = it.type
                objectClassName = it.objectClassName
            }
        }
        return GetParametersDescriptionsResponse().apply {
            reportId = doc.id!!
            reportTitle = doc.name!!
            parameters.addAll(params)
        }
    }

}

class  GetLastReportParametersRestHandler:RestHandler<GetLastReportParametersRequest, GetLastReportParametersResponse>{
    override fun service(
        request: GetLastReportParametersRequest,
        ctx: RestOperationContext
    ): GetLastReportParametersResponse {

        val savedParametersUid = "save_report_parameters_${AuthUtils.getCurrentUser()}_${request.reportId}$"
        return GetLastReportParametersResponse().also {
            it.savedParameters = Storage.get().loadDocument(SavedReportParameters::class, savedParametersUid, true)
        }
    }

}

class  SaveLastReportParametersRestHandler:RestHandler<SaveLastReportParametersRequest, SaveLastReportParametersResponse>{
    override fun service(
        request: SaveLastReportParametersRequest,
        ctx: RestOperationContext
    ): SaveLastReportParametersResponse {
        val savedParametersUid = "save_report_parameters_${AuthUtils.getCurrentUser()}_${request.reportId}$"
        val doc = Storage.get().loadDocument(SavedReportParameters::class, savedParametersUid, true)?:SavedReportParameters().also {
            it.uid = savedParametersUid
        }
        doc.parameters.clear()
        doc.parameters.addAll(request.parameters)
        Storage.get().saveDocument(doc)
        return SaveLastReportParametersResponse()
    }
}

class GenerateReportRestHandler:RestHandler<GenerateReportRequest, GenerateReportResponse>{
    override fun service(request: GenerateReportRequest, ctx: RestOperationContext): GenerateReportResponse {
        val handler = Registry.get().get(ServerReportHandler.TYPE, request.reportId)!!
        val validationResult = handler.validate(request.parameters)
        if(validationResult != null){
            return GenerateReportResponse().also {
                it.validationErrors.putAll(validationResult)
            }
        }
        val report = handler.generateReport(request.parameters)
        if(report.errorMessage != null){
            return GenerateReportResponse().also {
                it.errorMessage = report.errorMessage
            }
        }
        return GenerateReportResponse().also {
            it.report = report.report
        }
    }

}

class GenerateExcelRestHandler:RestHandler<GenerateExcelRequest,GenerateExcelResponse>{
    override fun service(request: GenerateExcelRequest, ctx: RestOperationContext): GenerateExcelResponse {
        val content = ExcelGenerator.generate(request.report)
        return GenerateExcelResponse().also {
            it.content = Base64.getEncoder().encodeToString(content)
        }
    }
}