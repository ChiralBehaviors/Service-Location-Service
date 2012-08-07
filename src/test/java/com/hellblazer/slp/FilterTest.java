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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Test;

/**
 * @author hhildebrand
 * 
 */
public class FilterTest {
    @Test
    public void testFindAssertions() throws Exception {
        Filter filter = new Filter("(x=y)");
        List<String> assertions = new ArrayList<>(filter.findAssertions("x"));
        assertNotNull(assertions);
        assertEquals(1, assertions.size());
        assertEquals("y", assertions.get(0));

        filter = new Filter("(&(x=y) (z=y))");
        assertions = new ArrayList<>(filter.findAssertions("x"));
        assertNotNull(assertions);
        assertEquals(1, assertions.size());
        assertEquals("y", assertions.get(0));

        filter = new Filter("(&(x=y) (z=a))");
        assertions = new ArrayList<>(filter.findAssertions("x"));
        assertNotNull(assertions);
        assertEquals(1, assertions.size());
        assertEquals("y", assertions.get(0));

        filter = new Filter("(&(x=y) (x=a))");
        Set<String> assr = filter.findAssertions("x");
        assertNotNull(assr);
        assertEquals(2, assr.size());
        assertTrue(assr.contains("y"));
        assertTrue(assr.contains("a"));

        filter = new Filter("(&(x=y) (!(x=a)))");
        assr = filter.findAssertions("x");
        assertNotNull(assr);
        assertEquals(1, assr.size());
        assertTrue(assr.contains("y"));
        assertFalse(assr.contains("a"));
    }
}
