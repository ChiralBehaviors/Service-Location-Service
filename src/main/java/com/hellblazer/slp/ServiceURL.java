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
public class ServiceURL implements Comparable<ServiceURL> {

    public static final int   NO_PORT = 0;

    private final ServiceType serviceType;
    private final String      serviceURL;
    private final URI         uri;
    private final String      urlPath;
    private final byte        priority;
    private final byte        weight;

    public ServiceURL(ServiceType type, URL url) throws URISyntaxException {
        this(type, url, (byte) 0, (byte) 0);
    }

    public ServiceURL(ServiceType type, URL url, byte weight, byte priority)
                                                                            throws URISyntaxException {
        serviceType = type;
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
        this.priority = priority;
        this.weight = weight;
    }

    public ServiceURL(String url) throws MalformedURLException {
        this(url, (byte) 0, (byte) 0);
    }

    public ServiceURL(String url, byte weight, byte priority)
                                                             throws MalformedURLException {
        /* Check that there are no non-ASCII characters in the URL,
        following RFC 2609.  */
        for (int i = 0; i < url.length(); i++) {
            char c = url.charAt(i);
            if (c < 32 || c >= 127) {
                throw new MalformedURLException("Service URL contains "
                                                + "non-ASCII character 0x"
                                                + Integer.toHexString(c));
            }
        }

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
        this.priority = priority;
        this.weight = weight;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        try {
            ServiceURL u = (ServiceURL) obj;
            result = u.getServiceType().equals(serviceType)
                     && u.getHost().equals(getHost())
                     && u.getPort() == getPort()
                     && u.getUrlPath().equals(getUrlPath());
        } catch (ClassCastException ex) {
        }

        return result;
    }

    public String getHost() {
        if (uri == null) {
            return "";
        }

        return uri.getHost();
    }

    public int getPort() {
        if (uri == null) {
            return NO_PORT;
        }
        int p = uri.getPort();
        if (p == -1) {
            return NO_PORT;
        }

        return p;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public String getServiceURL() {
        return serviceURL;
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

    @Override
    public int hashCode() {
        return serviceURL.hashCode();
    }

    @Override
    public String toString() {
        return serviceURL;
    }

    /**
     * @return the priority
     */
    public int getPriority() {
        return priority & 0xFF;
    }

    /**
     * @return the weight
     */
    public int getWeight() {
        return weight & 0xFF;
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(ServiceURL o) {
        int c = serviceURL.compareTo(o.serviceURL);
        if (c != 0) {
            return c;
        }
        c = Integer.valueOf(weight & 0xFF).compareTo(Integer.valueOf(o.weight & 0xFF));
        if (c != 0) {
            return c;
        }
        return Integer.valueOf(priority & 0xFF).compareTo(Integer.valueOf(o.priority & 0xFF));
    }
}