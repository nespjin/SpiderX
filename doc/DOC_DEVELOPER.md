# Developer Documentation

> Version: v1.0
>
> Date Time: 2022/01/25
>
> Code Repository: https://github.com/nespjin/OpenFishPlugin

## 项目结构

## 核心模块

## 编译器

## 运行时

## 打包

### 目标文件结构

#### Binary 文件类型

#### 概要

> 字节序: BIG-ENDIAN

|         区域         | 大小(Byte) |                注释                |
| :------------------: | :--------: | :--------------------------------: |
|       文件版本       |     2      |                                    |
| 文件标记(file flags) |     1      |           0x00 - binary            |
|        插件数        |     2      |                                    |
|   常量池中的常量数   |     2      |                                    |
|        常量池        |            | 包含很多常量, 字段持有对常量的引用 |
| 每个插件中字段的数量 |     2      |                                    |
|   字段1的常量引用    |     2      |                                    |
|   字段2的常量引用    |     2      |                                    |
|         ...          |            |                                    |
|   字段N的常量引用    |     2      |                                    |

#### 详细

|              区域               | 字段值类型 | 大小(Byte) |                             注释                             |
| :-----------------------------: | :--------: | :--------: | :----------------------------------------------------------: |
|            文件版本             |     -      |     2      |                                                              |
|      文件标记(file flags)       |     -      |     1      |                        0x00 - binary                         |
|             插件数              |     -      |     2      |                                                              |
|        常量池中的常量数         |     -      |     2      |                                                              |
|             常量池              |     -      |            |              包含很多常量, 字段持有对常量的引用              |
|      每个插件中字段的数量       |     -      |     2      |                                                              |
|       字段name的常量引用        |   String   |     2      |           name_data_index即表示常量池中第几个常量            |
|        字段id的常量引用         |   String   |     2      |                                                              |
|      字段author的常量引用       |   String   |     2      |                                                              |
|      字段version的常量引用      |   String   |     2      |                                                              |
|      字段runtime的常量引用      |   String   |     2      |                                                              |
|       字段time的常量引用        |   String   |     2      |                                                              |
|        字段tag的常量引用        |   String   |     2      |                                                              |
|   字段introduction的常量引用    |   String   |     2      |                                                              |
|    字段pages_count的常量引用    |  Integer   |     2      | 虽然字段pages_count不属于插件中的字段，但它处于字段区域，我们仍称其为字段 |
| 页面信息/pages_info[page_count] |     -      |     -      |                  包含各个页面字段的常量引用                  |
|  下一个插件字段name的常量引用   |   String   |     2      |               如果不存在下一个插件, 则文件结束               |
|              ....               |            |            |                                                              |

#### Page 结构

| 区域 | 字段值类型 | 注释 | | :-------------------------------------: | :--------: | :--: | | 字段id的常量引用 |
String | | | 字段url的常量引用 | String | | | 字段js的常量引用 | String | | | 字段dsl的常量引用 | Integer | | |
Dsl信息/dsl_field_info[dsl_field_count] | - | |

#### Dsl 结构

| 区域 | 字段值类型 | 注释 | | :-------------: | :--------: | :--: | | 字段1的常量引用 | String | | | 字段2的常量引用 |
String | | | 字段3的常量引用 | String | | | ... | ... | ... |


> Note: <br/>
> Index of field data equals to Index of constant in constants pool.
>

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

## 编辑器

## SDK

