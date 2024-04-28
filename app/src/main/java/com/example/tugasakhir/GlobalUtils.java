package com.example.tugasakhir;

import com.example.tugasakhir.models.ArrayHashModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
}
