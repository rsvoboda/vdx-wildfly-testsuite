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
    @ServerConfig(configuration = "standalone.xml", xmlTransformationGroovy = "webservices/AddWsdlAddressElementWithNoValue.groovy",
            subtreeName = "webservices", subsystemName = "webservices")
    public void addWsdlAddressElementWithNoValue()throws Exception {
        container().tryStartAndWaitForFail();

        String errorLog = container().getErrorMessageFromServerStart();
        assertTrue(errorLog.contains("OPVDX001: Validation error in standalone.xml"));
        assertTrue(errorLog.contains("<modify-wsdl-address/>"));
        assertTrue(errorLog.contains(" ^^^^ Wrong type for 'modify-wsdl-address'. Expected [BOOLEAN] but was"));
        assertTrue(errorLog.contains("|                  STRING"));

    }
}