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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * A class representing a service url. It contains the service type, service
 * access point (hostname) and URL path needed to reach the service.
 * 
 * @author <a href="mailto:hal.hildebrand@gmail.com">Hal Hildebrand</a>
 * 
 */
public class ServiceURL implements Serializable {
    public static final Protocol DEFAULT_TRANSPORT  = Protocol.TCP;

    public static final long     LIFETIME_DEFAULT   = 10;
    public static final int      LIFETIME_NONE      = 0;
    public static final long     LIFETIME_PERMANENT = -1;
    public static final int      NO_PORT            = 0;
    private static final int     DEFAULT_PRIORITY   = 0;
    private static final int     DEFAULT_WEIGHT     = 0;
    private static final long    serialVersionUID   = 1L;

    public static Object objectFromString(String objString) throws Exception {
        byte[] byteArray = Base64Coder.decodeLines(objString);
        ByteArrayInputStream isr = new ByteArrayInputStream(byteArray);
        ObjectInputStream ois = new ObjectInputStream(isr);
        Object obj = ois.readObject();
        ois.close();
        return obj;
    }

    public static String objectToString(Object obj)
                                                   throws IllegalArgumentException {
        String objRef64enc;
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(obj);
            oos.close();
            objRef64enc = Base64Coder.encodeLines(os.toByteArray());
            os.flush();
            os.close();
        } catch (Exception ex) {
            throw new IllegalArgumentException(
                                               "Not possible to convert object to String");
        }
        return objRef64enc;
    }

    private String            instanceName;
    private int               priority = DEFAULT_PRIORITY;
    private final ServiceType serviceType;
    private final String      serviceURL;
    private final Protocol    transport;
    private long              ttl;
    private final URI         uri;
    private final String      urlPath;
    private int               weight   = DEFAULT_WEIGHT;
    private String            zone;

    public ServiceURL(ServiceType type, URL url) throws URISyntaxException {
        this(type, url, DEFAULT_TRANSPORT);
    }

    public ServiceURL(Object obj, String type, long ttl, Protocol transport) {
        this(type + ":///" + objectToString(obj), ttl, transport);
    }

    public ServiceURL(String url) {
        this(url, LIFETIME_DEFAULT);
    }

    public ServiceURL(String url, long ttl) {
        this(url, ttl, DEFAULT_TRANSPORT);
    }

    public ServiceURL(ServiceType type, URL url, Protocol transport)
                                                                    throws URISyntaxException {
        serviceType = type;
        this.transport = transport;
        uri = url.toURI();
        urlPath = url.getPath();
        StringBuilder builder = new StringBuilder();
        builder.append(serviceType.toString());
        if (uri.getHost() != null) {
            builder.append("://");
            builder.append(uri.getHost());
            builder.append(':');
            builder.append(uri.getPort());
        } else {
            builder.append(':');
        }
        builder.append(uri.getPath());
        serviceURL = builder.toString();
    }

    public ServiceURL(String url, long ttl, Protocol transport) {
        int index = url.indexOf(":/");
        if (index == -1) {
            throw new IllegalArgumentException(
                                               String.format("No valid URL given %s",
                                                             url));
        }

        // create servicetype
        serviceType = new ServiceType(url.substring(0, index));

        String remaining = url.substring(index + 2);
        if (remaining.startsWith("/")) {
            remaining = remaining.substring(1);
        }

        // find URL path.
        int pathIndex = remaining.indexOf("/");
        if (pathIndex == -1 || pathIndex == remaining.length() - 1) {
            urlPath = "";
        } else {
            urlPath = remaining.substring(pathIndex);
        }

        // parse host and port.
        String host = "";

        if (pathIndex != -1) {
            host = remaining.substring(0, pathIndex);
        }

        if (!host.isEmpty()) {
            try {
                uri = new URI(String.format("srv://%s", host));
            } catch (URISyntaxException ex) {
                throw new IllegalArgumentException(String.format("Illegal URL",
                                                                 url), ex);
            }
        } else {
            uri = null;
        }

        serviceURL = url;
        this.ttl = ttl;
        this.transport = transport;
    }

    /**
     * @param url
     * @param ttl
     * @param protocol
     * @param priority
     * @param instanceName
     * @param weight
     * @param zone
     */
    public ServiceURL(String url, long ttl, Protocol protocol, int priority,
                      String instanceName, int weight, String zone) {
        this(url, protocol, ttl, instanceName, zone);

    }

    /**
     * @param url
     * @param ttl
     * @param instanceName
     * @param zone
     */
    public ServiceURL(String url, long ttl, String instanceName, String zone) {
        this(url, ttl);
        this.instanceName = instanceName;
        this.zone = zone;
    }

    /**
     * @param url
     * @param protocol
     * @param ttl
     * @param instanceName
     * @param zone
     */
    public ServiceURL(String url, Protocol transport, long ttl,
                      String instanceName, String zone) {
        this(url, ttl, transport);
        this.instanceName = instanceName;
        this.zone = zone;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        try {
            ServiceURL u = (ServiceURL) obj;
            result = u.getServiceType().equals(serviceType)
                     && u.getHost().equals(getHost())
                     && u.getPort() == getPort()
                     && u.getUrlPath().equals(getUrlPath())
                     && u.getTransport().equals(getTransport());
        } catch (ClassCastException ex) {
        }

        return result;
    }

    public String getDnsServiceType() {
        return serviceType.getDnsServiceType();
    }

    public String getHost() {
        if (uri == null) {
            return "";
        }

        return uri.getHost();
    }

    public String getInstanceName() {
        return instanceName;
    }

    public int getPort() {
        if (transport.equals(DEFAULT_TRANSPORT)) {
            if (uri == null) {
                return NO_PORT;
            }
            int p = uri.getPort();
            if (p == -1) {
                return NO_PORT;
            }

            return p;
        }
        return NO_PORT;
    }

    public int getPriority() {
        return priority;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public String getServiceURL() {
        return serviceURL;
    }

    public Protocol getTransport() {
        return transport;
    }

    public long getTtl() {
        return ttl;
    }

    public URI getUri() {
        return uri;
    }

    public URL getUrl() throws MalformedURLException {
        return new URL(serviceType.getProtocol(), getHost(), getPort(),
                       getUrlPath());
    }

    public String getUrlPath() {
        return urlPath;
    }

    public Object getUrlPathObject() {
        Object obj = null;
        try {
            obj = objectFromString(urlPath.substring(1));
        } catch (Exception ex) {
        }

        return obj;
    }

    public int getWeight() {
        return weight;
    }

    public String getZone() {
        return zone;
    }

    @Override
    public int hashCode() {
        return serviceURL.hashCode();
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    @Override
    public String toString() {
        return serviceURL;
    }
}