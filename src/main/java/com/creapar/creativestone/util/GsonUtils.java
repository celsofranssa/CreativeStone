package com.creapar.creativestone.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by Celso on 20/04/2016.
 */
public class GsonUtils {

    private static final GsonBuilder gsonBuilder = new GsonBuilder()
            .setPrettyPrinting();

    public static void registerType(
            RuntimeTypeAdapterFactory<?> adapter) {
        gsonBuilder.registerTypeAdapterFactory(adapter);
    }

    /**
     * Gson factory
     *
     * @return Gson object
     */
    public static Gson getGson() {
        return gsonBuilder.create();
    }
}
