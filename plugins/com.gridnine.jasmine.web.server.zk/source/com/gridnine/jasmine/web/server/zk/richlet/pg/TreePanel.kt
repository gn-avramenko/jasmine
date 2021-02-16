/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg

class TreePanel : ZkServerUiTree(createConfiguration()){
    init {
        val group1 = createGroup("group1", "Группа 1")
        val group2 = createGroup("group2", "Группа 2")
        setData(arrayListOf(group1, group2))
        setSelectListener {
            println("selected ${it.id}")
        }
        setOnContextMenuListener { node, event ->
            
        }
        setOnDropListener { target, source ->
            remove(source.id)
            insertBefore(source, target.id)
        }
    }

    private fun createGroup(groupId: String, groupTitle: String): ServerUiTreeItem {
        val result = ServerUiTreeItem(groupId, groupTitle, null)
        for(n in 0..5){
            result.children.add(createItem("${groupId}-$n", "Элемент ${groupId}-$n"))
        }
        return result
    }

    private fun createItem(itemId: String, itemTitle: String): ServerUiTreeItem {
        return ServerUiTreeItem(itemId, itemTitle, null)
    }

    companion object{
        private fun createConfiguration(): ServerUiTreeConfiguration {
            val result = ServerUiTreeConfiguration()
            result.width = "300px"
            result.height = "100%"
            result.enableDnd = true
            return result
        }
    }
}


