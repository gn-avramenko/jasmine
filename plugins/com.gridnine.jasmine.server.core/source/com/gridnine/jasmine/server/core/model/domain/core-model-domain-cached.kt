/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")
package com.gridnine.jasmine.server.core.model.domain

import com.gridnine.jasmine.server.core.model.common.BaseIdentity
import com.gridnine.jasmine.server.core.model.common.Xeption
import kotlin.reflect.KClass

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

open class _CachedObjectReference<D : BaseIdentity>: ObjectReference<D>(), CachedObject {
    override var allowChanges = false

    override var caption: String? = null

    override lateinit var type: KClass<D>

    override var uid: String? = null

    override fun getValue(propertyName: String): Any? {
        if(ObjectReference.type == propertyName){
            return type
        }
        if(ObjectReference.caption == propertyName){
            return caption
        }
        if(BaseIdentity.uid == propertyName){
            return uid
        }
        return super.getValue(propertyName)
    }

    override fun setValue(propertyName: String, value: Any?) {
        if(ObjectReference.type == propertyName){
            type = value as KClass<D>
            return
        }
        if(ObjectReference.caption == propertyName){
            caption = value as String?
            return
        }
        if(BaseIdentity.uid == propertyName){
            uid = value as String?
            return
        }
        super.setValue(propertyName, value)
    }
}
