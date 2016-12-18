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
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.wildfly.test.integration.vdx.TestBase;
import org.wildfly.test.integration.vdx.category.StandaloneTests;
import org.wildfly.test.integration.vdx.utils.server.ServerConfig;

import static org.junit.Assert.assertTrue;

/**
 *
 * Created by rsvoboda on 11/30/16.
 */

@RunAsClient
@RunWith(Arquillian.class)
@Category(StandaloneTests.class)
public class JBossWSTestCase extends TestBase {

    /*
     * <modify-wsdl-address /> instead of <modify-wsdl-address>true</modify-wsdl-address>
     */
    @Test
    @ServerConfig(configuration = "standalone.xml", xmlTransformationGroovy = "webservices/AddModifyWsdlAddressElementWithNoValue.groovy",
            subtreeName = "webservices", subsystemName = "webservices")
    public void modifyWsdlAddressElementWithNoValue()throws Exception {
        container().tryStartAndWaitForFail();

        String errorLog = container().getErrorMessageFromServerStart();
        assertTrue(errorLog.contains("OPVDX001: Validation error in standalone.xml"));
        assertTrue(errorLog.contains("<modify-wsdl-address/>"));
        assertTrue(errorLog.contains(" ^^^^ Wrong type for 'modify-wsdl-address'. Expected [BOOLEAN] but was"));
        assertTrue(errorLog.contains("|                  STRING"));
    }

    /*
     * <mmodify-wsdl-address>true</mmodify-wsdl-address> instead of <modify-wsdl-address>true</modify-wsdl-address>
     */
    @Test
    @ServerConfig(configuration = "standalone.xml", xmlTransformationGroovy = "webservices/AddIncorrectlyNamedModifyWsdlAddressElement.groovy",
            subtreeName = "webservices", subsystemName = "webservices")
    public void incorrectlyNamedModifyWsdlAddressElement()throws Exception {
        container().tryStartAndWaitForFail();

        String errorLog = container().getErrorMessageFromServerStart();
        assertTrue(errorLog.contains("OPVDX001: Validation error in standalone.xml"));
        assertTrue(errorLog.contains("<mmodify-wsdl-address>true</mmodify-wsdl-address>"));
        assertTrue(errorLog.contains("^^^^ 'mmodify-wsdl-address' isn't an allowed element here"));
        assertTrue(errorLog.contains(" Did you mean 'modify-wsdl-address'?"));
        assertTrue(errorLog.contains("Elements allowed here are:"));
        assertTrue(errorLog.contains("client-config"));
        assertTrue(errorLog.contains("wsdl-path-rewrite-rule"));
        assertTrue(errorLog.contains("endpoint-config"));
        assertTrue(errorLog.contains("wsdl-port"));
        assertTrue(errorLog.contains("modify-wsdl-address"));
        assertTrue(errorLog.contains("wsdl-secure-port"));
        assertTrue(errorLog.contains("wsdl-host"));
        assertTrue(errorLog.contains("wsdl-uri-scheme"));
    }

    /*
     * <modify-wsdl-address>ttrue</modify-wsdl-address> instead of <modify-wsdl-address>true</modify-wsdl-address>
     */
    @Test
    @ServerConfig(configuration = "standalone.xml", xmlTransformationGroovy = "webservices/AddModifyWsdlAddressElementWithIncorrectValue.groovy",
            subtreeName = "webservices", subsystemName = "webservices")
    public void incorrectValueOfModifyWsdlAddressElement()throws Exception {
        container().tryStartAndWaitForFail();

        String errorLog = container().getErrorMessageFromServerStart();
        assertTrue(errorLog.contains("OPVDX001: Validation error in standalone.xml"));
        assertTrue(errorLog.contains("<modify-wsdl-address>ttrue</modify-wsdl-address>"));
        assertTrue(errorLog.contains(" ^^^^ Wrong type for 'modify-wsdl-address'. Expected [BOOLEAN] but was"));
        assertTrue(errorLog.contains("                  STRING"));
    }

    /*
     * use webservices:1.1 instead of webservices:2.0 schema
     */
    @Test
    @ServerConfig(configuration = "standalone.xml", xmlTransformationGroovy = "ModifySubsystemConfiguration.groovy",
            subtreeName = "subsystem", subsystemName = "webservices",
            parameterName = "configurationXml",
            parameterValue =
                "        <subsystem xmlns=\"urn:jboss:domain:webservices:1.1\">\n"
                + "            <wsdl-host>${jboss.bind.address:127.0.0.1}</wsdl-host>\n"
                + "            <endpoint-config name=\"Standard-Endpoint-Config\"/>\n"
                + "            <endpoint-config name=\"Recording-Endpoint-Config\">\n"
                + "                <pre-handler-chain name=\"recording-handlers\" protocol-bindings=\"##SOAP11_HTTP ##SOAP11_HTTP_MTOM ##SOAP12_HTTP ##SOAP12_HTTP_MTOM\">\n"
                + "                    <handler name=\"RecordingHandler\" class=\"org.jboss.ws.common.invocation.RecordingServerHandler\"/>\n"
                + "                </pre-handler-chain>\n"
                + "            </endpoint-config>\n"
                + "            <client-config name=\"Standard-Client-Config\"/>\n"
                + "        </subsystem>"
    )
    public void oldSubsystemVersionOnNewerConfiguration()throws Exception {
        container().tryStartAndWaitForFail();

        String errorLog = container().getErrorMessageFromServerStart();
        assertTrue(errorLog.contains("OPVDX001: Validation error in standalone.xml"));
        assertTrue(errorLog.contains("^^^^ 'client-config' isn't an allowed element here"));
        assertTrue(errorLog.contains("Elements allowed here are: endpoint-config, modify-wsdl-address,"));
        assertTrue(errorLog.contains("wsdl-host, wsdl-port, wsdl-secure-port"));
    }

}