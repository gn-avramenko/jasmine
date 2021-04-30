/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.common.core.meta

import com.gridnine.jasmine.common.core.app.Disposable
import com.gridnine.jasmine.common.core.app.PublishableWrapper


class WebMessageDescription(id:String) : BaseModelElementDescription(id)

class WebMessagesBundleDescription(val id:String){
    val messages = linkedMapOf<String, WebMessageDescription>()
}

class WebMessagesMetaRegistry: Disposable {
    val bundles = linkedMapOf<String, WebMessagesBundleDescription>()

    override fun dispose() {
        wrapper.dispose()
    }
    companion object {
        private val wrapper = PublishableWrapper(WebMessagesMetaRegistry::class)
        fun get() = wrapper.get()
    }
}



