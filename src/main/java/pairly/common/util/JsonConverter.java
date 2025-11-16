package pairly.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonConverter {
    private static final Gson GSON = new GsonBuilder().create();

    public String toJson(Object object) {
        return GSON.toJson(object);
    }

    public <T> T fromJson(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }
}
