/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("UNCHECKED_CAST")

package com.gridnine.jasmine.server.reports.ui

import com.gridnine.jasmine.common.core.app.Registry
import com.gridnine.jasmine.common.core.model.ObjectReference
import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.common.core.utils.AuthUtils
import com.gridnine.jasmine.common.reports.model.domain.ReportDescription
import com.gridnine.jasmine.common.reports.model.domain.SavedReportParameters
import com.gridnine.jasmine.common.reports.model.misc.BaseReportRequestedParameter
import com.gridnine.jasmine.common.reports.model.misc.GeneratedReport
import com.gridnine.jasmine.common.reports.model.misc.ReportRequestedParameterType
import com.gridnine.jasmine.common.standard.model.l10n.StandardL10nMessagesFactory
import com.gridnine.jasmine.server.core.ui.common.BaseNodeWrapper
import com.gridnine.jasmine.server.core.ui.common.ContentType
import com.gridnine.jasmine.server.core.ui.common.UiNode
import com.gridnine.jasmine.server.core.ui.components.*
import com.gridnine.jasmine.server.core.ui.utils.UiUtils
import com.gridnine.jasmine.server.reports.excel.ExcelGenerator
import com.gridnine.jasmine.server.reports.model.ServerReportHandler
import com.gridnine.jasmine.server.standard.ui.mainframe.*

class ReportDescriptionUiListItemHandler : UiListItemHandler {
    override fun open(obj: ObjectReference<*>, navigationKey: String) {
        MainFrame.get().openTab(ReportDescriptionMainFrameTabHandler() as MainFrameTabHandler<Any>, obj)
    }

    override fun getId(): String {
        return ReportDescription::class.qualifiedName!!
    }

}

class ReportDescriptionMainFrameTabHandler : MainFrameTabHandler<ObjectReference<ReportDescription>> {
    override fun getTabId(obj: ObjectReference<ReportDescription>): String {
        return "report_${obj.uid}"
    }

    override fun createTabData(obj: ObjectReference<ReportDescription>, callback: MainFrameTabCallback): MainFrameTabData {
        val doc = Storage.get().loadDocument(obj)!!
        return MainFrameTabData(doc.name!!, PrepareReportPanel(doc))
    }

}

class PrepareReportPanel(doc: ReportDescription) : BaseNodeWrapper<BorderContainer>() {
    private val contentPanel: PrepareReportContentPanel
    init {
        _node = UiLibraryAdapter.get().createBorderLayout {
            width = "100%"
            height = "100%"
        }
        contentPanel = PrepareReportContentPanel(doc.name!!)
        _node.setEastRegion {
            width = "200px"
            content = PrepareReportParametersPanel(doc){
                contentPanel.setData(it)
            }
            collapsible = true
            showBorder = true
            title = "Параметры отчета"
        }
        _node.setCenterRegion {
            content = contentPanel
        }

    }
}

internal class ReportParameterUiWrapper(val parameterId: String, val handler: RequestedParameterUiHandler<BaseReportRequestedParameter, UiNode>, val editor: UiNode)

class PrepareReportParametersPanel(doc: ReportDescription, updateReport:(GeneratedReport)->Unit) : BaseNodeWrapper<GridLayoutContainer>() {

    private val savedParametersUid = "save_report_parameters_${AuthUtils.getCurrentUser()}_${doc.id}$"
    private val parameters = arrayListOf<ReportParameterUiWrapper>()
    private val savedParameters = Storage.get().loadDocument(SavedReportParameters::class, savedParametersUid, true)
            ?: let {
                SavedReportParameters().let {
                    it.uid = savedParametersUid
                    it
                }
            }

    init {
        _node = UiLibraryAdapter.get().createGridLayoutContainer {
            width = "100%"
            columns.add(GridLayoutColumnConfiguration("100%"))
        }
        val handler = Registry.get().get(ServerReportHandler.TYPE, doc.id!!)!!
        handler.getRequestedParametersDescriptions().forEach { param ->
            val parameterId = param.id
            val uiHandler = when (param.type) {
                ReportRequestedParameterType.LOCAL_DATE -> LocalDateRequestedParameterUiHandler()
                ReportRequestedParameterType.OBJECT_REFERENCE -> ObjectReferenceRequestedParameterUiHandler(param.objectClassName!!)
            } as RequestedParameterUiHandler<BaseReportRequestedParameter, UiNode>
            val editor = uiHandler.createEditor()
            savedParameters.parameters.find { it.id == parameterId }?.let {
                uiHandler.setValue(editor, it)
            }
            _node.addRow()
            val label = UiLibraryAdapter.get().createLabel { }
            label.setText(param.name)
            _node.addCell(GridLayoutCell(label))
            _node.addRow()
            _node.addCell(GridLayoutCell(editor))
            parameters.add(ReportParameterUiWrapper(parameterId, uiHandler, editor))
        }

        val applyButton = UiLibraryAdapter.get().createLinkButton {
            width = "100%"
            title = StandardL10nMessagesFactory.apply()
        }
        applyButton.setHandler {
            val params = arrayListOf<BaseReportRequestedParameter>()
            parameters.forEach { wrapper ->
                wrapper.handler.showValidation(wrapper.editor, null)
                val value = wrapper.handler.getValue(wrapper.editor)
                if (value != null) {
                    value.id = wrapper.parameterId
                    params.add(value)
                }
            }
            var hasValidationErrors = false
            handler.validate(params)?.entries?.forEach { entry ->
                if (entry.value != null) {
                    hasValidationErrors = true
                    val param = parameters.find { it.parameterId == entry.key }!!
                    param.handler.showValidation(param.editor, entry.value)
                }
            }
            if (hasValidationErrors) {
                UiUtils.showError(StandardL10nMessagesFactory.Validation_errors_exist())
                return@setHandler
            }
            savedParameters.parameters.clear()
            savedParameters.parameters.addAll(params)
            Storage.get().saveDocument(savedParameters, false)
            val result = handler.generateReport(params)
            if(result.errorMessage != null){
                UiUtils.showError(result.errorMessage!!)
                return@setHandler
            }
            result.report!!.descriptionUid = doc.uid
            updateReport.invoke(result.report!!)
        }
        _node.addRow()
        _node.addCell(GridLayoutCell(applyButton))
    }
}

class PrepareReportContentPanel(reportTitle: String) : BaseNodeWrapper<BorderContainer>() {

    private val reportResultPanel:ReportResultPanel
    init {
        _node = UiLibraryAdapter.get().createBorderLayout {
            width = "100%"
            height = "100%"
        }
        val northPanel = UiLibraryAdapter.get().createGridLayoutContainer {
            sClass = "jasmine-report-title"
            noPadding = true
            columns.add(GridLayoutColumnConfiguration("100%"))
            columns.add(GridLayoutColumnConfiguration("auto"))
        }
        northPanel.addRow()
        northPanel.addCell(GridLayoutCell(UiLibraryAdapter.get().createLabel {
            sClass = "jasmine-report-title"
            width = "100%"
            height = "100%"
        }.let {
            it.setText(reportTitle)
            it
        }))
        val downloadButton = UiLibraryAdapter.get().createLinkButton {
            iconClass = "z-icon-download"
        }
        northPanel.addCell(GridLayoutCell(downloadButton))

        _node.setNorthRegion {
            content = northPanel
        }
        reportResultPanel = ReportsUiComponentsFactory.get().createResultPanel {
            width = "100%"
            height = "100%"
        }
        downloadButton.setHandler {
            reportResultPanel.getData()?.let{
                UiLibraryAdapter.get().save(ExcelGenerator.generate(it), ContentType.EXCEL.getMimeType(), it.fileName)
            }
        }
        _node.setCenterRegion {
            content = reportResultPanel
        }
    }

    fun setData(report:GeneratedReport){
        reportResultPanel.setData(report)
    }

}