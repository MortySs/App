package com.example.morty.myapplication2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class test_end extends AppCompatActivity {
    private Button btn_end;
    private TextView txt;
    private int correct_answers_count; //
    private long q_count;
    private FirebaseAuth auth;

    private final Map<String, Object> test_end_data = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_end);
        auth = FirebaseAuth.getInstance();
        final FirebaseUser cus = auth.getCurrentUser();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        txt = findViewById(R.id.test_result_txt);
        btn_end = findViewById(R.id.test_end_btn);
        final CollectionReference user_completed = db.collection("users").document(cus.getEmail().toString()).collection("completed");
        final Intent intent = getIntent();
       correct_answers_count = intent.getIntExtra("c_a_c", 0);
        q_count=intent.getLongExtra("q_count",0) ;//("q_count",0);
        test_end_data.put("correct_a_count",correct_answers_count);
        user_completed.document(intent.getStringExtra("Test_id")).set(test_end_data);
        txt.setText("Вы решили "+correct_answers_count+" / "+q_count );



        btn_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(test_end.this,MainActivity.class);
                startActivity(intent);

            }
        });
    }
}
