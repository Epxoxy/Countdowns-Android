package com.example.xiaox.countdowns.basic.file;

import org.json.JSONObject;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by xiaox on 2/12/2017.
 */
public class TypeNamesFilter implements FilenameFilter {
    private String[] types;
    public TypeNamesFilter(String... types){
        this.types = types;
    }

    @Override
    public boolean accept(File dir, String filename) {
        for(String type : this.types){
            if(filename.endsWith(type)) return true;
        }
        return false;
    }
}
