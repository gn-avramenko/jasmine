/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UnsafeCastFromDynamic", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.core.ui

import com.gridnine.jasmine.web.core.application.EnvironmentJS
import com.gridnine.jasmine.web.core.model.common.BaseEntityJS
import com.gridnine.jasmine.web.core.model.ui.*
import kotlin.js.Promise

interface ErrorHandler{
    fun showError(msg:String, stacktrace:String)
    companion object{
        fun get() = EnvironmentJS.getPublished(ErrorHandler::class)
    }
}

open class Dialog<VM:BaseVMEntityJS, VS:BaseVSEntityJS, VV:BaseVVEntityJS,V:BaseView<VM,VS,VV>>{
    val properties = hashMapOf<String,Any>()
    val buttons = arrayListOf<DialogButtonWidget>()
    lateinit var view:V
    var editorView:BaseView<*,*,*>? = null
    lateinit var close:()->Unit
}

interface DialogButtonHandler<VM:BaseVMEntityJS, VS:BaseVSEntityJS, VV:BaseVVEntityJS,V:BaseView<VM,VS,VV>>{
    fun handle(dialog:Dialog<VM,VS,VV,V>)
}

interface TestableDialogButtonHandler<VM:BaseVMEntityJS, VS:BaseVSEntityJS, VV:BaseVVEntityJS,V:BaseView<VM,VS,VV>, T:Any>{
    fun handle(dialog:Dialog<VM,VS,VV,V>):Promise<T>
}

interface UiFactory{
    fun<VM:BaseVMEntityJS, VS:BaseVSEntityJS, VV:BaseVVEntityJS,V:BaseView<VM,VS,VV>,D:Dialog<VM,VS,VV,V>> showDialog(dialog:D, model:VM, settings:VS):D
    fun publishMainFrame()
    fun showConfirmDialog(question:String, handler:()->Unit)
    fun showNotification(message:String ,title:String?=null, timeout: Int=3000)
    companion object{
        fun get() = EnvironmentJS.getPublished(UiFactory::class)
    }

}


interface MainFrame{
    fun openTab(objectId: String, uid:String?, navigationKey:String?):Promise<Editor<*,*,*,*>>
    companion object{
        fun get()=EnvironmentJS.getPublished(MainFrame::class)
    }
}

interface MainFrameTool{
    val displayName:String
    val weight:Double
    fun handle(mainFrame: MainFrame)
}

class MainFrameConfiguration{
    var logoIconUrl:String? = null
    var logoText:String? = null
    var title:String? = null
    var showWorkspaceEditor:Boolean = true
    private val tools = arrayListOf<MainFrameTool>()
    fun addTool(tool:MainFrameTool){
        tools.add(tool)
        tools.sortBy { it.weight }
    }
    fun getTools() = tools

    companion object{
        fun get() = EnvironmentJS.getPublished(MainFrameConfiguration::class)
    }
}



