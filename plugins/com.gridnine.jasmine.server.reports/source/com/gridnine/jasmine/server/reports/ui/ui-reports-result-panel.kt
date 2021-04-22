/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.reports.ui

import com.gridnine.jasmine.common.core.app.PublishableWrapper
import com.gridnine.jasmine.common.reports.model.misc.GeneratedReport
import com.gridnine.jasmine.server.core.ui.common.BaseComponentConfiguration
import com.gridnine.jasmine.server.core.ui.common.UiNode

interface ReportResultPanel : UiNode{
    fun setData(data:GeneratedReport)
    fun getData():GeneratedReport?
}

class  ReportResultPanelConfiguration :BaseComponentConfiguration()

interface ReportsUiComponentsFactory{

    fun createResultPanel(configure:ReportResultPanelConfiguration.()->Unit):ReportResultPanel


    companion object {
        private val wrapper = PublishableWrapper(ReportsUiComponentsFactory::class)
        fun get() = wrapper.get()
    }
}