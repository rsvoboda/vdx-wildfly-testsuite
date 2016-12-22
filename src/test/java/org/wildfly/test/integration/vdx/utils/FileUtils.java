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

package org.wildfly.test.integration.vdx.utils;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileUtils {

    // TODO check if Java 8 methods can't be used instead these methods

    public static void copyFileFromResourcesToServer(String resourceFile, Path targetDirectory, boolean override) throws Exception {
        if (resourceFile == null || "".equals(resourceFile)) {
            return;
        }

        Path sourcePath = getResourceFile(resourceFile);
        if (sourcePath == null) {
            throw new Exception("Resource file " + resourceFile + " does not exist.");
        }

        Path targetPath = Paths.get(targetDirectory.toString(), sourcePath.getFileName().toString());
        if (Files.exists(targetPath) && !override) {
            // file already exists in config directory so do nothing
            return;
        }

        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
    }

    private static Path getResourceFile(String file) throws URISyntaxException {
        ClassLoader classLoader = FileUtils.class.getClassLoader();
        URL url = classLoader.getResource(file);
        if (url == null) {
            return null;
        } else {
            return Paths.get(url.toURI()); // toURI is Windows-friendly
        }
    }

}
