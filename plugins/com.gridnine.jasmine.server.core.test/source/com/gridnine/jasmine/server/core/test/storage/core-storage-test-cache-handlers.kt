/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.test.storage

import com.gridnine.jasmine.server.core.storage.cache.CacheConfiguration
import com.gridnine.jasmine.server.core.test.domain.TestDomainAsset
import com.gridnine.jasmine.server.core.test.domain.TestDomainDocument
import com.gridnine.jasmine.server.core.test.domain.TestDomainDocumentIndex
import kotlin.reflect.KClass


class TestDomainDocumentIndexStringPropertyCacheHandler:CacheConfiguration.CachedPropertyHandler<TestDomainDocument>{
    override fun getIndexClass(): KClass<*> {
        return TestDomainDocumentIndex::class
    }

    override fun getPropertyName(): String {
        return TestDomainDocumentIndex.stringPropertyProperty.name
    }

    override fun getIdentityClass(): KClass<TestDomainDocument> {
        return TestDomainDocument::class
    }

    override fun getValue(obj: TestDomainDocument): Any? {
        return obj.stringProperty
    }
}

class TestDomainAssetStringPropertyCacheHandler:CacheConfiguration.AssetCachedPropertyHandler<TestDomainAsset>(TestDomainAsset::class, TestDomainAsset.stringPropertyProperty.name)

