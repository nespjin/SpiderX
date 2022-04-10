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
