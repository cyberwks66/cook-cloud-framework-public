package com.cooksys.cloud.sdk.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Misc HTTP utility methods
 *
 * @author Tim Davidson
 */
public class HttpUtil {
    /**
     * Parses the error text from the standard Spring JSON error response
     *
     * @param responseBody
     * @return error text as String
     */
    public static String getErrorText(String responseBody) {
        final JsonParser parser = new JsonParser();
        final JsonObject obj = parser.parse(responseBody).getAsJsonObject();
        if (obj == null) {
            return null;
        }

        final JsonElement error = obj.get("error");
        if (error == null) {
            return null;
        }
        return error.getAsString();
    }

    /**
     * Parses the message text from the standard Spring JSON error response
     *
     * @param responseBody
     * @return message text as String
     */
    public static String getMessageText(String responseBody) {
        final JsonParser parser = new JsonParser();
        final JsonObject obj = parser.parse(responseBody).getAsJsonObject();
        if (obj == null) {
            return null;
        }

        final JsonElement error = obj.get("message");
        if (error == null) {
            return null;
        }
        return error.getAsString();
    }
}
