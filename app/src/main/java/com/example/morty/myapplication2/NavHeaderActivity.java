package com.example.morty.myapplication2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class NavHeaderActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextView mTextMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_header_main);
        mTextMessage = (TextView) findViewById(R.id.cuz_email);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser cuz = mAuth.getCurrentUser();

        if (cuz!=null) {
            mTextMessage.setText(cuz.getEmail());
        }
    }
}
