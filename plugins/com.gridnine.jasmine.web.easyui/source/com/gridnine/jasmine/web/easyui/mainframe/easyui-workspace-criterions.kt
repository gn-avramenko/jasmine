/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.easyui.mainframe

import com.gridnine.jasmine.server.standard.model.rest.*
import com.gridnine.jasmine.web.core.utils.TextUtilsJS
import com.gridnine.jasmine.web.core.utils.HtmlUtilsJS
import com.gridnine.jasmine.web.easyui.JQuery
import com.gridnine.jasmine.web.easyui.jQuery


@Suppress("UnsafeCastFromDynamic")
class EasyUiWorkspaceCriterionsEditor(private val divId: String, listId: String?) {

    private val uid = TextUtilsJS.createUUID()
    private val criterionsContainerEditor = CriterionsContainerEditor(divId, listId, uid, 0)

    init {
        val content = HtmlUtilsJS.html {
            div(id = "${divId}Header", style = "width:100%") {
                table(style = "width:100%;border-collapse: collapse") {
                    tr {
                        td(style = "width:${CriterionsContainerEditor.propertyFieldWidth}px;border:1px solid  #D3D3D3;padding:${CriterionsContainerEditor.padding}px") { "Поле"() }
                        td(style = "width:${CriterionsContainerEditor.conditionFieldWidth}px;border:1px solid #D3D3D3;padding:${CriterionsContainerEditor.padding}px") { "Условие"() }
                        td(style = "border:1px solid #D3D3D3;padding:${CriterionsContainerEditor.padding}px") { "Значение"() }
                        td(style = "width:20px") {
                            div(style = "display:inline;float:right;width:15px;height:20px", id = "${uid}criterionsAddToolButton", `class` = "jasmine-datagrid-expand") { }
                        }
                    }
                }
            }
            div(id = "${uid}${divId}Criterions", style = "width:100%") {

            }
            div(id = "${uid}criterionsAddDialogMenu", style = "display:none") {
                div(id = "${uid}criterionsAddSimpleCriterionMenuItem") { "простое условие"() }
                div(id = "${uid}criterionsAddOrCriterionMenuItem") { "логическое ИЛИ"() }
                div(id = "${uid}criterionsAddAndCriterionMenuItem") { "логическое И"() }
                div(id = "${uid}criterionsAddNotCriterionMenuItem") { "логическое НЕ"() }
            }
        }.toString()
        jQuery("#${divId}").html(content)
        criterionsContainerEditor.decorate()
        val addButtonDiv = jQuery("#${uid}criterionsAddToolButton")
        addButtonDiv.click {
            criterionsContainerEditor.idx = -1
            val pos = addButtonDiv.asDynamic().offset()
            criterionsContainerEditor.menuDiv.menu("show", object {
                val left = pos.left
                val top = pos.top
            })
        }
    }

    fun clear() {
        criterionsContainerEditor.clear()
    }

    fun readData(criterions: List<BaseWorkspaceCriterionDTJS>) {
        criterionsContainerEditor.readData(criterions)
    }

    fun writeData(criterions: MutableList<BaseWorkspaceCriterionDTJS>) {
        criterionsContainerEditor.writeData(criterions)
    }

}

@Suppress("UnsafeCastFromDynamic")
class CriterionsContainerEditor(private val divId: String, private val listId: String?, private val uid: String, private val indent: Int){
    lateinit var menuDiv: JQuery
    private val handlers = arrayListOf<CriterionHandler<*>>()

    var idx = 0

    fun decorate() {
        menuDiv = jQuery("#${uid}criterionsAddDialogMenu")
        menuDiv.menu(object {
            val onClick = onClick@ { item: dynamic ->
                //console.log("idx is $idx")
                var localIndent = indent
                if(idx != -1 && handlers.size > 0 ){
                    val target = handlers[idx]
                    if(target is CriterionsContainerHandler && target.getCriterionsCount() ==0){
                        localIndent = indent+CriterionsContainerEditor.indent
                    }
                }

                val handler: CriterionHandler<BaseWorkspaceCriterionDTJS> =
                        when (item.id) {
                            "${uid}criterionsAddSimpleCriterionMenuItem" -> {
                                SimpleCriterionHandler(listId!!, localIndent ) as CriterionHandler<BaseWorkspaceCriterionDTJS>
                            }
                            "${uid}criterionsAddOrCriterionMenuItem" -> {
                                EasyUiWorkspaceOrCriterionHandler(listId!!,localIndent) as CriterionHandler<BaseWorkspaceCriterionDTJS>
                            }
                            "${uid}criterionsAddAndCriterionMenuItem" -> {
                                EasyUiWorkspaceAndCriterionHandler(listId!!,localIndent) as CriterionHandler<BaseWorkspaceCriterionDTJS>
                            }
                            "${uid}criterionsAddNotCriterionMenuItem" -> {
                                EasyUiWorkspaceNotCriterionHandler(listId!!,localIndent) as CriterionHandler<BaseWorkspaceCriterionDTJS>
                            }
                            else -> throw IllegalArgumentException("unsupported menu item id ${item.id}")
                        }
                if(idx != -1 && handlers.size > 0){
                    val target = handlers[idx]
                    if(target is CriterionsContainerHandler && target.getCriterionsCount() ==0){
                        target.addCriterion(handler)
                        return@onClick
                    }
                }
                addCriterion(handler, idx)
            }
        })


    }

    fun clear() {
        val div = jQuery("#${uid}${divId}Criterions")
        div.asDynamic().off("click")
        div.empty()
    }

    fun readData(criterions: List<BaseWorkspaceCriterionDTJS>) {
        clear()
        if (listId == null) {
            return
        }
        criterions.forEach { crit ->
            val handler: CriterionHandler<BaseWorkspaceCriterionDTJS> =
                    when (crit) {
                        is SimpleWorkspaceCriterionDTJS -> SimpleCriterionHandler(listId, indent) as CriterionHandler<BaseWorkspaceCriterionDTJS>
                        is OrWorkspaceCriterionDTJS -> EasyUiWorkspaceOrCriterionHandler(listId,indent) as CriterionHandler<BaseWorkspaceCriterionDTJS>
                        is AndWorkspaceCriterionDTJS -> EasyUiWorkspaceAndCriterionHandler(listId,indent) as CriterionHandler<BaseWorkspaceCriterionDTJS>
                        is NotWorkspaceCriterionDTJS -> EasyUiWorkspaceNotCriterionHandler(listId,indent) as CriterionHandler<BaseWorkspaceCriterionDTJS>
                        else -> throw IllegalArgumentException("unsupported criterion type $crit")
                    }
            addCriterion(handler, handlers.size-1)
            handler.setData(crit)
        }
    }

    fun writeData(criterions: MutableList<BaseWorkspaceCriterionDTJS>) {
        criterions.clear()
        handlers.forEach {
            it.getData()?.let { crit -> criterions.add(crit) }
        }
    }


    fun addCriterion(handler: CriterionHandler<BaseWorkspaceCriterionDTJS>, position: Int) {
        //console.log("trying to addd to $position")
        val content = handler.getContent()
        val criterionsDiv = jQuery("#${uid}${divId}Criterions")
        val children = criterionsDiv.asDynamic().children()
        when {
            children.length ==0 -> {
                handlers.add(0,handler)
                criterionsDiv.append(content)
                //console.log("added to 0 because lengt = 0")
            }
            position == -1 -> {
                handlers.add(0,handler)
                //console.log("added to 0 becaus position -1")
                jQuery(content).asDynamic().insertBefore(children[0])
            }
            else -> {
                handlers.add(position+1,handler)
                //console.log("added to ${position+1}")
                jQuery(content).asDynamic().insertAfter(children[position])
            }
        }
        handler.decorate()

        val addButtonDiv = jQuery("#${handler.getUid()}Add")
        addButtonDiv.click {
            val menuDiv = jQuery("#${uid}criterionsAddDialogMenu")
            idx = handlers.indexOf(handler)
            //console.log("$idx : ${handler.getUid()} (${handlers.joinToString { it.getUid() }})")
            val pos = addButtonDiv.asDynamic().offset()
            menuDiv.menu("show", object {
                val left = pos.left
                val top = pos.top
            })
        }
        val removeButtonDiv = jQuery("#${handler.getUid()}Remove")
        removeButtonDiv.click {
            idx = handlers.indexOf(handler)
            jQuery(criterionsDiv.asDynamic().children()[idx]).remove()
            handlers.remove(handler)
        }
        val upButtonDiv = jQuery("#${handler.getUid()}Up")
        upButtonDiv.click {
            idx = handlers.indexOf(handler)
            if(idx == 0){
                return@click
            }
            move(criterionsDiv, idx, idx-1)
            handlers.add(idx-1, handlers.removeAt(idx))
        }
        val downButtonDiv = jQuery("#${handler.getUid()}Down")
        downButtonDiv.click {
            idx = handlers.indexOf(handler)
            if(idx ==handlers.size-1){
                return@click
            }
            move(criterionsDiv, idx, idx+1)
            handlers.add(idx+1, handlers.removeAt(idx))
        }
    }

    private fun move(criterionsDiv: JQuery, from: Int, to: Int) {
        val children = criterionsDiv.asDynamic().children()
        val toElm  = children[to]
        val detached = jQuery(children[from]).asDynamic().detach()
        if(to > from){
            detached.insertAfter(toElm)
        } else {
            detached.insertBefore(toElm)
        }
    }

    companion object{
        const val propertyFieldWidth = 300
        const val controlWidth = 70
        const val conditionFieldWidth = 200
        const val padding = 5
        const val indent = 40
    }

}


interface CriterionHandler<T : BaseWorkspaceCriterionDTJS> {
    fun getUid(): String
    fun getContent(): String
    fun decorate()
    fun setData(data: T?)
    fun getData(): T?

}

interface CriterionsContainerHandler<T : BaseWorkspaceCriterionDTJS> :CriterionHandler<T>{
    fun getCriterionsCount():Int
    fun addCriterion(handler:CriterionHandler<*>)
}