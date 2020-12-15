/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.mainframe.workspaceEditor

import com.gridnine.jasmine.server.standard.model.domain.*
import com.gridnine.jasmine.web.core.ui.UiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.WebGridLayoutCell
import com.gridnine.jasmine.web.core.ui.components.WebTableBox
import com.gridnine.jasmine.web.core.ui.components.WebTableBoxCell
import com.gridnine.jasmine.web.core.ui.components.WebTableBoxColumnWidth
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS

abstract class BaseCompoundCriterionHandler<C:BaseComplexWorkspaceCriterionJS>(private val tableBox: WebTableBox, private val indent:Int, private val listId:String, private val initData:C?) :CriterionHandler<C>{
    private val uuid = MiscUtilsJS.createUUID()

    override fun getComponents(): MutableList<WebTableBoxCell> {
        val result = arrayListOf<WebTableBoxCell>()
        val table = UiLibraryAdapter.get().createTableBox(tableBox){
            width ="100%"
            columnWidths.add(WebTableBoxColumnWidth(50,50,50))
            columnWidths.add(WebTableBoxColumnWidth(null,100,null))
        }
        val components = arrayListOf<WebTableBoxCell>()
        val opLabel = UiLibraryAdapter.get().createLabel(table)
        opLabel.setText(getOperationName())
        components.add(WebTableBoxCell(opLabel))
        val valueTableBox = UiLibraryAdapter.get().createTableBox(table){
            width = "100%"
            columnWidths.add(WebTableBoxColumnWidth(300-(indent+1)*51, 300-(indent+1)*51, 300-(indent+1)*51))
            columnWidths.add(WebTableBoxColumnWidth(200, 200, 200))
            columnWidths.add(WebTableBoxColumnWidth(null, 300, null))
            columnWidths.add(WebTableBoxColumnWidth(140, 140, 140))
        }
        val criterionsList = CriterionsListEditor(valueTableBox, indent+1)
        criterionsList.readData(listId, emptyList())
        components.add(WebTableBoxCell(valueTableBox))
        table.addRow(0, components)
        result.add(WebTableBoxCell(table, 3))
        return result
    }

    abstract fun getOperationName(): String?

    override fun getId(): String {
        return uuid
    }

}

class AndCriterionHandler(tableBox: WebTableBox, indent:Int, listId: String, initData:AndWorkspaceCriterionJS?):BaseCompoundCriterionHandler<AndWorkspaceCriterionJS>(tableBox,indent,listId,initData){
    override fun getOperationName(): String? {
        return "И"
    }
}

class OrCriterionHandler(tableBox: WebTableBox, indent:Int,  listId: String,initData:OrWorkspaceCriterionJS?):BaseCompoundCriterionHandler<OrWorkspaceCriterionJS>(tableBox,indent,listId,initData){
    override fun getOperationName(): String? {
        return "ИЛИ"
    }
}

class NotCriterionHandler(tableBox: WebTableBox, indent:Int, listId: String, initData:NotWorkspaceCriterionJS?):BaseCompoundCriterionHandler<NotWorkspaceCriterionJS>(tableBox,indent,listId, initData){
    override fun getOperationName(): String? {
        return "НЕ"
    }
}