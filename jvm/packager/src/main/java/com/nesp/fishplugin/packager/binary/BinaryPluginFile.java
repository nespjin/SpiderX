package com.nesp.fishplugin.packager.binary;

import com.google.common.primitives.Bytes;
import com.google.gson.JsonObject;
import com.nesp.fishplugin.core.Environment;
import com.nesp.fishplugin.core.PluginUtil;
import com.nesp.fishplugin.core.data.Page2;
import com.nesp.fishplugin.core.data.Plugin2;
import com.nesp.fishplugin.packager.PluginFile;
import com.nesp.fishplugin.tools.code.JsMinifier;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class BinaryPluginFile implements PluginFile {

    /**
     * 常量池最多可存0xFFFF个常量
     */
    public static final int CONSTANT_POOL_CONSTANT_COUNT_SIZE = 2;

    /**
     * UTF-8编码的字符串
     */
    public static final int CONSTANT_Utf8_info = 0x01;

    /**
     * 整形字面量，boolean、byte、char、short等类型都用int存放
     */
    public static final int CONSTANT_Integer_info = 0x03;

    /**
     * 浮点型字面量
     */
    public static final int CONSTANT_Float_info = 0x04;

    /**
     * 长整型字面量
     */
    public static final int CONSTANT_Long_info = 0x05;

    /**
     * 双精度浮点型字面量
     */
    public static final int CONSTANT_Double_info = 0x06;

    public static final byte[] FILE_VERSION_CURRENT = new byte[]{0x00, 0x01};

    private int fileVersion = bytes2Int(FILE_VERSION_CURRENT);

    public void setFileVersion(int fileVersion) {
        this.fileVersion = fileVersion;
    }

    public int getFileVersion() {
        return fileVersion;
    }

    private int pluginCount;

    public int getPluginCount() {
        return pluginCount;
    }

    public void setPluginCount(int pluginCount) {
        this.pluginCount = pluginCount;
    }

    public static final class Constant {
        private final int tag;
        private final byte[] bytes;

        public Constant(int tag, byte[] bytes) {
            this.tag = tag;
            this.bytes = bytes;
        }

        public int tag() {
            return tag;
        }

        public byte[] bytes() {
            return bytes;
        }
    }

    /**
     * 常量池
     */
    public List<Constant> constantPool = new ArrayList<>();

    /**
     * 字段引用
     */
    public List<FieldRef> fieldRefs = new ArrayList<>();

    private static final byte DEVICE_TYPE_ALL = (byte) 0xFF;

    public static final class FieldRef {
        private final byte deviceType;

        /**
         * 字段常量映射，下标为字段Index，值为常量索引
         */
        private final int index;

        public FieldRef(byte deviceType, int index) {
            this.deviceType = deviceType;
            this.index = index;
        }

        public byte deviceType() {
            return deviceType;
        }

        public int index() {
            return index;
        }
    }

    public static class Value<T> {
        private final byte deviceType;
        private final T value;

        public Value(byte deviceType, T value) {
            this.deviceType = deviceType;
            this.value = value;
        }

        public byte deviceType() {
            return deviceType;
        }

        public T value() {
            return value;
        }
    }

    private final File file;

    private final List<Byte> content = new ArrayList<>();

    public BinaryPluginFile(String filePath) {
        this(new File(filePath));
    }

    public BinaryPluginFile(File file) {
        this.file = file;
    }

    @Override
    public void write(Plugin2[] plugins) throws IOException {
        if (plugins == null || plugins.length == 0) {
            throw new NullPointerException("plugins must not be null or empty");
        }
        // clear content at first
        content.clear();

        // Write file version
        content.addAll(Bytes.asList(FILE_VERSION_CURRENT));

        // Write file flags
        byte b1 = int2Bytes(FILE_FLAG_BINARY)[0];
        content.add(b1);

        // Write plugin count
        content.addAll(Bytes.asList(int2Bytes(plugins.length, 2)));

        int dataIndexCountPerPlugin = -1;

        for (int i = 0; i < plugins.length; i++) {
            Plugin2 plugin = plugins[i];
            // Write plugin
            writeStringField(new Value<>(DEVICE_TYPE_ALL, plugin.getName()));
            writeStringField(new Value<>(DEVICE_TYPE_ALL, plugin.getId()));
            writeStringField(new Value<>(DEVICE_TYPE_ALL, plugin.getAuthor()));
            writeStringField(new Value<>(DEVICE_TYPE_ALL, plugin.getVersion()));
            writeStringField(new Value<>(DEVICE_TYPE_ALL, plugin.getRuntime()));
            writeStringField(new Value<>(DEVICE_TYPE_ALL, plugin.getTime()));
            StringJoiner tagsString = new StringJoiner(",");
            for (String tag : plugin.getTags()) tagsString.add(tag);
            writeStringField(new Value<>(DEVICE_TYPE_ALL, tagsString.toString()));
            writeIntegerField(new Value<>(DEVICE_TYPE_ALL, plugin.getDeviceFlags()));
            writeIntegerField(new Value<>(DEVICE_TYPE_ALL, plugin.getType()));
            writeStringField(new Value<>(DEVICE_TYPE_ALL, plugin.getIntroduction()));

            List<Page2> pages = plugin.getPages();
            writeIntegerField(new Value<>(DEVICE_TYPE_ALL, pages.size()));
            if (pages.size() > 0) {
                for (Page2 page : pages) {
                    writeStringField(new Value<>(DEVICE_TYPE_ALL, page.getId()));

                    String[] refUrls = page.getAllRefUrls();
                    for (int deviceType = 0; deviceType < refUrls.length - 1; deviceType++) {
                        writeStringField(new Value<>((byte) deviceType, refUrls[deviceType]));
                    }
                    writeStringField(new Value<>(DEVICE_TYPE_ALL, refUrls[refUrls.length - 1]));

                    String[] urls = page.getAllUrls();
                    for (int deviceType = 0; deviceType < urls.length - 1; deviceType++) {
                        writeStringField(new Value<>((byte) deviceType, urls[deviceType]));
                    }
                    writeStringField(new Value<>(DEVICE_TYPE_ALL, urls[urls.length - 1]));

                    String[] js = page.getAllJs();
                    for (int deviceType = 0; deviceType < js.length - 1; deviceType++) {
                        writeStringField(new Value<>((byte) deviceType, new JsMinifier().minify(js[deviceType]))); // will be empty
                    }
                    writeStringField(new Value<>(DEVICE_TYPE_ALL, new JsMinifier().minify(js[js.length - 1]))); // will be empty

                    Object[] dslArray = page.getAllDsl();
                    for (int deviceType = 0; deviceType < dslArray.length; deviceType++) {
                        Map<?, ?> dslMap = null;
                        Object dsl = dslArray[deviceType]; // Dsl is a map at present
                        if (dsl instanceof Map<?, ?>) {
                            dslMap = (Map<?, ?>) dsl;
                        } else if (dsl instanceof JSONObject) {
                            dslMap = ((JSONObject) dsl).toMap();
                        }
                        writeIntegerField(new Value<>(deviceType == dslArray.length - 1 ? DEVICE_TYPE_ALL : (byte) deviceType, dslMap == null ? 0 : dslMap.size()));

                        if (dslMap != null && dslMap.size() > 0) {
                            Collection<?> keySet = dslMap.keySet();
                            for (Object key : keySet) {
                                writeStringField(new Value<>((deviceType == dslArray.length - 1 ? DEVICE_TYPE_ALL : (byte) deviceType), String.valueOf(key)));
                                writeStringField(new Value<>((deviceType == dslArray.length - 1 ? DEVICE_TYPE_ALL : (byte) deviceType), String.valueOf(dslMap.get(key))));
                            }
                        }
                    }
                }
            }

            if (i == 0) {
                // compute data index count per plugin
                dataIndexCountPerPlugin = fieldRefs.size();
            }
        }

        // Add constant count of constant in constant poll
        int size = constantPool.size();
        content.addAll(Bytes.asList(int2Bytes(size, 2)));

        // Add Field Tables of constants pool
        for (Constant constant : constantPool) {
            // Add tag
            int tag = constant.tag();
            content.add(int2Bytes(tag)[0]);
            int primitivesTypeBytes = -1;
            if (tag == CONSTANT_Utf8_info) {
                // Add len
                content.addAll(Bytes.asList(int2Bytes(constant.bytes().length, 2)));
                // Add bytes
                content.addAll(Bytes.asList(constant.bytes()));
            }
            // 2022-01-25 Fix: The number of bytes of other types other than strings should be a fixed value
            else if (tag == CONSTANT_Integer_info) {
                primitivesTypeBytes = Integer.BYTES;
            } else if (tag == CONSTANT_Float_info) {
                primitivesTypeBytes = Float.BYTES;
            } else if (tag == CONSTANT_Long_info) {
                primitivesTypeBytes = Long.BYTES;
            } else if (tag == CONSTANT_Double_info) {
                primitivesTypeBytes = Double.BYTES;
            }

            if (primitivesTypeBytes > 0) {
                content.addAll(Bytes.asList(fillMinLen(constant.bytes(), primitivesTypeBytes)));
            }
        }

        content.addAll(Bytes.asList(int2Bytes(dataIndexCountPerPlugin, 2)));

        // Add Field
        for (FieldRef ref : fieldRefs) {
            content.add(ref.deviceType());
            content.addAll(Bytes.asList(int2Bytes(ref.index(), 2)));
        }

        writeContentToFile();
    }

    protected void writeContentToFile() throws IOException {
        System.out.println(file.getAbsolutePath());
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        if (!file.exists()) file.createNewFile();
        try (OutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(Bytes.toArray(content));
            outputStream.flush();
        }
    }

    protected void writeStringField(Value<String> value) {
        Constant valueTryFind = null;

        for (Constant constant : constantPool) {
            if (constant.tag() == CONSTANT_Utf8_info && new String(constant.bytes(), StandardCharsets.UTF_8).equals(value.value())) {
                valueTryFind = constant;
                break;
            }
        }

        if (valueTryFind != null) {
            int index = constantPool.indexOf(valueTryFind);
            fieldRefs.add(new FieldRef(value.deviceType(), index));
            return;
        }

        Constant constant = new Constant(CONSTANT_Utf8_info, value.value().getBytes(StandardCharsets.UTF_8));
        constantPool.add(constant);
        fieldRefs.add(new FieldRef(value.deviceType(), constantPool.size() - 1));
    }

    protected void writeIntegerField(Value<Integer> value) {
        Constant valueTryFind = null;

        for (Constant constant : constantPool) {
            if (constant.tag() == CONSTANT_Integer_info && bytes2Int(constant.bytes()) == value.value()) {
                valueTryFind = constant;
                break;
            }
        }

        if (valueTryFind != null) {
            fieldRefs.add(new FieldRef(value.deviceType(), constantPool.indexOf(valueTryFind)));
            return;
        }

        Constant constant = new Constant(CONSTANT_Integer_info, int2Bytes(value.value()));
        constantPool.add(constant);
        fieldRefs.add(new FieldRef(value.deviceType(), constantPool.size() - 1));
    }

    protected void writeFloatField(Value<Float> value) {
        Constant valueTryFind = null;
        for (Constant constant : constantPool) {
            if (constant.tag() == CONSTANT_Float_info && Float.intBitsToFloat(bytes2Int(constant.bytes())) == value.value()) {
                valueTryFind = constant;
                break;
            }
        }
        if (valueTryFind != null) {
            fieldRefs.add(new FieldRef(value.deviceType(), constantPool.indexOf(valueTryFind)));
            return;
        }

        Constant constant = new Constant(CONSTANT_Float_info, int2Bytes(Float.floatToIntBits(value.value())));
        constantPool.add(constant);
        fieldRefs.add(new FieldRef(value.deviceType(), constantPool.size() - 1));
    }

    protected void writeLongField(Value<Long> value) {
        Constant valueTryFind = null;
        for (Constant constant : constantPool) {
            if (constant.tag() == CONSTANT_Long_info && bytes2Long(constant.bytes()) == value.value()) {
                valueTryFind = constant;
                break;
            }
        }
        if (valueTryFind != null) {
            fieldRefs.add(new FieldRef(value.deviceType(), constantPool.indexOf(valueTryFind)));
            return;
        }

        Constant constant = new Constant(CONSTANT_Long_info, long2Bytes(value.value()));
        constantPool.add(constant);
        fieldRefs.add(new FieldRef(value.deviceType(), constantPool.size() - 1));
    }

    protected void writeDoubleField(Value<Double> value) {
        Constant valueTryFind = null;
        for (Constant constant : constantPool) {
            if (constant.tag() == CONSTANT_Double_info && Double.longBitsToDouble(bytes2Long(constant.bytes())) == value.value()) {
                valueTryFind = constant;
                break;
            }
        }
        if (valueTryFind != null) {
            fieldRefs.add(new FieldRef(value.deviceType(), constantPool.indexOf(valueTryFind)));
            return;
        }
        Constant constant = new Constant(CONSTANT_Double_info, long2Bytes(Double.doubleToLongBits(value.value())));
        constantPool.add(constant);
        fieldRefs.add(new FieldRef(value.deviceType(), constantPool.size() - 1));
    }

    @Override
    public int readFileVersion() throws IOException {
        if (content.isEmpty()) {
            readContentFromFile();
        }
        return bytes2Int(Bytes.toArray(content.subList(0, 2)));
    }

    @Override
    public int readFileFlags() throws IOException {
        if (content.isEmpty()) {
            readContentFromFile();
        }
        return bytes2Int(new byte[]{content.subList(2, 3).get(0)});
    }

    protected int readPos = -1;

    @Override
    public Plugin2[] read() throws IOException, ReadNotMatchTypeException {
        if (content.isEmpty()) {
            readContentFromFile();
        }

        // file version
        setFileVersion(readFileVersion());

        // flags - ignore

        // plugin count
        int pluginCount = bytes2Int(Bytes.toArray(content.subList(3, 5)));
        if (pluginCount < 1) {
            throw new IOException("file format error");
        }

        // parse pool
        int constantsCountInConstantsPool = bytes2Int(Bytes.toArray(content.subList(5, 7)));
        constantPool.clear();
        int readAreaPos = 7;
        for (int i = 0; i < constantsCountInConstantsPool; i++) {
            int tag = content.get(readAreaPos);
            readAreaPos++;

            int len = -1;
            if (tag == CONSTANT_Utf8_info) {
                len = bytes2Int(Bytes.toArray(content.subList(readAreaPos, readAreaPos + 2)));
                readAreaPos += 2;
            } else if (tag == CONSTANT_Integer_info) {
                len = Integer.BYTES;
            } else if (tag == CONSTANT_Float_info) {
                len = Float.BYTES;
            } else if (tag == CONSTANT_Long_info) {
                len = Long.BYTES;
            } else if (tag == CONSTANT_Double_info) {
                len = Double.BYTES;
            }

            if (len >= 0) {
                constantPool.add(new Constant(tag, Bytes.toArray(content.subList(readAreaPos, readAreaPos + len))));
                readAreaPos += len;
            }
        }

        // build data ref
        int dataIndexCount = bytes2Int(Bytes.toArray(content.subList(readAreaPos, readAreaPos + 2)));
        readAreaPos += 2;

        fieldRefs.clear();

        for (int i = 0; i < dataIndexCount * pluginCount; i++) {
            byte deviceType = content.subList(readAreaPos, readAreaPos + 1).get(0);
            readAreaPos += 1;
            int index = bytes2Int(Bytes.toArray(content.subList(readAreaPos, readAreaPos + 2)));
            readAreaPos += 2;
            fieldRefs.add(new FieldRef(deviceType, index));
        }

        Plugin2[] plugins = new Plugin2[pluginCount];

        for (int i = 0; i < pluginCount; i++) {
            readPos = -1; // reset read pos.
            Plugin2 plugin = new Plugin2();
            plugin.setName(readStringField().value());
            plugin.setId(readStringField().value());
            plugin.setAuthor(readStringField().value());
            plugin.setVersion(readStringField().value());
            plugin.setRuntime(readStringField().value());
            plugin.setTime(readStringField().value());

            String tagsString = readStringField().value();
            if (!tagsString.isEmpty()) {
                if (!tagsString.contains(",")) {
                    plugin.setTags(new ArrayList<>() {
                        {
                            add(tagsString);
                        }
                    });
                } else {
                    plugin.setTags(Arrays.asList(tagsString.split(",")));
                }
            }

            plugin.setDeviceFlags(readIntegerField().value());
            plugin.setType(readIntegerField().value());
            plugin.setIntroduction(readStringField().value());

            // page
            // page count
            int pageCount = readIntegerField().value();
            List<Page2> pages = new ArrayList<>();
            int deviceTypeCount = Environment.allDeviceTypes().length + 1;
            Value<String> curStringValue;
            int curDeviceType;
            for (int j = 0; j < pageCount; j++) {
                Page2 page = new Page2();

                page.setId(readStringField().value());

                for (int k = 0; k < deviceTypeCount; k++) {
                    curStringValue = readStringField();
                    if (curStringValue.value().isEmpty()) continue;
                    curDeviceType = curStringValue.deviceType();
                    page.setRefUrl(curStringValue.value(), curDeviceType == DEVICE_TYPE_ALL ? null : curDeviceType);
                }

                for (int k = 0; k < deviceTypeCount; k++) {
                    curStringValue = readStringField();
                    if (curStringValue.value().isEmpty()) continue;
                    curDeviceType = curStringValue.deviceType();
                    page.setUrl(curStringValue.value(), curDeviceType == DEVICE_TYPE_ALL ? null : curDeviceType);
                }

                for (int k = 0; k < deviceTypeCount; k++) {
                    curStringValue = readStringField();
                    if (curStringValue.value().isEmpty()) continue;
                    curDeviceType = curStringValue.deviceType();
                    page.setJs(curStringValue.value(), curDeviceType == DEVICE_TYPE_ALL ? null : curDeviceType);
                }

                for (int k = 0; k < deviceTypeCount; k++) {
                    // Dsl count
                    int dslMapSize = readIntegerField().value();
                    if (dslMapSize > 0) {
                        Map<String, String> dslMap = new HashMap<>();
                        for (int l = 0; l < dslMapSize; l++) {
                            curStringValue = readStringField();
                            Value<String> curStringValue2 = readStringField();
                            curDeviceType = curStringValue.deviceType();
                            dslMap.put(PluginUtil.getFieldNameWithDeviceType(curStringValue.value(), curDeviceType == DEVICE_TYPE_ALL ? null : curDeviceType), curStringValue2.value());
                        }
                        page.setDsl(dslMap);
                    }
                }
                pages.add(page);
            }
            plugin.setPages(pages);
            plugins[i] = plugin;
        }
        return plugins;
    }

    protected void readContentFromFile() throws IOException {
        byte[] buffer = new byte[1024];
        int readLen;
        try (InputStream inputStream = new FileInputStream(file);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            while ((readLen = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, readLen);
            }
            outputStream.flush();
            content.clear();
            content.addAll(Bytes.asList(outputStream.toByteArray()));
        }
    }

    protected Value<String> readStringField() throws IndexOutOfBoundsException, ReadNotMatchTypeException {
        FieldRef fieldRef = this.fieldRefs.get(++readPos);
        Constant constant = constantPool.get(fieldRef.index());
        if (constant.tag() != CONSTANT_Utf8_info) {
            throw new ReadNotMatchTypeException(getTypeNameByTag(constant.tag()) + " cant match to string");
        }
        return new Value<>(fieldRef.deviceType(), new String(constant.bytes()));
    }

    protected Value<Integer> readIntegerField() throws IndexOutOfBoundsException, ReadNotMatchTypeException {
        FieldRef fieldRef = this.fieldRefs.get(++readPos);
        Constant constant = constantPool.get(fieldRef.index());
        if (constant.tag() != CONSTANT_Integer_info) {
            throw new ReadNotMatchTypeException(getTypeNameByTag(constant.tag()) + " cant match to int");
        }
        return new Value<>(fieldRef.deviceType(), bytes2Int(constant.bytes()));
    }

    protected Value<Float> readFloatField() throws IndexOutOfBoundsException, ReadNotMatchTypeException {
        FieldRef fieldRef = this.fieldRefs.get(++readPos);
        Constant constant = constantPool.get(fieldRef.index());
        if (constant.tag() != CONSTANT_Float_info) {
            throw new ReadNotMatchTypeException(getTypeNameByTag(constant.tag()) + " cant match to float");
        }
        return new Value<>(fieldRef.deviceType(), Float.intBitsToFloat(bytes2Int(constant.bytes())));
    }

    protected Value<Long> readLongField() throws IndexOutOfBoundsException, ReadNotMatchTypeException {
        FieldRef fieldRef = this.fieldRefs.get(++readPos);
        Constant constant = constantPool.get(fieldRef.index());
        if (constant.tag() != CONSTANT_Long_info) {
            throw new ReadNotMatchTypeException(getTypeNameByTag(constant.tag()) + " cant match to long");
        }
        return new Value<>(fieldRef.deviceType(), bytes2Long(constant.bytes()));
    }

    protected Value<Double> readDoubleField() throws IndexOutOfBoundsException, ReadNotMatchTypeException {
        FieldRef fieldRef = this.fieldRefs.get(++readPos);
        Constant constant = constantPool.get(fieldRef.index());
        if (constant.tag() != CONSTANT_Double_info) {
            throw new ReadNotMatchTypeException(getTypeNameByTag(constant.tag()) + " cant match to double");
        }
        return new Value<Double>(fieldRef.deviceType(), Double.longBitsToDouble(bytes2Long(constant.bytes())));
    }

    protected String getTypeNameByTag(int tag) {
        return switch (tag) {
            case CONSTANT_Utf8_info -> "UTF-8 String";
            case CONSTANT_Integer_info -> "Integer";
            case CONSTANT_Float_info -> "Float";
            case CONSTANT_Long_info -> "Long";
            case CONSTANT_Double_info -> "Double";
            default -> "";
        };
    }

    private static byte[] long2Bytes(long l) {
        return long2Bytes2(l);
    }

    private static long bytes2Long(byte[] b) {
        return bytes2Long2(b);
    }

    private static byte[] int2Bytes(int n, int minLen) {
        return fillMinLen(int2Bytes(n), minLen);
    }

    private static byte[] fillMinLen(byte[] src, int minLen) {
        if (src.length >= minLen) return src;
        int diffLen = minLen - src.length;
        byte[] des = new byte[minLen];
        System.arraycopy(src, 0, des, diffLen, src.length);
        Arrays.fill(des, 0, diffLen, (byte) 0x00);
        return des;
    }

    private static byte[] int2Bytes(int n) {
        try {
            String hexString = Integer.toHexString(n);
            if (hexString.length() % 2 != 0) {
                hexString = "0" + hexString;
            }
            return Hex.decodeHex(hexString);
        } catch (DecoderException e) {
            return int2Bytes2(n);
        }
    }

    private static int bytes2Int(byte[] b) {
        return Integer.parseInt(Hex.encodeHexString(b), 16);
//        return bytes2Int2(b);
    }

    private static int bytes2Int2(byte[] b) {
        int res = 0;
        for (int i = 0; i < b.length; i++) {
            res += (b[i] & 0xFF) << (b.length - i - 1) * Byte.SIZE;
        }
        return res;
    }

    private static byte[] int2Bytes2(int n) {
        int len = Integer.BYTES;
        byte[] bytes = new byte[len];
        for (int i = 0; i < len; i++) {
            bytes[len - i - 1] = (byte) (n >> (Byte.SIZE * i) & 0xFF);
        }

        for (byte b : bytes) {
            if (b == 0x00) len--;
            else break;
        }

        if (len == 0) {
            bytes = new byte[]{0x00};
        } else if (len != bytes.length) {
            bytes = Arrays.copyOfRange(bytes, bytes.length - len, bytes.length);
        }

        return bytes;
    }

    private static long bytes2Long2(byte[] b) {
        long res = 0;
        for (int i = 0; i < b.length; i++) {
            res += (long) (b[i] & 0xFF) << (b.length - i - 1) * Byte.SIZE;
        }
        return res;
    }

    private static byte[] long2Bytes2(long n) {
        int len = Long.BYTES;
        byte[] bytes = new byte[len];
        for (int i = 0; i < len; i++) {
            bytes[len - i - 1] = (byte) (n >> (Byte.SIZE * i) & 0xFF);
        }

        for (byte b : bytes) {
            if (b == 0x00) len--;
            else break;
        }

        if (len == 0) {
            bytes = new byte[]{0x00};
        } else if (len != bytes.length) {
            bytes = Arrays.copyOfRange(bytes, bytes.length - len, bytes.length);
        }

        return bytes;
    }
}
