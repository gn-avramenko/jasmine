/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")
package com.gridnine.jasmine.server.core.model.domain

import com.gridnine.jasmine.server.core.model.common.Xeption

interface CachedObject{
    var allowChanges:Boolean
    companion object{
        const val allowChanges = "allowChanges"
    }
}

class ReadOnlyArrayList<T>:ArrayList<T>(){
    var allowChanges = false
    override fun add(element: T): Boolean {
        if(!allowChanges){
            throw Xeption.forDeveloper("changes disallowed")
        }
        return super.add(element)
    }

    override fun clear() {
        if(!allowChanges){
            throw Xeption.forDeveloper("changes disallowed")
        }
        super.clear()
    }

    override fun addAll(elements: Collection<T>): Boolean {
        if(!allowChanges){
            throw Xeption.forDeveloper("changes disallowed")
        }
        return super.addAll(elements)
    }

    override fun add(index: Int, element: T) {
        if(!allowChanges){
            throw Xeption.forDeveloper("changes disallowed")
        }
        super.add(index, element)
    }

    override fun remove(element: T): Boolean {
        if(!allowChanges){
            throw Xeption.forDeveloper("changes disallowed")
        }
        return super.remove(element)
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        if(!allowChanges){
            throw Xeption.forDeveloper("changes disallowed")
        }
        return super.removeAll(elements)
    }

    override fun removeAt(index: Int): T {
        if(!allowChanges){
            throw Xeption.forDeveloper("changes disallowed")
        }
        return super.removeAt(index)
    }

}

