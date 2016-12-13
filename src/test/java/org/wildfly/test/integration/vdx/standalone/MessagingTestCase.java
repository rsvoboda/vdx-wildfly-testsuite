package org.wildfly.test.integration.vdx.standalone;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.wildfly.test.integration.vdx.TestBase;
import org.wildfly.test.integration.vdx.category.StandaloneTests;
import org.wildfly.test.integration.vdx.utils.server.ServerConfig;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 * Created by rsvoboda on 12/13/16.
 */

@RunAsClient
@RunWith(Arquillian.class)
@Category(StandaloneTests.class)
public class MessagingTestCase extends TestBase {

    /*
     * append invalid element to subsystem definition
     * check that all elements are listed
     */
    @Test
    @ServerConfig(configuration = "standalone-full-ha.xml", xmlTransformationGroovy = "messaging/AddFooBar.groovy",
            subtreeName = "messaging", subsystemName = "messaging-activemq")
    public void modifyWsdlAddressElementWithNoValue() throws Exception {
        container().tryStartAndWaitForFail();

        String errorLog = container().getErrorMessageFromServerStart();
        assertFalse(errorLog.contains("more)"));  // something like '(and 24 more)' shouldn't be in the log
        assertTrue(errorLog.contains("<foo>bar</foo>"));
        assertTrue(errorLog.contains("^^^^ 'foo' isn't an allowed element here"));
        assertTrue(errorLog.contains("Elements allowed here are: "));
        assertTrue(errorLog.contains("acceptor"));
        assertTrue(errorLog.contains("address-setting"));
        assertTrue(errorLog.contains("bindings-directory"));
        assertTrue(errorLog.contains("bridge"));
        assertTrue(errorLog.contains("broadcast-group"));
        assertTrue(errorLog.contains("cluster-connection"));
        assertTrue(errorLog.contains("connection-factory"));
        assertTrue(errorLog.contains("connector"));
        assertTrue(errorLog.contains("connector-service"));
        assertTrue(errorLog.contains("discovery-group"));
        assertTrue(errorLog.contains("divert"));
        assertTrue(errorLog.contains("grouping-handler"));
        assertTrue(errorLog.contains("http-acceptor"));
        assertTrue(errorLog.contains("http-connector"));
        assertTrue(errorLog.contains("in-vm-acceptor"));
        assertTrue(errorLog.contains("in-vm-connector"));
        assertTrue(errorLog.contains("jms-queue"));
        assertTrue(errorLog.contains("jms-topic"));
        assertTrue(errorLog.contains("journal-directory"));
        assertTrue(errorLog.contains("large-messages-directory"));
        assertTrue(errorLog.contains("legacy-connection-factory"));
        assertTrue(errorLog.contains("live-only"));
        assertTrue(errorLog.contains("paging-directory"));
        assertTrue(errorLog.contains("pooled-connection-factory"));
        assertTrue(errorLog.contains("queue"));
        assertTrue(errorLog.contains("remote-acceptor"));
        assertTrue(errorLog.contains("remote-connector"));
        assertTrue(errorLog.contains("replication-colocated"));
        assertTrue(errorLog.contains("replication-master"));
        assertTrue(errorLog.contains("replication-slave"));
        assertTrue(errorLog.contains("security-setting"));
        assertTrue(errorLog.contains("shared-store-colocated"));
        assertTrue(errorLog.contains("shared-store-master"));
        assertTrue(errorLog.contains("shared-store-slave"));

    }

}
