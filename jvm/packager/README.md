# Packager

## Fish Plugin Package File Structure (fpk)

### Binary File Type

> Byte order: BIG-ENDIAN

#### File Structure

|                   Area                   | Size(Byte) | Comments      |
| :--------------------------------------: | :--------: | ------------- |
|               file version               |     2      |               |
|                file flags                |     1      | 0x00 - binary |
|               plugin count               |     2      |               |
| the count of constants in constants pool |     2      |               |
|              constants pool              |            |               |
|       data_index_count_per_plugin        |     2      |               |
|            field-1 data index            |     2      |               |
|            field-2 data index            |     2      |               |
|                   ...                    |            |               |
|            field-N data index            |            |               |

#### File Structure (Detail) 

|                   Area                   | Field Value Type | Size(Byte) | Comments      |
| :--------------------------------------: | :--------------: | :--------: | ------------- |
|               file version               |        -         |     2      |               |
|                file flags                |        -         |     1      | 0x00 - binary |
|               plugin count               |        -         |     2      |               |
| the count of constants in constants pool |        -         |     2      |               |
|              constants pool              |        -         |            |               |
|       data_index_count_per_plugin        |        -         |     2      |               |
|             name_data_index              |      String      |     2      |               |
|              id_data_index               |      String      |     2      |               |
|            author_data_index             |      String      |     2      |               |
|            version_data_index            |      String      |     2      |               |
|            runtime_data_index            |      String      |     2      |               |
|             time_data_index              |      String      |     2      |               |
|              tag_data_index              |      String      |     2      |               |
|         introduction_data_index          |      String      |     2      |               |
|          pages_count_data_index          |     Integer      |     2      |               |
|          pages_info[page_count]          |        -         |     -      |               |
|      name_data_index [next plugin]       |      String      |     2      |               |
|                   ....                   |                  |            |               |

#### Page Structure

|              Area               | Field Value Type | Comments |
|:-------------------------------:|:----------------:|:--------:|
|          id_data_index          |      String      |          |
|         url_data_index          |      String      |          |
|          js_data_index          |      String      |          |
|         dsl_field_count         |     Integer      |          |
| dsl_field_info[dsl_field_count] |        -         |          |

#### Dsl Structure

|              Area               | Field Value Type | Comments |
|:-------------------------------:|:----------------:|:--------:|
|        field1_data_index        |      String      |          |
|        field2_data_index        |      String      |          |
|        field3_data_index        |      String      |          |
|               ...               |       ...        |   ...    |


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
