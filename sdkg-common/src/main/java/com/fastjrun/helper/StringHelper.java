package com.fastjrun.helper;

import java.text.StringCharacterIterator;
import java.util.HashMap;
import java.util.Map;

public class StringHelper {
    public static final String DEFAULT_ENCODING = "UTF-8";

    public static String escape(String src, Map<String, String> map) {
        if (src == null || src.trim().length() == 0) {
            return "";
        }

        StringBuffer sb = new StringBuffer();
        StringCharacterIterator sci = new StringCharacterIterator(src);
        for (char c = sci.first(); c != StringCharacterIterator.DONE; c = sci.next()) {
            String ch = String.valueOf(c);
            if (map.containsKey(ch)) {
                ch = (String) map.get(ch);
            }
            sb.append(ch);
        }
        return sb.toString();
    }

    public static String escapeSQL(String input) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("'", "''");
        return escape(input, hashMap);
    }

    public static String escapeXML(String input) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("<", "&lt;");
        hashMap.put(">", "&gt;");
        hashMap.put("'", "&apos;");
        hashMap.put("\"", "&quot;");
        hashMap.put("&", "&amp;");
        return escape(input, hashMap);
    }

    /**
     * 去掉字符串中的逗号 例如：去掉金额字符串中的逗号
     */
    public static String removeComma(String string) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < string.length(); i++) {
            if (',' != string.charAt(i)) {
                sb.append(string.charAt(i));
            }
        }
        return sb.toString();
    }

    /**
     * 将字符串中的非字符转换成字母X
     * 
     */
    public static String toLetterOrDigit(String string) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < string.length(); i++) {
            if (Character.isLetterOrDigit(string.charAt(i))) {
                sb.append(string.charAt(i));
            } else {
                sb.append("X");
            }
        }
        return sb.toString();
    }

    /**
     * 将字符串中的非字母转换成字母
     * 
     */
    public static String toLetter(String string) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < string.length(); i++) {
            if (Character.isLetter(string.charAt(i))) {
                sb.append(string.charAt(i));
            } else if (Character.isDigit(string.charAt(i))) {
                switch (string.charAt(i)) {
                case '0': {
                    sb.append("A");
                    break;
                }
                case '1': {
                    sb.append("B");
                    break;
                }
                case '2': {
                    sb.append("C");
                    break;
                }
                case '3': {
                    sb.append("D");
                    break;
                }
                case '4': {
                    sb.append("E");
                    break;
                }
                case '5': {
                    sb.append("F");
                    break;
                }
                case '6': {
                    sb.append("G");
                    break;
                }
                case '7': {
                    sb.append("H");
                    break;
                }
                case '8': {
                    sb.append("I");
                    break;
                }
                case '9': {
                    sb.append("J");
                    break;
                }
                }
            } else {
                sb.append("M");
            }
        }
        return sb.toString();
    }

    public static String bytes2hex(byte[] bytes) {
        String result = "";
        String b = "";
        for (int i = 0; i < bytes.length; i++) {
            b = Integer.toHexString(bytes[i] & 0xFF);
            if (b.length() == 1) {
                b = "0" + b;
            }
            result += b;
        }
        return result.toUpperCase();
    }

    public static byte[] hex2bytes(String hexString) {
        // 转换成大写
        hexString = hexString.toUpperCase();

        // 计算字节数组的长度
        char[] chars = hexString.toCharArray();
        byte[] bytes = new byte[chars.length / 2];

        // 数组索引
        int index = 0;

        for (int i = 0; i < chars.length; i += 2) {
            byte newByte = 0x00;

            // 高位
            newByte |= char2byte(chars[i]);
            newByte <<= 4;

            // 低位
            newByte |= char2byte(chars[i + 1]);

            // 赋值
            bytes[index] = newByte;

            index++;
        }
        return bytes;
    }

    public static byte char2byte(char ch) {
        switch (ch) {
        case '0':
            return 0x00;
        case '1':
            return 0x01;
        case '2':
            return 0x02;
        case '3':
            return 0x03;
        case '4':
            return 0x04;
        case '5':
            return 0x05;
        case '6':
            return 0x06;
        case '7':
            return 0x07;
        case '8':
            return 0x08;
        case '9':
            return 0x09;
        case 'A':
            return 0x0A;
        case 'B':
            return 0x0B;
        case 'C':
            return 0x0C;
        case 'D':
            return 0x0D;
        case 'E':
            return 0x0E;
        case 'F':
            return 0x0F;
        default:
            return 0x00;
        }
    }

    /*
     * Converts a byte to hex digit and writes to the supplied buffer
     */
    private static void byte2hex(byte b, StringBuffer sb) {
        char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        int high = ((b & 0xf0) >> 4);
        int low = (b & 0x0f);
        sb.append(hexChars[high]);
        sb.append(hexChars[low]);
    }

    /**
     * Converts a byte array to hex string
     * 
     * @param bytes
     * @param c
     *            分隔符
     * @return 十六进制字符串
     */
    public static String toHexString(byte[] bytes, char c) {
        StringBuffer sb = new StringBuffer();
        int len = bytes.length;
        for (int i = 0; i < len; i++) {
            byte2hex(bytes[i], sb);
            if (i < len - 1) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Converts a byte array to hex string
     * 
     * @param bytes
     * @return 十六进制字符串
     */
    public static String toHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        int len = bytes.length;
        for (int i = 0; i < len; i++) {
            byte2hex(bytes[i], sb);
        }
        return sb.toString();
    }

    /**
     * 判断字符串是否为空
     * 
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        if (null == str || "".equals(str.trim())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断字符串是否不为空
     * 
     */
    public static boolean isNotEmpty(String str) {
        if (str != null && !"".equals(str.trim())) {
            return true;
        } else {
            return false;
        }
    }

    public static String replace(String string, String replacement) {
        if (string != null) {
            return string.replaceAll(replacement, "");
        } else {
            return null;
        }
    }

    /**
     * 
     */
    public static String trim(String string) {
        if (isEmpty(string)) {
            return "";
        } else {
            return string.trim();
        }
    }

}
