package com.example.colorplayer.dataloader;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Created by nv95 on 10.11.16.
 */

public class FolderLoader {

    private static List<File> finalList = new ArrayList<>();

    private static final String[] SUPPORTED_EXT = new String[] {
            "mp3"
    };

    public static List<File> getFinalList(){
        return finalList;
    }

    public static List<File> getMediaFiles(File dir, final boolean acceptDirs) {
        ArrayList<File> list = new ArrayList<>();
        list.add(new File(dir, "..")); // 상위 경로 추가
        if (dir.isDirectory()) {
            List<File> files = Arrays.asList(dir.listFiles(new FileFilter() {

                @Override
                public boolean accept(File file) {
                    if (file.isFile()) {
                        String name = file.getName();
                        if(!".nomedia".equals(name) && checkFileExt(name)){
                            if(!finalList.contains(file.getParentFile())
                             && !file.getParentFile().getPath().contains("Android"))
                                finalList.add(file.getParentFile());
                            return true;
                        } else
                            return false;
                    } else if (file.isDirectory()) {
                        // 하위 디렉토리 검사
                        List<File> tempList = getMediaFiles(file, true);
                        return acceptDirs && checkDir(file);
                    } else
                        return false;
                }
            }));
            // 2. 해당 디렉토리에 mp3나 wav 파일이 있으면 해당 dir 를 list 에 저장

            // 3.



            // 기존 로직

            Collections.sort(files, new FilenameComparator());
            Collections.sort(files, new DirFirstComparator());
            list.addAll(files);
        }

        return list;
    }

    public static boolean isMediaFile(File file) {
        return file.exists() && file.canRead() && checkFileExt(file.getName());
    }

    private static boolean checkDir(File dir) {
        return dir.exists() && dir.canRead() && !".".equals(dir.getName()) && dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                String name = pathname.getName();
                return !".".equals(name) && !"..".equals(name) && pathname.canRead() && (pathname.isDirectory()  || (pathname.isFile() && checkFileExt(name)));
            }

        }).length != 0;
    }

    private static boolean checkFileExt(String name) {
        if (TextUtils.isEmpty(name)) {
            return false;
        }
        int p = name.lastIndexOf(".") + 1;
        if (p < 1) {
            return false;
        }
        String ext = name.substring(p).toLowerCase();
        for (String o : SUPPORTED_EXT) {
            if (o.equals(ext)) {
                return true;
            }
        }
        return false;
    }

    private static class FilenameComparator implements Comparator<File> {
        @Override
        public int compare(File f1, File f2) {
            return f1.getName().compareTo(f2.getName());
        }
    }

    private static class DirFirstComparator implements Comparator<File> {
        @Override
        public int compare(File f1, File f2) {
            if (f1.isDirectory() == f2.isDirectory())
                return 0;
            else if (f1.isDirectory() && !f2.isDirectory())
                return -1;
            else
                return 1;
        }
    }
}
