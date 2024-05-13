package com.example.tugasakhir;

import android.content.Intent;
import android.os.Bundle;

import com.example.tugasakhir.models.PanggilBalik;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tugasakhir.databinding.ActivityRdbdebuggerBinding;

import java.util.ArrayList;

public class RDBDebugger extends AppCompatActivity {

    private ActivityRdbdebuggerBinding binding;
    private ListView lView;
    ArrayList<String> detailList;
    ArrayAdapter adapter;
    TugasAkhirContext taCtx;
    GlobalRDBStore rdbCtx;
    TextView outputTextElm;

    RDBDebugger activityRef = this;
    boolean muted = false;

    @Override
    protected void onResume() {
        super.onResume();
        muted = false;
        // bungkus timer keknya
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TextView pathDisplayElm = findViewById(R.id.rdbdebugger_path_view);
                pathDisplayElm.setText(taCtx.getGlobalData().rdbCurrentPath.toString());
            }
        }, 1000);
    }

    @Override
    protected  void onStop() {
        super.onStop();
        if (!taCtx.getGlobalData().rdbCurrentPath.isEmpty() && !muted) {
            taCtx.getGlobalData().rdbCurrentPath.remove(taCtx.getGlobalData().rdbCurrentPath.size() - 1);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRdbdebuggerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle(getTitle());

        FloatingActionButton fab = binding.fab;

        taCtx = (TugasAkhirContext)getApplicationContext();
        rdbCtx = taCtx.getRDBStore();
        outputTextElm = findViewById(R.id.rdbdebugger_path_view);
        lView = findViewById(R.id.listViewRDBDebugger);
        loadData();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadData();
//                taCtx.getUtils().httpGetRequest("https://tugasakhir-30385-default-rtdb.firebaseio.com/.json?shallow=true",new PanggilBalik<String>() {
//                    @Override
//                    public void kepanggil(String a) {
                        Snackbar.make(view, "Reloading...", Snackbar.LENGTH_LONG)
                                .setAction("Action", null)
                                .setAnchorView(R.id.fab).show();
//                        outputTextElm.setText(a);
//                    }
//                });
//                rdbCtx.getData(new PanggilBalik<String>() {
//                    @Override
//                    public void kepanggil(String a) {
//                        Snackbar.make(view, a, Snackbar.LENGTH_LONG)
//                                .setAction("Action", null)
//                                .setAnchorView(R.id.fab).show();
//                        outputTextElm.setText(a);
//                    }
//                });
//                rdbCtx.exploreData(new PanggilBalik<ArrayList<String>>() {
//                    @Override
//                    public void kepanggil(ArrayList<String> a) {
//                        outputTextElm.setText(a.toString());
//                    }
//                });
            }
        });
    }

    public void loadData() {
        TextView pathDisplayElm = findViewById(R.id.rdbdebugger_path_view);
        pathDisplayElm.setText(taCtx.getGlobalData().rdbCurrentPath.toString());
        taCtx.getRDBStore().exploreData2(taCtx.getGlobalData().rdbCurrentPath, new PanggilBalik<ArrayList<String>>() {
            @Override
            public void kepanggil(ArrayList<String> a) {
//                        Snackbar.make(view, a.toString(), Snackbar.LENGTH_LONG)
//                                .setAction("Action", null)
//                                .setAnchorView(R.id.fab).show();
//                        outputTextElm.setText(a.toString());
//                        detailList = new ArrayList<>();
                detailList = a;
                adapter = new ArrayAdapter<>(activityRef, android.R.layout.simple_list_item_1, detailList);
                lView.setAdapter(adapter);
                lView.setOnItemClickListener((parent, view, position, id) -> {
                    muted = true;
                    taCtx.getGlobalData().rdbCurrentPath.add(detailList.get(position));
                    Intent rdbActivityIntent = new Intent(getApplicationContext(), RDBDebugger.class);
                    startActivity(rdbActivityIntent);
                });
            }
        });
    }
}