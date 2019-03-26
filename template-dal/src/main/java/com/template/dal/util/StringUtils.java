package com.template.dal.util;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;

public class StringUtils {
    public static String captureName(String name) {
        char[] cs = name.toCharArray();
        if (cs[0] >= 'a' && cs[0] <= 'z') {
            cs[0] -= 32;
        }
        return String.valueOf(cs);
    }


    public static boolean isEmpty(String str) {
        return str== null ? true : "".equals(str.trim());
    }


    public static <T> String join(Iterable<T> elements, String sep, final String leftEleStr, final String rightEleStr) {
        return org.apache.commons.lang3.StringUtils.join(Iterators.transform(elements.iterator(), new Function<T, String>() {
            @Override
            public String apply(T from) {
                return leftEleStr + from + rightEleStr;
            }
        }), sep);
    }

    public static <T> String join(Iterable<T> elements, String sep, final String eleStr) {
        return join(elements, sep, eleStr, eleStr);
    }

    public static <T> String join(Iterable<T> elements,String sep){
        return join(elements, sep, "", "");
    }

    public static String startsWith(String str, String sep) {
        if (str == null) {
            return null;
        }
        return str.startsWith(sep) ? str : sep + str;
    }

    public static String[] split(String str, String sep) {
        if(isEmpty(str)){
            return new String[0];
        }
        return str.split(sep);
    }

    public static String splitHeader(String str, String sep) {
        if(isEmpty(str)){
            return new String();
        }
        return str.split(sep)[0];
    }

    // .023格式的转化为0.023
    public static String numStr(String s) {
        if(s.startsWith(".")) {
            s = "0" + s;
        }
        return s;
    }

    public static String delZero(String s) {
        if(s.endsWith("0")) {
            s = s.substring(0,s.length()-1);
            s = delZero(s);
        }
        if(s.endsWith(".")) {
            s = s.substring(0,s.length()-1);
        }
        return s;
    }

    public static String captureFirstChar(String str) {
        if (DataUtil.isEmpty(str)) {
            return "";
        }
        return new StringBuffer(str).replace(0, 1, String.valueOf(Character.toUpperCase(str.charAt(0)))).toString();
    }

    public static String camelToUnderline(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append('_');
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

}

