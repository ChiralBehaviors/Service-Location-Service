Service-Discovery
=================

An OSGi inspired service discovery API

This is an API for building network service discovery.  The API is inspired by the OSGi service framework and follows much the same patterns.  The main difference is that rather than registering an Object as the service, this framework uses SLP style service URLs as the registered entity.  These service URLS can be queried, and clients may register listeners to receive lifecycle events for the services in the system.

This project is licensed under the Apache license, version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html).

To build this project, you will need Java 1.7 (although the binaries are targeted at 1.6).  You will also need Maven 3.x.  To build, cd to the top level directory and:

    mvn clean install

Note that you still need implmentations of this API ;)  This project only provides the API and framework, not the underlying implementation.  For an implementation that is robust, distributed and in general awesome, see my [Nexus service discovery](https://github.com/Hellblazer/Nexus) project.

See the [project wiki](https://github.com/Hellblazer/Service-Location-Service/wiki) for design and usage.
    
### Maven configuration

include the hellblazer snapshot repository:

    <repository>
        <id>hellblazer-snapshots</id>
        <url>https://repository-hal900000.forge.cloudbees.com/snapshot/</url>
    </repository>
    
add as dependency:

    <dependency>
        <groupId>com.hellblazer</groupId>
        <artifactId>service-discovery</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>
