package com.gabriel.empms.controller.storage;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationProperties("file.upload")
@ConfigurationPropertiesScan
public class StorageProperties {

    /**
     * Folder location for storing files
     */
    private String location; // = "..\\upload-dir";

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}