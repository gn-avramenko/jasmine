/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.standard.ui.mainframe.workspace

import com.gridnine.jasmine.common.standard.model.domain.AndWorkspaceCriterion
import com.gridnine.jasmine.common.standard.model.domain.BaseComplexWorkspaceCriterion
import com.gridnine.jasmine.common.standard.model.domain.NotWorkspaceCriterion
import com.gridnine.jasmine.common.standard.model.domain.OrWorkspaceCriterion
import com.gridnine.jasmine.server.core.ui.components.Table
import com.gridnine.jasmine.server.core.ui.components.TableCell
import com.gridnine.jasmine.server.core.ui.components.TableColumnDescription
import com.gridnine.jasmine.server.core.ui.components.UiLibraryAdapter
import java.util.*

abstract class BaseCompoundCriterionHandler<C:BaseComplexWorkspaceCriterion>(private val tableBox: Table, private val indent:Int, private val listId:String, private val initData:C?) :UiCriterionHandler<C>{
    private val uuid = UUID.randomUUID().toString()
    private  lateinit var criterionsList:CriterionsListEditor

    override fun getComponents(): MutableList<TableCell> {
        val result = arrayListOf<TableCell>()
        val table = UiLibraryAdapter.get().createTable{
            width ="100%"
            columns.add(TableColumnDescription(null,50,50,50))
            columns.add(TableColumnDescription(null,null,100,null))
        }
        val components = arrayListOf<TableCell>()
        val opLabel = UiLibraryAdapter.get().createLabel { }
        opLabel.setText(getOperationName())
        components.add(TableCell(opLabel))
        val valueTableBox = UiLibraryAdapter.get().createTable{
            width = "100%"
            columns.add(TableColumnDescription(null,300-(indent+1)*51, 300-(indent+1)*51, 300-(indent+1)*51))
            columns.add(TableColumnDescription(null,200, 200, 200))
            columns.add(TableColumnDescription(null,null, 300, null))
            columns.add(TableColumnDescription(null,140, 140, 140))
        }
        criterionsList = CriterionsListEditor(valueTableBox, indent+1)
        criterionsList.setData(listId, initData?.criterions?: emptyList())
        components.add(TableCell(valueTableBox))
        table.addRow(0, components)
        result.add(TableCell(table, 3))
        return result
    }

    override fun getData(): C? {
        val data = criterionsList.getData()
        if(data.isEmpty()){
            return null
        }
        val result = createCriterion()
        result.uid = initData?.uid?:UUID.randomUUID().toString()
        result.criterions.addAll(data)
        return result
    }

    abstract fun createCriterion(): C

    abstract fun getOperationName(): String?

    override fun getId(): String {
        return uuid
    }

}

class AndCriterionHandler(tableBox: Table, indent:Int, listId: String, initData:AndWorkspaceCriterion?):BaseCompoundCriterionHandler<AndWorkspaceCriterion>(tableBox,indent,listId,initData){
    override fun getOperationName(): String? {
        return "И"
    }

    override fun createCriterion(): AndWorkspaceCriterion {
        return AndWorkspaceCriterion()
    }
}

class OrCriterionHandler(tableBox: Table, indent:Int,  listId: String,initData:OrWorkspaceCriterion?):BaseCompoundCriterionHandler<OrWorkspaceCriterion>(tableBox,indent,listId,initData){
    override fun getOperationName(): String? {
        return "ИЛИ"
    }

    override fun createCriterion(): OrWorkspaceCriterion {
        return OrWorkspaceCriterion()
    }
}

class NotCriterionHandler(tableBox: Table, indent:Int, listId: String, initData:NotWorkspaceCriterion?):BaseCompoundCriterionHandler<NotWorkspaceCriterion>(tableBox,indent,listId, initData){
    override fun getOperationName(): String? {
        return "НЕ"
    }

    override fun createCriterion(): NotWorkspaceCriterion {
        return NotWorkspaceCriterion()
    }
}