/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.common.reports.activator

import com.gridnine.jasmine.common.core.app.IPluginActivator
import com.gridnine.jasmine.common.core.meta.DomainMetaRegistry
import com.gridnine.jasmine.common.core.meta.MiscMetaRegistry
import com.gridnine.jasmine.common.core.meta.RestMetaRegistry
import com.gridnine.jasmine.common.core.parser.DomainMetadataParser
import com.gridnine.jasmine.common.core.parser.MiscMetadataParser
import com.gridnine.jasmine.common.core.parser.RestMetadataParser
import com.gridnine.jasmine.common.reports.WebPluginsAssociations
import java.util.*

class CommonReportsActivator :IPluginActivator{
    override fun configure(config: Properties) {
        DomainMetadataParser.updateDomainMetaRegistry(DomainMetaRegistry.get(), "com/gridnine/jasmine/common/reports/model/reports-domain.xml", javaClass.classLoader)
        MiscMetadataParser.updateMiscMetaRegistry(MiscMetaRegistry.get(), "com/gridnine/jasmine/common/reports/model/reports-misc.xml", javaClass.classLoader)
        RestMetadataParser.updateRestMetaRegistry(RestMetaRegistry.get(), "com/gridnine/jasmine/common/reports/model/reports-rest.xml", javaClass.classLoader)
        WebPluginsAssociations.registerAssociations()
    }
}