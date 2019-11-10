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
import java.util.concurrent.TimeUnit;

public class BootstrapMainStarter {

    public void start(String[] args, File nodeHome) throws Exception {
        Process theProcess = null;
        BufferedReader inStream = null, errStream = null;
        int tmpContArgs = 1;
        
        String[] tmpArgs = new String[args.length+1];

        if (args != null && "--npm".equals(args[0])) {
        	tmpArgs = new String[args.length];
        	tmpArgs[0] = nodeHome + "\\npm.cmd";
    	} else if (args != null && "--npx".equals(args[0])) {
        	tmpArgs = new String[args.length];
        	tmpArgs[0] = nodeHome + "\\npx.cmd";
        } else {
    		tmpArgs[0] = nodeHome + "\\node.exe";
    	}
        
        for (String tmpArg : args) {
        	if (!"--npm".equals(tmpArg))
        			tmpArgs[tmpContArgs++] = tmpArg;
        }
        
        try {
            theProcess = Runtime.getRuntime().exec(tmpArgs);
        } catch (IOException e) {
            System.err.println("Error en el método exec()");
            e.printStackTrace();
        }

        // leer en la corriente de salida estándar del programa llamado.
        try {
            inStream = new BufferedReader(new InputStreamReader(theProcess.getInputStream()));
            errStream = new BufferedReader(new InputStreamReader(theProcess.getErrorStream()));
            while (theProcess.isAlive()) {
                theProcess.waitFor(500, TimeUnit.MILLISECONDS);
                System.out.println(inStream.readLine());
                System.out.println(errStream.readLine());
            }
        } catch (IOException e) {
            System.err.println("Error en inStream.readLine()");
            e.printStackTrace();
        } catch (InterruptedException ie) {
            System.err.println("Interrumpido");
        } finally {
            inStream.close();
            errStream.close();
        }
    }

    static File findLauncher(File nodeHome) {
        File directory = new File(nodeHome, "./");
        if (directory.exists() && directory.isDirectory()) {
            File[] launcher = directory.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.matches("node*.exe");
                }
            });
            if (launcher != null && launcher.length == 1) {
                return launcher[0];
            }
        }
        return null;
    }
}
