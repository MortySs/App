package com.example.morty.myapplication2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class test_end extends AppCompatActivity {
    private Button btn_end;
    private TextView txt;
    private int correct_answers_count;
    private long q_count;
    private FirebaseAuth auth;
    private RatingBar ratingBar;

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
        ratingBar = findViewById(R.id.rating);
        ratingBar.setMax(5);
        ratingBar.setStepSize(1);

        final CollectionReference user_completed = db.collection("users").document(cus.getEmail().toString()).collection("completed");
        final CollectionReference tests = db.collection("tests");
        final Intent intent = getIntent();
        correct_answers_count = intent.getIntExtra("c_a_c", 0);
        q_count=intent.getLongExtra("q_count",0) ;//("q_count",0);
        test_end_data.put("correct_a_count",correct_answers_count);
        user_completed.document(intent.getStringExtra("Test_id")).set(test_end_data);
        final DocumentReference cur_test = tests.document(intent.getStringExtra("Test_id"));
        test_end_data.clear();
        cur_test.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if (document.get("rating") != null){
                            test_end_data.put("rating_sum", (double)ratingBar.getRating() + (double)document.get("rating_sum"));
                            test_end_data.put("rating_count", 1 + (double)document.get("rating_count"));
                            test_end_data.put("rating", (double)test_end_data.get("rating_sum") / (double)test_end_data.get("rating_count"));
                        }else {
                            test_end_data.put("rating_sum", (double)ratingBar.getRating());
                            test_end_data.put("rating_count", (double)1);
                            test_end_data.put("rating", (double)ratingBar.getRating());
                        }
                        Log.e("Rating", String.valueOf(ratingBar.getRating()));
                        test_end_data.put("solved_cnt", (long)document.get("solved_cnt")+1);
                        Log.d("testEnd", "onComplete: " + document.get("solved_cnt")+" "+test_end_data.get("solved_cnt"));
                    }
                }
            }
        });

        txt.setText("Вы решили "+correct_answers_count+" / "+q_count );



        btn_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(test_end.this,MainActivity.class);
                startActivity(intent);
                cur_test.update(test_end_data);
            }
        });
    }
}
