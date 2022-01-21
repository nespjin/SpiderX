package com.nesp.sdk.java.text;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * Time: Created 2021/8/2 16:51
 **/
@SuppressWarnings("unused")
public final class TextUtil {
    private static final String TAG = "TextUtil";

    private TextUtil() { /* cannot be instantiated */ }

    public static CharSequence ifEmpty(CharSequence cs, CharSequence csIfEmpty) {
        if (isEmpty(cs)) {
            return csIfEmpty;
        }
        return cs;
    }

    public static boolean exitsEmpty(CharSequence... charSequences) {
        if (charSequences == null || charSequences.length == 0) {
            return true;
        }

        for (final CharSequence charSequence : charSequences) {
            if (isEmpty(charSequence)) return true;
        }

        return false;
    }

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isNotEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }

    /**
     * @param text 要判断的内容
     * @return 是否为数字
     */
    public static boolean isNumber(final String text) {
        if (isEmpty(text)) return false;
        // String reg = "^[0-9]+(.[0-9]+)?$";
        final String reg = "^(-?\\d+)(\\.\\d+)?";
        return text.matches(reg);
    }

    /**
     * @param text 要判断的内容
     * @return 是否为数字或大小字母
     */
    public static boolean isNumberOrLetter(final String text) {
        if (isEmpty(text)) return false;
        final String reg = "^[a-z0-9A-Z]+$";
        return text.matches(reg);
    }

    /**
     * @param text 要判断的内容
     * @return 是否为Email
     */
    public static boolean isEmail(String text) {
        if (isEmpty(text)) return false;
        return text.matches("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
    }

    /**
     * @param text 要判断的内容
     * @return 是否为中文
     */
    public static boolean isChinese(String text) {
        if (isEmpty(text)) return false;
        return text.matches("^[\\u4E00-\\u9FA5\\uF900-\\uFA2D]+$");
    }

    /**
     * 判断字符是否是中文
     *
     * @param c 字符
     * @return 是否是中文
     */
    public static boolean isChinese(char c) {
        final Character.UnicodeBlock unicodeBlock = Character.UnicodeBlock.of(c);
        return unicodeBlock == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || unicodeBlock == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || unicodeBlock == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || unicodeBlock == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || unicodeBlock == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || unicodeBlock == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
    }

    public static boolean isHex(String key) {
        for (int i = key.length() - 1; i >= 0; i--) {
            final char c = key.charAt(i);
            if (!(c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a' && c <= 'f')) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param text   要判断的内容
     * @param minLen 最小长度 必须 > 0
     * @param maxLen 最大长度 必须 > minLen
     * @return 是否为中文或数字或字母并且长度在限制范围内
     */
    public static boolean isChineseOrNumberOrLetterLimitIn(final String text,
                                                           final int minLen,
                                                           final int maxLen) {
        if (isEmpty(text)) return false;
        if (minLen < 0) throw new IllegalArgumentException("minLen must > 0");
        if (maxLen < minLen) throw new IllegalArgumentException("maxLen must > minLen");
        return text.matches("^[\\u4E00-\\u9FA5\\uF900-\\uFA2D\\w]{" + minLen + "," + maxLen + "}$");
    }

    public static boolean isInteger(final String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static Integer toInteger(final String text, final Integer defaultValue) {
        return toInteger(text, 10, defaultValue);
    }

    public static Integer toInteger(final String text, final int radix, final Integer defaultValue) {
        try {
            return Integer.parseInt(text, radix);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static boolean isFloat(final String text) {
        try {
            Float.parseFloat(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static Float toFloat(final String text, final Float defaultValue) {
        try {
            return Float.parseFloat(text);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * @param text text
     * @return 是否为Double值
     */
    public static boolean isDouble(final String text) {
        try {
            Double.parseDouble(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static Double toDouble(final String text, final Double defaultValue) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * @param text text
     * @return 是否为链接
     */
    public static boolean isUrl(final String text) {
        return text.startsWith("https://") || text.startsWith("http://");
    }

    /**
     * @param text text
     * @return 编码类型
     */
    public static String getEncoding(String text) {
        String encode = "GB2312";
        try {
            if (text.equals(new String(text.getBytes(encode), encode))) {
                return encode;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        encode = "ISO-8859-1";
        try {
            if (text.equals(new String(text.getBytes(encode), encode))) {
                return encode;
            }
        } catch (Exception exception1) {
            exception1.printStackTrace();
        }
        encode = "UTF-8";
        try {
            if (text.equals(new String(text.getBytes(encode), encode))) {
                return encode;
            }
        } catch (Exception exception2) {
            exception2.printStackTrace();
        }
        encode = "GBK";
        try {
            if (text.equals(new String(text.getBytes(encode), encode))) {
                return encode;
            }
        } catch (Exception exception3) {
            exception3.printStackTrace();
        }
        encode = "ASCII";
        try {
            if (text.equals(new String(text.getBytes(encode), encode))) {
                return encode;
            }
        } catch (Exception exception3) {
            exception3.printStackTrace();
        }
        encode = "GB18030";
        try {
            if (text.equals(new String(text.getBytes(encode), encode))) {
                return encode;
            }
        } catch (Exception exception3) {
            exception3.printStackTrace();
        }
        encode = "Unicode";
        try {
            if (text.equals(new String(text.getBytes(encode), encode))) {
                return encode;
            }
        } catch (Exception exception3) {
            exception3.printStackTrace();
        }
        encode = "Shift_JIS";
        try {
            if (text.equals(new String(text.getBytes(encode), encode))) {
                return encode;
            }
        } catch (Exception exception3) {
            exception3.printStackTrace();
        }
        return "";
    }

    /**
     * 判断是否乱码
     */
    public static boolean isMessyCode2(String text) {
        if (isEmpty(text)) return false;
        try {
            Pattern p = Pattern.compile("\\s*|\t*|\r*|\n*");
            Matcher m = p.matcher(text);
            String after = m.replaceAll("");
            String temp = after.replaceAll("\\p{P}", "");
            char[] ch = temp.trim().toCharArray();
            int length = ch.length;
            for (char c : ch) {
                if (!Character.isLetterOrDigit(c)) {
                    String str = "" + c;
                    if (!str.matches("[\u4e00-\u9fa5]+")) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断字符串是否是乱码
     *
     * @param strName 字符串
     * @return 是否是乱码
     */
    public static boolean isMessyCode(String strName) {
        return !Charset.forName("GBK").newEncoder().canEncode(strName);
    }

    /**
     * 普通的英文半角空格Unicode编码
     */
    private static final int SPACE_32 = 32;

    /**
     * 中文全角空格Unicode编码(一个中文宽度)
     */
    private static final int SPACE_12288 = 12288;

    /**
     * 普通的英文半角空格但不换行Unicode编码(== &nbsp; == &#xA0; == no-break space)
     */
    private static final int SPACE_160 = 160;

    /**
     * 半个中文宽度(== &ensp; == en空格)
     */
    private static final int SPACE_8194 = 8194;

    /**
     * 一个中文宽度(== &emsp; == em空格)
     */
    private static final int SPACE_8195 = 8195;

    /**
     * 四分之一中文宽度(四分之一em空格)
     */
    private static final int SPACE_8197 = 8197;

    /**
     * 窄空格
     **/
    private static final int SPACE_8201 = 8201;

    /**
     * UTF8+BOM文件格式读取的第一个字符的ASCII码为65279
     **/
    private static final int SPACE_UTF8_BOM = 65279;

    /**
     * 去除字符串前后的空格, 包括半角空格和全角空格(中文)等各种空格, java的string.trim()只能去英文半角空格
     *
     * @param text text
     */
    public static String trim(String text) {
        return trimChar(text, TextUtil::isSpace);
    }

    public static boolean isSpace(char aChar) {
        return aChar == SPACE_32 || aChar == SPACE_12288 || aChar == SPACE_160 || aChar == SPACE_8194
                || aChar == SPACE_8195 || aChar == SPACE_8197 || aChar == SPACE_8201 || aChar == SPACE_UTF8_BOM;
    }

    public static String trimEnter(final String string) {
        String stringTmp = string.replace("\r\n", "\n")
                .replace("\r", "\n");
        return trimChar(stringTmp, c -> c == '\n');
    }

    private interface TrimCondition {
        boolean canTrim(char c);
    }

    public static String trimChar(final String text, final TrimCondition trimCondition) {
        if (TextUtil.isEmpty(text) || trimCondition == null) {
            return text;
        }

        char[] val = text.toCharArray();
        int st = 0;
        int len = val.length;
        while ((st < len) && trimCondition.canTrim(val[st])) {
            st++;
        }
        while ((st < len) && trimCondition.canTrim(val[len - 1])) {
            len--;
        }
        return ((st > 0) || (len < val.length)) ? text.substring(st, len) : text;
    }

    public static String removeUnusedDecimalPoint(String text) {
        if (text.indexOf(".") > 0) {
            text = text.replace("0+?$", "");
            text = text.replace("[.]$", "");
        }
        return text;
    }

    public static boolean checkLength(String text, int maxLength) {
        return text.getBytes(StandardCharsets.UTF_8).length <= maxLength;
    }

    public static String trimWithinMaxLength(String text, int maxLength) {
        String string = text;
        while (!checkLength(string, maxLength)) string = string.substring(0, string.length() - 1);
        return string;
    }

    public String trimStart(String text) {
        if (text == null) return null;
        if (text.trim().isEmpty()) return "";
        String result;
        if (text.startsWith(" ")) {
            result = text.substring(0, text.indexOf(text.trim().substring(0, 1)) + text.trim().length());
        } else {
            result = text.trim();
        }

        return result;
    }

    public static String trimBlankAndCarriageReturn(final String text) {
        if (text == null) return null;
        if (text.trim().isEmpty()) return "";
        String dest;
        Pattern pattern = Pattern.compile("\r|\n");
        Matcher matcher = pattern.matcher(text);
        dest = matcher.replaceAll(" ");
        return dest;
    }

    public static boolean isASCII(char c) {
        return c < 128;
    }
}
