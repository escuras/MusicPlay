package com.example.musicplay.domain;

import java.io.File;

public class WayPath implements Comparable<WayPath>{
    private String name;
    private String absolutePath;

    public WayPath(String name, String absolutePath){
        this.absolutePath = absolutePath;
        this.name = name;
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
}