/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jasmine.server.standard.test

import com.gridnine.jasmine.common.core.app.Environment
import com.gridnine.jasmine.common.core.meta.DomainMetaRegistry
import com.gridnine.jasmine.common.core.parser.DomainMetadataParser
import com.gridnine.jasmine.server.core.test.storage.StorageTestBase
import com.gridnine.jasmine.server.standard.model.SequenceNumberGenerator

abstract class StandardServerTestBase:StorageTestBase() {
    override fun setUp() {
        super.setUp()
        Environment.publish(SequenceNumberGenerator())
    }
    override fun registerDomainMetadata(result: DomainMetaRegistry) {
        DomainMetadataParser.updateDomainMetaRegistry(result, "com/gridnine/jasmine/common/standard/model/standard-model-domain.xml", this::class.java.classLoader)

    }
}