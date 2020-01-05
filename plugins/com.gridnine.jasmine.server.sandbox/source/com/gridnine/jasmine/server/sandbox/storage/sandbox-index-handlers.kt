/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused", "RemoveRedundantQualifierName", "UNCHECKED_CAST", "MemberVisibilityCanBePrivate", "RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.storage

import com.gridnine.jasmine.server.core.storage.IndexHandler
import com.gridnine.jasmine.server.sandbox.model.domain.SandboxComplexDocument
import com.gridnine.jasmine.server.sandbox.model.domain.SandboxComplexDocumentIndex
import com.gridnine.jasmine.server.sandbox.model.domain.SandboxUserAccount
import com.gridnine.jasmine.server.sandbox.model.domain.SandboxUserAccountIndex

class SandboxUserAccountIndexHandler:IndexHandler<SandboxUserAccount, SandboxUserAccountIndex>{
    override val documentClass = SandboxUserAccount::class
    override val indexClass = SandboxUserAccountIndex::class
    override fun createIndexes(doc: SandboxUserAccount): List<SandboxUserAccountIndex> {
        val idx = SandboxUserAccountIndex()
        idx.login = doc.login
        idx.name = doc.name
        return arrayListOf(idx)
    }
}

class SandboxComplexDocumentIndexHandler:IndexHandler<SandboxComplexDocument, SandboxComplexDocumentIndex>{
    override val documentClass = SandboxComplexDocument::class
    override val indexClass = SandboxComplexDocumentIndex::class
    override fun createIndexes(doc: SandboxComplexDocument): List<SandboxComplexDocumentIndex> {
        val idx = SandboxComplexDocumentIndex()
        idx.booleanProperty = doc.booleanProperty
        idx.dateProperty = doc.dateProperty
        idx.dateTimeProperty = doc.dateTimeProperty
        idx.entityRefProperty = doc.entityRefProperty
        idx.enumProperty = doc.enumProperty
        idx.floatProperty = doc.floatProperty
        idx.stringProperty = doc.stringProperty
        idx.integerProperty = doc.integerProperty
        return arrayListOf(idx)
    }
}
