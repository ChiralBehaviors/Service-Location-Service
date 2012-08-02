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
import static junit.framework.Assert.assertNull;

import java.net.URL;

import org.junit.Test;

/**
 * @author hhildebrand
 * 
 */
public class ServiceURLTest {

    @Test
    public void testParseServiceType() throws Exception {
        String url = "service:http://foo.com/bar";
        assertNull(ServiceURL.parseServiceType(url));
        url = "service:myService:http://foo.com/bar";
        assertEquals("myService", ServiceURL.parseServiceType(url));
        url = "service:myService:jar:http://foo.com/my.jar!/";
        assertEquals("myService", ServiceURL.parseServiceType(url));
        url = "service:myService:foo:jar:http://foo.com/my.jar!/";
        assertEquals("myService", ServiceURL.parseServiceType(url));
    }

    @Test
    public void testParseURL() throws Exception {
        String url = "service:http://foo.com/bar";
        assertEquals(new URL("http://foo.com/bar"), ServiceURL.parseUrl(url));
        url = "service:myService:http://foo.com/bar";
        assertEquals(new URL("http://foo.com/bar"), ServiceURL.parseUrl(url));
        url = "service:myService:jar:http://foo.com/my.jar!/";
        assertEquals(new URL("jar:http://foo.com/my.jar!/"),
                     ServiceURL.parseUrl(url));
    }

    @Test
    public void testServiceUrlParsing() throws Exception {
        ServiceURL url = new ServiceURL(
                                        "service:myService:jar:http://foo.com/my.jar!/");
        assertEquals("myService", url.getServiceType());
        assertEquals(new URL("jar:http://foo.com/my.jar!/"), url.getUrl());
    }
}
