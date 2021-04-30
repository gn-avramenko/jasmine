/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui

import com.gridnine.jasmine.web.core.common.EnvironmentJS

interface WebUiLibraryAdapter{

    fun showLoader()

    fun hideLoader()

    companion object{
        fun get() = EnvironmentJS.getPublished(WebUiLibraryAdapter::class)
    }
}