/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.standard.activator

import com.gridnine.jasmine.common.core.model.XeptionJS
import com.gridnine.jasmine.common.core.model.XeptionTypeJS
import com.gridnine.jasmine.web.core.remote.CoroutineExceptionHandler
import com.gridnine.jasmine.web.core.remote.RpcError
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.BaseWebNodeWrapper
import com.gridnine.jasmine.web.core.ui.components.WebTag
import kotlinx.browser.window

class StandardCoroutineExceptionHandler:CoroutineExceptionHandler{
    override fun handleException(e: Throwable) {
        console.log(e)
        val exceptionText = getExceptionText(e)
        try {
            WebUiLibraryAdapter.get().showDialog(ExceptionDialogPanel(exceptionText)) {
                title = "Произошла ошибка"
                button {
                    displayName = "Копировать"
                    handler = {dialog->
                        val textArea = window.document.getElementById("exception-dialog-textarea").asDynamic()
                        try {
                            textArea.focus()
                            textArea.select()
                            window.document.asDynamic().execCommand("copy")
                            dialog.close()
                        }catch (t:Throwable){
                            console.log(t)
                            dialog.close()
                        }
                    }
                }
            }
        } catch (t:Throwable){
            console.log(t)
        }
    }

    private fun getExceptionText(e: Throwable): String {
        if(e is XeptionJS){
            return when(e.type){
                XeptionTypeJS.FOR_END_USER -> e.userMessage?:""
                XeptionTypeJS.FOR_ADMIN -> """
Для администратора: ${e.adminMessage?:""}
Детали:
${e.stackTraceToString()}
""".trimMargin()
                XeptionTypeJS.FOR_DEVELOPER -> """
Для разработчика: ${e.developerMessage?:""}
Детали:
${e.stackTraceToString()}
""".trimMargin()
            }
        }
        if(e is RpcError){
            val reason = e.reason
            if(reason.type != null){
                val type = XeptionTypeJS.valueOf(reason.type)
                return when(type){
                    XeptionTypeJS.FOR_END_USER -> reason.message?:""
                    XeptionTypeJS.FOR_ADMIN -> """
Для администратора: ${reason.message?:""}.
Детали: 
${reason.stacktrace?:""}
""".trimIndent()
                    XeptionTypeJS.FOR_DEVELOPER -> """
Для разработчика: ${reason.message?:""}.
Детали: 
${reason.stacktrace?:""}
""".trimIndent()
                }
            } else {
                return """
Для разработчика: ${reason.message?:""}.
Детали: 
${reason.stacktrace?:""}
""".trimIndent()
            }
        }
        return """
Для разработчика: неизвестная ошибка.
Детали: 
${e.stackTraceToString()}
""".trimIndent()
    }


}

internal class ExceptionDialogPanel(errorText:String) : BaseWebNodeWrapper<WebTag>(){
    internal  val textArea:WebTag
    init {
        _node = WebUiLibraryAdapter.get().createTag("div","exception-dialog").also {
            it.getStyle().setParameters("width" to "700px", "height" to "520px", "overflow-x" to "auto", "overflow-y" to "auto")
        }
        textArea= WebUiLibraryAdapter.get().createTag("textarea","exception-dialog-textarea").also {
            it.getStyle().setParameters("width" to "100%", "height" to "500px","resize" to "none", "border" to "none")
        }
        textArea.setText(errorText)
        _node.getChildren().addChild(textArea)
    }
}