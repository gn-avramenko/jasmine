/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.common.standard.activator

import com.gridnine.jasmine.common.core.app.IPluginActivator
import com.gridnine.jasmine.common.core.app.Registry
import com.gridnine.jasmine.common.core.meta.*
import com.gridnine.jasmine.common.core.parser.*
import com.gridnine.jasmine.common.core.storage.DynamicCriterionCondition
import com.gridnine.jasmine.common.standard.WebPluginsAssociations
import com.gridnine.jasmine.common.standard.model.domain.StandardDynamicCriterionCondition
import com.gridnine.jasmine.common.standard.storage.TodayDynamicCriterionHandler
import java.util.*

class StandardCommonActivator :IPluginActivator{
    override fun configure(config: Properties) {
        L10nMetadataParser.updateL10nMessages(L10nMetaRegistry.get(), "com/gridnine/jasmine/common/standard/model/standard-l10n.xml", javaClass.classLoader)
        DomainMetadataParser.updateDomainMetaRegistry(DomainMetaRegistry.get(), "com/gridnine/jasmine/common/standard/model/standard-model-domain.xml", javaClass.classLoader)
        UiMetadataParser.updateUiMetaRegistry(UiMetaRegistry.get(), "com/gridnine/jasmine/common/standard/model/standard-ui.xml", javaClass.classLoader)
        RestMetadataParser.updateRestMetaRegistry(RestMetaRegistry.get(), "com/gridnine/jasmine/common/standard/model/core-rest.xml", javaClass.classLoader)
        RestMetadataParser.updateRestMetaRegistry(RestMetaRegistry.get(), "com/gridnine/jasmine/common/standard/model/standard-rest.xml", javaClass.classLoader)
        WebMessagesMetadataParser.updateWebMessages(WebMessagesMetaRegistry.get(), "com/gridnine/jasmine/common/standard/model/standard-web-messages.xml", javaClass.classLoader)
        Registry.get().register(TodayDynamicCriterionHandler())
        StandardDynamicCriterionCondition.values().forEach {cond ->
            Registry.get().register(object :DynamicCriterionCondition{
                override fun getDisplayName(): String {
                    return cond.toString()
                }

                override fun getId(): String {
                    return cond.name
                }
            })
        }
        WebPluginsAssociations.registerAssociations()
    }

}