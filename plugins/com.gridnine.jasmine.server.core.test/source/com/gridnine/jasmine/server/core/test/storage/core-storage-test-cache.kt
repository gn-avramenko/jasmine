/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("MISPLACED_TYPE_PARAMETER_CONSTRAINTS")

package com.gridnine.jasmine.server.core.test.storage

import com.gridnine.jasmine.server.core.model.domain.BaseAsset
import com.gridnine.jasmine.server.core.storage.Storage
import com.gridnine.jasmine.server.core.storage.StorageAdvice
import com.gridnine.jasmine.server.core.storage.StorageRegistry
import com.gridnine.jasmine.server.core.storage.search.EqualitySupport
import com.gridnine.jasmine.server.core.storage.search.PropertyNameSupport
import com.gridnine.jasmine.server.core.test.domain.TestDomainAsset
import org.junit.Assert
import org.junit.Test
import kotlin.reflect.KClass


class CacheTest:StorageTestBase(){

    lateinit var advice:OperationCountAdvice
    override fun setUp() {
        super.setUp()
        advice = OperationCountAdvice(2.0)
        StorageRegistry.register(advice)
    }

    @Test
    fun testInterceptor() {
        var asset = TestDomainAsset()
        asset.uid = "uid"
        asset.stringProperty = "test"
        Storage.get().saveAsset(asset)
        Assert.assertEquals(0, advice.loadCount)
        Storage.get().loadAsset(TestDomainAsset::class, "uid")
        Assert.assertEquals(1, advice.loadCount)
        Storage.get().loadAsset(TestDomainAsset::class, "uid")
        Assert.assertEquals(1, advice.loadCount)
        Storage.get().findUniqueAsset(TestDomainAsset::class,
                TestDomainAsset.stringProperty, "test")!!
        Assert.assertEquals(1, advice.findCount)
        asset = Storage.get().findUniqueAsset(TestDomainAsset::class,
                TestDomainAsset.stringProperty, "test")!!
        Assert.assertEquals(1, advice.findCount)
        Storage.get().saveAsset(asset)
        Storage.get().findUniqueAsset(TestDomainAsset::class,
                TestDomainAsset.stringProperty, "test")!!
        Assert.assertEquals(2, advice.findCount)
    }


    class OperationCountAdvice(override val priority: Double) :StorageAdvice{

        var loadCount = 0
        var findCount = 0

        override fun <D : BaseAsset> onLoadAsset(cls: KClass<D>, uid: String, callback: (cls: KClass<D>, uid: String) -> D?): D? {
            loadCount++
            return callback.invoke(cls, uid)
        }

        override fun <D : BaseAsset, E:PropertyNameSupport> onFindUniqueAsset(index: KClass<D>, propertyName: E, propertyValue: Any?, callback: (index: KClass<D>, propertyName: E, propertyValue: Any?) -> D?): D? where E : EqualitySupport{
            findCount++
            return callback.invoke(index, propertyName, propertyValue)
        }

    }
}