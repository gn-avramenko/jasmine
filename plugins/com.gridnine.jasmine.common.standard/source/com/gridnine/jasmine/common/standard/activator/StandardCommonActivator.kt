/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.common.standard.activator

import com.gridnine.jasmine.common.core.app.IPluginActivator
import com.gridnine.jasmine.common.core.meta.DomainMetaRegistry
import com.gridnine.jasmine.common.core.meta.L10nMetaRegistry
import com.gridnine.jasmine.common.core.meta.RestMetaRegistry
import com.gridnine.jasmine.common.core.meta.UiMetaRegistry
import com.gridnine.jasmine.common.core.parser.DomainMetadataParser
import com.gridnine.jasmine.common.core.parser.L10nMetadataParser
import com.gridnine.jasmine.common.core.parser.RestMetadataParser
import com.gridnine.jasmine.common.core.parser.UiMetadataParser
import java.util.*

class StandardCommonActivator :IPluginActivator{
    override fun configure(config: Properties) {
        L10nMetadataParser.updateL10nMessages(L10nMetaRegistry.get(), "com/gridnine/jasmine/common/standard/model/standard-l10n.xml", javaClass.classLoader)
        DomainMetadataParser.updateDomainMetaRegistry(DomainMetaRegistry.get(), "com/gridnine/jasmine/common/standard/model/standard-model-domain.xml", javaClass.classLoader)
        UiMetadataParser.updateUiMetaRegistry(UiMetaRegistry.get(), "com/gridnine/jasmine/common/standard/model/standard-ui.xml", javaClass.classLoader)
        RestMetadataParser.updateRestMetaRegistry(RestMetaRegistry.get(), "com/gridnine/jasmine/common/standard/model/standard-rest.xml", javaClass.classLoader)
    }

}