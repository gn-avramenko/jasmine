/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("UNCHECKED_CAST")

package com.gridnine.jasmine.web.reports.editor


import com.gridnine.jasmine.common.core.model.ObjectReferenceJS
import com.gridnine.jasmine.common.reports.model.domain.BaseReportRequestedParameterJS
import com.gridnine.jasmine.common.reports.model.domain.GeneratedReportJS
import com.gridnine.jasmine.common.reports.model.domain.SavedReportParametersJS
import com.gridnine.jasmine.common.reports.model.misc.ReportRequestedParameterDescriptionJS
import com.gridnine.jasmine.common.reports.model.misc.ReportRequestedParameterTypeJS
import com.gridnine.jasmine.common.reports.model.rest.*
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.BaseWebNodeWrapper
import com.gridnine.jasmine.web.core.ui.components.WebBorderContainer
import com.gridnine.jasmine.web.core.ui.components.WebNode
import com.gridnine.jasmine.web.core.utils.ContentTypeJS
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.reports.ReportsRestClient
import com.gridnine.jasmine.web.standard.WebMessages
import com.gridnine.jasmine.web.standard.mainframe.MainFrameTabCallback
import com.gridnine.jasmine.web.standard.mainframe.MainFrameTabData
import com.gridnine.jasmine.web.standard.mainframe.MainFrameTabHandler
import com.gridnine.jasmine.web.standard.utils.StandardUiUtils
import com.gridnine.jasmine.web.standard.widgets.WebGridLayoutWidget
import com.gridnine.jasmine.web.standard.widgets.WebLabelWidget

internal class ReportParameterUiWrapper(
    val parameterId: String,
    val handler: RequestedParameterWebHandler<BaseReportRequestedParameterJS, WebNode>,
    val editor: WebNode
)

class GenerateReportData(var reportRef: ObjectReferenceJS)

class ReportDescriptionMainFrameTabHandler : MainFrameTabHandler<GenerateReportData> {
    override fun getTabId(obj: GenerateReportData): String {
        return "report_${obj.reportRef.uid}"
    }

    override fun getId(): String {
        return GenerateReportData::class.simpleName!!
    }

    override suspend fun createTabData(obj: GenerateReportData, callback: MainFrameTabCallback): MainFrameTabData {
        val description =
            ReportsRestClient.reports_reports_getParametersDescriptions(GetParametersDescriptionsRequestJS().also {
                it.reportUid = obj.reportRef.uid
            })
        val savedParameters =
            ReportsRestClient.reports_reports_getLastReportParameters(GetLastReportParametersRequestJS().also {
                it.reportId = description.reportId
            })
        return MainFrameTabData(description.reportTitle, PrepareReportPanel(description.reportId, description.reportTitle, description.parameters, savedParameters.savedParameters))
    }

}

class PrepareReportPanel(
    reportId: String,
    reportName:String,
    reportParametersDescriptions: List<ReportRequestedParameterDescriptionJS>,
    savedParameters: SavedReportParametersJS?
) : BaseWebNodeWrapper<WebBorderContainer>() {
    private val contentPanel: PrepareReportContentPanel
    init {
        _node = WebUiLibraryAdapter.get().createBorderContainer {
            width = "100%"
            height = "100%"
        }
        contentPanel = PrepareReportContentPanel(reportName, reportId)
        _node.setEastRegion {
            width = 200
            content = PrepareReportParametersPanel(
                reportId, reportParametersDescriptions,
                savedParameters
            ) {
                contentPanel.setData(it)
            }
            collapsible = true
            showBorder = true
            title = "Параметры отчета"
        }
        _node.setCenterRegion {
            content =contentPanel
        }

    }
}

class PrepareReportParametersPanel(
    reportId: String,
    reportParametersDescriptions: List<ReportRequestedParameterDescriptionJS>,
    savedParameters: SavedReportParametersJS?,
    updateReport: suspend (GeneratedReportJS) -> Unit
) : BaseWebNodeWrapper<WebGridLayoutWidget>() {

    private val parameters = arrayListOf<ReportParameterUiWrapper>()


    init {
        val applyButton = WebUiLibraryAdapter.get().createLinkButton {
            width = "100%"
            title = WebMessages.apply
        }
        _node = WebGridLayoutWidget {
            width = "100%"
        }.also {widget ->
            widget.setColumnsWidths("100%")
            reportParametersDescriptions.forEach { paramDescr ->
                val uiHandler = when (paramDescr.type) {
                    ReportRequestedParameterTypeJS.LOCAL_DATE -> LocalDateRequestedParameterWebHandler()
                    ReportRequestedParameterTypeJS.OBJECT_REFERENCE -> ObjectReferenceRequestedParameterWebHandler(
                        paramDescr.objectClassName!!+"JS"
                    )
                } as RequestedParameterWebHandler<BaseReportRequestedParameterJS, WebNode>
                val editor = uiHandler.createEditor()
                savedParameters?.parameters?.find { it.id == paramDescr.id }?.let {
                    uiHandler.setValue(editor, it)
                }
                widget.addRow(WebLabelWidget(paramDescr.name))
                widget.addRow(editor)
                parameters.add(ReportParameterUiWrapper(paramDescr.id, uiHandler, editor))
            }
            widget.addRow(applyButton)
        }
        applyButton.setHandler {
            val params = arrayListOf<BaseReportRequestedParameterJS>()
            parameters.forEach { wrapper ->
                wrapper.handler.showValidation(wrapper.editor, null)
                val value = wrapper.handler.getValue(wrapper.editor)
                if (value != null) {
                    value.id = wrapper.parameterId
                    params.add(value)
                }
            }
            val reportData = ReportsRestClient.reports_reports_generateReport(GenerateReportRequestJS().also {
                it.reportId = reportId
                it.parameters.addAll(params)
            })
            if (reportData.validationErrors.isNotEmpty()) {
                reportData.validationErrors.entries.forEach { entry ->
                    val param = parameters.find { it.parameterId == entry.key }!!
                    param.handler.showValidation(param.editor, entry.value)
                }
                StandardUiUtils.showError("Есть ошибки валидации")
                return@setHandler
            }
            if (reportData.errorMessage != null) {
                StandardUiUtils.showError(reportData.errorMessage!!)
                return@setHandler
            }
            ReportsRestClient.reports_reports_saveLastReportParameters(SaveLastReportParametersRequestJS().also {
                it.reportId = reportId
                it.parameters.addAll(params)
            })
            updateReport.invoke(reportData.report!!)
        }
    }
}

class PrepareReportContentPanel(reportTitle: String, private val reportId:String) : BaseWebNodeWrapper<WebBorderContainer>() {

    private val reportResultPanel:ReportResultPanel
    init {
        _node = WebUiLibraryAdapter.get().createBorderContainer {
            width = "100%"
            height = "100%"
        }
        val downloadButton = WebUiLibraryAdapter.get().createLinkButton {
            title = "Экспорт"
        }
        val northPanel = WebGridLayoutWidget {
            className = "jasmine-report-title"
            noPadding = true
        }.also {
            it.setColumnsWidths("100%","auto")
            it.addRow(WebLabelWidget(reportTitle){
                className = "jasmine-report-title"
                width = "100%"
                height = "100%"
            }, downloadButton)
        }
       _node.setNorthRegion {
            content = northPanel
        }
        reportResultPanel = ReportResultPanel()
        downloadButton.setHandler {
            reportResultPanel.getData()?.let{rep ->
                val content = ReportsRestClient.reports_reports_generateExcel(GenerateExcelRequestJS().also {
                    it.report = rep
                }).content
                MiscUtilsJS.downloadFile(rep.fileName!!, ContentTypeJS.EXCEL, content)
            }
        }
        _node.setCenterRegion {
            content = reportResultPanel
        }
    }

    fun setData(report:GeneratedReportJS){
        reportResultPanel.setData(report, reportId)
    }

}