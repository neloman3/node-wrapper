/*
 * Copyright 2014 the original author or authors.
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

import java.io.File;

public class NodeUserHomeLookup {
    public static final String DEFAULT_NODE_USER_HOME = System.getProperty("user.home") + "/.node";
    public static final String NODE_USER_HOME_PROPERTY_KEY = "node.user.home";
    public static final String NODE_USER_HOME_ENV_KEY = "NODE_USER_HOME";

    public static File nodeUserHome() {
        String nodeUserHome;
        if ((nodeUserHome = System.getProperty(NODE_USER_HOME_PROPERTY_KEY)) != null) {
            return new File(nodeUserHome);
        }
        if ((nodeUserHome = System.getenv(NODE_USER_HOME_ENV_KEY)) != null) {
            return new File(nodeUserHome);
        }
        return new File(DEFAULT_NODE_USER_HOME);
    }
}
