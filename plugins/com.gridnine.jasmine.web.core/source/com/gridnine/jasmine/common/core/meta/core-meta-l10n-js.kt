/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.common.core.meta

import com.gridnine.jasmine.web.core.common.EnvironmentJS


class L10nMetaRegistryJS{
    val messages = linkedMapOf<String, Map<String, String>>()

    companion object {
        fun get() = EnvironmentJS.getPublished(L10nMetaRegistryJS::class)
    }
}


