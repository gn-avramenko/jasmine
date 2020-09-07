/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.core.model.l10n

import com.gridnine.jasmine.web.core.application.EnvironmentJS


class L10nMetaRegistryJS{
    val messages = linkedMapOf<String, Map<String, String>>()

    companion object {
        fun get(): L10nMetaRegistryJS {
            return EnvironmentJS.getPublished(L10nMetaRegistryJS::class)
        }
    }
}


