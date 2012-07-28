/** (C) Copyright 2010 Hal Hildebrand, All Rights Reserved
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
package com.hellblazer.slp;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author <a href="mailto:hal.hildebrand@gmail.com">Hal Hildebrand</a>
 * 
 */
public class HeaderMap extends HashMap<String, String> {
    private static final long serialVersionUID = 1L;

    /**
     * @param m
     */
    public HeaderMap(Map<String, String> m) {
        super();
        for (Map.Entry<String, String> entry : m.entrySet()) {
            super.put(entry.getKey().toLowerCase(), entry.getValue());
        }
    }

    @Override
    public boolean containsKey(Object key) {
        return super.containsKey(((String) key).toLowerCase());
    }

    @Override
    public String get(Object key) {
        return super.get(((String) key).toLowerCase());
    }

    @Override
    public String put(String key, String value) {
        throw new UnsupportedOperationException();
    }

}