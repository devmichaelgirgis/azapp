package com.exceedgulf.alainzoo.database.converters;

import android.arch.persistence.room.TypeConverter;

import com.exceedgulf.alainzoo.database.models.AnimalImages;
import com.exceedgulf.alainzoo.database.models.OpeningHoursChild;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by P.G on 01/08/2018.
 */

public class OpeningHoursStringArrayConverter {
    @TypeConverter
    public static ArrayList<OpeningHoursChild> fromString(String value) {
        Type listType = new TypeToken<ArrayList<OpeningHoursChild>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<OpeningHoursChild> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

}
