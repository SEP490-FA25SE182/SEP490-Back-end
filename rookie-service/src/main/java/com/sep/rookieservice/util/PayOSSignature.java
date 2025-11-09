package com.sep.rookieservice.util;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.commons.codec.digest.HmacUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class PayOSSignature {
    private PayOSSignature(){}

    public static String hmacSha256(String key, String data) {
        return HmacUtils.hmacSha256Hex(key.getBytes(StandardCharsets.UTF_8), data.getBytes(StandardCharsets.UTF_8));
    }

    /** Chuỗi data để ký khi tạo link (đúng thứ tự alpha của 5 field) */
    public static String buildCreateLinkDataString(
            long amount, String cancelUrl, String description, long orderCode, String returnUrl) {

        return "amount=" + amount +
                "&cancelUrl=" + nullSafe(cancelUrl) +
                "&description=" + nullSafe(description) +
                "&orderCode=" + orderCode +
                "&returnUrl=" + nullSafe(returnUrl);
    }

    /** Chuỗi data để verify webhook: sort key alpha; mảng -> mỗi object sort key trước khi stringify */
    @SuppressWarnings("unchecked")
    public static String buildWebhookDataString(Map<String, Object> data) {
        // sort keys
        List<String> keys = new ArrayList<>(data.keySet());
        Collections.sort(keys);

        List<String> parts = new ArrayList<>();
        for (String k : keys) {
            Object v = data.get(k);

            if (v == null || "null".equals(v) || "undefined".equals(v)) {
                parts.add(k + "=");
                continue;
            }

            if (v instanceof List<?> list) {
                JSONArray jsonArr = new JSONArray();
                for (Object ele : list) {
                    if (ele instanceof Map<?, ?> m) {
                        JSONObject sortedObj = sortJsonObjectKeys((Map<String, Object>) m);
                        jsonArr.add(sortedObj);
                    } else {
                        jsonArr.add(ele);
                    }
                }
                parts.add(k + "=" + jsonArr);
            } else if (v instanceof Map<?, ?> m) {
                JSONObject sortedObj = sortJsonObjectKeys((Map<String, Object>) m);
                parts.add(k + "=" + sortedObj);
            } else {
                parts.add(k + "=" + v);
            }
        }
        return String.join("&", parts);
    }

    private static String nullSafe(String s) {
        if (s == null || "null".equals(s) || "undefined".equals(s)) return "";
        return s;
    }

    /** Tạo JSONObject mới với key đã sort alpha */
    private static JSONObject sortJsonObjectKeys(Map<String, Object> map) {
        JSONObject sorted = new JSONObject();
        map.keySet().stream().sorted().forEach(k -> sorted.put(k, map.get(k)));
        return sorted;
    }

    // PayOSSignature.java
    public static String buildSignatureFromMap(Map<String, Object> data) {
        // sort keys alpha
        List<String> keys = new ArrayList<>(data.keySet());
        Collections.sort(keys);

        List<String> parts = new ArrayList<>();
        for (String k : keys) {
            Object v = data.get(k);
            String val;
            if (v == null || "null".equals(v) || "undefined".equals(v)) {
                val = "";
            } else if (v instanceof Map<?, ?> m) {
                // object -> sort keys trước khi stringify
                JSONObject sortedObj = sortJsonObjectKeys((Map<String, Object>) m);
                val = sortedObj.toJSONString();
            } else if (v instanceof List<?> list) {
                // array -> stringify giữ nguyên thứ tự phần tử
                JSONArray arr = new JSONArray();
                for (Object ele : list) {
                    if (ele instanceof Map<?, ?> mm) {
                        arr.add(sortJsonObjectKeys((Map<String, Object>) mm));
                    } else {
                        arr.add(ele);
                    }
                }
                val = arr.toJSONString();
            } else {
                val = String.valueOf(v);
            }
            try {
                val = URLEncoder.encode(val, StandardCharsets.UTF_8);
            } catch (Exception ignore) {  }

            parts.add(k + "=" + val);
        }
        return String.join("&", parts);
    }
}