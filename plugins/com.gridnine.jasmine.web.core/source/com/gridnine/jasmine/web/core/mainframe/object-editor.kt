/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.mainframe

import com.gridnine.jasmine.server.core.model.domain.ObjectReferenceJS
import com.gridnine.jasmine.server.standard.model.rest.GetEditorDataRequestJS
import com.gridnine.jasmine.server.standard.model.rest.GetEditorDataResponseJS
import com.gridnine.jasmine.web.core.StandardRestClient
import com.gridnine.jasmine.web.core.ui.UiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.WebComponent
import kotlin.js.Promise

class ObjectEditorTabHandler:MainFrameTabHandler<ObjectReferenceJS, GetEditorDataResponseJS>{
    override fun getTabId(obj: ObjectReferenceJS): String {
        return "${obj.type}||${obj.uid}"
    }

    override fun loadData(obj: ObjectReferenceJS): Promise<GetEditorDataResponseJS> {
        val request = GetEditorDataRequestJS()
        request.objectId = obj.type
        request.objectUid = obj.uid
      return StandardRestClient.standard_standard_getEditorData(request)
    }

    override fun createTabData(obj: ObjectReferenceJS, data: GetEditorDataResponseJS, parent: WebComponent, callback: MainFrameTabCallback): MainFrameTabData {
        val label = UiLibraryAdapter.get().createLabel(parent)
        label.setText("test label")
        return MainFrameTabData(data.title, label)
    }

}