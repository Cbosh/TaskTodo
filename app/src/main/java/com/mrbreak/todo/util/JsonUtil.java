package com.mrbreak.todo.util;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.mrbreak.todo.model.CategoryModel;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JsonUtil {
    private static Gson gson = new Gson();

    public static String convertCategoriestoString(List<CategoryModel> categories) {
        Gson gson = new Gson();
        return gson.toJson(
                categories,
                new TypeToken<ArrayList<CategoryModel>>() {
                }.getType());
    }

    public static List<CategoryModel> convertCategoriesString(String categoriesStr) {
        return Collections.singletonList(gson.fromJson(categoriesStr, CategoryModel.class));
    }

    public static List<CategoryModel> jsonStringToArray(String jsonString) throws JSONException {

        Gson gson = new Gson();
        List<CategoryModel> categories = new ArrayList<>();

        JSONArray jsonArray = new JSONArray(jsonString);

        for (int i = 0; i < jsonArray.length(); i++) {
            categories.add(gson.fromJson(jsonArray.getJSONObject(i).toString(), CategoryModel.class));
        }

        return categories;
    }

    public static String loadCategoriesJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("categories.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static List<CategoryModel> loadCategories(Context context) throws IOException {
        List<CategoryModel> categories = new ArrayList<>();
        try {
            String jsonFileStr = loadCategoriesJSONFromAsset(context);

            JsonObject jsonObject = gson.fromJson(jsonFileStr, JsonObject.class);
            jsonObject.get("categories").getAsJsonArray();
            categories = gson.fromJson(jsonObject.get("categories").getAsJsonArray(), new TypeToken<ArrayList<CategoryModel>>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return categories;
    }
}
