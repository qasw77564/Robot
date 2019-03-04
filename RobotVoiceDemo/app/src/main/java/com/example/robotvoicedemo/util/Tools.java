package com.example.robotvoicedemo.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.KeyEvent;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by evan on 2018/1/5.
 */

public class Tools {
//    public static final String TAG = Tools.class.getSimpleName();
    public static JsonParse JSONPARSE = new JsonParse();
    public static Format FORMAT = new Format();
    public static Network NETWORK = new Network();
    public static Longitude LONGITUDE = new Longitude();
//    public static Bitmaps BITMAPS = new Bitmaps();

    public static class Format {
        private static DecimalFormat decimal = new DecimalFormat();
        private static SimpleDateFormat simpleDate = new SimpleDateFormat();

        public static String decimal(String pattern, Number value) {
            if (Doubles.tryParse(value.toString()) != null) {
                decimal.applyPattern(pattern);
                return decimal.format(value);
            }
            return "";
        }

        public static String date(Locale locale, String template, Long time) {
            simpleDate = new SimpleDateFormat(template, locale);
            return simpleDate.format(time);
        }

        public static String format(String pattern, String formatPattern, String format) {
            simpleDate = new SimpleDateFormat(pattern);
            simpleDate.setTimeZone(TimeZone.getTimeZone("GMT"));
            try {
                SimpleDateFormat f = new SimpleDateFormat(formatPattern);
                f.setTimeZone(TimeZone.getTimeZone("GMT"));
                return f.format(simpleDate.parse(format));
            } catch (ParseException e) {
                return simpleDate.format(format);
            }
        }

        public static String formatDate(Date date) {
            simpleDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSS'Z'");
//            simpleDate.setTimeZone(TimeZone.getTimeZone("GMT"));
            return simpleDate.format(date);
        }

        public static String formatStartDate(Date date, String value) {
            simpleDate = new SimpleDateFormat("yyyy-MM-dd");
            return simpleDate.format(date) + value;
        }
    }

    public static class JsonParse {
        private static Gson GSON = new Gson();

        public final static String toJson(Object obj) {
            return GSON.toJson(obj);
        }

        public final static <T> T fromJson(String json, Class<T> classOfT) {
            GSON.fromJson(json, (Type) classOfT);
            return GSON.fromJson(json, (Type) classOfT);
        }

        public final static <T> List<T> fromJsonList(String json, Class<T[]> types) {
            if (Strings.isNullOrEmpty(json)) {
                return Lists.<T>newArrayList();
            }
            final T[] jsonToObject = GSON.fromJson(json, types);
            return Lists.newArrayList(jsonToObject);
        }

        public static String getJson(Context context, String fileName) {
            StringBuilder stringBuilder = new StringBuilder();
            try {
                AssetManager assetManager = context.getAssets();
                BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
                String line;
                while ((line = bf.readLine()) != null) {
                    stringBuilder.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

//        public static ArrayList<IdentityJsonBean> parseData(String result) {
//            ArrayList<IdentityJsonBean> detail = new ArrayList<>();
//            try {
//                JSONArray data = new JSONArray(result);
//                for (int i = 0; i < data.length(); i++) {
//                    IdentityJsonBean entity = GSON.fromJson(data.optJSONObject(i).toString(), IdentityJsonBean.class);
//                    detail.add(entity);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return detail;
//        }
    }

    public static class Network {
        public static boolean hasNetWork(ConnectivityManager cm) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnected()) {
                int networkType = activeNetwork.getType();
                return networkType == ConnectivityManager.TYPE_WIFI || networkType == ConnectivityManager.TYPE_MOBILE;
            } else {
                return false;
            }
        }
    }

    public static class Longitude {
        @SuppressLint("MissingPermission")
        public static Location get(Context context) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (loc == null) {
//                Log.d(TAG, "GPS fail");
                loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            if (loc == null) {
//                Log.d(TAG, "NETWORK fail");
            }
            return loc;
        }
    }

//    public static class GoogleVersion {
//        public static boolean checkVersion(Context context, Activity activity) {
//            GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
//            int result = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
//            if (googleAPI.isUserResolvableError(result)) {
//                Dialog dialog = googleAPI.getErrorDialog(activity, result, 9000);
//                dialog.setCanceledOnTouchOutside(false);
//                dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
//                    @Override
//                    public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
//                        return i == KeyEvent.KEYCODE_BACK;
//                    }
//                });
//                dialog.show();
//            }
//            return false;
//        }
    }