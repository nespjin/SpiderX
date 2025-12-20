# Developer Documentation

> Version: v1.0
>
> Date Time: 2022/01/25
>
> Code Repository: https://github.com/nespjin/OpenFishPlugin

## 一 项目结构

## 二 核心模块

## 三 编译器

## 四 运行时

## 五 打包

### 1 目标文件结构

#### 1.1 Binary 文件类型

#### 1.1.1 概要

> 字节序: BIG-ENDIAN

|         区域         | 大小(Byte) |                注释                |
| :------------------: |:--------:| :--------------------------------: |
|       文件版本       |    2     |                                    |
| 文件标记(file flags) |    1     |           0x00 - binary            |
|        插件数        |    2     |                                    |
|   常量池中的常量数   |    2     |                                    |
|        常量池        |          | 包含很多常量, 字段持有对常量的引用 |
| 每个插件中字段的数量 |    2     |                                    |
|   字段1的常量引用    |    -     |                                    |
|   字段2的常量引用    |    -     |                                    |
|         ...          |          |                                    |
|   字段N的常量引用    |    -     |                                    |

#### 字段引用

| Area | Size(Byte) | Comments |
|------|------------|----------|
| 设备类型 | 1          |          |
| 引用地址 | 2          |          |


#### 1.1.2 详细

|              区域               | 字段值类型 | 大小(Byte) |                             注释                             |
| :-----------------------------: | :--------: |:--------:| :----------------------------------------------------------: |
|            文件版本             |     -      |    2     |                                                              |
|      文件标记(file flags)       |     -      |    1     |                        0x00 - binary                         |
|             插件数              |     -      |    2     |                                                              |
|        常量池中的常量数         |     -      |    2     |                                                              |
|             常量池              |     -      |          |              包含很多常量, 字段持有对常量的引用              |
|      每个插件中字段的数量       |     -      |    2     |                                                              |
|       字段name的常量引用        |   String   |    -     |           name_data_index即表示常量池中第几个常量            |
|        字段id的常量引用         |   String   |    -     |                                                              |
|      字段author的常量引用       |   String   |    -     |                                                              |
|      字段version的常量引用      |   String   |    -     |                                                              |
|      字段runtime的常量引用      |   String   |    -     |                                                              |
|       字段time的常量引用        |   String   |    -     |                                                              |
|        字段tag的常量引用        |   String   |    -     |                                                              |
|   字段introduction的常量引用    |   String   |    -     |                                                              |
|    字段pages_count的常量引用    |  Integer   |    -     | 虽然字段pages_count不属于插件中的字段，但它处于字段区域，我们仍称其为字段 |
| 页面信息/pages_info[page_count] |     -      |    -     |                  包含各个页面字段的常量引用                  |
|  下一个插件字段name的常量引用   |   String   |    -     |               如果不存在下一个插件, 则文件结束               |
|              ....               |            |          |                                                              |

#### 1.1.3 Page 结构

| 区域                                 |  字段值类型   |  注释   |
|------------------------------------|-----|-----|
| 字段id的常量引用                          |  String   |     |
| 字段url的常量引用                         |  String   |     |
| 字段js的常量引用                          |    String |     |
| Dsl字段的常量引用的个数                      |  Integer   |     |
| Dsl信息/dsl_field_info[dsl_field_count] |            |     |


#### 1.1.4 Dsl 结构

|         区域         | 字段值类型  | 注释  |
|:------------------:|:------:|:---:|
|      字段1的常量引用      | String |     |
|      字段2的常量引用      | String |     |
| 字段3的常量引用     |       String |     |
|        ...         |  ...   | ... |
 
> Note: <br/>
> Index of field data equals to Index of constant in constants pool.

#### 字段结构
<table>
    <tr>
        <td style="font-weight: bold;" align="center">类型</td>
        <td style="font-weight: bold;" colspan="3" align="center">区域/Areas</td>
        <td style="font-weight: bold;" align="center">注释</td>
    </tr>
    <tr>
        <td align="center"></td>
        <td align="center">tag [1 Byte]</td>
        <td align="center">length [2 Byte]</td>
        <td align="center">bytes</td>
        <td align="center"></td>
    </tr>
    <tr>
        <td align="center">CONSTANT_Utf8_info</td>
        <td align="center">0x01</td>
        <td align="center">-</td>
        <td align="center">-</td>
        <td align="center">UTF-8 encoded string</td>
    </tr>
    <tr>
        <td align="center">CONSTANT_Integer_info</td>
        <td align="center">0x03</td>
        <td align="center">-</td>
        <td align="center">-</td>
        <td align="center">Integer literals, boolean, byte, char, short and other types are stored in int</td>
    </tr>
    <tr>
        <td align="center">CONSTANT_Float_info</td>
        <td align="center">0x04</td>
        <td align="center">-</td>
        <td align="center">-</td>
        <td align="center">floating point literal</td>
    </tr>
    <tr>
        <td align="center">CONSTANT_Long_info</td>
        <td align="center">0x05</td>
        <td align="center">-</td>
        <td align="center">-</td>
        <td align="center">long literal</td>
    </tr>
    <tr>
        <td align="center">CONSTANT_Double_info</td>
        <td align="center">0x06</td>
        <td align="center">-</td>
        <td align="center">-</td>
        <td align="center">Double-precision floating-point literal</td>
    </tr>
</table>

> 文件示例:

```shell
000000  00 01 00 00 01 00 1f 01 00 0a 54 65 73 74 50 61  ..........TestPa
000010  72 65 6e 74 01 00 1a 63 6f 6d 2e 6e 65 73 70 2e  rent...com.nesp.
000020  66 69 73 68 2e 70 6c 75 67 69 6e 2e 6d 6f 76 69  fish.plugin.movi
000030  65 01 00 00 01 00 07 31 2e 30 2e 30 2b 31 01 00  e......1.0.0+1..
000040  03 31 2d 33 01 00 27 32 30 32 31 2d 31 31 2d 33  .1-3..'2021-11-3
000050  30 20 32 32 3a 30 30 3a 31 31 2c 32 30 32 32 2d  0 22:00:11,2022-
000060  30 31 2d 30 31 20 30 38 3a 30 30 3a 31 31 01 00  01-01 08:00:11..
000070  04 74 61 67 31 03 00 00 00 07 03 00 00 00 00 01  .tag1...........
000080  00 17 54 68 65 20 73 69 6d 70 6c 65 20 69 6e 74  ..The simple int
000090  72 6f 64 75 63 74 69 6f 6e 03 00 00 00 04 01 00  roduction.......
0000a0  06 6d 6f 76 69 65 73 01 00 19 70 6f 73 74 3a 68  .movies...post:h
0000b0  74 74 70 3a 2f 2f 78 78 78 2e 78 78 2f 61 3f 69  ttp://xxx.xx/a?i
0000c0  64 3d 31 01 00 1d 66 75 6e 63 74 69 6f 6e 20 6c  d=1...function l
0000d0  6f 61 64 50 61 67 65 28 29 7b 6c 65 74 20 61 3d  oadPage(){let a=
0000e0  31 3b 7d 03 00 00 00 06 01 00 06 66 69 65 6c 64  1;}........field
0000f0  31 01 00 25 64 73 6c 3a 63 6c 61 73 73 2e 6d 61  1..%dsl:class.ma
000100  69 6e 2f 74 61 67 2e 64 69 76 2e 32 2f 74 61 67  in/tag.div.2/tag
000110  2e 69 6d 67 40 68 72 65 66 01 00 06 66 69 65 6c  .img@href...fiel
000120  64 30 01 00 21 63 6c 61 73 73 2e 6d 61 69 6e 2f  d0..!class.main/
000130  74 61 67 2e 64 69 76 2e 32 2f 74 61 67 2e 69 6d  tag.div.2/tag.im
000140  67 40 68 72 65 66 01 00 06 66 69 65 6c 64 36 01  g@href...field6.
000150  00 29 28 64 73 6c 3a 63 6c 61 73 73 2e 6d 61 69  .)(dsl:class.mai
000160  6e 2f 74 61 67 2e 64 69 76 2e 32 2f 74 61 67 2e  n/tag.div.2/tag.
000170  69 6d 67 29 2e 74 72 69 6d 28 29 01 00 06 66 69  img).trim()...fi
000180  65 6c 64 32 01 00 2d 73 65 6c 65 63 74 6f 72 3a  eld2..-selector:
000190  23 63 6f 6e 74 65 6e 74 5f 76 69 65 77 73 20 3e  #content_views >
0001a0  20 70 3a 6e 74 68 2d 63 68 69 6c 64 28 32 29 40   p:nth-child(2)@
0001b0  68 72 65 66 01 00 06 66 69 65 6c 64 35 01 00 2d  href...field5..-
0001c0  72 65 67 65 78 3a 2f 5e 28 3f 3a 43 68 61 70 74  regex:/^(?:Chapt
0001d0  65 72 7c 53 65 63 74 69 6f 6e 29 20 5b 31 2d 39  er|Section) [1-9
0001e0  5d 5b 30 2d 39 5d 7b 30 2c 31 7d 24 2f 01 00 06  ][0-9]{0,1}$/...
0001f0  66 69 65 6c 64 34 01 00 54 78 70 61 74 68 3a 2f  field4..Txpath:/
000200  68 74 6d 6c 2f 62 6f 64 79 2f 64 69 76 5b 33 5d  html/body/div[3]
000210  2f 64 69 76 5b 31 5d 2f 6d 61 69 6e 2f 64 69 76  /div[1]/main/div
000220  5b 31 5d 2f 61 72 74 69 63 6c 65 2f 64 69 76 2f  [1]/article/div/
000230  64 69 76 5b 31 5d 2f 64 69 76 2f 64 69 76 2f 70  div[1]/div/div/p
000240  5b 32 5d 2f 74 65 78 74 28 29 5b 31 5d 01 00 06  [2]/text()[1]...
000250  73 65 61 72 63 68 01 00 30 68 74 74 70 3a 2f 2f  search..0http://
000260  78 78 78 78 2e 78 78 78 2f 69 6e 64 65 78 2e 70  xxxx.xxx/index.p
000270  68 70 3f 6d 3d 76 6f 64 2d 73 65 61 72 63 68 26  hp?m=vod-search&
000280  77 64 3d 7b 7b 73 74 7d 7d 01 00 04 6d 61 69 6e  wd={{st}}...main
000290  01 00 06 64 65 74 61 69 6c 00 27 00 00 00 01 00  ...detail.'.....
0002a0  02 00 03 00 04 00 05 00 06 00 07 00 08 00 09 00  ................
0002b0  0a 00 0b 00 0c 00 0d 00 0e 00 0f 00 10 00 11 00  ................
0002c0  12 00 13 00 14 00 15 00 16 00 17 00 18 00 19 00  ................
0002d0  1a 00 1b 00 1c 00 0d 00 08 00 1d 00 0c 00 0d 00  ................
0002e0  08 00 1e 00 02 00 02 00 08                       .........
```

## 编辑器

## SDK

