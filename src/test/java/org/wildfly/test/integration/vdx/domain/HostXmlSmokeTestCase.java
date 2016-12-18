/*
 * Copyright 2016 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  *
 * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.wildfly.test.integration.vdx.domain;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.wildfly.extras.creaper.commands.foundation.offline.xml.GroovyXmlTransform;
import org.wildfly.extras.creaper.commands.foundation.offline.xml.Subtree;
import org.wildfly.extras.creaper.core.offline.OfflineCommand;
import org.wildfly.test.integration.vdx.TestBase;
import org.wildfly.test.integration.vdx.category.DomainTests;
import org.wildfly.test.integration.vdx.transformations.DoNothing;
import org.wildfly.test.integration.vdx.utils.server.ServerConfig;

import static org.junit.Assert.assertTrue;

/**
 *
 * Created by rsvoboda on 12/15/16.
 */

@RunAsClient
@RunWith(Arquillian.class)
@Category(DomainTests.class)
public class HostXmlSmokeTestCase extends TestBase {

    private final String simpleXmlElement = "unexpectedElement";
    private final String simpleXmlValue = "valueXYZ";
    private final String simpleXml = "<" + simpleXmlElement + ">" + simpleXmlValue + "</" + simpleXmlElement + ">\n";

    @Test
    @ServerConfig(configuration = "host.xml", xmlTransformationGroovy = "host/ManagementAuditLogElement.groovy")
    public void addManagementAuditLogElement() throws Exception {
        container().tryStartAndWaitForFail();

        String errorLog = container().getErrorMessageFromServerStart();
        assertTrue(errorLog.contains("^^^^ 'foo' isn't an allowed element here"));
        assertTrue(errorLog.contains("Elements allowed here are: formatters, handlers, logger, server-logger"));
    }

    private String addElementAndStart(Subtree subtree, String elementXml) throws Exception {
        container().tryStartAndWaitForFail(
                (OfflineCommand) ctx -> ctx.client.apply(GroovyXmlTransform.of(DoNothing.class, "AddElement.groovy")
                        .subtree("path", subtree).parameter("elementXml", elementXml)
                        .build()));

        String errorLog = container().getErrorMessageFromServerStart();
        assertTrue(errorLog.contains("^^^^ '" + simpleXmlElement + "' isn't an allowed element here"));
        return errorLog;
    }

    @Test
    @ServerConfig(configuration = "host.xml")
    public void appendElementInJvms() throws Exception {
        String errorLog = addElementAndStart( Subtree.jvms(), simpleXml);
        assertTrue(errorLog.contains("Elements allowed here are: jvm"));
    }

    @Test
    @ServerConfig(configuration = "host.xml")
    public void appendElementInServers() throws Exception {
        String errorLog = addElementAndStart( Subtree.servers(), simpleXml);
        assertTrue(errorLog.contains("Elements allowed here are: server"));
    }
    @Test
    @ServerConfig(configuration = "host.xml")
    public void appendElementInDC() throws Exception {
        String errorLog = addElementAndStart( Subtree.domainController(), simpleXml);
        assertTrue(errorLog.contains("Elements allowed here are: local, remote"));
    }
    @Test
    @ServerConfig(configuration = "host.xml")
    public void appendElementInInterfaces() throws Exception {
        String errorLog = addElementAndStart( Subtree.interfaces(), simpleXml);
        assertTrue(errorLog.contains("Elements allowed here are: interface"));
    }
    @Test
    @ServerConfig(configuration = "host.xml")
    public void appendElementInManagement() throws Exception {
        String errorLog = addElementAndStart( Subtree.management(), simpleXml);
        assertTrue(errorLog.contains("Elements allowed here are:"));
        assertTrue(errorLog.contains("audit-log"));
        assertTrue(errorLog.contains("management-interfaces"));
        assertTrue(errorLog.contains("configuration-changes"));
        assertTrue(errorLog.contains("outbound-connections"));
        assertTrue(errorLog.contains("identity"));
        assertTrue(errorLog.contains("security-realms"));

    }
    @Test
    @ServerConfig(configuration = "host.xml")
    public void appendElementInExtensions() throws Exception {
        String errorLog = addElementAndStart( Subtree.extensions(), simpleXml);
        assertTrue(errorLog.contains("Elements allowed here are: extension"));
    }
    @Test
    @ServerConfig(configuration = "host.xml")
    public void appendElementInProperties() throws Exception {
        String errorLog = addElementAndStart( Subtree.systemProperties(), simpleXml);
        assertTrue(errorLog.contains("Elements allowed here are: property"));
    }



}