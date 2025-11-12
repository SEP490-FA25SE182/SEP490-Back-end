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

    private static JSONObject sortJsonObjectKeys(Map<String, Object> map) {
        JSONObject sorted = new JSONObject();
        map.keySet().stream().sorted().forEach(k -> sorted.put(k, map.get(k)));
        return sorted;
    }

    /// PayOut

    private static String nullSafe(String s) {
        return (s == null || "null".equals(s) || "undefined".equals(s)) ? "" : s;
    }

    private static String urlEncRFC3986(String s) {
        if (s == null) return "";
        String e = java.net.URLEncoder.encode(s, java.nio.charset.StandardCharsets.UTF_8);
        // Chuyển từ application/x-www-form-urlencoded sang RFC-3986
        e = e.replace("+", "%20").replace("%7E", "~").replace("*", "%2A");
        return e;
    }

    /** Ký theo quy ước 5 field: amount, description, referenceId, toAccountNumber, toBin.
     *  encodeValues=false => RAW (không encode)
     *  encodeValues=true  => RFC-3986 (space=%20, v.v.)
     */
    public static String canonical5(long amount, String description, String referenceId,
                                    String toAccountNumber, String toBin, boolean encodeValues) {
        String a   = String.valueOf(amount);
        String d   = description == null ? "" : description;
        String r   = referenceId == null ? "" : referenceId;
        String acc = toAccountNumber == null ? "" : toAccountNumber;
        String bin = toBin == null ? "" : toBin;

        if (encodeValues) {
            a   = urlEncRFC3986(a);
            d   = urlEncRFC3986(d);
            r   = urlEncRFC3986(r);
            acc = urlEncRFC3986(acc);
            bin = urlEncRFC3986(bin);
        }
        return "amount=" + a
                + "&description=" + d
                + "&referenceId=" + r
                + "&toAccountNumber=" + acc
                + "&toBin=" + bin;
    }

}