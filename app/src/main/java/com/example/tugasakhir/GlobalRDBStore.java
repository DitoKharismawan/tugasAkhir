package com.example.tugasakhir;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.tugasakhir.models.PanggilBalik;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class GlobalRDBStore {
    private GlobalConstant gConstant = new GlobalConstant();
    private GlobalUtils gUtils = new GlobalUtils();
    public Task<Void> setAwbCount(Integer value, PanggilBalik dataListener) {
        return fireGetCfgRef().push().setValue(value);
    }


    public void getAwbCount(PanggilBalik<Integer> dataListener) {
        fireGetCfgRef().child("awbCount").addListenerForSingleValueEvent(
            new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Data dapat berupa single value atau Map jika memiliki child
                        Object o = dataSnapshot.getValue();
                        if (o instanceof Integer) {
                            dataListener.kepanggil((Integer) o);
                        }
                        else {

                        }

                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Log error atau tampilkan pesan error
                    Log.e("DetailConnoteActivity", "Error loading data", databaseError.toException());
                }
            }

        );
    }

    public void getData(PanggilBalik<String> dataListener) {
        fireGetRef().addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Data dapat berupa single value atau Map jika memiliki child
                            Object o = dataSnapshot.getValue();
                            dataListener.kepanggil(o.toString());
//                            if (o instanceof Integer) {
//                                dataListener.kepanggil((Integer) o);
//                            }
//                            else {
//
//                            }
                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        dataListener.kepanggil("[Error] " + databaseError.toException().getMessage());
                        // Log error atau tampilkan pesan error
                        Log.e("DetailConnoteActivity", "Error loading data", databaseError.toException());
                    }
                }

        );
    }

    public void exploreData(PanggilBalik<ArrayList<String>> dataListener) {
//        fireGetRef().addChildEventListener();
        fireGetRef().addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Data dapat berupa single value atau Map jika memiliki child
                            Object o = dataSnapshot.getValue();
                            ArrayList<String> list = new ArrayList<String>();
                            if (o instanceof Map) {
                                for (Object obj : ((Map<?, ?>) o).keySet().toArray()) {
                                    if (obj instanceof String) {
                                        list.add((String) obj);
                                    }
                                }
                            }
                            dataListener.kepanggil(list);
//                            if (o instanceof Integer) {
//                                dataListener.kepanggil((Integer) o);
//                            }
//                            else {
//
//                            }
                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Log error atau tampilkan pesan error
                        Log.e("DetailConnoteActivity", "Error loading data", databaseError.toException());
                    }
                }

        );
    }

    public void exploreData2(ArrayList<String> path, PanggilBalik<ArrayList<String>> dataListener) {
//        "https://tugasakhir-30385-default-rtdb.firebaseio.com/.json?shallow=true"
        final String[] pathStr = {""};
        path.forEach(pathElm -> {
            pathStr[0] = pathStr[0] + "/" + pathElm;
        });
        String urlStr = gConstant.FIREBASE_BASE_URL + "/" + pathStr[0] + ".json?shallow=true";
        gUtils.httpGetRequest(urlStr, new PanggilBalik<String>() {
            @Override
            public void kepanggil(String a) {
                ArrayList<String> listKey = new ArrayList<>();
                Log.d("App", "[RDBStore] Parsing JSON...");
//                JSONObject object = new JSONObject(response[0]);
                try {
                    JSONObject obj = new JSONObject(a);
                    Iterator<String> objKeys = obj.keys();
                    while (objKeys.hasNext()) {
                        listKey.add(objKeys.next());
                    }
                    dataListener.kepanggil(listKey);
                }
                catch (JSONException e) {
                    Log.d("App", "[Util] Failed to Parse JSON, maybe its primitive?");
                    listKey.add(path.get(path.size() - 1) + " : " + a);
                    dataListener.kepanggil(listKey);
                }
            }
        });
    }

//    https://tugasakhir-30385-default-rtdb.firebaseio.com/.json?shallow=true


    private DatabaseReference fireGetCfgRef() {
        return fireGetRef().child(gConstant.FIREBASE_CONFIG_KEY);
    }

    private DatabaseReference fireGetRef() {
        return FirebaseDatabase.getInstance().getReference();
    }
}
