/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.reports.zk.components

import com.gridnine.jasmine.common.core.utils.TextUtils
import com.gridnine.jasmine.common.reports.model.misc.GeneratedReport
import com.gridnine.jasmine.server.reports.ui.ReportResultPanel
import com.gridnine.jasmine.server.reports.ui.ReportResultPanelConfiguration
import com.gridnine.jasmine.server.zk.ui.components.ZkUiComponent
import com.gridnine.jasmine.server.zk.ui.components.configureBasicParameters
import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zul.*

class ZkReportResultPanel(configure: ReportResultPanelConfiguration.() -> Unit) : ReportResultPanel,ZkUiComponent{

    private var component: Tabbox? = null

    private var reportData: GeneratedReport? = null

    private var tabs: Tabs? = null

    private  var tabpanels: Tabpanels? = null

    private val config = ReportResultPanelConfiguration()
    init {
        config.configure()
    }
    override fun setData(data: GeneratedReport) {
        this.reportData = data
        if(component!= null){
            setDataInternal()
        }
    }

    private fun setDataInternal() {
        component!!.getChildren<HtmlBasedComponent>().clear()
        if(reportData != null) {
            val tabs = Tabs()
            this.tabs = tabs
            component!!.appendChild(tabs)
            val panels = Tabpanels()
            this.tabpanels = panels
            component!!.appendChild(panels)
            reportData!!.lists.forEach { list ->
                val tab = Tab()
                tab.isClosable = false
                tab.id = TextUtils.generateUid()
                tab.label = list.title
                tabs.appendChild(tab)

                val tabbPanel = Tabpanel()
                tabbPanel.vflex = "1"
                tabbPanel.hflex = "1"
                tabpanels!!.appendChild(tabbPanel)
            }
            if(reportData!!.lists.isNotEmpty()) {
                component!!.selectedIndex = 0
            }
        }
    }

    override fun getData(): GeneratedReport? {
        return reportData
    }

    override fun getZkComponent(): HtmlBasedComponent {
        if(component!= null){
            return component!!
        }
        component = Tabbox()
        component!!.orient ="bottom"
        configureBasicParameters(component!!, config)
        setDataInternal()
        return component!!
    }

}