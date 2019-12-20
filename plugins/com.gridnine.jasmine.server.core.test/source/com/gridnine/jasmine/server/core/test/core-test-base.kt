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
import org.junit.After
import org.junit.Before
import org.slf4j.LoggerFactory
import java.io.File
import java.util.*


abstract class TestBase {


    private val log = LoggerFactory.getLogger(TestBase::class.java)

    private val appHome = File(
            String.format("./test/app-home/%s/", UUID.randomUUID().toString()))

    init {
        setSysProp("org.apache.commons.logging.Log",
                "org.apache.commons.logging.impl.SimpleLog")
        setSysProp("org.apache.commons.logging.simplelog.defaultlog", "info")
        setSysProp("org.apache.commons.logging.simplelog.showlogname", "true")
        setSysProp("org.apache.commons.logging.simplelog.showShortLogname",
                "true")
        setSysProp("org.apache.commons.logging.simplelog.showdatetime", "true")
        // per package logging configuration
        setSysProp("org.apache.commons.logging.simplelog.log.com.gridnine",
                "debug")
        log.info("logging system initialized")
    }

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



    protected fun setSysProp(name: String, value: String) {
        if (System.getProperty(name) == null) {
            System.setProperty(name, value)
        }
    }
}


abstract class CoreTestBase : TestBase() {

    override fun setUp() {
        super.setUp()
        publishDomainMetadataProvider()
        publishRestMetadataProvider()
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
