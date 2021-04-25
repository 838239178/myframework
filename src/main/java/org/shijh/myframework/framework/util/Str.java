package org.shijh.myframework.framework.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Str {
    //language=RegExp
    public static final String EMAIL_REGEX = ".*@.*\\..*";

    public static String urlDecode(String s) {
        try {
            return URLDecoder.decode(s, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return "url decode error";
        }
    }
    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s,"utf-8");
        } catch (UnsupportedEncodingException e) {
            return "url encode error";
        }
    }

    public static String b64Encode(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static byte[] b64Decode(String s) {
        return Base64.getDecoder().decode(s);
    }

    /**
     * 如果为null或者空字符串都认为是Empty
     *
     * @param s
     * @return
     */
    public static boolean empty(String s) {
        return s == null || s.isEmpty();
    }

    /**
     * 正则匹配，返回所有符合条件的表达式 <br/>
     *
     * @param regex 正则表达式
     * @param str 目标字符串
     * @return <b>String[]</b> 所有匹配到的字符串
     */
    public static String[] match(String regex, String str) {
        Matcher matcher = Pattern.compile(regex).matcher(str);
        List<String> groups = new ArrayList<>();
        while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                String s = matcher.group(i);
                groups.add(s);
            }
        }
        return groups.toArray(new String[0]);
    }

    /**
     * 返回正则匹配的第一个值，如果没有匹配到任何值则返回null
     *
     * @param regex 正则表达式
     * @param str 目标字符串
     * @return String or null
     */
    public static String matchFst(String regex, String str) {
        String[] groups = match(regex, str);
        return groups.length > 0 ? groups[0] : null;
    }
}
