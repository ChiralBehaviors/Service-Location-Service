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

import java.util.Map;

/**
 * The interface to a filter which can be used to match attributes or
 * <link>ServiceReference</>
 * 
 * @author <a href="mailto:hal.hildebrand@gmail.com">Hal Hildebrand</a>
 * 
 */
public interface Filter {

    /**
     * Filter using a Map. The Filter is executed using the Map's keys.
     * 
     * @param properties
     *            the map whose keys are used in the match.
     * @return <code>true</code> if the Map's keys match this filter;
     *         <code>false</code> otherwise.
     */
    boolean match(Map<String, String> properties);

    boolean match(ServiceReference reference);

    /**
     * Filter with case sensitivity using a <tt>Map<String, String></tt> object.
     * The FilterImpl is executed using the <tt>Map</tt> object's keys and
     * values. The keys are case sensitivley matched with the filter.
     * 
     * @param properties
     *            The <tt>Map</tt> object whose keys are used in the match.
     * 
     * @return <tt>true</tt> if the <tt>Map</tt> object's keys and values match
     *         this filter; <tt>false</tt> otherwise.
     */
    boolean matchCase(Map<String, String> properties);

}