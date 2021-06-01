/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.standard.workspaceEditor

import com.gridnine.jasmine.common.standard.model.rest.AndWorkspaceCriterionDTJS
import com.gridnine.jasmine.common.standard.model.rest.BaseComplexWorkspaceCriterionDTJS
import com.gridnine.jasmine.common.standard.model.rest.NotWorkspaceCriterionDTJS
import com.gridnine.jasmine.common.standard.model.rest.OrWorkspaceCriterionDTJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.WebTableBox
import com.gridnine.jasmine.web.core.ui.components.WebTableBoxCell
import com.gridnine.jasmine.web.core.ui.components.WebTableBoxColumnWidth
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS

abstract class BaseWebCompoundCriterionHandler<C:BaseComplexWorkspaceCriterionDTJS>(private val tableBox: WebTableBox, private val indent:Int, private val listId:String, private val initData:C?) :WebCriterionHandler<C>{
    private val uuid = MiscUtilsJS.createUUID()
    private  lateinit var criterionsList:WebWorkspaceCriterionsListEditor

    override fun getComponents(): MutableList<WebTableBoxCell> {
        val result = arrayListOf<WebTableBoxCell>()
        val table = WebUiLibraryAdapter.get().createTableBox{
            width ="100%"
            columnWidths.add(WebTableBoxColumnWidth(50,50,50))
            columnWidths.add(WebTableBoxColumnWidth(null, 100, null))
        }
        val components = arrayListOf<WebTableBoxCell>()
        val opLabel = WebUiLibraryAdapter.get().createLabel { }
        opLabel.setText(getOperationName())
        components.add(WebTableBoxCell(opLabel))
        val valueTableBox = WebUiLibraryAdapter.get().createTableBox{
            width = "100%"
            columnWidths.add(WebTableBoxColumnWidth(300-(indent+1)*51, 300-(indent+1)*51, 300-(indent+1)*51))
            columnWidths.add(WebTableBoxColumnWidth(200, 200, 200))
            columnWidths.add(WebTableBoxColumnWidth(null, 300, null))
            columnWidths.add(WebTableBoxColumnWidth(140, 140, 140))
        }
        criterionsList = WebWorkspaceCriterionsListEditor(valueTableBox, indent+1)
        criterionsList.setData(listId, initData?.criterions?: emptyList())
        components.add(WebTableBoxCell(valueTableBox))
        table.addRow(0, components)
        result.add(WebTableBoxCell(table, 3))
        return result
    }

    override fun getData(): C? {
        val data = criterionsList.getData()
        if(data.isEmpty()){
            return null
        }
        val result = createCriterion()
        result.criterions.addAll(data)
        return result
    }

    abstract fun createCriterion(): C

    abstract fun getOperationName(): String?

    override fun getId(): String {
        return uuid
    }

}

class WebAndCriterionHandler(tableBox: WebTableBox, indent:Int, listId: String, initData:AndWorkspaceCriterionDTJS?):BaseWebCompoundCriterionHandler<AndWorkspaceCriterionDTJS>(tableBox,indent,listId,initData){
    override fun getOperationName(): String {
        return "И"
    }

    override fun createCriterion(): AndWorkspaceCriterionDTJS {
        return AndWorkspaceCriterionDTJS()
    }
}

class WebOrCriterionHandler(tableBox: WebTableBox, indent:Int,  listId: String,initData:OrWorkspaceCriterionDTJS?):BaseWebCompoundCriterionHandler<OrWorkspaceCriterionDTJS>(tableBox,indent,listId,initData){
    override fun getOperationName(): String {
        return "ИЛИ"
    }

    override fun createCriterion(): OrWorkspaceCriterionDTJS {
        return OrWorkspaceCriterionDTJS()
    }
}

class WebNotCriterionHandler(tableBox: WebTableBox, indent:Int, listId: String, initData:NotWorkspaceCriterionDTJS?):BaseWebCompoundCriterionHandler<NotWorkspaceCriterionDTJS>(tableBox,indent,listId, initData){
    override fun getOperationName(): String {
        return "НЕ"
    }

    override fun createCriterion(): NotWorkspaceCriterionDTJS {
        return NotWorkspaceCriterionDTJS()
    }
}