package com.example.morty.myapplication2;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class test_end extends AppCompatActivity {
    private Button btn_end;
    long seconds, minute;
    private TextView txt, time;
    private long q_count;
    private FirebaseAuth auth;
    private RatingBar ratingBar;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final Map<String, Object> test_end_data = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_end);
        auth = FirebaseAuth.getInstance();
        final FirebaseUser cus = auth.getCurrentUser();

        txt = findViewById(R.id.test_result_txt);
        time = findViewById(R.id.end_time);
        btn_end = findViewById(R.id.test_end_btn);
        ratingBar = findViewById(R.id.rating);
        ratingBar.setMax(5);
        ratingBar.setStepSize(1);

        q_count = getIntent().getLongExtra("q_count", 0);
        seconds = getIntent().getLongExtra("seconds", 0);
        minute = getIntent().getLongExtra("minute", 0);

        final CollectionReference tests = db.collection("tests");
        final Intent intent = getIntent();
        final DocumentReference cur_test = tests.document(intent.getStringExtra("Test_id"));


        final long correct_answers_count = intent.getIntExtra("c_a_c", 0);
        txt.setText("Вы решили "+correct_answers_count+" / "+ q_count );
        final String CurrentEmail = cus.getEmail();
        final DocumentReference user = db.collection("users").document(CurrentEmail);

        btn_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ratingBar.getRating() == 0) {
                    Toast.makeText(getApplicationContext(), "Оцените тест", Toast.LENGTH_LONG).show();
                } else {
                    final Intent intent = new Intent(test_end.this, MainActivity.class);
                    startActivity(intent);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            cur_test.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        final DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {

                                            user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot doc = task.getResult();
                                                        if (doc.exists()) {

                                                            q_count = intent.getLongExtra("q_count", 0);//("q_count",0);
                                                            time.setText("Время: " + minute + " : " + seconds);
                                                            ArrayList<String> Emails;
                                                            ArrayList<String> Names;
                                                            ArrayList<Long> EmailsSolvedCount;
                                                            ArrayList<Long> Seconds;
                                                            ArrayList<Long> Minutes;
                                                            ArrayList<Double> Ratings;
                                                            ArrayList<Long> CorrectAnswersCounts;
                                                            if (document.get("ratings") != null) {
                                                                Ratings = (ArrayList<Double>) document.get("ratings");
                                                            } else {
                                                                Ratings = new ArrayList<>();
                                                            }
                                                            if (document.get("solved_emails") != null) {
                                                                Emails = (ArrayList<String>) document.get("solved_emails");
                                                                Names = (ArrayList<String>) document.get("solved_names");
                                                                EmailsSolvedCount = (ArrayList<Long>) document.get("emails_solved_count");
                                                                Seconds = (ArrayList<Long>) document.get("seconds");
                                                                Minutes = (ArrayList<Long>) document.get("minutes");
                                                                CorrectAnswersCounts = (ArrayList<Long>) document.get("correct_a_counts");
                                                            } else {
                                                                Emails = new ArrayList<>();
                                                                Names = new ArrayList<>();
                                                                EmailsSolvedCount = new ArrayList<>();
                                                                Seconds = new ArrayList<>();
                                                                Minutes = new ArrayList<>();
                                                                CorrectAnswersCounts = new ArrayList<>();
                                                            }
                                                            test_end_data.put("solved_cnt", (long) document.get("solved_cnt") + 1);
                                                            double rating = ratingBar.getRating();
                                                            boolean userSolve = false;

                                                            for (int i = 0; i < Emails.size(); i++) {
                                                                if (Emails.get(i).equals(CurrentEmail)) {
                                                                    userSolve = true;
                                                                    test_end_data.put("solved_cnt", document.get("solved_cnt"));
                                                                    long sCount = EmailsSolvedCount.get(i);
                                                                    double lastRating = Ratings.get(i);
                                                                    Emails.remove(i);
                                                                    Names.remove(i);
                                                                    EmailsSolvedCount.remove(i);
                                                                    Seconds.remove(i);
                                                                    Minutes.remove(i);
                                                                    CorrectAnswersCounts.remove(i);
                                                                    Emails.add(CurrentEmail);
                                                                    Names.add(doc.getString("name"));
                                                                    EmailsSolvedCount.add(sCount + 1);
                                                                    Seconds.add(seconds);
                                                                    Minutes.add(minute);
                                                                    CorrectAnswersCounts.add(correct_answers_count);

                                                                    Ratings.remove(i);
                                                                    Ratings.add(rating);
                                                                    if (document.get("rating") != null) {
                                                                        test_end_data.put("rating_sum", (double) document.get("rating_sum") + rating - lastRating);
                                                                        test_end_data.put("rating_count", document.get("rating_count"));
                                                                        test_end_data.put("rating", (double) test_end_data.get("rating_sum") / (long) test_end_data.get("rating_count"));
                                                                    } else {
                                                                        test_end_data.put("rating_sum", rating);
                                                                        test_end_data.put("rating_count", (long) 1);
                                                                        test_end_data.put("rating", rating);
                                                                    }

                                                                    break;
                                                                }
                                                            }
                                                            if (!userSolve) {
                                                                Emails.add(CurrentEmail);
                                                                Names.add(doc.getString("name"));
                                                                EmailsSolvedCount.add((long) 1);
                                                                Seconds.add(seconds);
                                                                Minutes.add(minute);
                                                                CorrectAnswersCounts.add(correct_answers_count);

                                                                Ratings.add(rating);
                                                                if (document.get("rating") != null) {
                                                                    test_end_data.put("rating_sum", rating + (double) document.get("rating_sum"));
                                                                    test_end_data.put("rating_count", 1 + (long) document.get("rating_count"));
                                                                    test_end_data.put("rating", (double) test_end_data.get("rating_sum") / (long) test_end_data.get("rating_count"));
                                                                } else {
                                                                    test_end_data.put("rating_sum", rating);
                                                                    test_end_data.put("rating_count", (long) 1);
                                                                    test_end_data.put("rating", rating);
                                                                }
                                                            }
                                                            test_end_data.put("solved_emails", Emails);
                                                            test_end_data.put("solved_names", Names);
                                                            test_end_data.put("emails_solved_count", EmailsSolvedCount);
                                                            test_end_data.put("seconds", Seconds);
                                                            test_end_data.put("minutes", Minutes);
                                                            test_end_data.put("correct_a_counts", CorrectAnswersCounts);
                                                            test_end_data.put("ratings", Ratings);
                                                            cur_test.update(test_end_data);
                                                            Log.d("testEnd", "onComplete: " + document.get("solved_cnt") + " " + test_end_data.get("solved_cnt"));
                                                        } else {
                                                            Log.d("user", "No such document");
                                                        }
                                                    } else {
                                                        Log.d("user", "get failed with ", task.getException());
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }
                            });
                        }
                    }, 2000);
                }
            }
        });
    }
}