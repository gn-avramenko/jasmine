/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.mainframe.workspaceEditor

import com.gridnine.jasmine.server.standard.model.domain.SimpleWorkspaceCriterionJS
import com.gridnine.jasmine.web.core.ui.UiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.WebTableBox
import com.gridnine.jasmine.web.core.ui.components.WebTableBoxCell
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS

class SimpleCriterionHandler(private val tableBox: WebTableBox, private val initData:SimpleWorkspaceCriterionJS?) :CriterionHandler<SimpleWorkspaceCriterionJS>{
    private val uuid = MiscUtilsJS.createUUID()

    override fun getComponents(): MutableList<WebTableBoxCell> {
        val result = arrayListOf<WebTableBoxCell>()
        val propertySelect = UiLibraryAdapter.get().createSelect(tableBox){
            width = "100%"
            showClearIcon = false
        }
        result.add(WebTableBoxCell(propertySelect))
        val conditionSelect = UiLibraryAdapter.get().createSelect(tableBox){
            width = "100%"
            showClearIcon = false
        }
        result.add(WebTableBoxCell(conditionSelect))
        val valueControl = UiLibraryAdapter.get().createDivsContainer(tableBox){
            width = "100%"
        }
        result.add(WebTableBoxCell(valueControl))
        return result
    }

    override fun getId(): String {
        return uuid
    }

}