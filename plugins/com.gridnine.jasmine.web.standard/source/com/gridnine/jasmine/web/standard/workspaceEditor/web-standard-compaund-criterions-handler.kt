/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.standard.workspaceEditor

import com.gridnine.jasmine.common.standard.model.rest.AndWorkspaceCriterionDTJS
import com.gridnine.jasmine.common.standard.model.rest.BaseComplexWorkspaceCriterionDTJS
import com.gridnine.jasmine.common.standard.model.rest.NotWorkspaceCriterionDTJS
import com.gridnine.jasmine.common.standard.model.rest.OrWorkspaceCriterionDTJS
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.standard.widgets.GeneralTableBoxWidget
import com.gridnine.jasmine.web.standard.widgets.WebGeneralTableBoxWidgetCell
import com.gridnine.jasmine.web.standard.widgets.WebGeneralTableBoxWidgetColumnWidth
import com.gridnine.jasmine.web.standard.widgets.WebLabelWidget

abstract class BaseWebCompoundCriterionHandler<C:BaseComplexWorkspaceCriterionDTJS>(private val tableBox: GeneralTableBoxWidget, private val indent:Int, private val listId:String, private val initData:C?) :WebCriterionHandler<C>{
    private val uuid = MiscUtilsJS.createUUID()
    private  lateinit var criterionsList:WebWorkspaceCriterionsListEditor

    override fun getComponents(): MutableList<WebGeneralTableBoxWidgetCell> {
        val result = arrayListOf<WebGeneralTableBoxWidgetCell>()
        val table = GeneralTableBoxWidget{
            width ="100%"
            columnWidths.add(WebGeneralTableBoxWidgetColumnWidth(50,50,50))
            columnWidths.add(WebGeneralTableBoxWidgetColumnWidth(null, 100, null))
        }
        val components = arrayListOf<WebGeneralTableBoxWidgetCell>()
        val opLabel = WebLabelWidget(getOperationName())
        components.add(WebGeneralTableBoxWidgetCell(opLabel))
        val valueTableBox = GeneralTableBoxWidget{
            width = "100%"
            columnWidths.add(WebGeneralTableBoxWidgetColumnWidth(300-(indent+1)*51, 300-(indent+1)*51, 300-(indent+1)*51))
            columnWidths.add(WebGeneralTableBoxWidgetColumnWidth(180, 180, 180))
            columnWidths.add(WebGeneralTableBoxWidgetColumnWidth(null, 300, null))
            columnWidths.add(WebGeneralTableBoxWidgetColumnWidth(140, 140, 140))
        }
        criterionsList = WebWorkspaceCriterionsListEditor(valueTableBox, indent+1)
        criterionsList.setData(listId, initData?.criterions?: emptyList())
        components.add(WebGeneralTableBoxWidgetCell(valueTableBox))
        table.addRow(0, components)
        result.add(WebGeneralTableBoxWidgetCell(table, 3))
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

class WebAndCriterionHandler(tableBox: GeneralTableBoxWidget, indent:Int, listId: String, initData:AndWorkspaceCriterionDTJS?):BaseWebCompoundCriterionHandler<AndWorkspaceCriterionDTJS>(tableBox,indent,listId,initData){
    override fun getOperationName(): String {
        return "И"
    }

    override fun createCriterion(): AndWorkspaceCriterionDTJS {
        return AndWorkspaceCriterionDTJS()
    }
}

class WebOrCriterionHandler(tableBox: GeneralTableBoxWidget, indent:Int,  listId: String,initData:OrWorkspaceCriterionDTJS?):BaseWebCompoundCriterionHandler<OrWorkspaceCriterionDTJS>(tableBox,indent,listId,initData){
    override fun getOperationName(): String {
        return "ИЛИ"
    }

    override fun createCriterion(): OrWorkspaceCriterionDTJS {
        return OrWorkspaceCriterionDTJS()
    }
}

class WebNotCriterionHandler(tableBox: GeneralTableBoxWidget, indent:Int, listId: String, initData:NotWorkspaceCriterionDTJS?):BaseWebCompoundCriterionHandler<NotWorkspaceCriterionDTJS>(tableBox,indent,listId, initData){
    override fun getOperationName(): String {
        return "НЕ"
    }

    override fun createCriterion(): NotWorkspaceCriterionDTJS {
        return NotWorkspaceCriterionDTJS()
    }
}