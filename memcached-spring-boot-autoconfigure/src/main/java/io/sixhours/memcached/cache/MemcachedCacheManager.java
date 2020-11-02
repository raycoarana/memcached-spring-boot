/**
 * Copyright 2016-2020 Sixhours
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.sixhours.memcached.cache;

import net.spy.memcached.MemcachedClient;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * {@link CacheManager} implementation for Memcached.
 * <p>
 * By default appends prefix {@code memcached:spring-boot} and uses namespace key value of {@code namespace-key}
 * to avoid clashes with other data that might be kept in the cache. Custom prefix can be specified
 * in Spring configuration file e.g.
 * <br><br>
 * <code>
 * memcached.cache.prefix=custom-prefix<br>
 * memcached.cache.namespace=custom-namespace-key
 * </code>
 *
 * @author Igor Bolic
 */
public class MemcachedCacheManager implements CacheManager {

    private final ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<>();

    final MemcachedClient memcachedClient;

    private int expiration = Default.EXPIRATION;
    private String prefix = Default.PREFIX;
    private String namespace = Default.NAMESPACE;
    private Map<String, Integer> expirations;

    /**
     * Construct a {@link MemcachedCacheManager}
     *
     * @param memcachedClient {@link MemcachedClient}
     */
    public MemcachedCacheManager(MemcachedClient memcachedClient) {
        this.memcachedClient = memcachedClient;
    }

    @Override
    public Cache getCache(String name) {
        Cache cache = this.cacheMap.get(name);
        if (cache == null) {

            cache = createCache(name);
            final Cache currentCache = cacheMap.putIfAbsent(name, cache);

            if (currentCache != null) {
                cache = currentCache;
            }
        }
        return cache;
    }

    @Override
    public Collection<String> getCacheNames() {
        return Collections.unmodifiableSet(cacheMap.keySet());
    }

    private MemcachedCache createCache(String name) {
        int expiration = determineExpiration(name);
        return new MemcachedCache(name, memcachedClient, expiration, prefix, namespace);
    }

    private int determineExpiration(String name) {
        Integer expiration = null;

        if (expirations != null) {
            expiration = expirations.get(name);
        }

        return Optional.ofNullable(expiration).orElse(this.expiration);
    }

    /**
     * Sets global expiration for all cache names.
     * Custom expiration is used in case it is defined by {@code expirations} {@link Map} property.
     *
     * @param expiration the expiration
     */
    public void setExpiration(int expiration) {
        this.expiration = expiration;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * Sets expiration time for cache keys.
     *
     * @param expirations {@link Map} of expiration times per cache key
     */
    public void setExpirations(Map<String, Integer> expirations) {
        this.expirations = (expirations != null ? new ConcurrentHashMap<>(expirations) : null);
    }
}
