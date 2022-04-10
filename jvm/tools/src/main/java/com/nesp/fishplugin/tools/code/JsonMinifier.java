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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Minify the json code, support multiple-line or single-line comments
 */
public class JsonMinifier implements CodeMinifier {

    @Override
    public String minify(String code) {
        if (code == null) return "";
        String tokenizer = "\"|(/\\*)|(\\*/)|(//)|\\n|\\r";
        String magic = "(\\\\)*$";
        boolean inString = false;
        boolean inMultilineComment = false;
        boolean inSingleLineComment = false;
        String tmp = "";
        String tmp2 = "";
        List<String> new_str = new ArrayList<String>();
        int from = 0;
        String lc = "";
        String rc = "";

        Pattern pattern = Pattern.compile(tokenizer);
        Matcher matcher = pattern.matcher(code);

        Pattern magicPattern = Pattern.compile(magic);
        Matcher magicMatcher;
        boolean foundMagic;

        if (!matcher.find()) {
            return code;
        } else {
            matcher.reset();
        }

        while (matcher.find()) {
            lc = code.substring(0, matcher.start());
            rc = code.substring(matcher.end(), code.length());
            tmp = code.substring(matcher.start(), matcher.end());

            if (!inMultilineComment && !inSingleLineComment) {
                tmp2 = lc.substring(from);
                if (!inString) {
                    tmp2 = tmp2.replaceAll("(\\n|\\r|\\s)*", "");
                }

                new_str.add(tmp2);
            }
            from = matcher.end();

            if (tmp.charAt(0) == '\"' && !inMultilineComment && !inSingleLineComment) {
                magicMatcher = magicPattern.matcher(lc);
                foundMagic = magicMatcher.find();
                if (!inString || !foundMagic || (magicMatcher.end() - magicMatcher.start()) % 2 == 0) {
                    inString = !inString;
                }
                from--;
                rc = code.substring(from);
            } else if (tmp.startsWith("/*") && !inString && !inMultilineComment && !inSingleLineComment) {
                inMultilineComment = true;
            } else if (tmp.startsWith("*/") && !inString && inMultilineComment && !inSingleLineComment) {
                inMultilineComment = false;
            } else if (tmp.startsWith("//") && !inString && !inMultilineComment && !inSingleLineComment) {
                inSingleLineComment = true;
            } else if ((tmp.startsWith("\n") || tmp.startsWith("\r")) && !inString && !inMultilineComment && inSingleLineComment) {
                inSingleLineComment = false;
            } else if (!inMultilineComment && !inSingleLineComment && !tmp.substring(0, 1).matches("\\n|\\r|\\s")) {
                new_str.add(tmp);
            }
        }

        new_str.add(rc);
        StringBuilder sb = new StringBuilder();
        for (String str : new_str) {
            sb.append(str);
        }

        return sb.toString();
    }


}
