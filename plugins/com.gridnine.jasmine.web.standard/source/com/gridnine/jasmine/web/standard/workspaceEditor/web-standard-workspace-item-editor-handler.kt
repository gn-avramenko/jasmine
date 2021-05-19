/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.standard.workspaceEditor

import com.gridnine.jasmine.common.core.model.GeneralSelectBoxConfigurationJS
import com.gridnine.jasmine.common.core.model.SelectItemJS
import com.gridnine.jasmine.common.standard.model.rest.BaseWorkspaceItemDTJS
import com.gridnine.jasmine.common.standard.model.workspace.WorkspaceElementGeneralEditor
import com.gridnine.jasmine.web.core.common.RegistryItemJS
import com.gridnine.jasmine.web.core.common.RegistryItemTypeJS
import com.gridnine.jasmine.web.core.common.RegistryJS
import com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.BaseWebNodeWrapper
import com.gridnine.jasmine.web.core.ui.components.WebBorderContainer
import com.gridnine.jasmine.web.core.ui.components.WebDivsContainer
import com.gridnine.jasmine.web.core.ui.components.WebNode
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.standard.OptionsIds
import com.gridnine.jasmine.web.standard.mainframe.WebOptionsHandler
import kotlin.reflect.KClass

class WorkspaceItemEditorHandler(private val itemsTypes:List<SelectItemJS>) : WorkspaceElementEditorHandler<WorkspaceItemEditor,BaseWorkspaceItemDTJS>{
    override fun getId(): String {
        return "workspace-item-editor"
    }

    override fun createEditor(): WorkspaceItemEditor {
        return WorkspaceItemEditor(itemsTypes)
    }

    override fun setData(editor: WorkspaceItemEditor, data: BaseWorkspaceItemDTJS) {
       editor.setData(data)
    }

    override fun getData(editor: WorkspaceItemEditor): BaseWorkspaceItemDTJS {
        return editor.getData()
    }

    override fun validate(editor: WorkspaceItemEditor): Boolean {
        if(MiscUtilsJS.isBlank(editor.generalEditor.nameWidget.getValue())){
            editor.generalEditor.nameWidget.showValidation("Поле должно быть заполнено")
            return false
        }
        if(editor.generalEditor.typeWidget.getValue() == null){
            editor.generalEditor.typeWidget.showValidation("Поле должно быть заполнено")
            return false
        }
        return true
    }

    override fun getName(data: BaseWorkspaceItemDTJS): String {
        return data.displayName?:"???"
    }

}

class WorkspaceItemEditor(private val itemsTypes:List<SelectItemJS>):BaseWebNodeWrapper<WebBorderContainer>(){
    internal val generalEditor = WorkspaceElementGeneralEditor()

    private val editorsCache = hashMapOf<String, WebNode>()

    private val divsContainer:WebDivsContainer

    init {
        _node = WebUiLibraryAdapter.get().createBorderContainer {
            fit = true
        }
        _node.setNorthRegion {
            content = generalEditor
        }
        generalEditor.typeWidget.configure(GeneralSelectBoxConfigurationJS().apply {
            possibleValues.addAll(itemsTypes)
        })
        divsContainer = WebUiLibraryAdapter.get().createDivsContainer {
            width ="100%"
            height = "100%"
        }
        generalEditor.typeWidget.setChangeListener {si ->
            si?.let {
                WebOptionsHandler.get().ensureOptionLoaded(OptionsIds.standard_workspace_elements_handlers, it.id)
                editorsCache.getOrPut(it.id){
                    generalEditor.uidValue = MiscUtilsJS.createUUID()
                    val handler = RegistryJS.get().get(WorkspaceItemVariantHandler.TYPE, it.id)!!
                    val editor  = handler.createEditor()
                    divsContainer.addDiv(it.id, editor)
                    divsContainer.show(it.id)
                    editor
                }
            }
        }

        _node.setCenterRegion {
            content = divsContainer
        }
    }

    internal fun setData(item: BaseWorkspaceItemDTJS){
        val qualifiedName = ReflectionFactoryJS.get().getQualifiedClassName(item::class)
        generalEditor.uidValue = item.uid
        generalEditor.nameWidget.setValue(item.displayName)
        generalEditor.typeWidget.setValue(itemsTypes.find { it.id == qualifiedName})
        val handler = RegistryJS.get().get(WorkspaceItemVariantHandler.TYPE, qualifiedName)!!
        val edt = editorsCache.getOrPut(qualifiedName){
            val editor  = handler.createEditor()
            divsContainer.addDiv(qualifiedName, editor)
            editor
        }
        divsContainer.show(qualifiedName)
        handler.setData(edt, item)
    }

    internal fun getData():BaseWorkspaceItemDTJS{
        val activeId = divsContainer.getActiveDivId()!!
        val handler = RegistryJS.get().get(WorkspaceItemVariantHandler.TYPE, activeId)!!
        val result = handler.getData(editorsCache.get(activeId)!!)
        result.uid = generalEditor.uidValue
        result.displayName = generalEditor.nameWidget.getValue()
        return result
    }
}

interface WorkspaceItemVariantHandler<M:BaseWorkspaceItemDTJS, W:WebNode>: RegistryItemJS<WorkspaceItemVariantHandler<BaseWorkspaceItemDTJS, WebNode>> {

    fun getModelClass():KClass<M>

    fun createEditor():W

    fun setData(editor:W, data:M)

    fun getData(editor:W):M

    override fun getId(): String {
        return ReflectionFactoryJS.get().getQualifiedClassName(getModelClass())
    }
    override fun getType(): RegistryItemTypeJS<WorkspaceItemVariantHandler<BaseWorkspaceItemDTJS, WebNode>> {
        return TYPE
    }

    companion object{
        val TYPE = RegistryItemTypeJS<WorkspaceItemVariantHandler<BaseWorkspaceItemDTJS, WebNode>>("workspace-item-variant-handlers")
    }
}