/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.easyui.mainframe

import com.gridnine.jasmine.server.standard.model.rest.*
import com.gridnine.jasmine.web.core.utils.TextUtilsJS
import com.gridnine.jasmine.web.core.utils.HtmlUtilsJS

abstract class BaseEasyUiWorkspaceComplexCriterionHandler<T:BaseComplexWorkspaceCriterionDTJS>(listId: String, indent:Int)  : CriterionsContainerHandler<T>{
    private val uid = TextUtilsJS.createUUID()

    private val delegate = CriterionsContainerEditor("", listId, uid, indent+CriterionsContainerEditor.indent)

    override fun getUid(): String {
        return uid
    }

    override fun getContent(): String {
        return HtmlUtilsJS.html {
            table(id = "${uid}Table", style = "width:100%;border-collapse: collapse") {
                tr {
                    td(style = "width:${CriterionsContainerEditor.indent}px;border:1px solid  #D3D3D3;padding:5px") { getDisplayName()() }
                    td(style = "border:1px solid #D3D3D3;padding:0px", id = "${uid}Content" ) {
                        div(id = "${uid}Criterions", style = "width:100%") {

                        }
                        div(id = "${uid}criterionsAddDialogMenu", style = "display:none") {
                            div(id = "${uid}criterionsAddSimpleCriterionMenuItem") { "простое условие"() }
                            div(id = "${uid}criterionsAddOrCriterionMenuItem") { "логическое ИЛИ"() }
                            div(id = "${uid}criterionsAddAndCriterionMenuItem") { "логическое И"() }
                            div(id = "${uid}criterionsAddNotCriterionMenuItem") { "логическое НЕ"() }
                        }
                    }
                    td(style = "width:${CriterionsContainerEditor.controlWidth}px") {
                        ("<div style=\"display:inline;width:15px;height:30px\" id=\"${uid}Up\" class = \"jasmine-datagrid-sort-asc\"></div>" +
                                "<div style=\"display:inline;width:15px;height:30px\" id=\"${uid}Down\" class = \"jasmine-datagrid-sort-desc\"></div>" +
                                "<div style=\"display:inline;width:15px;height:30px\" id=\"${uid}Add\" class = \"jasmine-datagrid-expand\"></div>" +
                                "<div style=\"display:inline;width:15px;height:30px\" id=\"${uid}Remove\" class = \"jasmine-datagrid-collapse\"></div>"
                                )()
                    }
                }
            }

        }.toString()
    }

    abstract fun createCriterion():T

    abstract fun getDisplayName():String

    override fun decorate() {
        delegate.decorate()
    }

    override fun setData(data: T?) {
        delegate.readData(data?.criterions?: emptyList())
    }

    override fun getData(): T? {
        val criterions = arrayListOf<BaseWorkspaceCriterionDTJS>()
        delegate.writeData(criterions)
        if(criterions.isEmpty()){
            return null
        }
        val result  = createCriterion()
        result.criterions.addAll(criterions)
        return result
    }

    override fun getCriterionsCount(): Int {
        return 0
    }

    override fun addCriterion(handler: CriterionHandler<*>) {
        delegate.addCriterion(handler as CriterionHandler<BaseWorkspaceCriterionDTJS>, 0)
    }
}

class EasyUiWorkspaceOrCriterionHandler(listId: String, indent:Int):BaseEasyUiWorkspaceComplexCriterionHandler<OrWorkspaceCriterionDTJS>(listId, indent){
    override fun createCriterion(): OrWorkspaceCriterionDTJS {
        return OrWorkspaceCriterionDTJS()
    }

    override fun getDisplayName(): String {
        return "ИЛИ"
    }
}

class EasyUiWorkspaceAndCriterionHandler(listId: String, indent:Int):BaseEasyUiWorkspaceComplexCriterionHandler<AndWorkspaceCriterionDTJS>(listId, indent){
    override fun createCriterion(): AndWorkspaceCriterionDTJS {
        return AndWorkspaceCriterionDTJS()
    }
    override fun getDisplayName(): String {
        return "И"
    }
}

class EasyUiWorkspaceNotCriterionHandler(listId: String, indent:Int):BaseEasyUiWorkspaceComplexCriterionHandler<NotWorkspaceCriterionDTJS>(listId, indent){
    override fun createCriterion(): NotWorkspaceCriterionDTJS {
        return NotWorkspaceCriterionDTJS()
    }
    override fun getDisplayName(): String {
        return "НЕ"
    }
}