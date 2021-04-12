/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.storage.cache

import com.gridnine.jasmine.common.core.model.BaseAsset
import com.gridnine.jasmine.common.core.model.BaseDocument
import com.gridnine.jasmine.common.core.model.BaseIdentity
import com.gridnine.jasmine.server.core.storage.OperationContext
import com.gridnine.jasmine.server.core.storage.StorageInterceptor
import kotlin.reflect.KClass

class InvalidateCacheStorageInterceptor(override val priority: Double, private val advice: CacheStorageAdvice) : StorageInterceptor{

    override fun <A : BaseAsset> onDelete(asset: A, context: OperationContext<A>) {
        onDeleteInternal(asset, context)
    }

    override fun <D : BaseDocument> onDelete(doc: D, context: OperationContext<D>) {
        onDeleteInternal(doc, context)
    }

    override fun <A : BaseAsset> onSave(asset: A, context: OperationContext<A>) {
        onSaveInternal(asset, context)
    }

    override fun <D : BaseDocument> onSave(doc: D, context: OperationContext<D>) {
        onSaveInternal(doc, context)
    }

    private fun <A:BaseIdentity> onSaveInternal(obj: A, context: OperationContext<A>) {
        if(CacheConfiguration.get().isCached(obj::class)){
            context.globalContext.transactionContext.postCommitCallbacks.add{
                advice.invalidateResolveCache(obj::class, obj.uid)
            }
        }
        CacheConfiguration.get().getCachedPropertyHandlers(obj::class as KClass<A>).forEach { handler  ->
            context.globalContext.transactionContext.postCommitCallbacks.add{
                val newValue = handler.getValue(obj)
                val oldObject = context.localContext.oldObject
                if(oldObject != null && newValue !=handler.getValue(oldObject)){
                    advice.invalidateFindCache(handler.getIndexClass(), handler.getPropertyName(), handler.getValue(oldObject))
                    advice.invalidateFindCache(handler.getIdentityClass(), handler.getPropertyName(), handler.getValue(obj))
                }
            }
        }

    }

    private fun <A:BaseIdentity> onDeleteInternal(obj: A, context: OperationContext<A>) {
        if(CacheConfiguration.get().isCached(obj::class)){
            context.globalContext.transactionContext.postCommitCallbacks.add{
                advice.invalidateResolveCache(obj::class, obj.uid)
            }
        }
        CacheConfiguration.get().getCachedPropertyHandlers(obj::class as KClass<A>).forEach { handler  ->
            context.globalContext.transactionContext.postCommitCallbacks.add{
                advice.invalidateFindCache(handler.getIndexClass(), handler.getPropertyName(), handler.getValue(obj))
            }
        }
    }

}