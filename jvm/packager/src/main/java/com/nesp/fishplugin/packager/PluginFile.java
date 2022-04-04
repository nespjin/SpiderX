package com.nesp.fishplugin.packager;

import com.nesp.fishplugin.core.data.Plugin2;
import com.nesp.fishplugin.packager.binary.ReadNotMatchTypeException;

import java.io.IOException;

/**
 * The Fish Plugin File that extension is `.fpk` apk Android Application Package
 */
public interface PluginFile {

    int FILE_VERSION_SIZE = 2;

    int FILE_FLAGS_SIZE = 1;

    int FILE_FLAG_BINARY = 0x00;

    int FILE_FLAG_JSON = 0x01;

    int readFileVersion() throws IOException;

    int readFileFlags() throws IOException;

    void write(Plugin2[] plugins) throws IOException;

    Plugin2[] read() throws IOException, ReadNotMatchTypeException;

}
