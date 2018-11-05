package com.exceedgulf.alainzoo.database.converters;

import android.arch.persistence.room.TypeConverter;

import com.exceedgulf.alainzoo.models.VisitAnimalModel;
import com.exceedgulf.alainzoo.models.VisitWhatsNewModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by ehab.alagoza on 12/27/2017.
 */

public class VisitWhatsNewModelStringArrayConverter {
    @TypeConverter
    public static ArrayList<VisitWhatsNewModel> fromString(String value) {
        Type listType = new TypeToken<ArrayList<VisitWhatsNewModel>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<VisitWhatsNewModel> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

}
