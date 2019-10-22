package com.example.musicplay.domain;

import java.io.Serializable;
import java.util.Objects;

public class Audio implements Serializable {

    public static final String ID = "id";
    public static final String DATA = "data";
    public static final String TITLE = "title";
    public static final String ALBUM = "album";
    public static final String ARTIST = "artist";
    public static final String LIST_ID = "listId";

    private String data;
    private String title;
    private String album;
    private String artist;
    private long id = -1;
    private long listId;

    public Audio(String data, String title, String album, String artist, long id, long listId) {
        this.data = data;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.listId = listId;
        this.id = id;
    }

    public Audio(String data, String title, String album, String artist) {
        this.data = data;
        this.title = title;
        this.album = album;
        this.artist = artist;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getListId() {
        return listId;
    }

    public void setListId(long listId) {
        this.listId = listId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Audio audio = (Audio) o;
        return id == audio.id &&
                listId == audio.listId &&
                Objects.equals(data, audio.data) &&
                Objects.equals(title, audio.title) &&
                Objects.equals(album, audio.album) &&
                Objects.equals(artist, audio.artist);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, title, album, artist, id, listId);
    }

    @Override
    public String toString() {
        return title;
    }
}
