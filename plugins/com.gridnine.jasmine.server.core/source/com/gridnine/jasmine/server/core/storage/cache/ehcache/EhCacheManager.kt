/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.storage.cache.ehcache

import com.gridnine.jasmine.common.core.app.ConfigurationProvider
import com.gridnine.jasmine.common.core.model.BaseIdentity
import com.gridnine.jasmine.common.core.model.ObjectReference
import com.gridnine.jasmine.server.core.storage.cache.CacheManager
import com.gridnine.jasmine.server.core.storage.cache.CachedValue
import com.gridnine.jasmine.server.core.storage.cache.KeyValueCache
import org.ehcache.config.builders.CacheConfigurationBuilder
import org.ehcache.config.builders.CacheManagerBuilder
import org.ehcache.config.builders.ExpiryPolicyBuilder
import org.ehcache.config.builders.ResourcePoolsBuilder
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass


class EhCacheManager : CacheManager{
    private val  cacheManager = CacheManagerBuilder.newCacheManagerBuilder().withSerializer(CachedValue::class.java, CachedValueSerializer::class.java).build(true)

    private val resolveCaches = ConcurrentHashMap<String, KeyValueCache<*>>()

    private val findCache = ConcurrentHashMap<String, MutableMap<String, KeyValueCache<ObjectReference<*>>>>()

    override fun <D : Any> getOrCreateResolveCache(cls: KClass<D>): KeyValueCache<D> {
        val className = cls.java.name
        val cacheName = "resolve_$className"

        return resolveCaches.getOrPut(cacheName){
            val capacity = (ConfigurationProvider.get().getProperty("cache.resolve.capacity.$className")?:ConfigurationProvider.get().getProperty("cache.resolve.capacity.default")?:"100").toLong()
            val expirationInSeconds = (ConfigurationProvider.get().getProperty("cache.resolve.expiration.$className")?:ConfigurationProvider.get().getProperty("cache.expiration.default")?:"3600").toLong()
            val delegate = cacheManager.createCache(cacheName,
                    CacheConfigurationBuilder.newCacheConfigurationBuilder(String::class.java, CachedValue::class.java, ResourcePoolsBuilder.heap(capacity))
                            .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(expirationInSeconds))))
            object:KeyValueCache<D>{
                override fun get(key: String): CachedValue<D>? {
                    return delegate[key] as CachedValue<D>?
                }

                override fun put(key: String, value: CachedValue<D>) {
                    delegate.put(key, value)
                }

                override fun replace(key: String, oldValue: CachedValue<D>?, newValue: CachedValue<D>) {
                    if(oldValue == null){
                        delegate.putIfAbsent(key, newValue)
                    } else {
                        delegate.replace(key, oldValue, newValue)
                    }
                }

            }
        } as KeyValueCache<D>
    }

    override fun <E : BaseIdentity> getOrCreateFindCache(cls: KClass<*>, fieldName: String): KeyValueCache<ObjectReference<E>> {
        val className = cls.java.name

        return findCache.getOrPut(className){
            ConcurrentHashMap<String, KeyValueCache<ObjectReference<*>>>()
        }.getOrPut(fieldName){
            val cacheName = "find_${className}_$fieldName"
            val capacity = (ConfigurationProvider.get().getProperty("cache.find.capacity.${className}.$fieldName")?:ConfigurationProvider.get().getProperty("cache.capacity.default")?:"100").toLong()
            val expirationInSeconds = (ConfigurationProvider.get().getProperty("cache.find.expiration.$fieldName")?:ConfigurationProvider.get().getProperty("cache.expiration.default")?:"3600").toLong()
            val delegate = cacheManager.createCache(cacheName,
                    CacheConfigurationBuilder.newCacheConfigurationBuilder(String::class.java, CachedValue::class.java, ResourcePoolsBuilder.heap(capacity))
                            .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(expirationInSeconds))))
            object:KeyValueCache<ObjectReference<*>>{
                override fun get(key: String): CachedValue<ObjectReference<*>>? {
                    return delegate[key] as CachedValue<ObjectReference<*>>?
                }

                override fun put(key: String, value: CachedValue<ObjectReference<*>>) {
                    delegate.put(key, value)
                }

                override fun replace(key: String, oldValue: CachedValue<ObjectReference<*>>?, newValue: CachedValue<ObjectReference<*>>) {
                    if(oldValue == null){
                        delegate.putIfAbsent(key, newValue)
                    } else {
                        delegate.replace(key, oldValue, newValue)
                    }
                }

            }

        }as KeyValueCache<ObjectReference<E>>
    }

    override fun dispose() {
        cacheManager.close()
        super.dispose()
    }

}