package com.example.morty.myapplication2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class MyTestsSolveActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference tests = db.collection("tests");
    DocumentReference test;
    private final ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
    private  HashMap<String, String> map;
    ListView usersList;
    String testId;
    TextView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_tests_solve);

        mAuth = FirebaseAuth.getInstance();
        usersList = (ListView) findViewById(R.id.my_test_list);
        info = (TextView) findViewById(R.id.my_test_solve_info);
        testId = getIntent().getStringExtra("Test_id");
        test = tests.document(testId);
        test.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        long qCount = document.getLong("q_count");
                        String QuestionCount;
                            if (qCount <= 20) {
                                if (qCount == 1) {
                                    QuestionCount = "1 вопрос";
                                } else {
                                    if (qCount == 2 || qCount == 3 || qCount ==4) {
                                        QuestionCount = qCount + " вопроса";
                                    } else {
                                        QuestionCount = qCount + " вопросов";
                                    }
                                }
                            } else {
                                if (String.valueOf(qCount).endsWith("1")) {
                                    QuestionCount = qCount + " вопрос";
                                } else {
                                    if (String.valueOf(qCount).endsWith("2") || String.valueOf(qCount).endsWith("3") || String.valueOf(qCount).endsWith("4")) {
                                        QuestionCount = qCount + " вопроса";
                                    } else {
                                        QuestionCount = qCount + " вопросов";
                                    }
                                }
                            }
                            if (document.getDouble("rating") == null){
                            info.setText(document.get("test_name") + ", создатель " + document.get("name") + ", категория - " + document.get("category") + ", " +
                                    QuestionCount + ". Этот тест еще не был решен");
                        }else {
                                DecimalFormat df = new DecimalFormat("#.##");

                                info.setText(document.get("test_name") + ", создатель " + document.get("name") + ", категория - " + document.get("category") + ", " +
                                        QuestionCount + ", рейтинг: " + df.format(document.get("rating")));
                            }
                        if(!info.getText().toString().endsWith("решен")) UpdateList();
                        Log.d("MyTestsSolve", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("MyTestsSolve", "No such document");
                    }
                } else {
                    Log.d("MyTestsSolve", "get failed with ", task.getException());
                }
            }
        });
    }

    void UpdateList(){
        usersList.setAdapter(null);
        arrayList.clear();

        test.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("MortyList", document.getId() + " => " + document.getData());
                        ArrayList<String> Emails = (ArrayList<String>) document.get("solved_emails");
                        ArrayList<String> Names = (ArrayList<String>) document.get("solved_names");
                        ArrayList<Long> EmailsSolvedCount = (ArrayList<Long>) document.get("emails_solved_count");
                        ArrayList<Long> Seconds = (ArrayList<Long>) document.get("seconds");
                        ArrayList<Long> Minutes = (ArrayList<Long>) document.get("minutes");
                        ArrayList<Long> CorrectAnswersCounts = (ArrayList<Long>) document.get("correct_a_counts");

                        if(Emails != null) {
                            for (int i = 0; i < Emails.size(); i++) {
                                map = new HashMap<>();
                                map.put("Email", Emails.get(i));
                                map.put("Name", Names.get(i));

                                long sCount = EmailsSolvedCount.get(i);
                                String SolvedCount;
                                if (sCount <= 20) {
                                    if (sCount == 2 || sCount == 3 || sCount ==4) {
                                        SolvedCount = "решал(-a)" + sCount + " раза";
                                    } else {
                                            SolvedCount = "решал(-a)" + sCount + " раз";
                                    }
                                } else {
                                    if (String.valueOf(sCount).endsWith("2") || String.valueOf(sCount).endsWith("3") || String.valueOf(sCount).endsWith("4")) {
                                        SolvedCount = "решал(-a)" + sCount + " раза";
                                    } else {
                                        SolvedCount = "решал(-a)" + sCount + " раз";
                                    }
                                }
                                map.put("Emails_Solved_Count", SolvedCount);

                                String time = "время " + Minutes.get(i) + ":" + Seconds.get(i);
                                map.put("Time", time);

                                long cCount = CorrectAnswersCounts.get(i);
                                String CorrectCount;
                                if (cCount <= 20) {
                                    if (cCount == 2 || cCount == 3 || cCount ==4) {
                                        CorrectCount = cCount + " решено верно";
                                    } else {
                                        CorrectCount = cCount + " решен верно";
                                    }
                                } else {
                                    if (String.valueOf(cCount).endsWith("2") || String.valueOf(cCount).endsWith("3") || String.valueOf(cCount).endsWith("4")) {
                                        CorrectCount = cCount + " решено верно";
                                    } else {
                                        CorrectCount = cCount + " решен верно";
                                    }
                                }
                                map.put("Correct_Answers_Count", CorrectCount);

                                arrayList.add(map);
                                SimpleAdapter adapter = new SimpleAdapter(MyTestsSolveActivity.this, arrayList, R.layout.my_tests_solve_item,
                                        new String[]{"Email", "Name", "Emails_Solved_Count", "Time", "Correct_Answers_Count"},
                                        new int[]{R.id.my_test_solve_user_email, R.id.my_test_solve_user_name, R.id.my_test_solve_solved_count,
                                                R.id.my_test_solve_time, R.id.my_test_solve_right_count});
                                usersList.setAdapter(adapter);

                                usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                                        Intent intent = new Intent(MyTestsSolveActivity.this, User_profile.class);
                                        intent.putExtra("Email", arrayList.get((int) id).get("Email"));
                                        startActivity(intent);
                                    }
                                });

                            }
                        }
                    }
                }
            }
        });
    }
}
