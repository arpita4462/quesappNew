package com.atrio.quesapp.StoreData;

import android.content.Context;
import android.content.SharedPreferences;

import com.atrio.quesapp.model.ListData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Admin on 10-07-2017.
 */

public class SharedPreference {
    public static final String PREFS_NAME = "NKDROID_APP";
    public static final String FAVORITES = "Favorite";
    public SharedPreference() {
        super();
    }
    public void storeFavorites(Context context, List favorites) {
// used for store arrayList in json format
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        editor = settings.edit();
       /* Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);*/
        //editor.putString(FAVORITES, jsonFavorites);
        editor.commit();
    }
    public ArrayList loadFavorites(Context context) {
// used for retrieving arraylist from json formatted string
        SharedPreferences settings;
        List favorites;
        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        if (settings.contains(FAVORITES)) {
            String jsonFavorites = settings.getString(FAVORITES, null);
           // Gson gson = new Gson();
            //ListData[] favoriteItems = gson.fromJson(jsonFavorites,ListData[].class);
            favorites = Arrays.asList(0);
            favorites = new ArrayList(favorites);
        } else
            return null;
        return (ArrayList) favorites;
    }
    public void addFavorite(Context context, ListData beanSampleList) {
        List favorites = loadFavorites(context);
        if (favorites == null)
            favorites = new ArrayList();
        favorites.add(beanSampleList);
        storeFavorites(context, favorites);
    }
    public void removeFavorite(Context context, ListData beanSampleList) {
        ArrayList favorites = loadFavorites(context);
        if (favorites != null) {
            favorites.remove(beanSampleList);
            storeFavorites(context, favorites);
        }
    }
}
