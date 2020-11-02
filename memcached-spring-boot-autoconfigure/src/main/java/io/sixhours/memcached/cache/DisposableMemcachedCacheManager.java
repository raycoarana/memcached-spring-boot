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
import org.springframework.beans.factory.DisposableBean;

/**
 * Disposable {@link MemcachedCacheManager} bean.
 *
 * @author Igor Bolic
 */
class DisposableMemcachedCacheManager extends MemcachedCacheManager implements DisposableBean {

    public DisposableMemcachedCacheManager(MemcachedClient memcachedClient) {
        super(memcachedClient);
    }

    @Override
    public void destroy() {
        this.memcachedClient.shutdown();
    }
}
