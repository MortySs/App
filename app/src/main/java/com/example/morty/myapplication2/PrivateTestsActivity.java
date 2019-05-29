package com.example.morty.myapplication2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class PrivateTestsActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseUser cus;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
    private HashMap<String, String> map;
    private ProgressBar progressBar;
    private ArrayList<Integer> deletedId = new ArrayList<>();
    private TextView not_auth;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    ListView questions;
    String email;
    boolean isCus, isThereTest = false;
    TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflater inflater = getLayoutInflater();
        View myView = inflater.inflate(R.layout.my_tests_item, null);
        View myView2 = inflater.inflate(R.layout.my_tests,null);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_tests);
        mAuth = FirebaseAuth.getInstance();
        cus = mAuth.getCurrentUser();

        tabLayout = (TabLayout) findViewById(R.id.pr_tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("по паролю"),0);
        tabLayout.addTab(tabLayout.newTab().setText("только для подписчиков"),1);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tabLayout.getSelectedTabPosition() == 0)  updateTestsPass();
                else if (tabLayout.getSelectedTabPosition() == 1)  updateTestsSub();
                Log.i("TAG", "onTabSelected: " + tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                Log.i("TAG", "onTabUnselected: " + tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if(tabLayout.getSelectedTabPosition() == 0)  updateTestsPass();
                else if (tabLayout.getSelectedTabPosition() == 1)  updateTestsSub();
                Log.i("TAG", "onTabReselected: " + tab.getPosition());
            }
        });


        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        if(tabLayout.getSelectedTabPosition() == 0)  updateTestsPass();
                        else if (tabLayout.getSelectedTabPosition() == 1)  updateTestsSub();
                    }
                }, 1000);
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        not_auth = (TextView) myView2.findViewById(R.id.not_auth_text);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(PrivateTestsActivity.this, TestCreateActivity.class);
                startActivity(intent);
            }
        });


        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        questions = (ListView) findViewById(R.id.list);

        if(getIntent().getStringExtra("email") == null){
            isCus = true;
            email = cus.getEmail();
        } else{
            email = getIntent().getStringExtra("email");
            isCus = false;
        }

        updateTestsPass();
    }

    void updateTestsSub(){
        arrayList.clear();
        questions.setAdapter(null);
        final CollectionReference tests = db.collection("tests");
        Query q = tests.whereEqualTo("private_status", "sub");

        q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (final QueryDocumentSnapshot document1 : task.getResult()) {
                        isThereTest = true;
                        final DecimalFormat df = new DecimalFormat("#.##");
                        map = new HashMap<>();
                        map.put("test_maker_email",document1.get("test_maker_email").toString());
                        final DocumentReference user = db.collection("users").document(map.get("test_maker_email"));
                        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        ArrayList<String> subs = (ArrayList<String>)document.get("subscribers");
                                        if(subs.contains(cus.getEmail())){
                                            Log.d("MortyList", document1.getId() + " => " + document1.getData());
                                            map = new HashMap<>();
                                            map.put("Test_id",document1.getId());
                                           map.put("private_status",document1.get("private_status").toString());
                                            map.put("test_maker_email",document1.get("test_maker_email").toString());
                                            map.put("Test_name",document1.get("test_name").toString());
                                            map.put("Q_count", "Вопросов: " + document1.get("q_count").toString());
                                            map.put("S_count",document1.get("solved_cnt").toString());
                                            if (document.get("rating")!=null) map.put("Rating",df.format(document.get("rating")));
                                            if (document.get("name")!=null) map.put("P_name", document.get("name").toString());

                                            arrayList.add(map);
                                            SimpleAdapter adapter = new SimpleAdapter(PrivateTestsActivity.this, arrayList, R.layout.my_tests_item,
                                                    new String[]{"Test_name", "Q_count", "P_name", "S_count", "Rating"},
                                                    new int[]{R.id.test_name, R.id.q_count, R.id.person_name, R.id.solved_count, R.id.test_rating});
                                            questions.setAdapter(adapter);
                                            progressBar.setVisibility(View.GONE);

                                            questions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View itemClicked, int position, final long id) {

                                                }
                                            });
                                        }
                                        Log.d("testsCount", "DocumentSnapshot data: " + document1.getData());
                                    } else {
                                        Log.d("testsCount", "No such document");
                                    }
                                } else {
                                    Log.d("testsCount", "get failed with ", task.getException());
                                }
                            }
                        });


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
                    Toast.makeText(PrivateTestsActivity.this, "Нет созданных тестов", Toast.LENGTH_LONG).show();
                }
            }
        }, 2000);

        final DocumentReference other_tests = db.collection("oth_info").document("tests");
        other_tests.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    deletedId = (ArrayList)document.get("deletedId");
                }
            }
        });

    }

    void updateTestsPass(){
        arrayList.clear();
        questions.setAdapter(null);
        final CollectionReference tests = db.collection("tests");
        Query q = tests.whereEqualTo("private_status", "pass");

        q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        isThereTest = true;
                        DecimalFormat df = new DecimalFormat("#.##");

                        Log.d("MortyList", document.getId() + " => " + document.getData());
                        map = new HashMap<>();
                        map.put("Test_id",document.getId());
                        map.put("private_status",document.get("private_status").toString());
                        map.put("test_pass",document.get("test_pass").toString());
                        map.put("Test_name",document.get("test_name").toString());
                        map.put("Q_count", "Вопросов: " + document.get("q_count").toString());
                        map.put("S_count",document.get("solved_cnt").toString());
                        if (document.get("rating")!=null) map.put("Rating",df.format(document.get("rating")));
                        if (document.get("name")!=null) map.put("P_name", document.get("name").toString());

                        arrayList.add(map);
                        SimpleAdapter adapter = new SimpleAdapter(PrivateTestsActivity.this, arrayList, R.layout.my_tests_item,
                                new String[]{"Test_name", "Q_count", "P_name", "S_count", "Rating"},
                                new int[]{R.id.test_name, R.id.q_count, R.id.person_name, R.id.solved_count, R.id.test_rating});
                        questions.setAdapter(adapter);
                        progressBar.setVisibility(View.GONE);
                        questions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, final long id) {
                                if(map.get("private_status").equals("pass")){
                                    LayoutInflater li = LayoutInflater.from(PrivateTestsActivity.this);
                                    View promptsView = li.inflate(R.layout.prompt, null);
                                    AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(PrivateTestsActivity.this);
                                    mDialogBuilder.setView(promptsView);

                                    final TextView textView = (TextView) promptsView.findViewById(R.id.tv);
                                    final EditText userInput = (EditText) promptsView.findViewById(R.id.input_text);
                                    textView.setText("Введите пароль:");
                                    mDialogBuilder
                                            .setCancelable(false)
                                            .setPositiveButton("Ок",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int DialogId) {
                                                            if (userInput.getText().toString().equals(map.get("test_pass"))){
                                                                Intent intent = new Intent(PrivateTestsActivity.this, test_view.class);
                                                                intent.putExtra("Test_id",arrayList.get((int)id).get("Test_id"));
                                                                startActivity(intent);
                                                            }else {
                                                                Toast.makeText(getApplicationContext(), "неверный пароль", Toast.LENGTH_SHORT).show();
                                                            }
                                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                                                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                                                        }
                                                    })
                                            .setNegativeButton("Отмена",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int DialogId) {
                                                            dialog.cancel();
                                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                                                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                                                        }
                                                    });
                                    AlertDialog alertDialog = mDialogBuilder.create();
                                    alertDialog.show();
                                }

                            }
                        });
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
                    Toast.makeText(PrivateTestsActivity.this, "Нет созданных тестов", Toast.LENGTH_LONG).show();
                }
            }
        }, 2000);

        final DocumentReference other_tests = db.collection("oth_info").document("tests");
        other_tests.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    deletedId = (ArrayList)document.get("deletedId");
                }
            }
        });

    }


}
