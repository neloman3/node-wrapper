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

import static com.ne3world.node.wrapper.Download.UNKNOWN_VERSION;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Properties;

import com.ne3world.node.wrapper.cli.CommandLineParser;
import com.ne3world.node.wrapper.cli.ParsedCommandLine;
import com.ne3world.node.wrapper.cli.SystemPropertiesCommandLineConverter;

public class NodeWrapperMain {
    public static final String NODE_USER_HOME_OPTION = "n";
    public static final String NODE_USER_HOME_DETAILED_OPTION = "node-user-home";
    public static final String NODE_QUIET_OPTION = "q";
    public static final String NODE_QUIET_DETAILED_OPTION = "quiet";

    public static void main(String[] args) throws Exception {
        File wrapperJar = wrapperJar();
        File propertiesFile = wrapperProperties(wrapperJar);
        File rootDir = rootDir(wrapperJar);

        CommandLineParser parser = new CommandLineParser();
        parser.allowUnknownOptions();
        parser.option(NODE_USER_HOME_OPTION, NODE_USER_HOME_DETAILED_OPTION).hasArgument();
        parser.option(NODE_QUIET_OPTION, NODE_QUIET_DETAILED_OPTION);

        SystemPropertiesCommandLineConverter converter = new SystemPropertiesCommandLineConverter();
        converter.configure(parser);

        ParsedCommandLine options = parser.parse(args);

        Properties systemProperties = System.getProperties();
        systemProperties.putAll(converter.convert(options, new HashMap<String, String>()));

        File nodeUserHome = nodeUserHome(options);

        addSystemProperties(nodeUserHome, rootDir);

        Logger logger = logger(options);

        WrapperExecutor wrapperExecutor = WrapperExecutor.forWrapperPropertiesFile(propertiesFile);
        wrapperExecutor.execute(
                args,
                new Install(logger, new Download(logger, "nodew", UNKNOWN_VERSION), new PathAssembler(nodeUserHome)),
                new BootstrapMainStarter());
    }

    private static void addSystemProperties(File nodeHome, File rootDir) {
        System.getProperties().putAll(SystemPropertiesHandler.getSystemProperties(new File(nodeHome, "node.properties")));
        System.getProperties().putAll(SystemPropertiesHandler.getSystemProperties(new File(rootDir, "node.properties")));
    }

    private static File rootDir(File wrapperJar) {
        return wrapperJar.getParentFile().getParentFile().getParentFile();
    }

    private static File wrapperProperties(File wrapperJar) {
        return new File(wrapperJar.getParent(), wrapperJar.getName().replaceFirst("\\.jar$", ".properties"));
    }

    private static File wrapperJar() {
        URI location;
        try {
            location = NodeWrapperMain.class.getProtectionDomain().getCodeSource().getLocation().toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        if (!location.getScheme().equals("file")) {
            throw new RuntimeException(String.format("Cannot determine classpath for wrapper Jar from codebase '%s'.", location));
        }
        try {
            return Paths.get(location).toFile();
        } catch (NoClassDefFoundError e) {
            return new File(location.getPath());
        }
    }

    private static File nodeUserHome(ParsedCommandLine options) {
        if (options.hasOption(NODE_USER_HOME_OPTION)) {
            return new File(options.option(NODE_USER_HOME_OPTION).getValue());
        }
        return NodeUserHomeLookup.nodeUserHome();
    }

    private static Logger logger(ParsedCommandLine options) {
        return new Logger(options.hasOption(NODE_QUIET_OPTION));
    }
}
