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

package com.nesp.fishplugin.tools.code;

import com.yahoo.platform.yui.compressor.CssCompressor;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public class CssMinifier implements CodeMinifier {

    @Override
    public String minify(String code) {
        if (code == null || code.isEmpty()) return "";
        try {
            final StringReader reader = new StringReader(code);
            final StringWriter writer = new StringWriter();
            final CssCompressor compressor = new CssCompressor(reader);
            compressor.compress(writer, -1);
            return writer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
