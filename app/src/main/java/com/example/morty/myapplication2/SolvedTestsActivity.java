package com.example.morty.myapplication2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class SolvedTestsActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseUser cus;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
    private  HashMap<String, String> map;
    private ProgressBar progressBar;
    private TextView not_auth;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    ListView questions;
    String email;
    boolean isCus, isThereTest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflater inflater = getLayoutInflater();
        View myView2 = inflater.inflate(R.layout.my_tests,null);

        not_auth = (TextView) myView2.findViewById(R.id.not_auth_text);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_tests);

        mAuth = FirebaseAuth.getInstance();
        cus = mAuth.getCurrentUser();

        if(getIntent().getStringExtra("email") == null){
            isCus = true;
            email = cus.getEmail();
        } else{
            email = getIntent().getStringExtra("email");
            isCus = false;
        }

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        questions = (ListView) findViewById(R.id.list);

        updateTests();

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        updateTests();
                    }
                }, 1000);
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SolvedTestsActivity.this, TestCreateActivity.class);
                startActivity(intent);
            }
        });
    }

    void updateTests(){
        arrayList.clear();
        questions.setAdapter(null);
        final CollectionReference tests = db.collection("tests");
        tests.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        Log.d("MortyList", document.getId() + " => " + document.getData());
                        map = new HashMap<>();

                        ArrayList<String> Emails = (ArrayList<String>) document.get("solved_emails");

                        if(Emails != null) {
                            for (int i = 0; i < Emails.size(); i++) {
                                if (Emails.get(i).equals(email)) {

                                    isThereTest = true;
                                    DecimalFormat df = new DecimalFormat("#.##");

                                    map.put("Test_id", document.getId());
                                    map.put("Test_name", document.get("test_name").toString());
                                    map.put("Q_count", "Вопросов: " + document.get("q_count").toString());
                                    map.put("S_count", document.get("solved_cnt").toString());
                                    if (document.get("rating") != null)
                                        map.put("Rating", df.format(document.get("rating")));
                                    if (document.get("name") != null)
                                        map.put("P_name", document.get("name").toString());

                                    ArrayList<Long> EmailsSolvedCount = (ArrayList<Long>) document.get("emails_solved_count");
                                    ArrayList<Long> Seconds = (ArrayList<Long>) document.get("seconds");
                                    ArrayList<Long> Minutes = (ArrayList<Long>) document.get("minutes");
                                    ArrayList<Long> CorrectAnswersCounts = (ArrayList<Long>) document.get("correct_a_counts");

                                    map.put("R_count", CorrectAnswersCounts.get(i) + " решено верно");
                                    map.put("Time", "Время " + Minutes.get(i) + ":" + Seconds.get(i));
                                    String MySCount = EmailsSolvedCount.get(i).toString();

                                    boolean is234 = MySCount.endsWith("2") || MySCount.endsWith("3") || MySCount.endsWith("4");
                                    if(isCus) {
                                        if (is234) {
                                            map.put("My_S_count", "Решен вами " + MySCount + " раза");
                                        } else
                                            map.put("My_S_count", "Решен вами " + MySCount + " раз");
                                    }else {
                                        if (is234) {
                                            map.put("My_S_count", "Решен им(-ей) " + MySCount + " раза");
                                        } else
                                            map.put("My_S_count", "Решен им(-ей) " + MySCount + " раз");
                                    }

                                    arrayList.add(map);
                                    SimpleAdapter adapter = new SimpleAdapter(SolvedTestsActivity.this, arrayList, R.layout.solved_tests_item,
                                            new String[]{"Test_name", "Q_count", "P_name", "S_count", "Rating", "R_count", "Time", "My_S_count"},
                                            new int[]{R.id.test_name, R.id.q_count, R.id.person_name, R.id.solved_count, R.id.test_rating, R.id.my_answers, R.id.my_time, R.id.my_solved_count});
                                    questions.setAdapter(adapter);
                                    progressBar.setVisibility(View.GONE);
                                    questions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View itemClicked, int position, final long id) {
                                            LayoutInflater li = LayoutInflater.from(SolvedTestsActivity.this);
                                            View promptsView = li.inflate(R.layout.delete_prompt, null);
                                            AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(SolvedTestsActivity.this);
                                            mDialogBuilder.setView(promptsView);

                                            final TextView delete = (TextView) promptsView.findViewById(R.id.delete_tv);
                                            delete.setText("Решить тест или посмотреть информацию?");
                                            mDialogBuilder
                                                    .setCancelable(false)
                                                    .setPositiveButton("Решить",
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int DialogId) {
                                                                    Intent intent = new Intent(SolvedTestsActivity.this, test_view.class);
                                                                    intent.putExtra("Test_id",arrayList.get((int)id).get("Test_id"));
                                                                    startActivity(intent);
                                                                }
                                                            })
                                                    .setNegativeButton("Посмотреть",
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int DialogId) {
                                                                    Intent intent = new Intent(SolvedTestsActivity.this, MyTestsSolveActivity.class);
                                                                    intent.putExtra("Test_id",arrayList.get((int)id).get("Test_id"));
                                                                    startActivity(intent);
                                                                }
                                                            });
                                            AlertDialog alertDialog = mDialogBuilder.create();
                                            alertDialog.show();
                                        }
                                    });

                                    break;
                                }
                            }
                        }
                    }
                } else {
                    not_auth.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    Log.d("MortyList", "Error getting documents: ", task.getException());
                }
            }
        });
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isThereTest) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SolvedTestsActivity.this, "Нет решенных тестов", Toast.LENGTH_LONG).show();
                }
            }
        }, 2000);
    }

}