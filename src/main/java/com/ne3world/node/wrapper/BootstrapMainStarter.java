/*
 * Copyright 2007-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ne3world.node.wrapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;

public class BootstrapMainStarter {

    public void start(String[] args, File nodeHome) throws Exception {
        Process theProcess = null;
        BufferedReader inStream = null;

        try {
            theProcess = Runtime.getRuntime().exec("node.exe");
        } catch (IOException e) {
            System.err.println("Error en el método exec()");
            e.printStackTrace();
        }

        // leer en la corriente de salida estándar del programa llamado.
        try {
            inStream = new BufferedReader(new InputStreamReader(theProcess.getInputStream()));
            System.out.println(inStream.readLine());
        } catch (IOException e) {
            System.err.println("Error en inStream.readLine()");
            e.printStackTrace();
        }
    }

    static File findLauncherJar(File nodeHome) {
        File libDirectory = new File(nodeHome, "lib");
        if (libDirectory.exists() && libDirectory.isDirectory()) {
            File[] launcherJars = libDirectory.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.matches("node-launcher-.*\\.jar");
                }
            });
            if (launcherJars != null && launcherJars.length == 1) {
                return launcherJars[0];
            }
        }
        return null;
    }
}
