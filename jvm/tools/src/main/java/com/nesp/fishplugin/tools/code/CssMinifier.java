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
