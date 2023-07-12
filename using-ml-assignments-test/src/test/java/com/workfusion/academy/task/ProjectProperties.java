package com.workfusion.academy.task;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

public class ProjectProperties {

    private final Properties properties;

    public ProjectProperties() {
        this("project.properties");
    }

    public ProjectProperties(String propertiesResourceName) {
        properties = readProjectProperties(propertiesResourceName);
    }

    public String getProjectVersion() {
        return properties.getProperty("project.version");
    }

    public String getPackageModuleName() {
        return properties.getProperty("package.module.name");
    }

    public String getDefaultBundlePath() {
        final String packageModuleName = getPackageModuleName();
        final String projectVersion = getProjectVersion();
        return String.format("../%s/target/%s-%s.zip", packageModuleName, packageModuleName, projectVersion);
    }

    private static Properties readProjectProperties(String resourceName) {
        try (InputStream inputStream = ProjectProperties.class.getClassLoader().getResourceAsStream(resourceName)) {
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
