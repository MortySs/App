package com.example.morty.myapplication2;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyTestsActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference tests = db.collection("tests");
    private final ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
    private  HashMap<String, String> map;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_tests);
        final FirebaseUser cus = mAuth.getCurrentUser();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        final ListView questions = (ListView) findViewById(R.id.list);
        tests.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        Log.d("MortyList", document.getId() + " => " + document.getData());
                        map = new HashMap<>();
                        map.put("Test_name","'"+document.getId()+"'");
                        map.put("Q_count","Вопросов: "+document.get("q_count").toString());
                        map.put("P_name",document.get("name").toString());
                        arrayList.add(map);
                        SimpleAdapter adapter = new SimpleAdapter(MyTestsActivity.this, arrayList, R.layout.my_tests_item,
                                new String[]{"Test_name", "Q_count","P_name"},
                                new int[]{R.id.test_name,R.id.q_count,R.id.person_name});
                        questions.setAdapter(adapter);
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    Log.d("MortyList", "Error getting documents: ", task.getException());
                }
            }
        });


    }

}
