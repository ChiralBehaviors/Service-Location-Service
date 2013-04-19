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

import java.net.URI;
import java.net.URL;

import org.junit.Test;

/**
 * @author hhildebrand
 * 
 */
public class ServiceURLTest {

    @Test
    public void testServiceUrlParsing() throws Exception {
        ServiceURL url = new ServiceURL(
                                        "service:myService:jar:http://foo.com/my.jar!/");
        assertEquals("service:myService:jar:http",
                     url.getServiceType().toString());
        assertEquals(new URI("srv://foo.com"), url.getUri());
        assertEquals("/my.jar!/", url.getUrlPath());
    }

    @Test
    public void testServiceUrlParsing2() throws Exception {
        ServiceURL url = new ServiceURL(
                                        "service:configuration:http://192.168.56.1:56989/configuration");
        assertEquals("service:configuration:http",
                     url.getServiceType().toString());
        assertEquals(new URI("srv://192.168.56.1:56989"), url.getUri());
        assertEquals("/configuration", url.getUrlPath());
        assertEquals(new URL("http://192.168.56.1:56989/configuration"),
                     url.getUrl());
    }

    @Test
    public void testNoUrlPath() throws Exception {
        ServiceURL url = new ServiceURL(
                                        "service:configuration:http://192.168.56.1:56989");
        assertEquals("service:configuration:http",
                     url.getServiceType().toString());
        assertEquals(new URI("srv://192.168.56.1:56989"), url.getUri());
        assertEquals("/", url.getUrlPath());
        assertEquals(new URL("http://192.168.56.1:56989/"),
                     url.getUrl());
    }
}
