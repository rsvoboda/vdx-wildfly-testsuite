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
import org.wildfly.extras.creaper.core.offline.OfflineCommandContext;
import org.wildfly.test.integration.vdx.TestBase;
import org.wildfly.test.integration.vdx.category.DomainTests;
import org.wildfly.test.integration.vdx.transformations.AddNonExistentElementToMessagingSubsystem;
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

    @Test
    @ServerConfig(configuration = "host.xml", xmlTransformationGroovy = "host/ManagementAuditLogElement.groovy")
    public void addUnexpectedExtension() throws Exception {
        container().tryStartAndWaitForFail();
    }

    private void addElementAndStart(Subtree subtree, String elementXml) throws Exception {
        container().tryStartAndWaitForFail(
                (OfflineCommand) ctx -> ctx.client.apply(GroovyXmlTransform.of(DoNothing.class, "AddElement.groovy")
                        .subtree("path", subtree).parameter("elementXml", elementXml)
                        .build()));
    }

    @Test
    @ServerConfig(configuration = "host.xml")
    public void appendElementInJvms() throws Exception {
        addElementAndStart( Subtree.jvms(), "<element>value</element>\n");
    }
    @Test
    @ServerConfig(configuration = "host.xml")
    public void appendElementInServers() throws Exception {
        addElementAndStart( Subtree.servers(), "<element>value</element>\n");
    }
    @Test
    @ServerConfig(configuration = "host.xml")
    public void appendElementInDC() throws Exception {
        addElementAndStart( Subtree.domainController(), "<element>value</element>\n");
    }
    @Test
    @ServerConfig(configuration = "host.xml")
    public void appendElementInInterfaces() throws Exception {
        addElementAndStart( Subtree.interfaces(), "<element>value</element>\n");
    }
    @Test
    @ServerConfig(configuration = "host.xml")
    public void appendElementInManagement() throws Exception {
        addElementAndStart( Subtree.management(), "<element>value</element>\n");
    }
    @Test
    @ServerConfig(configuration = "host.xml")
    public void appendElementInExtensions() throws Exception {
        addElementAndStart( Subtree.extensions(), "<element>value</element>\n");
    }
    @Test
    @ServerConfig(configuration = "host.xml")
    public void appendElementInProperties() throws Exception {
        addElementAndStart( Subtree.systemProperties(), "<element>value</element>\n");
    }



}