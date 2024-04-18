package com.example.tugasakhir;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class ButtonViewDetailActivity extends AppCompatActivity {
    ListView listViewScannedResultsBag;
    CustomArrayAdapter listAdapter;
    ArrayList<BagsAdaptedArrayList> bagsAdaptedList = new ArrayList<>();
    ButtonViewDetailActivity ref = this;
    TugasAkhirContext taCtx;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_view_detail);
        listViewScannedResultsBag = findViewById(R.id.listViewScannedResultsBag);
        taCtx = (TugasAkhirContext)getApplicationContext();
    }

    @Override
    protected void onResume() {
        super.onResume();
        buildBagList();
    }

    private void buildBagList() {
        bagsAdaptedList = new ArrayList<>();

        HashMap<String, ArrayList<String>> bagsHolder = taCtx.getBagDataStore().getBagsHolder();
        for (String bagName : bagsHolder.keySet()) {
            ArrayList<String> connoteList = bagsHolder.get(bagName);
            bagsAdaptedList.add(new BagsAdaptedArrayList(bagName, connoteList.size()));
        }

        boolean isScannedSomeConnote = !bagsHolder.isEmpty();
//        Toast.makeText(this, "[SCN DETAIL] " + taCtx.getBagDataStore().getTotalConnoteCount(), Toast.LENGTH_SHORT).show();

        // Memastikan hasil scan tidak null dan tidak kosong
        if (isScannedSomeConnote) {
            // Menggunakan ArrayAdapter untuk menampilkan data dalam ListView
//            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, scannedResults);
            CustomArrayAdapter adapter = new CustomArrayAdapter(getApplicationContext(), bagsAdaptedList, ref);
            listAdapter = adapter;
            listViewScannedResultsBag.setAdapter(adapter);

            // Menambahkan onClickListener pada item ListView
            listViewScannedResultsBag.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                    // TODO Auto-generated method stub
                    if (listViewScannedResultsBag.isItemChecked(pos)){
                        listViewScannedResultsBag.setItemChecked(pos,false);
                    }else{
                        listViewScannedResultsBag.setItemChecked(pos,true);
                    }
                    //lvMain.setSelection();

                    return true;
                }
            });

            listViewScannedResultsBag.setOnItemClickListener((parent, view, position, id) -> {
//                String selectedItem = scannedResults.get(position);
//                String selectedItem = scannedResults.get(position);
                // Pindah ke DetailConnoteActivity dengan membawa data terpilih (contoh: nomor AWB)
//                navigateToDetailConnoteActivity(selectedItem);
                BagsAdaptedArrayList selectedBag = bagsAdaptedList.get(position);
                ArrayList<String> connoteContents = bagsHolder.get(selectedBag.getBagId());
                navigateToDetailConnoteActivity(selectedBag.getBagId(), connoteContents);
            });
        } else {
            Toast.makeText(this, "No scanned data available.", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteBagHook(String bagId, Integer arrayIndex) {
        Toast.makeText(getApplicationContext(), "[Direct] : " + bagId + " [By Idx] : " + bagsAdaptedList.get(arrayIndex).getBagId(), Toast.LENGTH_SHORT).show();
        taCtx.getBagDataStore().getBagsHolder().remove(bagsAdaptedList.get(arrayIndex).getBagId());
        listAdapter.remove(bagsAdaptedList.get(arrayIndex));
    }

//    private void navigateToDetailConnoteActivity(String selectedItem) {
    private void navigateToDetailConnoteActivity(String nomorBag, ArrayList<String> connoteContents) {
        // Di sini Anda dapat membuat Intent dan memulai DetailConnoteActivity,
        // serta mengirim data terpilih menggunakan Intent (misalnya, nomor AWB).

        Intent intent = new Intent(ButtonViewDetailActivity.this, DetailConnoteActivity.class);
        intent.putExtra("selectedItem", nomorBag);
        intent.putStringArrayListExtra("connoteContents", connoteContents);
        startActivity(intent);

    }
}

