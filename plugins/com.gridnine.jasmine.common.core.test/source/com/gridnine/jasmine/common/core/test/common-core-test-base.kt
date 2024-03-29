/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("UseExpressionBody")

package com.gridnine.jasmine.common.core.test

import com.gridnine.jasmine.common.core.app.ConfigurationProvider
import com.gridnine.jasmine.common.core.app.Environment
import com.gridnine.jasmine.common.core.lock.LockManager
import com.gridnine.jasmine.common.core.lock.StandardLockManager
import com.gridnine.jasmine.common.core.meta.*
import com.gridnine.jasmine.common.core.parser.CustomMetadataParser
import com.gridnine.jasmine.common.core.parser.DomainMetadataParser
import com.gridnine.jasmine.common.core.parser.RestMetadataParser
import com.gridnine.jasmine.common.core.reflection.ReflectionFactory
import com.gridnine.jasmine.common.core.serialization.SerializationProvider
import com.gridnine.jasmine.common.core.storage.CachedObjectsConverter
import com.gridnine.jasmine.common.core.utils.TextUtils
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.slf4j.LoggerFactory
import java.io.File
import java.math.BigDecimal


abstract class TestBase {


    val log = LoggerFactory.getLogger(this::class.java)

    private val appHome = File(
            String.format("./test/app-home/%s/", TextUtils.generateUid()))

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
        Environment.test = true
    }

    protected fun getContent(relativePath:String):ByteArray?{
        val url = javaClass.getResource(relativePath)?: return null
        return url.openStream().use{
            it.readAllBytes()
        }
    }

    protected fun assertEquals(expected:Number, actual:BigDecimal){
        Assert.assertEquals(expected.toDouble(), actual.toDouble(), 0.00000001)
    }
}


@Suppress("UNUSED_PARAMETER")
abstract class CommonCoreTestBase : TestBase() {

    override fun setUp() {
        super.setUp()
        publishConfigurationProvider()
        publishDomainMetadataProvider()
        publishMiscMetadataProvider()
        publishRestMetadataProvider()
        publishCustomMetaRegistry()
        publishReflectionFactory()
        publishSerializer()
        publishCachedObjectsConverter()
        publishLockManager()
        publishL10nRegistry()
    }

    protected fun publishL10nRegistry() {
        Environment.publish(L10nMetaRegistry())
    }

    protected fun publishConfigurationProvider() {
        Environment.publish(ConfigurationProvider::class, object:ConfigurationProvider{
            override fun getProperty(propertyName: String): String? {
                return System.getProperty(propertyName)
            }
        })
    }

    protected fun publishLockManager() {
        Environment.publish(LockManager::class, StandardLockManager())
    }


    private fun publishCustomMetaRegistry() {
        val result = CustomMetaRegistry()
        registerCustomMetadata(result)
        Environment.publish(result)
    }

    protected fun registerCustomMetadata(result: CustomMetaRegistry){
        CustomMetadataParser.updateCustomMetaRegistry(result, "com/gridnine/jasmine/common/core/meta/core-custom.xml", this::class.java.classLoader)
    }

    private fun publishCachedObjectsConverter() {
        Environment.publish(CachedObjectsConverter())
    }

    private fun publishSerializer() {
        Environment.publish(SerializationProvider())
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
        RestMetadataParser.updateRestMetaRegistry(result, "com/gridnine/jasmine/common/core/test/model/core-test-model-rest.xml", this::class.java.classLoader)
    }

    private fun publishDomainMetadataProvider() {
        val result = DomainMetaRegistry()
        registerDomainMetadata(result)
        Environment.publish(result)

    }

    private fun publishMiscMetadataProvider() {
        val result = MiscMetaRegistry()
        registerMiscMetadata(result)
        Environment.publish(result)

    }

    protected fun registerMiscMetadata(result: MiscMetaRegistry) {
    }

    protected open fun registerDomainMetadata(result: DomainMetaRegistry) {
        DomainMetadataParser.updateDomainMetaRegistry(result, "com/gridnine/jasmine/common/core/test/model/core-test-model-domain.xml", this::class.java.classLoader)
    }




}
