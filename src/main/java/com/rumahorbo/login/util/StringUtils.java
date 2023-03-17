package com.rumahorbo.login.util;

import org.json.JSONObject;

import java.util.Base64;

public class StringUtils {

    StringUtils() {}

    public static String getNameByDecodeToken(String token) {
        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        if (chunks.length > 1) {
            String payload = new String(decoder.decode(chunks[1]));
            JSONObject object = new JSONObject(payload);
            return object.getString("preferred_username");
        }
        return "";
    }
}
