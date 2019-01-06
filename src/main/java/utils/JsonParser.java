package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JsonParser {
    private static Gson gson ;

    static {
        gson = new GsonBuilder()
                .create();
    }

    public static <T> T parse(String json, Class<T> claz) {
        return gson.fromJson(json, claz);
    }

    public static <T> T parse(Reader reader, Class<T> claz) {
        return gson.fromJson(reader, claz);
    }

    public static <T> List<T> parseList(String json, Class<T[]> claz) {
        T[] arr = gson.fromJson(json, claz);
        return new ArrayList<>(Arrays.asList(arr));
    }

    public static String toJSONString(Object obj) {
        return gson.toJson(obj);
    }
}
