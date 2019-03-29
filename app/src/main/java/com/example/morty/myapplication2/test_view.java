package com.example.morty.myapplication2;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
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
private final ArrayList<String> cor_a = new ArrayList<>();
private FirebaseAuth mAuth;
private long id_q;
private long q_count,i;
private TextView text;
private FrameLayout f1,f2,f3,f4;
private int c_a_n,c_a_c;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final DocumentReference tests = db.collection("tests").document("Тест тест тест");
    final  CollectionReference test_a = db.collection("tests").document("Тест тест тест").collection("answers");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_view);
        i=0;
        c_a_c=1;
        a_1 = (Button)findViewById(R.id.a_button);
        a_2 = (Button)findViewById(R.id.a_button2);
        a_3 = (Button)findViewById(R.id.a_button3);
        a_4 = (Button)findViewById(R.id.a_button4);
        text = (TextView)findViewById(R.id.q_text);
        f1 = (FrameLayout)findViewById(R.id.b1_back);
        f2 = (FrameLayout)findViewById(R.id.b2_back);
        f3 = (FrameLayout)findViewById(R.id.b3_back);
        f4 = (FrameLayout)findViewById(R.id.b4_back);
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser cus = mAuth.getCurrentUser();

        Upd();

    a_1.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
         if(i==q_count){
             Handler h = new Handler();
             h.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     Intent intent = new Intent(test_view.this,test_end.class);
                     if (c_a_c!=1)
                     intent.putExtra("c_a_c",c_a_c);
                     intent.putExtra("q_count",q_count+1);
                     startActivity(intent);
                 }
             },2000);
         }else{
            i++;
            if(cor_a.get(0).equals("true")){
                f1.setBackgroundColor(ContextCompat.getColor(test_view.this, R.color.greenColor));
                c_a_c++;
            }else{
                switch (c_a_n){
                    case 1:
                        f2.setBackgroundColor(ContextCompat.getColor(test_view.this,R.color.greenColor));
                    case 2:
                        f3.setBackgroundColor(ContextCompat.getColor(test_view.this,R.color.greenColor));
                    case 3:
                        f4.setBackgroundColor(ContextCompat.getColor(test_view.this,R.color.greenColor));
                }
                f1.setBackgroundColor(ContextCompat.getColor(test_view.this,R.color.redColor));
            }
             Handler handler = new Handler();
             handler.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     cor_a.clear();
                     Upd();
                 }
             }, 2000);

         }
        }
    });

    a_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(i==q_count){
                    Handler h = new Handler();
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(test_view.this,test_end.class);
                            if (c_a_c!=1)
                            intent.putExtra("c_a_c",c_a_c);
                            intent.putExtra("q_count",q_count+1);
                            startActivity(intent);
                        }
                    },2000);
                }else{
                    i++;
                    if(cor_a.get(1).equals("true")){
                        c_a_c++;
                        f2.setBackgroundColor(ContextCompat.getColor(test_view.this, R.color.greenColor));
                    }else{
                        switch (c_a_n){
                            case 0:
                                f1.setBackgroundColor(ContextCompat.getColor(test_view.this,R.color.greenColor));
                            case 2:
                                f3.setBackgroundColor(ContextCompat.getColor(test_view.this,R.color.greenColor));
                            case 3:
                                f4.setBackgroundColor(ContextCompat.getColor(test_view.this,R.color.greenColor));
                        }
                        f2.setBackgroundColor(ContextCompat.getColor(test_view.this,R.color.redColor));
                    }
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            cor_a.clear();
                            Upd();
                        }
                    }, 2000);

                }
            }
        });

    a_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(i==q_count){
                    Handler h = new Handler();
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(test_view.this,test_end.class);
                            if (c_a_c!=1)
                            intent.putExtra("c_a_c",c_a_c);
                            intent.putExtra("q_count",q_count+1);
                            startActivity(intent);
                        }
                    },2000);
                }else{
                    i++;
                    if(cor_a.get(2).equals("true")){
                        c_a_c++;
                        f3.setBackgroundColor(ContextCompat.getColor(test_view.this, R.color.greenColor));
                    }else{
                        switch (c_a_n){
                            case 0:
                                f1.setBackgroundColor(ContextCompat.getColor(test_view.this,R.color.greenColor));
                            case 1:
                                f2.setBackgroundColor(ContextCompat.getColor(test_view.this,R.color.greenColor));
                            case 3:
                                f4.setBackgroundColor(ContextCompat.getColor(test_view.this,R.color.greenColor));
                        }
                        f3.setBackgroundColor(ContextCompat.getColor(test_view.this,R.color.redColor));
                    }
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            cor_a.clear();
                            Upd();
                        }
                    }, 2000);

                }
            }
        });

    a_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(i==q_count){
                    Handler h = new Handler();
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(test_view.this,test_end.class);
                            if (c_a_c!=1)
                            intent.putExtra("c_a_c",c_a_c);
                            intent.putExtra("q_count",q_count+1);
                            startActivity(intent);
                        }
                    },2000);

                }else{
                    i++;
                    if(cor_a.get(3).equals("true")){
                        c_a_c++;
                        f4.setBackgroundColor(ContextCompat.getColor(test_view.this, R.color.greenColor));
                    }else{ switch (c_a_n){
                        case 0:
                            f1.setBackgroundColor(ContextCompat.getColor(test_view.this,R.color.greenColor));
                        case 1:
                            f2.setBackgroundColor(ContextCompat.getColor(test_view.this,R.color.greenColor));
                        case 2:
                            f3.setBackgroundColor(ContextCompat.getColor(test_view.this,R.color.greenColor));
                    }

                        f4.setBackgroundColor(ContextCompat.getColor(test_view.this,R.color.redColor));
                    }
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            cor_a.clear();
                            Upd();
                        }
                    }, 2000);

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
                    q_count=(long) document.get("q_count")-1;
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
                        f1.setBackgroundColor(0);
                        f2.setBackgroundColor(0);
                        f3.setBackgroundColor(0);
                        f4.setBackgroundColor(0);
                        a_1.setText(document.get("0").toString());
                        a_2.setText(document.get("1").toString());
                        a_3.setText(document.get("2").toString());
                        a_4.setText(document.get("3").toString());

                       cor_a.add(0,document.get("is_cor_0").toString());
                       cor_a.add(1,document.get("is_cor_1").toString());
                       cor_a.add(2,document.get("is_cor_2").toString());
                       cor_a.add(3,document.get("is_cor_3").toString());

                        c_a_n=cor_a.indexOf("true");
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
