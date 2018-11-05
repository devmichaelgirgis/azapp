package com.exceedgulf.alainzoo.database.converters;

import android.arch.persistence.room.TypeConverter;

import com.exceedgulf.alainzoo.database.models.Features;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by P.P
 */

public class FeaturesArrayConverter {

    @TypeConverter
    public static ArrayList<Features> fromString(String value) {
        Type listType = new TypeToken<ArrayList<Features>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<Features> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}
