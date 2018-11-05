package com.exceedgulf.alainzoo.database.converters;

import android.arch.persistence.room.TypeConverter;

import com.exceedgulf.alainzoo.database.models.AnimalImages;
import com.exceedgulf.alainzoo.database.models.WhatsNew;
import com.exceedgulf.alainzoo.database.models.WhatsNewDate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by P.P on 30/01/2017.
 */

public class DateStringArrayConverter {
    @TypeConverter
    public static ArrayList<WhatsNewDate> fromString(String value) {
        Type listType = new TypeToken<ArrayList<WhatsNewDate>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<WhatsNewDate> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }


}
