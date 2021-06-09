/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.reports.rest

import com.gridnine.jasmine.common.reports.model.domain.ReportsWorkspaceItem
import com.gridnine.jasmine.common.reports.model.rest.ReportsWorkspaceItemDT
import com.gridnine.jasmine.common.standard.model.domain.ListWorkspaceItem
import com.gridnine.jasmine.common.standard.model.rest.ListWorkspaceItemDT
import com.gridnine.jasmine.server.standard.rest.BaseWorkspaceItemListFromDTConverter
import com.gridnine.jasmine.server.standard.rest.BaseWorkspaceItemListToDTConverter

class WorkspaceReportsItemFromDTConverter : BaseWorkspaceItemListFromDTConverter<ReportsWorkspaceItem, ReportsWorkspaceItemDT>(){

    override fun getId(): String {
        return ReportsWorkspaceItemDT::class.qualifiedName!!
    }

    override fun createModel(): ReportsWorkspaceItem {
        return ReportsWorkspaceItem()
    }

}