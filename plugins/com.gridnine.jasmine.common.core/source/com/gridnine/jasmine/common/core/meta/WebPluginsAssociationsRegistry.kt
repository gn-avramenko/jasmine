/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.common.core.meta

import com.gridnine.jasmine.common.core.app.PublishableWrapper

class WebPluginsAssociationsRegistry {

    val associations = hashMapOf<String,String>()

    val links = hashMapOf<String,String>()

    companion object {
        private val wrapper = PublishableWrapper(WebPluginsAssociationsRegistry::class)
        fun get() = wrapper.get()
    }
}