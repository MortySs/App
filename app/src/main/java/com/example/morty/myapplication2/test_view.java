package com.example.morty.myapplication2;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class test_view extends AppCompatActivity {
private Button a_1,a_2,a_3,a_4;
private ArrayList cor_a = new ArrayList();
private FirebaseAuth mAuth;
private long id_q;
private long q_count,i;
private TextView text;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final DocumentReference tests = db.collection("tests").document("FirstTest");
    final  CollectionReference test_a = db.collection("tests").document("FirstTest").collection("answers");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_view);
        i=1;
        a_1 = (Button)findViewById(R.id.a_button);
        a_2 = (Button)findViewById(R.id.a_button2);
        a_3 = (Button)findViewById(R.id.a_button3);
        a_4 = (Button)findViewById(R.id.a_button4);
        text = (TextView)findViewById(R.id.q_text);

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser cus = mAuth.getCurrentUser();

        Upd();

    a_1.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
         if(i==q_count){
             Toast.makeText(test_view.this, "Все", Toast.LENGTH_SHORT).show();
         }else{
            i++;
             Upd();
         }
        }
    });

    a_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(i==q_count){
                    Toast.makeText(test_view.this, "Все", Toast.LENGTH_SHORT).show();
                }else{
                    i++;
                    Upd();
                }
            }
        });

    a_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(i==q_count){
                    Toast.makeText(test_view.this, "Все", Toast.LENGTH_SHORT).show();
                }else{
                    i++;
                  Upd();
                }
            }
        });

    a_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(i==q_count){
                    Toast.makeText(test_view.this, "Все", Toast.LENGTH_SHORT).show();
                }else{
                    i++;
                    Upd();
                }
            }
        });


    }

    private void Upd(){
        tests.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    q_count=(long) document.get("q_count");
                    text.setText(document.get(""+i).toString());
                    Log.d("LOL", "DocumentSnapshot data: " + document.getData());
                } else {
                    Log.d("LOL", "No such document");
                }
            } else {
                Log.d("LOL", "get failed with ", task.getException());
            }
        }
    });

        DocumentReference a = test_a.document(""+i);
        a.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        a_1.setText(document.get("1").toString());
                        a_2.setText(document.get("2").toString());
                        a_3.setText(document.get("3").toString());
                        a_4.setText(document.get("4").toString());
                        Log.d("LOL", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("LOL", "No such document");
                    }
                } else {
                    Log.d("LOL", "get failed with ", task.getException());
                }
            }
        });
    }
}
