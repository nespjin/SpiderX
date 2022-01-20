package com.nesp.fishplugin.compiler

import com.google.gson.Gson
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class CompilerTest {

    private val gson = Gson()

    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun compileFromDisk() {
        // 安装：
        // 用户输入Plugin的ProtoBuffer -> load -> grammar -> compiler -> 最终Plugin的ProtoBuffer。
        // 加载:
        //
        // plugin文件结构
        // version_code(32 byte) body_length(64 byte)
        //
        //
        // Result:
        //
        // {
        //    "name": "TestParent",
        //    "version": "1.0.0+1",
        //    "runtime": "1-3",
        //    "time": "2021-11-30 22:00:11,2022-01-01 08:00:11",
        //    "tags": [
        //        "tag1"
        //    ],
        //    "deviceFlags": 7,
        //    "type": 0,
        //    "introduction": "The simple introduction",
        //    "pages": [
        //        {
        //            "id": "movies",
        //            "url": "post:http://xxx.xx/a?id\u003d1",
        //            "js": "function loadPage() {\r\n    let a \u003d 1;\r\n}",
        //            "dsl": {
        //                "field1": "dsl:class.main/tag.div.2/tag.img@href",
        //                "field0": "class.main/tag.div.2/tag.img@href",
        //                "field6": "(dsl:class.main/tag.div.2/tag.img).trim()",
        //                "field2": "selector:#content_views \u003e p:nth-child(2)@href",
        //                "field5": "regex:/^(?:Chapter|Section) [1-9][0-9]{0,1}$/",
        //                "field4": "xpath:/html/body/div[3]/div[1]/main/div[1]/article/div/div[1]/div/div/p[2]/text()[1]"
        //            }
        //        },
        //        {
        //            "id": "search",
        //            "url": "http://xxxx.xxx/index.php?m\u003dvod-search\u0026wd\u003d{{st}}",
        //            "js": "function loadPage() {\r\n    let a \u003d 1;\r\n}"
        //        },
        //        {
        //            "id": "main",
        //            "url": "post:http://xxx.xx/a?id\u003d1",
        //            "js": "function loadPage() {\r\n    let a \u003d 1;\r\n}"
        //        },
        //        {
        //            "id": "detail",
        //            "refUrl": "",
        //            "url": "",
        //            "js": ""
        //        }
        //    ]
        //}
        val compileFromDisk = Compiler.compileFromDisk("./plugin.json")
        println("compile: message = " + compileFromDisk.message)
        println("compile: data = " + gson.toJson(compileFromDisk.data))
    }
}