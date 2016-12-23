package org.wildfly.test.integration.vdx.standalone;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.wildfly.test.integration.vdx.TestBase;
import org.wildfly.test.integration.vdx.category.StandaloneTests;
import org.wildfly.test.integration.vdx.utils.server.ServerConfig;

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
        assertDoesNotContain(errorLog,"more)");  // something like '(and 24 more)' shouldn't be in the log
        assertContains(errorLog,"<foo>bar</foo>");
        assertContains(errorLog,"^^^^ 'foo' isn't an allowed element here");
        assertContains(errorLog,"Elements allowed here are: ");
        assertContains(errorLog,"acceptor");
        assertContains(errorLog,"address-setting");
        assertContains(errorLog,"bindings-directory");
        assertContains(errorLog,"bridge");
        assertContains(errorLog,"broadcast-group");
        assertContains(errorLog,"cluster-connection");
        assertContains(errorLog,"connection-factory");
        assertContains(errorLog,"connector");
        assertContains(errorLog,"connector-service");
        assertContains(errorLog,"discovery-group");
        assertContains(errorLog,"divert");
        assertContains(errorLog,"grouping-handler");
        assertContains(errorLog,"http-acceptor");
        assertContains(errorLog,"http-connector");
        assertContains(errorLog,"in-vm-acceptor");
        assertContains(errorLog,"in-vm-connector");
        assertContains(errorLog,"jms-queue");
        assertContains(errorLog,"jms-topic");
        assertContains(errorLog,"journal-directory");
        assertContains(errorLog,"large-messages-directory");
        assertContains(errorLog,"legacy-connection-factory");
        assertContains(errorLog,"live-only");
        assertContains(errorLog,"paging-directory");
        assertContains(errorLog,"pooled-connection-factory");
        assertContains(errorLog,"queue");
        assertContains(errorLog,"remote-acceptor");
        assertContains(errorLog,"remote-connector");
        assertContains(errorLog,"replication-colocated");
        assertContains(errorLog,"replication-master");
        assertContains(errorLog,"replication-slave");
        assertContains(errorLog,"security-setting");
        assertContains(errorLog,"shared-store-colocated");
        assertContains(errorLog,"shared-store-master");
        assertContains(errorLog,"shared-store-slave");

    }

}
