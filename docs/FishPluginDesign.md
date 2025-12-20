# 小丑插件设计文档

## 插件兼容性

1. 支持不同种类的设备
2. 支持不同类型的应用
3. 支持不同的数据源

## 插件工具

1. CLI
2. GUI

## 插件的生命周期

1. 开发
2. 上传
3. 下载安装
4. 加载
5. 运行

## 插件的安装加载方式

1. 本地安装加载（本地插件安装包）
2. 从商店安装加载（在线插件）
3. 从网络加载（云插件）

## 插件的本地加载方式

> 一下几种解析方式可以混用

1. 动态获取(JS)
2. 静态加载(DSL)
3. 正则表达式

## 插件的运行机制

App -> Plugin -> Data Package

```json
{

}
```



## 插件结构 

### 引擎侧

```json5
{
    /** 
        字段后缀
        对于字段url的后缀可为
        url-{设备类型}

        设备类型：
           0: 手机
           1: 平板
           2: 桌面

       例如： 如果同时存在 url与url-2，则在桌面段使用url-2，其它端使用url
    **/
    "parent": { 
        /**
        被继承的插件（又称父插件，当前插件相对于父插件为子插件）

        如果父插件与子插件具有相同的字段，则使用子插件的字段

        继承方式（用户输入）：
        对象：{}
        路径引用: "C:/xx/xx/xx/SamplePlugin.json"、"../xx/xx/SamplePlugin.json"
        网络引用: "https://xxx/xxx/SamplePlugin.json"
        
        */

    },
    "name": "", // 插件名称
    "version": "1.0.0+1", // 版本：版本名+版本号
    "runtime": "1-3", // 支持的运行时，不允许用户输入
    "time": "2021-11-30 22:00:11,2022-01-01 08:00:11", // 创建时间,更新时间，不允许用户输入
    "tag": [], // String[], 标签
    
    /**
    支持的设备种类（手机、平板、电脑） 8bit
    
    bit7 bit6 bit5 bit4 bit3 bit2 bit1 bit0
     0    0    0    0    0    0    0    0

     bit0: 是否支持手机 0 不支持 1 支持
     bit1: 是否支持平板 0 不支持 1 支持
     bit2: 是否支持电脑 0 不支持 1 支持

    例如：值 00000001 仅支持手机，值 00000011 支持手机和平板
    */
    "deviceFlag": "00000000",   
    "type": 0, // 0:影视
    "introduction": "", // 介绍
    "ref": { // 引用。可自定义 字段、对象、js代码
        "commonUrl":"", // 字段引用
        "commonJs":"function a() { }", // js 代码引用
        "commonJs-2":
        "moviesDsl": { // 对象引用

        }
    },
    "pages": [ // 页面集
        { 
            "id": "main",
            "url": "{{ref.commonUrl}}", // 引用字段
            "url-2":"", // 在电脑会使用该字段
            "js": "{{ref.commonJs}}", // 引用Js，在电脑会使用commonJs-2
            "dsl": {}
        },
        {
            "id": "movies",
            "url": "{{ref.commonUrl}}", // 引用字段
            "js": "{{ref.commonJs}}", // 引用Js，在电脑会使用commonJs-2
            "dsl": "{{ref.moviesDsl}}" // 引用对象
        }
    ],
    "extensions": {} // 扩展 Object
}
```

