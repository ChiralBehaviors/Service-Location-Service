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
    private static final long  serialVersionUID = 1L;
    public static final String IANA             = "";
    public static final String servicePrefix    = "service:";
    private String             concreteType     = "";
    private String             abstractType     = "";
    private String             simpleType       = "";

    String                     typeName;
    String                     namingAuthority  = IANA;       ;

    boolean                    isServiceType    = false, isAbstract = false;

    /**
     * Create a service type object from the type name. The name may take the
     * form of any valid service type name. Type-name may be : simple (ex -->
     * service:http[.na]) abstract (ex--> service:login[.na]:ftp) or standard
     * url scheme, in which case it is not a service:url
     * 
     * @throws IllegalArgumentException
     *             if the name is syntactically incorrect.
     */
    public ServiceType(String typeName) throws IllegalArgumentException {
        this.typeName = typeName;
        String temporaryTypeName = "";
        if (typeName.indexOf(servicePrefix) != -1) {
            isServiceType = true;
            temporaryTypeName = typeName.substring(servicePrefix.length());
        } else {
            isServiceType = false;
            return;
        }
        // simple or abstract ?
        isAbstract = temporaryTypeName.indexOf(":") != -1;
        // look for a naming authority
        int type_na_separator = temporaryTypeName.indexOf(".");
        if (type_na_separator != -1) {
            String _na = temporaryTypeName.substring(type_na_separator + 1);
            if (_na.indexOf(":") != -1) {
                _na = _na.substring(0, _na.indexOf(":"));
            }
            namingAuthority = _na;
        }

        // remove naming authority from type name;
        String undefinedType = temporaryTypeName;
        if (!namingAuthority.equals(IANA)) {
            undefinedType = temporaryTypeName.substring(0,
                                                        temporaryTypeName.indexOf("."));
        }
        // parse abstract and concrete names
        if (isAbstractType()) {
            abstractType = !namingAuthority.equals(IANA) ? temporaryTypeName.substring(0,
                                                                                       temporaryTypeName.indexOf("."))
                                                        : temporaryTypeName.substring(0,
                                                                                      temporaryTypeName.indexOf(":"));
            concreteType = temporaryTypeName.substring(temporaryTypeName.indexOf(":") + 1);
        } else {
            simpleType = undefinedType;
        }
    }

    /**
     * Return true if the parameter is a ServiceType object and the type names
     * match.
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof ServiceType) {
            return typeName.equals(((ServiceType) o).typeName);
        }
        return false;
    }

    /**
     * The fully formatted abstract type name, if it is an abstract type,
     * otherwise the empty string.
     * 
     * @return a String representing the abstract type name.
     */
    public String getAbstractTypeName() {
        if (isAbstractType()) {
            return
            //      ServiceType.servicePrefix+
            abstractType;
        }
        return "";
    }

    /**
     * The concrete type name without naming authority.
     * 
     * @return a String representing the concrete type name.
     */
    public String getConcreteTypeName() {
        if (isAbstractType()) {
            return concreteType;
        }
        return "";
    }

    /**
     * The naming authority name. IANA default is the empty String.
     * 
     * @return a String representing the naming authority
     */
    public String getNamingAuthority() {
        return namingAuthority;
    }

    /**
     * The principle type name, which is either the abstract type name or the
     * protocol name, without naming authority.
     * 
     * @return a String representing the principle type name .
     */
    public String getPrincipleTypeName() {
        if (isAbstractType()) {
            return abstractType;
        }
        return simpleType;
    }

    /** Return a hashcode of the service type */
    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    /**
     * Return true if type name is for an abstract type.
     * 
     * @return a flag indicating whether the service type is abstract or not.
     */
    public boolean isAbstractType() {
        return isAbstract;
    }

    /**
     * Return true if naming authority is default.
     * 
     * @return a flag indicating whether the naming authority of this service
     *         type is the IANA default or not.
     */
    public boolean isNADefault() {
        return namingAuthority.equals(IANA);
    }

    /**
     * Return true if the type name came from service: URL.
     * 
     * @return a flag indicating whether the URL string which this object was
     *         constructed with was a Service URL or not.
     */
    public boolean isServiceURL() {
        return isServiceType;
    }

    /**
     * The service type name, as a string, formatted as in the call to the
     * constructor.
     * 
     * @return a String representing the service type
     */
    @Override
    public String toString() {
        String result = "";
        if (isServiceType) {
            result += ServiceType.servicePrefix;
            String naPrefix = getNamingAuthority().equals(IANA) ? "" : ".";
            result += isAbstractType() ? getAbstractTypeName() + naPrefix
                                         + getNamingAuthority() + ":"
                                         + getConcreteTypeName()
                                      : getPrincipleTypeName() + naPrefix
                                        + getNamingAuthority();
        } else {
            // it is a URL, simply return scheme.
            result = typeName;
        }
        return result;
    }
}