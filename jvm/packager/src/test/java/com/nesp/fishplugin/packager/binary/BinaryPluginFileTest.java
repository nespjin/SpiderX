package com.nesp.fishplugin.packager.binary;

import com.google.gson.Gson;
import com.nesp.fishplugin.core.data.Plugin2;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class BinaryPluginFileTest {

    String pluginJson = """
            {
                "name": "TestParent",
                "id": "com.nesp.fish.plugin.movie",
                "author": "",
                "version": "1.0.0+1",
                "runtime": "1-3",
                "time": "2021-11-30 22:00:11,2022-01-01 08:00:11",
                "tags": [
                    "tag1"
                ],
                "deviceFlags": 7,
                "type": 0,
                "introduction": "The simple introduction",
                "pages": [
                    {
                        "id": "movies",
                        "url": "post:http://xxx.xx/a?id\\u003d1",
                        "js": "function loadPage() {\\r\\n    let a \\u003d 1;\\r\\n}",
                        "dsl": {
                            "field1": "dsl:class.main/tag.div.2/tag.img@href",
                            "field0": "class.main/tag.div.2/tag.img@href",
                            "field6": "(dsl:class.main/tag.div.2/tag.img).trim()",
                            "field2": "selector:#content_views \\u003e p:nth-child(2)@href",
                            "field5": "regex:/^(?:Chapter|Section) [1-9][0-9]{0,1}$/",
                            "field4": "xpath:/html/body/div[3]/div[1]/main/div[1]/article/div/div[1]/div/div/p[2]/text()[1]"
                        }
                    },
                    {
                        "id": "search",
                        "url": "http://xxxx.xxx/index.php?m\\u003dvod-search\\u0026wd\\u003d{{st}}",
                        "js": "function loadPage() {\\r\\n    let a \\u003d 1;\\r\\n}"
                    },
                    {
                        "id": "main",
                        "url": "post:http://xxx.xx/a?id\\u003d1",
                        "url_0": "post0:http://xxx.xx/a?id\\u003d1",
                        "js": "function loadPage() {\\r\\n    let a \\u003d 1;\\r\\n}"
                    },
                    {
                        "id": "detail",
                        "refUrl": "",
                        "url": "",
                        "js": ""
                    }
                ]
            }
            """;

    private final Gson gson = new Gson();
    private BinaryPluginFile binaryPluginFile = new BinaryPluginFile("./plugin.fpk");
    private long startTimeMillis = System.currentTimeMillis();

    @BeforeEach
    void before() {

    }

    @Test
    void write() {
        Plugin2 plugin = new Plugin2(new JSONObject(pluginJson));
        try {
            startTimeMillis = System.currentTimeMillis();
            binaryPluginFile.write(new Plugin2[]{plugin,plugin,plugin,plugin,plugin,plugin,plugin,plugin});
            long x = System.currentTimeMillis() - startTimeMillis;
            System.out.println(x); // 211/197/199/222/216 233/210/192/203/236
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void read() {
        Plugin2[] plugins = null;
        try {
            startTimeMillis = System.currentTimeMillis();
            plugins = binaryPluginFile.read();
            long x = System.currentTimeMillis() - startTimeMillis;
            System.out.println(x);// 13/15/16/ 15/13/18/15/15
        } catch (IOException | ReadNotMatchTypeException e) {
            e.printStackTrace();
        }
        System.out.println(gson.toJson(plugins));
    }

}