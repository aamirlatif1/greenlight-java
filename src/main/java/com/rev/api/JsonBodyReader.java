package com.rev.api;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.Strictness;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import spark.Request;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.RecordComponent;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reads and validates a single JSON object from a request body, mirroring the
 * error taxonomy of Alex Edwards' "Let's Go Further" readJSON helper: malformed
 * syntax, wrong top-level type, unknown fields, wrong field types, trailing
 * content, empty bodies and oversized bodies are all reported distinctly.
 */
public final class JsonBodyReader {

    private static final long MAX_BODY_BYTES = 1_048_576; // 1MB
    private static final Pattern LINE_COLUMN = Pattern.compile("at line (\\d+) column (\\d+)");
    private static final Gson gson = new Gson();

    private JsonBodyReader() {}

    public static <T> T read(Request request, Class<T> type) {
        byte[] bytes = request.bodyAsBytes();
        if (bytes == null || bytes.length == 0) {
            throw new BadRequestException("body must not be empty");
        }
        if (bytes.length > MAX_BODY_BYTES) {
            throw new BadRequestException("body must not be larger than " + MAX_BODY_BYTES + " bytes");
        }

        String body = new String(bytes, StandardCharsets.UTF_8);
        if (body.isBlank()) {
            throw new BadRequestException("body must not be empty");
        }

        JsonReader reader = new JsonReader(new StringReader(body));
        reader.setStrictness(Strictness.STRICT);

        JsonElement root;
        try {
            root = JsonParser.parseReader(reader);
        } catch (JsonSyntaxException e) {
            throw new BadRequestException("body contains badly-formed JSON" + positionSuffix(body, e));
        }

        if (!root.isJsonObject()) {
            throw new BadRequestException("body contains incorrect JSON type (at character 1)");
        }

        try {
            if (reader.peek() != JsonToken.END_DOCUMENT) {
                throw new BadRequestException("body must only contain a single JSON value");
            }
        } catch (IOException e) {
            throw new BadRequestException("body must only contain a single JSON value");
        }

        JsonObject object = root.getAsJsonObject();
        validateFields(object, type);

        return gson.fromJson(object, type);
    }

    private static void validateFields(JsonObject object, Class<?> type) {
        RecordComponent[] components = type.getRecordComponents();

        for (String key : object.keySet()) {
            RecordComponent match = null;
            for (RecordComponent component : components) {
                if (component.getName().equals(key)) {
                    match = component;
                    break;
                }
            }
            if (match == null) {
                throw new BadRequestException("body contains unknown key \"" + key + "\"");
            }
            if (!typeMatches(match.getType(), object.get(key))) {
                throw new BadRequestException("body contains incorrect JSON type for \"" + key + "\"");
            }
        }
    }

    private static boolean typeMatches(Class<?> javaType, JsonElement element) {
        if (element.isJsonNull()) {
            return !javaType.isPrimitive();
        }
        if (javaType == String.class) {
            return element.isJsonPrimitive() && element.getAsJsonPrimitive().isString();
        }
        if (javaType == int.class || javaType == Integer.class
                || javaType == long.class || javaType == Long.class
                || javaType == double.class || javaType == Double.class) {
            return element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber();
        }
        if (javaType == boolean.class || javaType == Boolean.class) {
            return element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean();
        }
        if (List.class.isAssignableFrom(javaType)) {
            return element.isJsonArray();
        }
        return true;
    }

    private static String positionSuffix(String body, JsonSyntaxException e) {
        Throwable cause = e.getCause();
        String message = cause != null ? cause.getMessage() : e.getMessage();
        if (message == null) {
            return "";
        }
        Matcher matcher = LINE_COLUMN.matcher(message);
        if (!matcher.find()) {
            return "";
        }
        int line = Integer.parseInt(matcher.group(1));
        int column = Integer.parseInt(matcher.group(2));

        int lineStart = 0;
        for (int currentLine = 1; currentLine < line; currentLine++) {
            int newline = body.indexOf('\n', lineStart);
            if (newline < 0) {
                break;
            }
            lineStart = newline + 1;
        }
        return " (at character " + (lineStart + column) + ")";
    }
}
