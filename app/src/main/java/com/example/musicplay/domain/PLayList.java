package com.example.musicplay.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PLayList {

    public static final String ID = "id";
    public static final String NAME = "name";

    private long id = -1;
    private String name;
    private List<Audio> audios = new ArrayList<>();

    public PLayList(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public PLayList(String name) {
        this.name = name;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Audio> getAudios() {
        return audios;
    }

    public void setAudios(List<Audio> audios) {
        this.audios = audios;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PLayList audioList = (PLayList) o;
        return id == audioList.id &&
                Objects.equals(name, audioList.name) &&
                Objects.equals(audios, audioList.audios);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, audios);
    }

    @Override
    public String toString() {
        return name;
    }
}
