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
package com.hellblazer.slp.local;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.internal.verification.Times;

import com.fasterxml.uuid.Generators;
import com.hellblazer.slp.ServiceEvent;
import com.hellblazer.slp.ServiceEvent.EventType;
import com.hellblazer.slp.ServiceListener;
import com.hellblazer.slp.ServiceReference;
import com.hellblazer.slp.ServiceScope;
import com.hellblazer.slp.ServiceURL;

/**
 * @author hhildebrand
 * 
 */
public class LocalScopeTest {

    @Test
    public void testServiceListener() throws Exception {
        ServiceListener serviceListener = mock(ServiceListener.class);
        Executor executor = new Executor() {
            @Override
            public void execute(Runnable command) {
                command.run();
            }
        };

        ServiceScope localScope = new LocalScope(
                                                 executor,
                                                 Generators.randomBasedGenerator());
        ServiceURL url1 = new ServiceURL("service:http://foo.bar/two");
        ServiceURL url2 = new ServiceURL("service:http://foo.bar/one");

        localScope.addServiceListener(serviceListener,
                                      "(serviceType=service:http)");

        UUID reference1 = localScope.register(url1,
                                              new HashMap<String, String>());
        localScope.register(url2, new HashMap<String, String>());
        localScope.setProperties(reference1, new HashMap<String, String>());
        localScope.unregister(reference1);

        ArgumentCaptor<ServiceEvent> eventCaptor = ArgumentCaptor.forClass(ServiceEvent.class);
        verify(serviceListener, new Times(4)).serviceChanged(eventCaptor.capture());
        List<ServiceEvent> events = eventCaptor.getAllValues();
        assertNotNull(events);
        assertEquals(EventType.REGISTERED, events.get(0).getType());
        assertEquals(url1, events.get(0).getReference().getUrl());
        assertEquals(EventType.REGISTERED, events.get(1).getType());
        assertEquals(url2, events.get(1).getReference().getUrl());
        assertEquals(EventType.MODIFIED, events.get(2).getType());
        assertEquals(url1, events.get(2).getReference().getUrl());
        assertEquals(EventType.UNREGISTERED, events.get(3).getType());
        assertEquals(url1, events.get(3).getReference().getUrl());
        verifyNoMoreInteractions(serviceListener);
    }

    @Test
    public void testServiceLookup() throws Exception {
        Executor executor = new Executor() {
            @Override
            public void execute(Runnable command) {
                command.run();
            }
        };

        ServiceScope localScope = new LocalScope(
                                                 executor,
                                                 Generators.randomBasedGenerator());
        ServiceURL url1 = new ServiceURL("service:http://foo.bar/one");
        ServiceURL url2 = new ServiceURL("service:http://foo.bar/two");

        UUID reference1 = localScope.register(url1,
                                              new HashMap<String, String>());
        String serviceType = "service:http";
        assertEquals(url1, localScope.getServiceReference(serviceType).getUrl());

        localScope.register(url2, new HashMap<String, String>());
        List<ServiceReference> references = localScope.getServiceReferences(serviceType,
                                                                            "");
        assertNotNull(references);
        assertEquals(2, references.size());
        Set<ServiceURL> urls = new HashSet<ServiceURL>();
        urls.add(references.get(0).getUrl());
        urls.add(references.get(1).getUrl());
        assertTrue(urls.contains(url1));
        assertTrue(urls.contains(url2));
        localScope.unregister(reference1);
        references = localScope.getServiceReferences(serviceType, "");
        assertEquals(1, references.size());
        assertEquals(url2, references.get(0).getUrl());
    }
}
