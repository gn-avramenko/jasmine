/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.server.core.test

import com.gridnine.jasmine.server.core.app.Environment
import com.gridnine.jasmine.server.core.model.domain.DomainMetaRegistry
import com.gridnine.jasmine.server.core.model.domain.DomainMetadataParser
import com.gridnine.jasmine.server.core.model.rest.RestMetaRegistry
import com.gridnine.jasmine.server.core.model.rest.RestMetadataParser
import com.gridnine.jasmine.server.core.reflection.ReflectionFactory
import com.gridnine.jasmine.server.core.serialization.JsonSerializer
import com.gridnine.jasmine.server.core.storage.cache.CachedObjectsConverter
import org.junit.After
import org.junit.Before
import org.slf4j.LoggerFactory
import java.io.File
import java.util.*


abstract class TestBase {


    private val log = LoggerFactory.getLogger(TestBase::class.java)

    private val appHome = File(
            String.format("./test/app-home/%s/", UUID.randomUUID().toString()))

    @Before
    open fun setUp() {
        setUpEnvironment()
    }

    @After
    open fun tearDown() {
        Environment.dispose()
    }

    @After
    fun dropAppHome() {
        appHome.deleteRecursively()
    }

    protected fun setUpEnvironment() {

        if (appHome.exists()) {
            appHome.deleteRecursively()
        }
        appHome.mkdirs()
        Environment.configure(appHome)
    }
}


abstract class CoreTestBase : TestBase() {

    override fun setUp() {
        super.setUp()
        publishDomainMetadataProvider()
        publishRestMetadataProvider()
        publishReflectionFactory()
        publishSerializer()
        publishCachedObjectsConverter()
    }

    private fun publishCachedObjectsConverter() {
        Environment.publish(CachedObjectsConverter())
    }

    private fun publishSerializer() {
        Environment.publish(JsonSerializer())
    }

    protected fun publishReflectionFactory() {
        Environment.publish(ReflectionFactory())
    }

    private fun publishRestMetadataProvider() {
        val result = RestMetaRegistry()
        registerRestMetadata(result)
        Environment.publish(result)

    }

    protected fun registerRestMetadata(result: RestMetaRegistry){
        RestMetadataParser.updateRestMetaRegistry(result, "com/gridnine/jasmine/server/core/test/model/core-test-model-rest.xml", this::class.java.classLoader)
    }

    private fun publishDomainMetadataProvider() {
        val result = DomainMetaRegistry()
        registerDomainMetadata(result)
        Environment.publish(result)

    }

    protected fun registerDomainMetadata(result: DomainMetaRegistry) {
        DomainMetadataParser.updateDomainMetaRegistry(result, "com/gridnine/jasmine/server/core/test/model/core-test-model-domain.xml", this::class.java.classLoader)
    }




}
