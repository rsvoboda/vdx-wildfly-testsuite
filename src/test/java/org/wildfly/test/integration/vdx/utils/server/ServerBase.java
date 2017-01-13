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

package org.wildfly.test.integration.vdx.utils.server;

import org.jboss.arquillian.container.test.api.ContainerController;
import org.junit.Assert;
import org.wildfly.extras.creaper.commands.foundation.offline.ConfigurationFileBackup;
import org.wildfly.extras.creaper.commands.foundation.offline.xml.GroovyXmlTransform;
import org.wildfly.extras.creaper.commands.foundation.offline.xml.Subtree;
import org.wildfly.extras.creaper.core.offline.OfflineCommand;
import org.wildfly.extras.creaper.core.offline.OfflineManagementClient;
import org.wildfly.test.integration.vdx.transformations.DoNothing;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class ServerBase implements Server {

    protected Path testArchiveDirectory = null;
    private ConfigurationFileBackup configurationFileBackup = new ConfigurationFileBackup();
    private static Server server = null;

    @Override
    public void tryStartAndWaitForFail(OfflineCommand... offlineCommands) throws Exception {

        // stop server if running
        stop();

        // copy logging.properties
        copyLoggingPropertiesToConfiguration();

        // if configuration file is not in configuration directory then copy from resources directory (never override)
        copyConfigFilesFromResourcesIfItDoesNotExist();

        // backup config
        backupConfiguration();

        // apply transformation(s)
        if (offlineCommands == null) {
            applyXmlTransformation();
        } else {
            getOfflineManagementClient().apply(offlineCommands);
        }

        // archive configuration used during server start
        archiveModifiedUsedConfig();

        try {
            // tryStartAndWaitForFail - this must throw exception due invalid xml
            startServer();

            // fail the test if server starts
            Assert.fail("Server started successfully - probably xml was not invalidated/damaged correctly.");

        } catch (Exception ex) {
            System.out.println("Start of the server failed. This is expected.");
        } finally {

            // restore original config if it exists
            restoreConfigIfBackupExists();
        }
    }

    @Override
    public void tryStartAndWaitForFail() throws Exception {
        tryStartAndWaitForFail(null);
    }

    @Override
    public abstract Path getServerLogPath();

    /**
     * Copies logging.properties which will log ERROR messages to target/errors.log file
     *
     * @throws Exception when copy fails
     */
    protected abstract void copyLoggingPropertiesToConfiguration() throws Exception;

    /**
     * This will copy config file from resources directory to configuration directory of application server
     * This never overrides existing files, so the file with the same name in configuration directory of server has precedence
     *
     * @throws Exception when copy operation
     */
    protected abstract void copyConfigFilesFromResourcesIfItDoesNotExist() throws Exception;

    protected abstract void archiveModifiedUsedConfig() throws Exception;

    protected abstract OfflineManagementClient getOfflineManagementClient() throws Exception;

    protected abstract void startServer() throws Exception;

    @Override
    public String getErrorMessageFromServerStart() throws Exception {
        return String.join("\n", Files.readAllLines(Paths.get(ERRORS_LOG_FILE_NAME)));
    }

    private void backupConfiguration() throws Exception {
        // destroy any existing backup config
        getOfflineManagementClient().apply(configurationFileBackup.destroy());
        // backup any existing config
        getOfflineManagementClient().apply(configurationFileBackup.backup());
    }

    private void restoreConfigIfBackupExists() throws Exception {
        if (configurationFileBackup == null) {
            throw new Exception("Backup config is null. This can happen if this method is called before " +
                    "startServer() call. Check tryStartAndWaitForFail() sequence that backupConfiguration() was called.");
        }
        System.out.println("Restoring server configuration. Configuration to be restored " + getServerConfig());
        getOfflineManagementClient().apply(configurationFileBackup.restore());
    }

    /**
     * Damages xml config file only if config file has valid syntax. This relies on well-formed xml.
     *
     * @throws Exception if not valid xml transformation
     */
    @SuppressWarnings("deprecation")
    private void applyXmlTransformation() throws Exception {
        ServerConfig serverConfig = getServerConfig();

        if (serverConfig.xmlTransformationGroovy().equals("")) {

            // skip as no transformation is needed - e.g. using prepared configuration

        } else {

            if (serverConfig.subtreeName().equals("")) {  // standalone or domain case without subtree
                getOfflineManagementClient()
                        .apply(GroovyXmlTransform.of(DoNothing.class, serverConfig.xmlTransformationGroovy())
                                .parameter(serverConfig.parameterName(), serverConfig.parameterValue())
                                .build());
                return;
            }
            if (serverConfig.profileName().equals("")) {  // standalone case with subtree
                getOfflineManagementClient()
                        .apply(GroovyXmlTransform.of(DoNothing.class, serverConfig.xmlTransformationGroovy())
                                .subtree(serverConfig.subtreeName(), Subtree.subsystem(serverConfig.subsystemName()))
                                .parameter(serverConfig.parameterName(), serverConfig.parameterValue())
                                .build());

            } else {  // domain case with subtree
                getOfflineManagementClient()
                        .apply(GroovyXmlTransform.of(DoNothing.class, serverConfig.xmlTransformationGroovy())
                                .subtree(serverConfig.subtreeName(), Subtree.subsystemInProfile(serverConfig.profileName(), serverConfig.subsystemName()))
                                .parameter(serverConfig.parameterName(), serverConfig.parameterValue())
                                .build());

            }
        }

    }

    /**
     * @return returns Search stacktrace for @ServerConfig annotation and return it, returns null if there is none
     */
    static ServerConfig getServerConfig() {
        Throwable t = new Throwable();
        StackTraceElement[] elements = t.getStackTrace();
        String callerMethodName;
        String callerClassName;
        ServerConfig serverConfig = null;

        for (int level = 1; level < elements.length; level++) {
            try {
                callerClassName = elements[level].getClassName();
                callerMethodName = elements[level].getMethodName();
                Method method = Class.forName(callerClassName).getMethod(callerMethodName);
                serverConfig = method.getAnnotation(ServerConfig.class);
                if (serverConfig != null) {
                    break;
                }
            } catch (Exception e) {
                // ignore
            }
        }
        return serverConfig;
    }

    /**
     * Creates instance of server. If -Ddomain=true system property is specified it will be domain server,
     * otherwise standalone server will be used.
     *
     * @param controller arquillian container controller
     * @return Server instance - standalone by default or domain if -Ddomain=true is set
     */
    public static Server getOrCreateServer(ContainerController controller) {
        if (server == null) {
            if (Server.isDomain()) {
                server = new ServerDomain(controller);
            } else {
                server = new ServerStandalone(controller);
            }
        }
        return server;
    }

    @Override
    public void setTestArchiveDirectory(Path testArchiveDirectory) {
        this.testArchiveDirectory = testArchiveDirectory;
    }
}
