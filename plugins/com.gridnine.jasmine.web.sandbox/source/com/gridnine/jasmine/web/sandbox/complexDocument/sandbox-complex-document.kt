/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.sandbox.complexDocument

import com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentEditorVMJS
import com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentEditorVSJS
import com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentEditorVVJS
import com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentEditorView
import com.gridnine.jasmine.web.core.model.ui.ViewInterceptor

class SandboxComplexDocumentViewInterceptor:ViewInterceptor<SandboxComplexDocumentEditorVMJS,SandboxComplexDocumentEditorVSJS,SandboxComplexDocumentEditorVVJS,SandboxComplexDocumentEditorView>{
    override fun onCreate(view: SandboxComplexDocumentEditorView) {
        view.generalTile.compactView.stringProperty.valueChangeListener = {newValue:String?, _:String? ->
            view.generalTile.fullView.stringProperty.setData(newValue)
        }
    }

}
