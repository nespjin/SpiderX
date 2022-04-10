/*
 * Copyright (c) 2022.  NESP Technology.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nesp.sdk.java.io;

import com.nesp.sdk.java.text.TextUtil;

import org.apache.commons.io.FileUtils;

import java.io.*;

/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/8/10 20:18
 * Description:
 **/
public final class FileUtil {

    private FileUtil() {
    }

    /**
     * @param file file
     * @return 文件扩展名 or null
     */
    public static String extensionName(final File file) {
        if (file == null || file.getName().lastIndexOf(".") < 0) return null;
        return file.getName().substring(file.getName().lastIndexOf(".") + 1);
    }

    public static boolean replaceFileContent(final File file, final String target, final String replacement) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("File must not be null");
        }

        if (!file.exists()) {
            throw new FileNotFoundException("File '" + file + "' does not exist");
        }

        if (file.isDirectory()) {
            throw new IOException("File '" + file + "' exists but is a directory");
        }

        if (TextUtil.isEmpty(target)) {
            throw new IllegalArgumentException("Target must not be null or empty");
        }

        if (replacement == null) {
            throw new IllegalArgumentException("Replacement must not be null");
        }

        FileReader fileReader = null;
        FileWriter fileWriter = null;

        final StringBuilder content = new StringBuilder();
        int readLen = 0;
        char[] buffer = new char[1024];

        try {
            fileReader = new FileReader(file);
            while ((readLen = fileReader.read(buffer)) != -1) {
                content.append(buffer, 0, readLen);
            }
            final String newContent = content.toString().replace(target, replacement);
            fileWriter = new FileWriter(file);
            fileWriter.write(newContent);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public static boolean copy(File source, File destination) {
        return copy(source, destination, true);
    }

    public static boolean copy(File source, File destination, boolean overwrite) {
        if (overwrite && destination.exists()) {
            destination.deleteOnExit();
        }
        try {
            FileUtils.copyFile(source, destination);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * @return 创建新文件
     */
    public static boolean createFile(final String directoryPath, final String fileName) {
        if (TextUtil.isEmpty(directoryPath)) {
            throw new IllegalArgumentException("Directory path must not be empty");
        }

        if (TextUtil.isEmpty(fileName)) {
            throw new IllegalArgumentException("File name must not be empty");
        }

        final File directory = new File(directoryPath);
        if (!directory.exists() && !directory.mkdirs()) {
            return false;
        }

        final File targetFile = new File(directory, fileName);
        try {
            return targetFile.exists() || targetFile.createNewFile();
        } catch (IOException e) {
            return false;
        }

    }


}
