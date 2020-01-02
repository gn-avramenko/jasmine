/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UnsafeCastFromDynamic", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.core.ui

import com.gridnine.jasmine.web.core.application.EnvironmentJS
import com.gridnine.jasmine.web.core.model.ui.BaseVMEntityJS
import com.gridnine.jasmine.web.core.model.ui.BaseVSEntityJS
import com.gridnine.jasmine.web.core.model.ui.BaseVVEntityJS
import com.gridnine.jasmine.web.core.model.ui.BaseView

interface ErrorHandler{
    fun showError(msg:String, stacktrace:String)
    companion object{
        fun get() = EnvironmentJS.getPublished(ErrorHandler::class)
    }
}

class Dialog<VM:BaseVMEntityJS, VS:BaseVSEntityJS, VV:BaseVVEntityJS,V:BaseView<VM,VS,VV>>{
    lateinit var view:V
    var editorView:BaseView<*,*,*>? = null
    lateinit var close:()->Unit
}

interface UiFactory{
    fun<VM:BaseVMEntityJS, VS:BaseVSEntityJS, VV:BaseVVEntityJS,V:BaseView<VM,VS,VV>> showDialog(dialogId:String,model:VM, settings:VS):Dialog<VM,VS,VV,V>
    companion object{
        fun get() = EnvironmentJS.getPublished(UiFactory::class)
    }

}