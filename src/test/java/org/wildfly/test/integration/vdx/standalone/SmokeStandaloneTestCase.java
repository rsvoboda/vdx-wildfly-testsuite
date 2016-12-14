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

package org.wildfly.test.integration.vdx.standalone;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.wildfly.test.integration.vdx.TestBase;
import org.wildfly.test.integration.vdx.category.StandaloneTests;
import org.wildfly.test.integration.vdx.transformations.AddNonExistentElementToMessagingSubsystem;
import org.wildfly.test.integration.vdx.transformations.TypoInExtensions;
import org.wildfly.test.integration.vdx.utils.StringRegexUtils;
import org.wildfly.test.integration.vdx.utils.server.ServerConfig;

/**
 * Smoke test case - it tests whether Wildlfy/EAP test automation is working and basic VDX functionality.
 */
@RunAsClient
@RunWith(Arquillian.class)
@Category(StandaloneTests.class)
public class SmokeStandaloneTestCase extends TestBase {

    @Test
    @ServerConfig(configuration = "duplicate-attribute.xml")
    public void testWithExistingConfigInResources() throws Exception {
        container().tryStartAndWaitForFail();
        // assert that log contains bad message
        String expectedErrorMessage = "OPVDX001: Validation error in duplicate-attribute.xml --------------------------\n" +
                "|\n" +
                "|  123: <job-repository name=\"in-memory\">\n" +
                "|  124:   <jdbc data-source=\"foo\"\n" +
                "|  125:         data-source=\"bar\"/>\n" +
                "|               ^^^^ 'data-source' can't appear more than once on this element\n" +
                "|\n" +
                "|  126: </job-repository>\n" +
                "|  127: <thread-pool name=\"batch\">\n" +
                "|  128:     <max-threads count=\"10\"/>\n" +
                "|\n" +
                "| A 'data-source' attribute first appears here:\n" +
                "|\n" +
                "|  122: <default-thread-pool name=\"batch\"/>\n" +
                "|  123: <job-repository name=\"in-memory\">\n" +
                "|  124:   <jdbc data-source=\"foo\"\n" +
                "|               ^^^^\n" +
                "|\n" +
                "|  125:         data-source=\"bar\"/>\n" +
                "|  126: </job-repository>\n" +
                "|  127: <thread-pool name=\"batch\">\n" +
                "|\n" +
                "| The primary underlying error message was:\n" +
                "| > Duplicate attribute 'data-source'.\n" +
                "| >  at [row,col {unknown-source}]: [125,39]\n" +
                "|\n" +
                "|-------------------------------------------------------------------------------";

        assertExpectedError(StringRegexUtils.addLinesToListAndEscapeRegexChars(StringRegexUtils.removeLineNumbersWithDoubleDotFromString(expectedErrorMessage)),
                container().getErrorMessageFromServerStart());
        assertExpectedError(StringRegexUtils.convertStringLinesToOneRegex(StringRegexUtils.removeLineNumbersWithDoubleDotFromString(expectedErrorMessage)),
                container().getErrorMessageFromServerStart());
    }

    @Test
    @ServerConfig(configuration = "standalone-full-ha-to-damage.xml", xmlTransformationClass = TypoInExtensions.class)
    public void typoInExtensionsWithConfigInResources() throws Exception {
        container().tryStartAndWaitForFail();
        // assert that log contains bad message
        String expectedErrorMessage = "OPVDX001: Validation error in standalone-full-ha-to-damage.xml ----------------\n" +
                "|\n" +
                "|  1: <?xml version=\"1.0\" encoding=\"UTF-8\"?><server xmlns=\"urn:jboss:domain:5.0\">\n" +
                "|  2:   <extensions>\n" +
                "|  3:     <extension modules=\"org.aaajboss.as.clustering.infinispan\"/>\n" +
                "|                    ^^^^ 'modules' isn't an allowed attribute for the 'extension' element\n" +
                "|                         \n" +
                "|                         Did you mean 'module'?\n" +
                "|                         \n" +
                "|                         Attributes allowed here are: module \n" +
                "|\n" +
                "|  4:     <extension modules=\"org.aaajboss.as.clustering.infinispan\"/>\n" +
                "|  5:     <extension modules=\"org.aaajboss.as.clustering.infinispan\"/>\n" +
                "|  6:     <extension modules=\"org.aaajboss.as.clustering.infinispan\"/>\n" +
                "|\n" +
                "| The primary underlying error message was:\n" +
                "| > ParseError at [row,col]:[3,5]\n" +
                "| > Message: WFLYCTL0197: Unexpected attribute 'modules' encountered\n" +
                "|\n" +
                "|-------------------------------------------------------------------------------\n";

        assertExpectedError(StringRegexUtils.addLinesToListAndEscapeRegexChars(StringRegexUtils.removeLineNumbersWithDoubleDotFromString(expectedErrorMessage)),
                container().getErrorMessageFromServerStart());
    }

    // FIXME
    /*  returned response
    OPVDX001: Validation error in standalone-full-ha.xml ---------------------------
|
|  336: <subsystem xmlns="urn:jboss:domain:messaging-activemq:1.1">
|  337:   <server name="default">
|  338:     <cluster id="3"/>
|                    ^^^^ 'id' isn't an allowed attribute for the 'cluster' element
|
|                         Attributes allowed here are: name, password, user
|
|  339:     <security-setting name="#">
|  340:       <role delete-non-durable-queue="true" name="guest" consume="true" create-non-durable-queue="true" send="true"/>
|  341:     </security-setting>
|
| 'id' is allowed on elements:
| - server > profile > {urn:jboss:domain:resource-adapters:4.0}subsystem > resource-adapters > resource-adapter
| - server > profile > {urn:jboss:domain:resource-adapters:4.0}subsystem > resource-adapters > resource-adapter > module
|
|
| The primary underlying error message was:
| > ParseError at [row,col]:[338,9]
| > Message: WFLYCTL0376: Unexpected attribute 'id' encountered. Valid
| >   attributes are: 'user, password, name'
|
|-------------------------------------------------------------------------------
     */
    @Test
    @Ignore("FIXME, wrong expectation on journal element presence !!!!!!!!!!!")
    @ServerConfig(configuration = "standalone-full-ha.xml", xmlTransformationClass = AddNonExistentElementToMessagingSubsystem.class)
    public void addNonExistingElementToMessagingSubsystem() throws Exception {
        container().tryStartAndWaitForFail();
        // assert that log contains bad message
        String expectedErrorMessage = "OPVDX001: Validation error in standalone-full-ha.xml ---------------------------\n" +
                "|\n" +
                "|  370: <subsystem xmlns=\"urn:jboss:domain:messaging-activemq:1.1\">\n" +
                "|  371:   <server name=\"default\">\n" +
                "|  372:     <cluster id=\"3\"/>\n" +
                "|                    ^^^^ 'id' isn't an allowed attribute for the 'cluster' element\n" +
                "|                         \n" +
                "|                         Attributes allowed here are: name, password, user \n" +
                "|\n" +
                "|  373:     <journal min-files=\"10\" compact-min-files=\"0\" type=\"ASYNCIO\"/>\n" +
                "|  374:     <security enabled=\"false\"/>\n" +
                "|  375:     <security-setting name=\"#\">\n" +
                "|\n" +
                "| 'id' is allowed on elements: \n" +
                "| - server > profile > {urn:jboss:domain:resource-adapters:4.0}subsystem > resource-adapters > resource-adapter\n" +
                "| - server > profile > {urn:jboss:domain:resource-adapters:4.0}subsystem > resource-adapters > resource-adapter > module\n" +
                "|\n" +
                "|\n" +
                "| The primary underlying error message was:\n" +
                "| > ParseError at [row,col]:[372,9]\n" +
                "| > Message: WFLYCTL0376: Unexpected attribute 'id' encountered. Valid\n" +
                "| >   attributes are: 'user, password, name'\n" +
                "|\n" +
                "|-------------------------------------------------------------------------------";

        assertExpectedError(StringRegexUtils.addLinesToListAndEscapeRegexChars(StringRegexUtils.removeLineNumbersWithDoubleDotFromString(expectedErrorMessage)),
                container().getErrorMessageFromServerStart());
    }
}