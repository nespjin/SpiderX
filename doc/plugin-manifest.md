# 插件配置文件
插件配置文件描述了插件的配置信息，比如插件的描述、图标、作者、版本等信息。

## 基础
### 引用
引用是对其它字段、文件等。

#### 本地文件引用
本地文件引用语法为：
`file://<文件路径>`
例如：
`file:///plugins/xxx/plugin.json`

#### 网络文件引用
网络文件引用语法为：
`http(s)://<文件路径>`
例如：
`https://raw.githubusercontent.com/xxxx/master/plugins/xxx/plugin.json`

#### 字段引用
字段引用语法为：
`${ref 中的字段访问路径}`

#### 请求 URL
请求URL语法为：
`<请求类型>@<URL>?<参数1>=<参数1值>&<参数2>=<参数2值>`
在请求 URL 中参数的值可以使用占位符用来表示参数，占位符的语法为 `${占位符id}`，已有的占位符 `id` 有：

- `st`：表示用户输入的搜索词

##### 请求类型
请求类型为下面之一，默认为 `GET`：
- `GET`
- `POST`

## 字段
配置文件的格式为JSON，字段如下：

### 字段后缀
对于字段url的后缀可为：
`url@<屏幕尺寸类型>`

屏幕尺寸类型：
- compact: 小屏幕设备，比如：手机
- medium: 中等屏幕设备，比如：平板
- expanded: 宽屏设备，比如：PC
默认为全部屏幕设备

例如： 如果同时存在 url与url@medium，则在中等屏幕设备会使用url@medium，其它设备使用url。

### parent
插件的父插件，用于继承父插件的配置信息，当前插件相对于父插件为子插件。子插件的配置信息会覆盖父插件的配置信息，即如果父插件与子
插件都定义了相同的字段，则子插件的字段会覆盖父插件的字段。
支持对象、路径引用和网络引用三种形式。（见 [引用](#引用)）。

### id
插件的唯一标识符，采用域名命名空间，如：com.example.plugin。
插件 `id` 不可重复。

### name
插件名称

### author
插件的作者。

### version
插件版本，需要符合[语义化版本 2.0](https://semver.org/lang/zh-CN/)标准语法。

### runtimeVersion
插件兼容的运行时版本号。支持 `>=`、`>`、`<=`、`<` 运算符。

### description
插件的简介。

### tags
插件的标签，用于标识插件的特点。

### supportedScreenTypes
插件支持的屏幕类型。一个或多个值，可选值有：
`compact`、 `medium`、　`expanded`。

### variables
引用。可自定义 字段、对象、js代码
1. 在引用内不能引用其她变量
2. 只能定义一级引用

### dataset
插件的数据集。表示插件的数据来源。

#### id
数据集的id。

#### url
数据集的url，表示数据集的访问地址。

### js
插件的js代码。
可以为合法的　javascript 代码或　javascript 文件引用。

### dsl
插件的dsl代码。
在 dsl 中定义数据字段的爬取规则。
字段的爬取规则语法为：
`<爬取规则类型>:<爬取规则>`

数据字段的爬取规则类型有以下三种，默认为 DSL：
1. DSL
2. Selector
3. XPath

例如：
- `dsl:class.main/tag.div.2/tag.img@href`
- `selector:#content_views > p:nth-child(2)@href`
- `xpath://*[@id="content_views"]/p[2]/text()[1]`

### api

## 示例
```jsonc
{
    "parent": { },
    "id": "", 
    "name": "", 
    "author": "",
    "version": "1.0.0+1",
    "runtimeVersion": ">=1.1.0,<2.0.0", 
    "description": "",
    "tags": [],
    "supportedScreenTypes":[],
    "variables": {
        "commonUrl":"post:http://xxx.xx/a?id=1", // 字段引用 url 格式，请求类型:地址?统一参数表
        "searchUrl":"http://xxxx.xxx/index.php?m=vod-search&wd=${st}", // {{st}} 会替换为用户输入的关键字
        "detailPageUrl":"https://www.baidu.com",
        "commonJs":"function a() { }", // js 代码引用
        "commonJs＠expanded":"file://../Sample.js", // 本地引用
        "sampleJs":"https://xxx.xx/sample.js", // 网络引用
        "moviesDsl": {
          // 对象引用，DSL 支持
          // 格式：类型:值
          // 类型可不写
          "property0": "class.main/tag.div.2/tag.img@href", // 默认dsl
          "property1": "dsl:class.main/tag.div.2/tag.img@href", // fish-dsl
          "property2": "selector:#content_views > p:nth-child(2)@href", // selector
          "property3": "xpath://*[@id=\"content_views\"]/p[2]/text()[1]", // XPath
          "property4": "xpath:/html/body/div[3]/div[1]/main/div[1]/article/div/div[1]/div/div/p[2]/text()[1]", // fullXPath
          // "property5": "regex:/^(?:Chapter|Section) [1-9][0-9]{0,1}$/", // 正则表达式

          // 高级用法
          // 1.使用DSL函数
          "property6":"dsl:class.main/tag.div.2/tag.img@trim;concat(a)",
          // 2.使用DSL变量 暂未实现
          "property7": [
         "let a = dsl:class.main/tag.div.2/tag.img",
         "let b = dsl:class.main/tag.div.2/tag.img",
         "let c = a@href",
         "c.trim()",
          ]
        }
    },
    "dataset": [ // 数据集, id不可重复
        // 在JS中需要实现 function JsRuntime_LoadPage()
        { 
            "id": "home", // 不支持字段后缀
            "url": "${commonUrl}", // 引用字段 ref.commonUrl
            "url@expaded":"", // 在桌面端会使用该字段
            // 若js与dsl同时存在，则使用dsl
            "js": "${commonJs}", // 引用Js，在桌面端会使用ref.commonJs＠expanded
            "dsl": {}
        },
        {
            "id": "category",
            "url": "${commonUrl}", // 引用字段
            "js": "${commonJs}", // 引用Js，在桌面端会使用ref.commonJs＠expanded
            "dsl": "${moviesDsl}" // 引用对象 ref.moviesDsl
        },
        {
            "id":"detail",
            "refUrl":"${detailPageUrl}", // 在线引用链接 ref.detailPageUrl
        }
    ],
    "extensions": {} // 扩展 Object
}

/**
DSL函数:

replace(a,b)

split(a,1)
分割字符串

concat(b)
合并多个字符串

trim()
去掉首尾空白

trimStart()
去掉首部空白

trimEnd()
去掉尾部空白

公共js函数:

  /**
      *
      * @returns {number} Api版本
     getApiLevel: function () {
         return RUNTIME_API_LEVEL_A;
     },

     /**
      *
      * @returns {number} Runtime 版本号
     getVersionCode: function () {
         return 1;
     },

     /**
      *
      * @returns {string} Runtime 版本名

     getVersionName: function () {
         return "1.0";
     },

     /**
      *
      * @returns {string} Runtime build号
     getBuild:function () {
         return "";
     }

     /**
      *
      * @returns {number} 设备类型: 0: 手机 1: 平板 2: 桌面

     getDeviceType: function () {
         return 0;
     },

     /**
      *
      * @returns {boolean} 是否是手机
     isMobilePhone: function () {
         return false;
     },

     /**
      *
      * @returns {boolean} 是否是平板
     isTable: function () {
         return false;
     },

     /**
      *
      * @returns {boolean} 是否是桌面
     isDesktop: function () {
         return false;
     },

     /**
      * 向应用端发送数据[弃用]
      * @param type {number} 数据类型
      * @param data {Object} 数据
     sendData: function (type, data) {
     },

     /**
      * 向应用端发送错误消息
      * @param errorMsg {string} 错误消息字符串
     sendError: function (errorMsg) {
     },

     /**
      * 尝试执行method，catch到异常后自动调用sendError发送错误信息到应用端。
      * @param method {function}
     tryRun: function (method) {
         try {
             method();
         } catch (e) {
             this.sendError(e.toString());
         }
     }
 */
/**
小丑鱼影视js函数：

// 创建一个HomePage对象
createHomePage()

// 创建一个CategoryPage对象
createCategoryPage()

// 创建一个SearchPage对象
createSearchPage()

// 创建一个DetailPage对象
createDetailPage()

// 向应用端发送主页面数据
// 
// 参数:
// 
// homePage：主页面Object
sendHomePage(homePage)

// 向应用端发送视频分类页面数据
// 
// 参数:
// 
// categoryPage：视频分类页面Object
sendCategoryPage(categoryPage)

// 向应用端发送搜索页面数据
// 
// 参数:
// 
// searchPage：搜索页面Object
sendSearchPage(searchPage)

// 向应用端发送视频详情数据
// 
// 参数:
// 
// detailPage：视频详情页面Object
sendDetailPage(detailPage)

**/
```
