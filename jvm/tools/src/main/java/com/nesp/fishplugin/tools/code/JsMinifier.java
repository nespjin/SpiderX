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

import com.yahoo.platform.yui.compressor.JavaScriptCompressor;
import org.apache.logging.log4j.LogManager;
import org.mozilla.javascript.EvaluatorException;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public class JsMinifier implements CodeMinifier {

    @Override
    public String minify(String code) {
        if (code == null || code.isEmpty()) return "";
        try {
            final ErrorReporter errorReporter = new ErrorReporter();
            final StringReader reader = new StringReader(code);
            final StringWriter writer = new StringWriter();
            final JavaScriptCompressor compressor = new JavaScriptCompressor(reader, errorReporter);
            compressor.compress(writer, -1, false, false, true, false);
            return writer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static final class ErrorReporter implements org.mozilla.javascript.ErrorReporter {

        @Override
        public void warning(String s, String s1, int i, String s2, int i1) {
            System.out.println(s + " " + s1 + " " + i + " " + s2 + " " + i1);
            LogManager.getLogger(JsMinifier.class).warn(s + " " + s1 + " " + i + " " + s2 + " " + i1);
        }

        @Override
        public void error(String s, String s1, int i, String s2, int i1) {
            System.err.println(s + " " + s1 + " " + i + " " + s2 + " " + i1);
            LogManager.getLogger(JsMinifier.class).error(s + " " + s1 + " " + i + " " + s2 + " " + i1);
        }

        @Override
        public EvaluatorException runtimeError(String s, String s1, int i, String s2, int i1) {
            LogManager.getLogger(JsMinifier.class).error(s + " " + s1 + " " + i + " " + s2 + " " + i1);
            return new EvaluatorException(s + " " + s1 + " " + i + " " + s2 + " " + i1);
        }
    }
}
