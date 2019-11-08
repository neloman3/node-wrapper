package com.ne3world.node.wrapper;

import java.io.Closeable;
import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class MainNodeWrapper {

    public void start(String[] args, File nodeHome) throws Exception {
        File nodeJar = findLauncherJar(nodeHome);
        if (nodeJar == null) {
            throw new RuntimeException(String.format("Could not locate the Node launcher JAR in Node distribution '%s'.", nodeHome));
        }
        URLClassLoader contextClassLoader = new URLClassLoader(new URL[]{nodeJar.toURI().toURL()}, ClassLoader.getSystemClassLoader().getParent());
        Thread.currentThread().setContextClassLoader(contextClassLoader);
        Class<?> mainClass = contextClassLoader.loadClass("org.gradle.launcher.GradleMain");
        Method mainMethod = mainClass.getMethod("main", String[].class);
        mainMethod.invoke(null, new Object[]{args});
        if (contextClassLoader instanceof Closeable) {
            ((Closeable) contextClassLoader).close();
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
            if (launcherJars!=null && launcherJars.length==1) {
                return launcherJars[0];
            }
        }
        return null;
    }
}
