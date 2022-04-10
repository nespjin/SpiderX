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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonMinifierTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void minify() {
        final String json = "{\n" +
                "  \"name\": \"PluginName\",\n" +
                "  \"version\": \"1.0.0+1\",\n" +
                "  \"runtime\": \"1-3\",\n" +
                "  \"time\": \"2022-01-15 22:00:11,2022-01-01 08:00:11\",\n" +
                "  \"tags\": [\n" +
                "    \"Sample\"\n" +
                "  ],\n" +
                "  \"deviceFlags\": \"00000111\",\n" +
                "  \"type\": 0,\n" +
                "  \"ref\": {\n" +
                "    \"categoryJs\": \"path:./js/category.js\"\n" +
                "  },\n" +
                "  \"pages\": [\n" +
                "    {\n" +
                "      \"id\": \"home\",\n" +
                "      \"url\": \"http://m.benbenji.com/\",\n" +
                "      \"js\": \"path:./js/home.js\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"category_movie\",\n" +
                "      \"url\": \"http://m.benbenji.com/dy/index_1_______1.html\",\n" +
                "      \"js\": \"{{categoryJs}}\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"category_soap\",\n" +
                "      \"url\": \"http://m.benbenji.com/dsj/index_1_______1.html\",\n" +
                "      \"js\": \"{{categoryJs}}\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"category_variety\",\n" +
                "      \"url\": \"http://m.benbenji.com/arts/\",\n" +
                "      \"js\": \"{{categoryJs}}\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"category_anim\",\n" +
                "      \"url\": \"http://m.benbenji.com/dm/\",\n" +
                "      \"js\": \"{{categoryJs}}\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"search\",\n" +
                "      \"url\": \"http://m.benbenji.com/vod-search-wd-{{st}}-p-1.html\",\n" +
                "      \"js\": \"path:./js/search.js\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"detail\",\n" +
                "      \"js\": \"path:./js/detail.js\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        final CodeMinifier codeMinifier = new JsonMinifier();
        System.out.println(codeMinifier.minify(json));
    }
}