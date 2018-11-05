package com.exceedgulf.alainzoo.database.converters;

import android.arch.persistence.room.TypeConverter;

import com.exceedgulf.alainzoo.database.models.Vote;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by ehab.alagoza on 12/27/2017.
 */

public class VoteStringArrayConverter {
    @TypeConverter
    public static ArrayList<Vote> fromString(String value) {
        Type listType = new TypeToken<ArrayList<Vote>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<Vote> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

}
