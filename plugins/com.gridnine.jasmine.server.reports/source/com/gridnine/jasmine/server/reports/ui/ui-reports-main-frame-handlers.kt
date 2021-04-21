/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("UNCHECKED_CAST")

package com.gridnine.jasmine.server.reports.ui

import com.gridnine.jasmine.common.core.model.ObjectReference
import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.common.reports.model.domain.ReportDescription
import com.gridnine.jasmine.server.core.ui.common.BaseNodeWrapper
import com.gridnine.jasmine.server.core.ui.components.*
import com.gridnine.jasmine.server.standard.ui.mainframe.*

class ReportDescriptionUiListItemHandler:UiListItemHandler{
    override fun open(obj: ObjectReference<*>, navigationKey: String) {
        MainFrame.get().openTab(ReportDescriptionMainFrameTabHandler() as MainFrameTabHandler<Any>, obj)
    }

    override fun getId(): String {
        return ReportDescription::class.qualifiedName!!
    }

}

class ReportDescriptionMainFrameTabHandler:MainFrameTabHandler<ObjectReference<ReportDescription>>{
    override fun getTabId(obj: ObjectReference<ReportDescription>): String {
        return "report_${obj.uid}"
    }

    override fun createTabData(obj: ObjectReference<ReportDescription>, callback: MainFrameTabCallback): MainFrameTabData {
        val doc = Storage.get().loadDocument(obj)!!
        return MainFrameTabData(doc.name!!, PrepareReportPanel(doc.name!!))
    }

}

class PrepareReportPanel(reportTitle:String) : BaseNodeWrapper<BorderContainer>(){
    init {
        _node = UiLibraryAdapter.get().createBorderLayout {
            width = "100%"
            height = "100%"
        }
        _node.setEastRegion {
            width = "200px"
            content = PrepareReportParametersPanel()
            collapsible = true
            showBorder = true
            title = "Параметры отчета"
        }
        _node.setCenterRegion {
            content = PrepareReportContentPanel(reportTitle)
        }

    }
}

class PrepareReportParametersPanel : BaseNodeWrapper<GridLayoutContainer>(){
    init {
        _node = UiLibraryAdapter.get().createGridLayoutContainer {
            width = "100%"
            height = "100%"
            columns.add(GridLayoutColumnConfiguration("100%"))
        }
        _node.addRow()
        _node.addCell(GridLayoutCell(UiLibraryAdapter.get().createLabel {  }.let {
            it.setText("Filters")
            it
        }))
    }
}
class PrepareReportContentPanel(reportTitle:String) : BaseNodeWrapper<BorderContainer>(){
    init {
        _node = UiLibraryAdapter.get().createBorderLayout {
            width = "100%"
            height = "100%"
        }
        _node.setNorthRegion {
            height = "25px"
            content = UiLibraryAdapter.get().createLabel {
                width = "100%"
                height = "100%"
                sClass = "jasmine-report-title"
            }.let {
                it.setText(reportTitle)
                it
            }

        }
        _node.setCenterRegion {
            content = UiLibraryAdapter.get().createLabel {
            width = "100%"
            height = "100%"
        }.let {
            it.setText("Отчет")
            it
            }
        }
    }
}