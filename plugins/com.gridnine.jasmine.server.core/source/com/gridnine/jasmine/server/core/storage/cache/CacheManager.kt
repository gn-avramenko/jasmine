/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.storage.cache

import com.gridnine.jasmine.server.core.app.Disposable
import com.gridnine.jasmine.server.core.app.PublishableWrapper
import com.gridnine.jasmine.server.core.model.common.BaseIdentity
import com.gridnine.jasmine.server.core.model.domain.ObjectReference
import kotlin.reflect.KClass

interface CacheManager :Disposable {

    override fun dispose() {
        wrapper.dispose()
    }

    fun <D:Any> getOrCreateResolveCache(cls: KClass<D>): KeyValueCache<D>

    fun <E:BaseIdentity> getOrCreateFindCache(cls: KClass<*>, fieldName:String): KeyValueCache<ObjectReference<E>>

    companion object {
        private val wrapper = PublishableWrapper(CacheManager::class)
        fun get() = wrapper.get()
    }

}

data class CachedValue<D>(val timeStamp:Long,val value:D?){
    override fun equals(other: Any?): Boolean {
        if(other is CachedValue<*>){
            return timeStamp == other.timeStamp
        }
        return false
    }

    override fun hashCode(): Int {
        return timeStamp.hashCode()
    }
}

interface KeyValueCache<D:Any>{
    fun get(key:String):CachedValue<D>?
    fun put(key:String, value: CachedValue<D>)
    fun replace(key:String, oldValue:CachedValue<D>?, newValue:CachedValue<D>)
}