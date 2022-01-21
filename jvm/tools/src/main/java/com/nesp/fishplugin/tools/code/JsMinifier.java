package com.nesp.fishplugin.tools.code;

import com.yahoo.platform.yui.compressor.JavaScriptCompressor;
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
        }

        @Override
        public void error(String s, String s1, int i, String s2, int i1) {
            System.err.println(s + " " + s1 + " " + i + " " + s2 + " " + i1);
        }

        @Override
        public EvaluatorException runtimeError(String s, String s1, int i, String s2, int i1) {
            return new EvaluatorException(s + " " + s1 + " " + i + " " + s2 + " " + i1);
        }
    }
}
