package com.example.musicplay.util;

import java.util.HashSet;
import java.util.Set;

public class FileUtils {

    private static String FILE_SEPARATOR = ".";

    private static final String[] AUDIO_TERMINATIONS = new String[]{"8svx","webm",
            "wv","wma","wav","vox","voc","tta","sln","raw","ra","rm","opus",
            "mogg","ogg","oga","nsf","nmf","msv","mpc","mp3","nmf","m4p",
            "m4b","m4a","ivs","iklax","gsm","flac","dvf","dss","dct",
            "awb","au","ape","amr","alac","aiff","act","aax","aac","aa","3gp"};

    public static boolean isAudio(String file){
        int index = file.lastIndexOf(FILE_SEPARATOR);
        if(index < 0) {
            return false;
        }
        String extension = file.substring(index + 1, file.length());
        for (String termination : AUDIO_TERMINATIONS) {
            if(termination.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }
}
