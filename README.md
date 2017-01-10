# vdx-wildfly-testsuite
Testsuite for projectodd/vdx [https://github.com/projectodd/vdx/] pretty print feature in WildFly

[![Build Status](https://travis-ci.org/jboss-eap-qe/vdx-wildfly-testsuite.svg?branch=master)](https://travis-ci.org/jboss-eap-qe/vdx-wildfly-testsuite)

Running the Testsuite
-------------------

Ensure you have JDK 8 (or newer) installed

> java -version

Ensure you have Maven 3.2.5 (or newer) installed

> mvn -version

To run against local (e.g. latest WildFly master build) server use following command:

> mvn -P all test -Djboss.home=/path/to/location/of/the/server

To run against released WildFly 10.1.0.Final (will be downloaded) use following command, :

> mvn -P all test

Profile `all` redirects test output to file, so look at $TEST_NAME-output.txt files in target/surefire directory if needed.


Running one test
-------------------

To run test against application server running in standalone mode:

> mvn test -Djboss.home=/path/to/location/of/the/server -Dtest=$TEST

To run test against application server running in domain mode:

> mvn test -Djboss.home=/path/to/location/of/the/server -Ddomain -Dtest=$TEST

`-Ddomain` is hint to use correct `wildfly-arquillian-container` artifact

Of course you can specify concrere test not just whole TestCase class - for example `JBossWSTestCase#incorrectValueOfModifyWsdlAddressElement`


Used technologies
-----------------

JDK 8 (https://docs.oracle.com/javase/8)
 
Apache Maven 3.2.5 (https://maven.apache.org)

Creaper (https://github.com/wildfly-extras/creaper/)

Arquillian (http://arquillian.org/)

JUnit (http://junit.org/junit4/)

Groovy (http://www.groovy-lang.org/)


Travis CI
---------

There is Travis CI configured to test PRs. 
  	    