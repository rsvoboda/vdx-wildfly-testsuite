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

package org.wildfly.test.integration.vdx.utils;import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

public class FileUtils {

    // TODO check if Java 8 methods can't be used instead these methods

    public void copyFileFromResourcesToServer(String resourceFile, String targetDirectory, boolean override) throws Exception {
        if (resourceFile == null || "".equals(resourceFile)) {
            return;
        }

        Path sourcePath = getResourceFile(resourceFile);
        if (sourcePath == null) {
            throw new Exception("Resource file " + resourceFile + " does not exist.");
        }

        Path targetPath = Paths.get(targetDirectory, sourcePath.getFileName().toString());
        if (Files.exists(targetPath) && !override) {
            // file already exists in config directory so do nothing
            return;
        }

        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Copies file to directory. Overrides if the same file already exists in target directory.
     * @param file file to copy
     * @param directory target directory
     * @throws Exception when copy fails
     */
    public void copyFileToDirectory(Path file, Path directory) throws Exception {
        Files.copy(file, directory.resolve(file.getFileName()), StandardCopyOption.REPLACE_EXISTING);
    }

    private Path getResourceFile(String file) {
        ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource(file);
        if (url == null) {
            return null;
        } else {
            return Paths.get(url.getPath());
        }
    }

    public static String readFile(String path) throws IOException {
        File file = new File(path);
        StringBuilder fileContents = new StringBuilder((int)file.length());
        final String lineSeparator = System.getProperty("line.separator");

        try (Scanner scanner = new Scanner(file)) {
            while(scanner.hasNextLine()) {
                fileContents.append(scanner.nextLine());
                fileContents.append(lineSeparator);
            }
            return fileContents.toString();
        }
    }

    public static boolean isPathExists(Path path)   {
        return path.toFile().exists();
    }

    // just for local testing
//    public static void main(String[] args) {
//        System.out.println(new FileUtils().getResourceFile("duplicaste-attribute.xml"));
//    }

}
