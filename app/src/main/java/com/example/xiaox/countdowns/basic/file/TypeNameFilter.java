package com.example.xiaox.countdowns.basic.file;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by xiaox on 2/12/2017.
 */
public class TypeNameFilter implements FilenameFilter{
    private String type;
    public TypeNameFilter(String type){
        this.type = type;
    }

    @Override
    public boolean accept(File dir, String filename) {
        return filename.endsWith(this.type);
    }
}
