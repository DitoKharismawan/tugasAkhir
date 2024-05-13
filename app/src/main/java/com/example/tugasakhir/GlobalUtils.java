package com.example.tugasakhir;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.tugasakhir.models.ArrayHashModel;
import com.example.tugasakhir.models.PanggilBalik;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GlobalUtils {
    public  <T> ArrayList<ArrayHashModel<T>> hashMapToArray(HashMap<String, T> hashMap) {
        ArrayList<ArrayHashModel<T>> arrayList = new ArrayList<>();
        for (Map.Entry<String, T> entry : hashMap.entrySet()) {
            ArrayHashModel<T> genericClass = new ArrayHashModel<>();
            genericClass.key = entry.getKey();
            genericClass.data = entry.getValue();
            arrayList.add(genericClass);
        }
        return arrayList;
    }


    public void httpGetRequest(String url, PanggilBalik<String> dataListener) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        final String[] response = new String[1];

        executor.execute(
                new Runnable() {
                    @Override
                    public void run() {
                        {
                            // Background work here

                            try {
                                HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
//                HttpURLConnection con = (HttpURLConnection) new URL("https://ip-api.com/json/").openConnection();
                                response[0] = convertStreamToString(con.getInputStream());
                                Log.d("App", "[TestHTTP] OK " + url);
                                handler.post(() -> {
                                    // UI Thread work here
                                    // return locationItems;
                                    dataListener.kepanggil( response[0] );
                                });
//                JSONObject object = new JSONObject(response[0]);
//                dataListener.kepanggil("[Data] " + response[0]);
                            } catch (Exception e) {
                                Log.d("App", "[TestHTTP] Gagal " + e.getMessage());
//                dataListener.kepanggil("[Error] " + e.getMessage());
                            }
                        }
                    }
                }
            );
    }

    public static String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }
}
