/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.reports.zk.components

import com.gridnine.jasmine.server.reports.ui.ReportResultPanel
import com.gridnine.jasmine.server.reports.ui.ReportResultPanelConfiguration
import com.gridnine.jasmine.server.reports.ui.ReportsUiComponentsFactory

class ZkReportsUiComponentsFactory : ReportsUiComponentsFactory{
    override fun createResultPanel(configure: ReportResultPanelConfiguration.() -> Unit): ReportResultPanel {
        return ZkReportResultPanel(configure)
    }


}