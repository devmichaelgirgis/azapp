package com.exceedgulf.alainzoo.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.R;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class Utils {
    private static final String KEY_SHARED_PREF = "ANDROID_WEB_CHAT";
    private static final int KEY_MODE_PRIVATE = 0;
    private static Utils ourInstance = null;

    public static Utils getInstance() {
        if (ourInstance == null) {
            ourInstance = new Utils();
        }
        return ourInstance;
    }


    public static boolean isValidInternationalMobile(final String mobilenumber) {
        if (!TextUtils.isEmpty(mobilenumber)) {
            if (mobilenumber.startsWith("+971")) {
                return (mobilenumber.substring(4)).length() == 9;
            } else if (mobilenumber.startsWith("0971")) {
                return (mobilenumber.substring(4)).length() == 9;
            } else if (mobilenumber.startsWith("00971")) {
                return (mobilenumber.substring(5)).length() == 9;
            } else if (mobilenumber.startsWith("05")) {
                return (mobilenumber.substring(2)).length() == 8;
            }
        }
        return false;
    }

    public static boolean isValidMobile(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    public String getFormatedHtml(final String htmlData) {
        String lang = LangUtils.getCurrentLanguage();
        String dir;
        String textColor = "#565563";
        String fontStyle = "";
        if (!TextUtils.isEmpty(htmlData)) {
            if (LangUtils.getCurrentLanguage().equalsIgnoreCase("ar")) {
                dir = "rtl";
                fontStyle = "<style type=\"text/css\">\n" +
                        "@font-face {\n" +
                        "    font-family: MyFont;\n" +
                        "    src: url(\"file:///android_asset/fonts/avenir_next_regular_ar.ttf\")\n" +
                        "}\n" +
                        "body {\n" +
                        "    font-family: MyFont;\n" +
                        "    font-size: 15px;\n" +
                        " color:" + textColor + ";\n" +
                        "    background-color:#ffffff;\n" +
                        "}\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "    </style>";
            } else {
                dir = "ltr";
                fontStyle = "<style type=\"text/css\">\n" +
                        "@font-face {\n" +
                        "    font-family: MyFont;\n" +
                        "    src: url(\"file:///android_asset/fonts/avenir_next_regular_en.ttf\")\n" +
                        "}\n" +
                        "body {\n" +
                        "    font-family: MyFont;\n" +
                        "    font-size: 15px;\n" +
                        " color:" + textColor + ";\n" +
                        "    background-color:#ffffff;\n" +
                        "}\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "    </style>";
            }
            return "<!DOCTYPE html>\n" +
                    "<html LANG=\"" + lang + "\">\n" +
                    "<head>\n" + fontStyle +
                    "    \n<meta charset=\"UTF-8\">\n" +
                    "</head>\n" +
                    "<body dir=\"" + dir + "\">\n" +
                    "\n" +
                    htmlData +
                    "\n" +
                    "</body>\n" +
                    "</html>";
        } else {
            return "";
        }

    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return model.toUpperCase();
        } else {
            return manufacturer.toUpperCase() + " " + model;
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }


    public static String formatDateTime(String oldDate, String oldFormat, String newFormat) {
        try {
            DateFormat inputFormat = new SimpleDateFormat(oldFormat, Locale.ENGLISH);
            //DateFormat outputFormat = new SimpleDateFormat(newFormat, Locale.getDefault());
            DateFormat outputFormat = new SimpleDateFormat(newFormat, Locale.ENGLISH);
            Date date = inputFormat.parse(oldDate);
            return outputFormat.format(date);
        } catch (Exception e) {
            return oldDate;
        }
    }

    public File createFile() {
        final File appDirectory = new File(Environment.getExternalStorageDirectory(), AppAlainzoo.getAppAlainzoo().getString(R.string.application_name));
        if (!appDirectory.exists()) {
            if (!appDirectory.mkdirs()) {
                appDirectory.mkdir();
            }
        }
        final File imageFile = new File(appDirectory.toString(), "tmp_" + System.currentTimeMillis() + ".png");
        return imageFile;
    }

    /**
     * Checks if phone is connected to network or not.
     *
     * @param mContext required for creating AlertDialog and checking phone state.
     * @return true if phone is connected to internet otherwise false.
     */
    public boolean isNetworkAvailable(final Context mContext) {

        boolean isNetAvailable = false;
        if (mContext != null) {
            final ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (mConnectivityManager != null) {
                final NetworkInfo activeNetwork = mConnectivityManager.getActiveNetworkInfo();
                isNetAvailable = activeNetwork != null && activeNetwork.isConnected();
            }
        }
        return isNetAvailable;
    }


    /**
     * Hides keyboard from screen if it is showing
     *
     * @param mActivity requires for checking keyboard is open or not
     */
    public void hideSoftKeyboard(Context mActivity) {
        if (mActivity != null && !((AppCompatActivity) mActivity).isFinishing()) {
            final InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
            if (inputMethodManager.isActive()) {
                if (((AppCompatActivity) mActivity).getCurrentFocus() != null) {
                    inputMethodManager.hideSoftInputFromWindow(((AppCompatActivity) mActivity).getCurrentFocus().getWindowToken(), 0);
                }
            }
        }
    }


}
