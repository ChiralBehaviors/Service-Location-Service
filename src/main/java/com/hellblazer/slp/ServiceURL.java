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

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Deque;
import java.util.LinkedList;
import java.util.StringTokenizer;

/**
 * A class representing a service url. It contains the service type, service
 * access point (hostname) and URL path needed to reach the service.
 * 
 * @author <a href="mailto:hal.hildebrand@gmail.com">Hal Hildebrand</a>
 * 
 */
public class ServiceURL implements Serializable {
    public static final String    SERVICE_PREFIX    = "service:";
    public static final int       TTL_DEFAULT       = 100;
    public static final int       TTL_MAXIMUM       = 65535;
    public static final int       TTL_NONE          = 0;
    public static final int       TTL_PERMANENT     = -1;
    private static final Protocol DEFAULT_TRANSPORT = Protocol.TCP;
    private static final long     serialVersionUID  = 1L;

    public static String parseServiceType(String serviceURL)
                                                            throws MalformedURLException {
        if (!serviceURL.startsWith(SERVICE_PREFIX)) {
            throw new MalformedURLException(serviceURL);
        }
        int index = serviceURL.indexOf('/');
        if (index <= 0) {
            throw new MalformedURLException(serviceURL);
        }
        StringTokenizer tokens = new StringTokenizer(
                                                     serviceURL.substring(0,
                                                                          index),
                                                     ":");
        Deque<String> protocols = new LinkedList<>();

        while (tokens.hasMoreElements()) {
            protocols.add(tokens.nextToken());
        }
        if (protocols.size() == 0) {
            throw new MalformedURLException(serviceURL);
        }

        protocols.removeLast();
        if (protocols.size() == 0) {
            throw new MalformedURLException(serviceURL);
        }
        protocols.removeFirst();

        return protocols.size() >= 1 ? protocols.getFirst() : null;
    }

    public static URL parseUrl(String serviceURL) throws MalformedURLException {
        if (!serviceURL.startsWith(SERVICE_PREFIX)) {
            throw new MalformedURLException(serviceURL);
        }
        int index = serviceURL.indexOf('/');
        if (index <= 0) {
            throw new MalformedURLException(serviceURL);
        }
        StringTokenizer tokens = new StringTokenizer(
                                                     serviceURL.substring(0,
                                                                          index),
                                                     ":");
        Deque<String> protocols = new LinkedList<>();

        while (tokens.hasMoreElements()) {
            protocols.add(tokens.nextToken());
        }
        if (protocols.size() == 0) {
            throw new MalformedURLException(serviceURL);
        }
        protocols.removeFirst();
        if (protocols.size() == 0) {
            throw new MalformedURLException(serviceURL);
        }
        if (protocols.size() > 1) {
            protocols.removeFirst();
        }

        StringBuilder sb = new StringBuilder(serviceURL.length());
        while (protocols.size() > 0) {
            sb.append(protocols.removeFirst());
            sb.append(':');
        }
        sb.append(serviceURL.substring(index));
        return new URL(sb.toString());
    }

    private final String   serviceType;
    private final Protocol transport;
    private final int      ttl;
    private final int      priority;
    private final int      weight;
    private final String   zone;
    private final String   instanceName;
    private final URL      url;

    public ServiceURL(String url) throws MalformedURLException {
        this(url, TTL_DEFAULT, DEFAULT_TRANSPORT);
    }

    public ServiceURL(String url, int ttl) throws MalformedURLException {
        this(url, ttl, DEFAULT_TRANSPORT);
    }

    public ServiceURL(String url, int ttl, Protocol transport)
                                                              throws MalformedURLException {
        this(parseServiceType(url), parseUrl(url), ttl, transport, 0, null, 0,
             null);
    }

    public ServiceURL(String abstractServiceType, URL url, int ttl) {
        this(abstractServiceType, url, ttl, DEFAULT_TRANSPORT, 0, null, 0, null);
    }

    public ServiceURL(String serviceType, URL url, int ttl, Protocol transport,
                      int priority, String instanceName, int weight, String zone) {
        assert url != null : "url cannot be null";
        this.serviceType = serviceType == null ? url.getProtocol()
                                              : serviceType;
        this.url = url;
        this.ttl = ttl;
        this.transport = transport;
        this.priority = priority;
        this.weight = weight;
        this.instanceName = instanceName;
        this.zone = zone;
    }

    public ServiceURL(URL url) {
        this(url, TTL_DEFAULT, DEFAULT_TRANSPORT);
    }

    public ServiceURL(URL url, int ttl) {
        this(url, ttl, DEFAULT_TRANSPORT);
    }

    public ServiceURL(URL url, int ttl, Protocol transport) {
        this(null, url, ttl, transport, 0, null, 0, null);
    }

    public String getHost() {
        return url.getHost();
    }

    public int getPort() {
        return url.getPort();
    }

    public String getPrefixedServiceType() {
        StringBuilder sb = new StringBuilder();
        sb.append(SERVICE_PREFIX);
        sb.append(serviceType);
        return sb.toString();
    }

    public String getServiceType() {
        return serviceType;
    }

    public Protocol getTransport() {
        return transport;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(SERVICE_PREFIX);
        if (!serviceType.equals(url.getProtocol())) {
            sb.append(serviceType);
            sb.append(':');
        }
        sb.append(url.toExternalForm());
        return sb.toString();
    }

    public int getTtl() {
        return ttl;
    }

    public int getPriority() {
        return priority;
    }

    public int getWeight() {
        return weight;
    }

    public String getZone() {
        return zone;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public URL getUrl() {
        return url;
    }
}