/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused", "RemoveRedundantQualifierName", "UNCHECKED_CAST", "MemberVisibilityCanBePrivate", "RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.rest

import com.gridnine.jasmine.server.sandbox.model.domain.SandboxComplexDocumentIndex
import com.gridnine.jasmine.server.sandbox.model.domain.SandboxUserAccountIndex
import com.gridnine.jasmine.server.standard.model.rest.ListWorkspaceItemDT
import com.gridnine.jasmine.server.standard.model.rest.WorkspaceDT
import com.gridnine.jasmine.server.standard.model.rest.WorkspaceGroupDT
import com.gridnine.jasmine.server.standard.rest.WorkspaceProvider

class SandboxWorkspaceProvider : WorkspaceProvider{
    override fun getWorkspace(): WorkspaceDT {
        val result = WorkspaceDT()
        run{
            val group = WorkspaceGroupDT()
            group.displayName = "Настройки"
            val item = ListWorkspaceItemDT()
            item.columns.add(SandboxUserAccountIndex.login.name)
            item.columns.add(SandboxUserAccountIndex.name.name)
            item.listId="com.gridnine.jasmine.server.sandbox.model.ui.SandboxUserAccountList"
            item.displayName = "Профили"
            group.items.add(item)
            result.groups.add(group)
        }
        run{
            val group = WorkspaceGroupDT()
            group.displayName = "Объекты"
            val item = ListWorkspaceItemDT()
            item.columns.add(SandboxComplexDocumentIndex.stringProperty.name)
            item.columns.add(SandboxComplexDocumentIndex.enumProperty.name)
            item.columns.add(SandboxComplexDocumentIndex.booleanProperty.name)
            item.columns.add(SandboxComplexDocumentIndex.dateProperty.name)
            item.columns.add(SandboxComplexDocumentIndex.dateTimeProperty.name)
            item.columns.add(SandboxComplexDocumentIndex.floatProperty.name)
            item.listId="com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentList"
            item.displayName = "Сложные объекты"
            group.items.add(item)
            result.groups.add(group)
        }
        return result
    }

}