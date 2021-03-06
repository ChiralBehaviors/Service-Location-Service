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

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The ServiceScope is the context within services are provided and discovered.
 * Service providers can register, modify and unregister services. Clients can
 * query for existing services and set up listeners which allow the clients to
 * be notified when services matching a query are registered, modified or
 * deleted.
 * 
 * <p>
 * Services are represened by an <link>ServiceURL</link> and optional key,value
 * pairs defining the attributes of these services. All services within a scope
 * are uniquely identified by a service registration, represented by a
 * <link>UUID</link>.
 * 
 * @author <a href="mailto:hal.hildebrand@gmail.com">Hal Hildebrand</a>
 * 
 */
public interface ServiceScope {
    public static final String SERVICE_REGISTRATION = "sd.service.registration";
    public static final String SERVICE_TYPE         = "sd.service.type";
    public static final String SERVICE_URL_PATH     = "sd.service.url.path";

    /**
     * Add a listener which will receive lifecycle events for events which match
     * the supplied query filter. Each {listener, query} pair is unique, meaning
     * that a service listener can be registered for multiple queires within a
     * scope.
     * 
     * <p>
     * The syntax of a filter string is the string representation of LDAP search
     * filters as defined in RFC 1960: <i>A String Representation of LDAP Search
     * Filters</i> (available at http://www.ietf.org/rfc/rfc1960.txt). It should
     * be noted that RFC 2254: <i>A String Representation of LDAP Search
     * Filters</i> (available at http://www.ietf.org/rfc/rfc2254.txt) supersedes
     * RFC 1960 but only adds extensible matching and is not applicable for this
     * API.
     * 
     * <p>
     * The string representation of an LDAP search filter is defined by the
     * following grammar. It uses a prefix format.
     * 
     * <pre>
     *   &lt;filter&gt; ::= '(' &lt;filtercomp&gt; ')'
     *   &lt;filtercomp&gt; ::= &lt;and&gt; | &lt;or&gt; | &lt;not&gt; | &lt;item&gt;
     *   &lt;and&gt; ::= '&' &lt;filterlist&gt;
     *   &lt;or&gt; ::= '|' &lt;filterlist&gt;
     *   &lt;not&gt; ::= '!' &lt;filter&gt;
     *   &lt;filterlist&gt; ::= &lt;filter&gt; | &lt;filter&gt; &lt;filterlist&gt;
     *   &lt;item&gt; ::= &lt;simple&gt; | &lt;present&gt; | &lt;substring&gt;
     *   &lt;simple&gt; ::= &lt;attr&gt; &lt;filtertype&gt; &lt;value&gt;
     *   &lt;filtertype&gt; ::= &lt;equal&gt; | &lt;approx&gt; | &lt;greater&gt; | &lt;less&gt;
     *   &lt;equal&gt; ::= '='
     *   &lt;approx&gt; ::= '~='
     *   &lt;greater&gt; ::= '&gt;='
     *   &lt;less&gt; ::= '&lt;='
     *   &lt;present&gt; ::= &lt;attr&gt; '=*'
     *   &lt;substring&gt; ::= &lt;attr&gt; '=' &lt;initial&gt; &lt;any&gt; &lt;final&gt;
     *   &lt;initial&gt; ::= NULL | &lt;value&gt;
     *   &lt;any&gt; ::= '*' &lt;starval&gt;
     *   &lt;starval&gt; ::= NULL | &lt;value&gt; '*' &lt;starval&gt;
     *   &lt;final&gt; ::= NULL | &lt;value&gt;
     * </pre>
     * 
     * <code>&lt;attr&gt;</code> is a string representing an attribute, or key,
     * in the properties objects of the registered services. Attribute names are
     * not case sensitive; that is cn and CN both refer to the same attribute.
     * <code>&lt;value&gt;</code> is a string representing the value, or part of
     * one, of a key in the properties objects of the registered services. If a
     * <code>&lt;value&gt;</code> must contain one of the characters '
     * <code>*</code> ' or '<code>(</code>' or '<code>)</code>', these
     * characters should be escaped by preceding them with the backslash '
     * <code>\</code>' character. Note that although both the
     * <code>&lt;substring&gt;</code> and <code>&lt;present&gt;</code>
     * productions can produce the <code>'attr=*'</code> construct, this
     * construct is used only to denote a presence filter.
     * 
     * <p>
     * Examples of LDAP filters are:
     * 
     * <pre>
     *   &quot;(cn=Babs Jensen)&quot;
     *   &quot;(!(cn=Tim Howes))&quot;
     *   &quot;(&(&quot; + ServiceScope.SERVICE_TYPE + &quot;=service:acs)(|(group=A)(group=B*)))&quot;
     *   &quot;(load<0.5)&quot;
     * </pre>
     * 
     * @param listener
     *            - the ServiceListener to add.
     * @param query
     *            - the String representation of the LDAP query (can be null)
     * 
     * @throws InvalidSyntaxException
     *             - if the supplied query contains invalid syntax.
     */
    void addServiceListener(ServiceListener listener, String query)
                                                                   throws InvalidSyntaxException;

    /**
     * Answer the ServiceReference registered under the service registration id
     * 
     * @param serviceRegistration
     * @return
     */
    ServiceReference getReference(UUID serviceRegistration);

    /**
     * Answer the first service that matches the supplied service type. A
     * service type is one of three different types:
     * 
     * <pre>
     *  simple type    : 'service:simpletype' 
     *                          e.g. 'service:http' ,
     *  abstract type  : 'service:abstract-type-name:concrete-type-name' 
     *                          e.g. 'service:login:telnet'.
     *  any URL scheme : 'http:' for example.
     * </pre>
     * 
     * @param serviceType
     *            - a service type.
     * @return the first ServiceReference that matches this service type
     * @throws InvalidSyntaxException
     */
    ServiceReference getServiceReference(String serviceType)
                                                            throws InvalidSyntaxException;

    /**
     * Answer the list of all service refences that match the service type and
     * supplied query filter.
     * 
     * A service type is one of three different types:
     * 
     * <pre>
     *  simple type    : 'service:simpletype' 
     *                          e.g. 'service:http' ,
     *  abstract type  : 'service:abstract-type-name:concrete-type-name' 
     *                          e.g. 'service:login:telnet'.
     *  any URL scheme : 'http:' for example.
     * </pre>
     * 
     * <p>
     * The syntax of a filter string is the string representation of LDAP search
     * filters as defined in RFC 1960: <i>A String Representation of LDAP Search
     * Filters</i> (available at http://www.ietf.org/rfc/rfc1960.txt). It should
     * be noted that RFC 2254: <i>A String Representation of LDAP Search
     * Filters</i> (available at http://www.ietf.org/rfc/rfc2254.txt) supersedes
     * RFC 1960 but only adds extensible matching and is not applicable for this
     * API.
     * 
     * <p>
     * The string representation of an LDAP search filter is defined by the
     * following grammar. It uses a prefix format.
     * 
     * <pre>
     *   &lt;filter&gt; ::= '(' &lt;filtercomp&gt; ')'
     *   &lt;filtercomp&gt; ::= &lt;and&gt; | &lt;or&gt; | &lt;not&gt; | &lt;item&gt;
     *   &lt;and&gt; ::= '&' &lt;filterlist&gt;
     *   &lt;or&gt; ::= '|' &lt;filterlist&gt;
     *   &lt;not&gt; ::= '!' &lt;filter&gt;
     *   &lt;filterlist&gt; ::= &lt;filter&gt; | &lt;filter&gt; &lt;filterlist&gt;
     *   &lt;item&gt; ::= &lt;simple&gt; | &lt;present&gt; | &lt;substring&gt;
     *   &lt;simple&gt; ::= &lt;attr&gt; &lt;filtertype&gt; &lt;value&gt;
     *   &lt;filtertype&gt; ::= &lt;equal&gt; | &lt;approx&gt; | &lt;greater&gt; | &lt;less&gt;
     *   &lt;equal&gt; ::= '='
     *   &lt;approx&gt; ::= '~='
     *   &lt;greater&gt; ::= '&gt;='
     *   &lt;less&gt; ::= '&lt;='
     *   &lt;present&gt; ::= &lt;attr&gt; '=*'
     *   &lt;substring&gt; ::= &lt;attr&gt; '=' &lt;initial&gt; &lt;any&gt; &lt;final&gt;
     *   &lt;initial&gt; ::= NULL | &lt;value&gt;
     *   &lt;any&gt; ::= '*' &lt;starval&gt;
     *   &lt;starval&gt; ::= NULL | &lt;value&gt; '*' &lt;starval&gt;
     *   &lt;final&gt; ::= NULL | &lt;value&gt;
     * </pre>
     * 
     * <code>&lt;attr&gt;</code> is a string representing an attribute, or key,
     * in the properties objects of the registered services. Attribute names are
     * not case sensitive; that is cn and CN both refer to the same attribute.
     * <code>&lt;value&gt;</code> is a string representing the value, or part of
     * one, of a key in the properties objects of the registered services. If a
     * <code>&lt;value&gt;</code> must contain one of the characters '
     * <code>*</code> ' or '<code>(</code>' or '<code>)</code>', these
     * characters should be escaped by preceding them with the backslash '
     * <code>\</code>' character. Note that although both the
     * <code>&lt;substring&gt;</code> and <code>&lt;present&gt;</code>
     * productions can produce the <code>'attr=*'</code> construct, this
     * construct is used only to denote a presence filter.
     * 
     * <p>
     * Examples of LDAP filters are:
     * 
     * <pre>
     *   &quot;(cn=Babs Jensen)&quot;
     *   &quot;(!(cn=Tim Howes))&quot;
     *   &quot;(&(&quot; + ServiceScope.SERVICE_TYPE + &quot;=service:acs)(|(group=A)(group=B*)))&quot;
     *   &quot;(load<0.5)&quot;
     * </pre>
     * 
     * @param serviceType
     *            - the service type to match
     * @param query
     *            - the filter string to match
     * @return the list of matching service references.
     * @throws InvalidSyntaxException
     */
    List<ServiceReference> getServiceReferences(String serviceType, String query)
                                                                                 throws InvalidSyntaxException;

    /**
     * Register a service with the scope.
     * 
     * @param url
     *            - the ServiceURL defining the service
     * @param properties
     *            - the map of properties
     * @return the UUID representing the service registration.
     */
    UUID register(ServiceURL url, Map<String, String> properties);

    /**
     * Remove a listener from the scope. All queries the listener is registered
     * for in this scope are removed.
     * 
     * @param listener
     *            - the listener to remove.
     */
    void removeServiceListener(ServiceListener listener);

    /**
     * Remove the {query, listener} pair from the scope
     * 
     * @param listener
     *            - the listener to remove.
     * @throws InvalidSyntaxException
     */
    void removeServiceListener(ServiceListener listener, String query)
                                                                      throws InvalidSyntaxException;

    /**
     * Update the service properties for an existing service. If this scope is
     * not responsible for this service registration, no effect will occur.
     * 
     * @param serviceRegistration
     *            - the UUID identifying the service instance
     * @param properties
     *            - the map of properties for the service
     */
    void setProperties(UUID serviceRegistration, Map<String, String> properties);

    /**
     * Start the service scope instance.
     * 
     * @return the scope instance.
     */
    ServiceScope start();

    /**
     * Stop the service scope instance.
     * 
     * @return the scope instance
     */
    ServiceScope stop();

    /**
     * Unregister the service from the scope. If this scope is not responsible
     * for this service registration, no effect will occur.
     * 
     * @param serviceRegistration
     *            - the UUID service registration that identifies the service
     */
    void unregister(UUID serviceRegistration);
}