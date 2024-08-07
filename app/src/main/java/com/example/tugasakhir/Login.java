package com.example.tugasakhir;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Login extends AppCompatActivity {
    private Button btnLogin;
    private ImageView imageView2;
    private EditText etUsername, etPassword;
    private DatabaseReference database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnLogin = findViewById(R.id.btnLogin);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        imageView2 = findViewById(R.id.imageView2);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                database = FirebaseDatabase.getInstance().getReference("users");
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Username Atau Password Salah", Toast.LENGTH_SHORT).show();
                } else {
                    database.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.child(username).exists()) {
                                if (snapshot.child(username).child("password").getValue(String.class).equals(password)){
                                    Toast.makeText(getApplicationContext(), "Login Berhasil", Toast.LENGTH_SHORT).show();
                                    String userId = snapshot.child(username).getKey();
                                    String userOrigin = snapshot.child(username).child("origin").getValue(String.class);
                                    String userFCode = snapshot.child(username).child("fCode").getValue(String.class);
                                    String userNama = snapshot.child(username).child("nama").getValue(String.class);
                                    TugasAkhirContext app = (TugasAkhirContext) getApplicationContext();
                                    app.setUserId(userId);
                                    app.setUsername(username);
                                    app.setOrigin(userOrigin);
                                    app.setNama(userNama);
                                    app.setFCode(userFCode);
                                    Intent masuk = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(masuk);
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Data belum terdaftar", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }
        });
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent rdbActivityIntent = new Intent(getApplicationContext(), RDBDebugger.class);
                startActivity(rdbActivityIntent);
            }
        });
    }
}