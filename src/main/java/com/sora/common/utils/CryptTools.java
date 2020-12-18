package com.sora.common.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class CryptTools {

    public static String crypt(String str) {
        MessageDigest digest = null;
        String crypt = "";
        try {
            digest = MessageDigest.getInstance("SHA-256");
            digest.update(str.getBytes("UTF-8"));
            crypt = byteHex(digest.digest());
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return crypt;
    }

    private static String byteHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        String str;
        for (int i = 0; i < bytes.length; i++) {
            str = Integer.toHexString(bytes[i] & 0xFF);
            if (str.length() == 1) sb.append("0");
            sb.append(str);
        }
        return sb.toString();
    }

    public static String getText() {
        int[] array = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        Random random = new Random();
        StringBuffer text = new StringBuffer();
        Stream.iterate(0, x -> x + 1).limit(6).forEach(x -> text.append(array[random.nextInt(array.length)]));
        return text.toString();
    }

    public static String getUuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static boolean isPhone(String phone) {
        Pattern pattern = Pattern.compile("^((13[0-9])|(19[0-9])|(17[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
        return pattern.matcher(phone).matches();
    }

    public static boolean isEmail(String email) {
        String text = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(text);
        return pattern.matcher(email).matches();
    }

    public static String createSerial() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        StringBuffer buffer = new StringBuffer(UUID.randomUUID().toString().replaceAll("\\D", ""));
        while (buffer.length() < 18) {
            buffer.append(UUID.randomUUID().toString().replaceAll("\\D", ""));
        }
        return sdf.format(new Date()) + buffer.toString().substring(0, 18);
    }

}
