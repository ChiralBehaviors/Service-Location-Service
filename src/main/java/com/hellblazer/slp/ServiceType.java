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
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.StringTokenizer;

/**
 * The service type represents a service advertisement's type. They may be of
 * three types :
 * 
 * <pre>
 *      simple type     : 'service:simpletype' 
 *                              e.g. 'service:http' , 'service:telnet' 
 *      abstract type   : 'service:abstract-type-name:concrete-type-name' 
 *                              e.g. 'service:login:telnet'.
 *      any URL scheme  : e.g 'http:'.
 * 
 * @author <a href="mailto:hal.hildebrand@gmail.com">Hal Hildebrand</a>
 * 
 */
public class ServiceType implements Serializable {
    public static final String IANA             = "";
    public static final String SERVICE          = "service";
    public static final String SERVICE_PREFIX   = "service:";
    private static final long  serialVersionUID = 1L;

    private final String       abstractType;
    private final String       concreteType;
    private final boolean      isService;

    /**
     * Create a service type object from the type name. The name may take the
     * form of any valid service type name. Type-name may be : simple (ex -->
     * service:http[.na]) abstract (ex--> service:login[.na]:ftp) or standard
     * url scheme, in which case it is not a service:url
     * 
     * @throws IllegalArgumentException
     *             if the name is syntactically incorrect.
     */
    public ServiceType(String typeName) {
        StringTokenizer tokens = new StringTokenizer(typeName, ":");
        if (!tokens.hasMoreElements()) {
            throw new IllegalArgumentException("");
        }
        Deque<String> elements = new ArrayDeque<String>();
        while (tokens.hasMoreElements()) {
            elements.add(tokens.nextToken());
        }
        if (SERVICE.equals(elements.getFirst())) {
            isService = true;
            elements.remove();
            if (elements.size() > 1) {
                abstractType = elements.remove();
            } else if (elements.size() == 0) {
                throw new IllegalArgumentException(
                                                   String.format("Invalid service url: %s",
                                                                 typeName));
            } else {
                abstractType = null;
            }
        } else {
            isService = false;
            abstractType = null;
        }
        StringBuilder builder = new StringBuilder();
        while (!elements.isEmpty()) {
            builder.append(elements.removeFirst());
            if (!elements.isEmpty()) {
                builder.append(":");
            }
        }
        concreteType = builder.toString();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ServiceType other = (ServiceType) obj;
        if (abstractType == null) {
            if (other.abstractType != null) {
                return false;
            }
        } else if (!abstractType.equals(other.abstractType)) {
            return false;
        }
        if (concreteType == null) {
            if (other.concreteType != null) {
                return false;
            }
        } else if (!concreteType.equals(other.concreteType)) {
            return false;
        }
        if (isService != other.isService) {
            return false;
        }
        return true;
    }

    /**
     * The fully formatted abstract type name, if it is an abstract type,
     * otherwise the empty string.
     * 
     * @return a String representing the abstract type name.
     */
    public String getAbstractTypeName() {
        return abstractType != null ? abstractType : "";
    }

    /**
     * The concrete type name without naming authority.
     * 
     * @return a String representing the concrete type name.
     */
    public String getConcreteTypeName() {
        return concreteType;
    }

    /**
     * @return
     */
    public String getProtocol() {
        return concreteType;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                 + (abstractType == null ? 0 : abstractType.hashCode());
        result = prime * result
                 + (concreteType == null ? 0 : concreteType.hashCode());
        result = prime * result + (isService ? 1231 : 1237);
        return result;
    }

    /**
     * Return true if type name is for an abstract type.
     * 
     * @return a flag indicating whether the service type is abstract or not.
     */
    public boolean isAbstractType() {
        return abstractType != null;
    }

    /**
     * Return true if the type name came from service: URL.
     * 
     * @return a flag indicating whether the URL string which this object was
     *         constructed with was a Service URL or not.
     */
    public boolean isServiceURL() {
        return isService;
    }

    /**
     * The service type name, as a string, formatted as in the call to the
     * constructor.
     * 
     * @return a String representing the service type
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (isService) {
            sb.append(SERVICE_PREFIX);
            if (isAbstractType()) {
                sb.append(getAbstractTypeName());
                sb.append(':');
                sb.append(getConcreteTypeName());
            } else {
                sb.append(getConcreteTypeName());
            }
        } else {
            sb.append(concreteType);
        }
        return sb.toString();
    }
}