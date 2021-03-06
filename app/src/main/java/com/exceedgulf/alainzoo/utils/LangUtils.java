package com.exceedgulf.alainzoo.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import com.exceedgulf.alainzoo.preference.PrefCons;
import com.exceedgulf.alainzoo.preference.SharedPrefceHelper;

import java.util.Locale;


public class LangUtils {

    public static Configuration getLocal(Context context) {
        final String lang = SharedPrefceHelper.getInstance().get(PrefCons.APP_LANGUAGE, "en");
        // final String lang = "ar";
        //final Configuration config = new Configuration();
        final Locale locale = new Locale(lang);
        //Locale.setDefault(locale);
        //config.locale = locale;

        final Resources resources = context.getResources();

        final Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return configuration;
    }

    public static String getCurrentLanguage() {
        return SharedPrefceHelper.getInstance().get(PrefCons.APP_LANGUAGE, "en");
    }

//
//    public static boolean changeAppToArabic(Activity activity) {
//        Resources res;
//
//        res = activity.getResources();
//        res.getConfiguration().locale.getDisplayLanguage();
//
//        if (!res.getConfiguration().locale.getDisplayLanguage()
//                .equals("Arabic")) {
//            // Change locale settings in the app.
//            DisplayMetrics dm = res.getDisplayMetrics();
//            android.content.res.Configuration conf = res.getConfiguration();
////			conf.locale = new Locale(activity.getResources().getString(
////					R.string.lang));
//            conf.locale = new Locale(SharedPrefceHelper.getInstance().get(PrefCons.APP_LANGUAGE, activity.getResources().getString(R.string.lang)));
//            ChangeDesignToLTR(activity);
//            res.updateConfiguration(conf, dm);
//            res.getConfiguration().locale.getDisplayLanguage();
//
//        }
//
//        if (res.getConfiguration().locale.getDisplayLanguage().equals("Arabic")) {
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    public static boolean changeAppToArabic(Context context) {
//        Resources res;
//
//        res = context.getResources();
//        res.getConfiguration().locale.getDisplayLanguage();
//
//        if (!res.getConfiguration().locale.getDisplayLanguage().equals("Arabic")) {
//            DisplayMetrics dm = res.getDisplayMetrics();
//            Configuration conf = res.getConfiguration();
//            conf.locale = new Locale(SharedPrefceHelper.getInstance().get(PrefCons.APP_LANGUAGE, "ar"));
//            conf.setLayoutDirection(new Locale("ar"));
//            res.updateConfiguration(conf, dm);
//            res.getConfiguration().locale.getDisplayLanguage();
//
//
//        }
//        if (res.getConfiguration().locale.getDisplayLanguage().equals("Arabic")) {
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    public static boolean changeAppToEnglish(Context context) {
//        Resources res;
//
//        res = context.getResources();
//        res.getConfiguration().locale.getDisplayLanguage();
//
//        if (!res.getConfiguration().locale.getDisplayLanguage().equals("English")) {
//            DisplayMetrics dm = res.getDisplayMetrics();
//            android.content.res.Configuration conf = res.getConfiguration();
//            conf.locale = new Locale(SharedPrefceHelper.getInstance().get(PrefCons.APP_LANGUAGE, "en"));
//            conf.setLayoutDirection(new Locale("en"));
//            res.updateConfiguration(conf, dm);
//            res.getConfiguration().locale.getDisplayLanguage();
//        }
//
//        if (res.getConfiguration().locale.getDisplayLanguage().equals("English")) {
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//
//    public static String getAppLang(Activity activity) {
//        Resources res;
//        res = activity.getResources();
//        return res.getConfiguration().locale.getDisplayLanguage();
//    }
//
//    public static String getAppLang(Context context) {
//        Resources res;
//        res = context.getResources();
//        return res.getConfiguration().locale.getDisplayLanguage();
//    }
//
//    public static String convertEnglishNumbersToArabic(String str) {
//        char[] arabicChars = {'٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩'};
//        StringBuilder builder = new StringBuilder();
//        for (int i = 0; i < str.length(); i++) {
//            if (Character.isDigit(str.charAt(i))) {
//                builder.append(arabicChars[(int) (str.charAt(i)) - 48]);
//            } else {
//                builder.append(str.charAt(i));
//            }
//        }
//
//        return ReverseDate(builder.toString());
//    }
//
//    public static String ReverseDate(String str) {
//        String words[] = str.split("-");
//
//        StringBuilder builder = new StringBuilder();
//        builder.append(words[2]);
//        builder.append("-");
//        builder.append(words[1]);
//        builder.append("-");
//        builder.append(words[0]);
//
//        return builder.toString();
//    }

}
