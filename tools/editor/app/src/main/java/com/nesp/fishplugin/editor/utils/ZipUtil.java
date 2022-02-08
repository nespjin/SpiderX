package com.nesp.fishplugin.editor.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class ZipUtil {
    private ZipUtil() {
        //no instance
    }

    public static boolean unzip(File zipFile, File destination) {
        if (zipFile == null || !zipFile.exists()) return false;
        if (!destination.exists()) {
            if (!destination.mkdirs()) {
                System.out.println("destination " + destination.getAbsolutePath() + " create failed.");
            }
        } else {
            if (!destination.isDirectory()) {
                return false;
            }
        }

        try (ZipFile zipFile1 = new ZipFile(zipFile)) {
            Enumeration<? extends ZipEntry> entries = zipFile1.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();
                File file = new File(destination, zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    if (!file.mkdirs()) {
                        System.out.println("dir " + file.getAbsolutePath() + " create failed.");
                    }
                } else {
                    if (!file.getParentFile().exists()) {
                        if (file.getParentFile().mkdirs()) {
                            System.out.println("parent " + file.getAbsolutePath() + " create failed.");
                        }
                    }
                    if (!file.createNewFile()) {
                        System.out.println("file " + file.getAbsolutePath() + " create failed.");
                    }

                    try (InputStream inputStream = zipFile1.getInputStream(zipEntry);
                         FileOutputStream outputStream = new FileOutputStream(file)) {
                        int len;
                        byte[] buffer = new byte[1024];
                        while ((len = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, len);
                        }
                        outputStream.flush();
                    }
                }
            }
        } catch (IOException e) {
            return false;
        }

        return false;
    }
}
