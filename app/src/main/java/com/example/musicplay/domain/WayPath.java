package com.example.musicplay.domain;

import java.io.File;
import java.util.Objects;

public class WayPath implements Comparable<WayPath>{
    private String name;
    private String absolutePath;
    private String folder;

    public WayPath(String name, String absolutePath, String folder){
        this.absolutePath = absolutePath;
        this.name = name;
        this.folder = folder;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getName() {
        return name;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public boolean isDirectory(){
        return new File(absolutePath).isDirectory();
    }

    @Override
    public int compareTo(WayPath o) {
        return this.name.compareTo(o.getName());
    }

    @Override
    public String toString() {
        return name;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WayPath wayPath = (WayPath) o;
        return Objects.equals(absolutePath, wayPath.absolutePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(absolutePath);
    }
}