/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("UNCHECKED_CAST")

package com.gridnine.jasmine.web.reports.list

import com.gridnine.jasmine.common.core.meta.DatabasePropertyTypeJS
import com.gridnine.jasmine.common.core.meta.DomainMetaRegistryJS
import com.gridnine.jasmine.common.reports.model.domain.ReportDescriptionIndexJS
import com.gridnine.jasmine.common.reports.model.rest.ReportsWorkspaceItemDTJS
import com.gridnine.jasmine.common.standard.model.rest.GetListRequestJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.reports.editor.GenerateReportData
import com.gridnine.jasmine.web.standard.StandardRestClient
import com.gridnine.jasmine.web.standard.mainframe.MainFrame
import com.gridnine.jasmine.web.standard.mainframe.MainFrameTabCallback
import com.gridnine.jasmine.web.standard.mainframe.MainFrameTabData
import com.gridnine.jasmine.web.standard.mainframe.MainFrameTabHandler
import com.gridnine.jasmine.web.standard.widgets.WebGridLayoutWidget

class WebReportsListMainFrameTabHandler : MainFrameTabHandler<ReportsWorkspaceItemDTJS>{
    override fun getTabId(obj: ReportsWorkspaceItemDTJS): String {
        return obj.uid!!
    }

    override suspend fun createTabData(obj: ReportsWorkspaceItemDTJS, callback: MainFrameTabCallback): MainFrameTabData {
        return MainFrameTabData(obj.displayName!!, WebReportsListPanel(obj))
    }

    override fun getId(): String {
        return ReportsWorkspaceItemDTJS::class.simpleName!!
    }

}

class WebReportsListPanel(obj: ReportsWorkspaceItemDTJS) : BaseWebNodeWrapper<WebBorderContainer>(){
    init {
        _node = WebUiLibraryAdapter.get().createBorderContainer {
            fit = true
        }
        val searchBox = WebUiLibraryAdapter.get().createSearchBox {
            width = "100%"
        }
        val toolsPanel = WebGridLayoutWidget {
            width = "100%"
        }.also {
            it.setColumnsWidths("100%", "200px")
            it.addRow(null, searchBox)
        }
        _node.setNorthRegion {
            content = toolsPanel
        }
        val domainDescr  = DomainMetaRegistryJS.get().indexes[obj.listId+"JS"]!!


        val dataGrid = WebUiLibraryAdapter.get().createDataGrid<ReportDescriptionIndexJS> {
            fit = true
            fitColumns = true
            showPagination = true
            selectionType = DataGridSelectionType.NONE
            obj.columns.forEach { col ->
                val propertyDescr = domainDescr.properties[col]
                val collectionDescr = domainDescr.collections[col]
                if (propertyDescr == null && collectionDescr == null) {
                    throw IllegalArgumentException("no field description found for id $col")
                }
                val type = propertyDescr?.type
                val number = type == DatabasePropertyTypeJS.BIG_DECIMAL || type == DatabasePropertyTypeJS.LONG || type == DatabasePropertyTypeJS.INT

                column {
                    fieldId = col
                    title = propertyDescr?.displayName ?: collectionDescr!!.displayName!!
                    sortable = propertyDescr != null
                    horizontalAlignment = if (number) WebDataHorizontalAlignment.RIGHT else WebDataHorizontalAlignment.LEFT
                    resizable = true
                    formatter = MiscUtilsJS.createListFormatter(type)
                    width =200
                }
            }
        }
        dataGrid.setLoader {
            val req = GetListRequestJS()
            req.columns.addAll(obj.columns)
            req.criterions.addAll(obj.criterions)
            req.desc = it.desc
            req.listId = obj.listId!!
            req.page = it.page
            req.rows = it.rows
            req.sortColumn = it.sortColumn
            req.freeText = searchBox.getValue()
            val resp = StandardRestClient.standard_standard_getList(req)
            WebDataGridResponse(resp.totalCount!!, resp.items as List<ReportDescriptionIndexJS> )
        }
        searchBox.setSearcher {
            dataGrid.reload()
        }
        dataGrid.setRowDblClickListener {
            MainFrame.get().openTab(GenerateReportData(it.document!!))
        }

        _node.setCenterRegion {
            content = dataGrid
        }
    }
}

