# Packager

## Fish Plugin Package File Structure (fpk)

### Binary File Type

> Byte order: BIG-ENDIAN

#### File Structure

|                   Area                   | Size(Byte) | Comments      |
|:----------------------------------------:|:----------:| ------------- |
|               file version               |     2      |               |
|                file flags                |     1      | 0x00 - binary |
|               plugin count               |     2      |               |
| the count of constants in constants pool |     2      |               |
|              constants pool              |            |               |
|        data_ref_count_per_plugin         |     2      |               |
|               field-1 ref                |     -      |               |
|               field-2 ref                |     -      |               |
|                   ...                    |            |               |
|               field-N ref                |     -      |               |

#### Field Ref

| Area        | Size(Byte) | Comments |
|-------------|------------|----------|
| device type | 1          |          |
| data index  | 2          |          |

#### File Structure (Detail)

|                   Area                   | Field Value Type | Size(Byte) | Comments      |
|:----------------------------------------:| :--------------: |:----------:| ------------- |
|               file version               |        -         |     2      |               |
|                file flags                |        -         |     1      | 0x00 - binary |
|               plugin count               |        -         |     2      |               |
| the count of constants in constants pool |        -         |     2      |               |
|              constants pool              |        -         |            |               |
|       data_index_count_per_plugin        |        -         |     2      |               |
|                 name_ref                 |      String      |     -      |               |
|                  id_ref                  |      String      |     -      |               |
|                author_ref                |      String      |     -      |               |
|               version_ref                |      String      |     -      |               |
|               runtime_ref                |      String      |     -      |               |
|                 time_ref                 |      String      |     -      |               |
|                 tag_ref                  |      String      |     -      |               |
|             introduction_ref             |      String      |     -      |               |
|             pages_count_ref              |     Integer      |     -      |               |
|          pages_info[page_count]          |        -         |     -      |               |
|          name_ref [next plugin]          |      String      |     -      |               |
|                   ....                   |                  |            |               |

#### Page Structure

|              Area               | Field Value Type | Comments |
|:-------------------------------:|:----------------:|:--------:|
|             id_ref              |      String      |          |
|             url_ref             |      String      |          |
|             js_ref              |      String      |          |
|         dsl_field_count         |     Integer      |          |
| dsl_field_info[dsl_field_count] |        -         |          |

#### Dsl Structure

|        Area        | Field Value Type | Comments |
|:------------------:|:----------------:|:--------:|
|      field1_ref      |      String      |          |
| field2_ref |      String      |          |
| field3_ref |      String      |          |
|        ...         |       ...        |   ...    |

> Note: <br/>
> Index of field data equals to Index of constant in constants pool.
>

#### Field Structure

|         Type          | Areas                                           |                                    Comments                                    |
|:---------------------:|:------------------------------------------------|:------------------------------------------------------------------------------:|
|  CONSTANT_Utf8_info   | tag = 0x01 [1 Byte] <br/> length [] <br/> bytes |                              UTF-8 encoded string                              |
| CONSTANT_Integer_info | tag = 0x03 [1 Byte] <br/> bytes                 | Integer literals, boolean, byte, char, short and other types are stored in int |
|  CONSTANT_Float_info  | tag = 0x04 [1 Byte] <br/> bytes                 |                             floating point literal                             |
|  CONSTANT_Long_info   | tag = 0x05 [1 Byte] <br/> bytes                 |                                  long literal                                  |
| CONSTANT_Double_info  | tag = 0x06 [1 Byte] <br/> bytes                 |                    Double-precision floating-point literal                     |
