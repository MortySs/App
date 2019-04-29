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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class MyTestsActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
    private  HashMap<String, String> map;
    private ProgressBar progressBar;
    private ArrayList<Integer> deletedId = new ArrayList<>();
    private TextView not_auth;
    final Context context = this;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    ListView questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflater inflater = getLayoutInflater();
        View myView = inflater.inflate(R.layout.my_tests_item, null);
        View myView2 = inflater.inflate(R.layout.my_tests,null);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_tests);
        mAuth = FirebaseAuth.getInstance();

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

        not_auth = (TextView) myView2.findViewById(R.id.not_auth_text);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyTestsActivity.this, TestCreateActivity.class);
                startActivity(intent);
            }
        });


        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        questions = (ListView) findViewById(R.id.list);
        updateTests();
        questions.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long q_id) {
                LayoutInflater li = LayoutInflater.from(MyTestsActivity.this);
                View promptsView = li.inflate(R.layout.delete_prompt, null);
                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(MyTestsActivity.this);
                mDialogBuilder.setView(promptsView);

                final TextView delete = (TextView) promptsView.findViewById(R.id.delete_tv);
                delete.setText("Удалить тест?");
                mDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Да",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        final FirebaseUser cus = mAuth.getCurrentUser();
                                        final CollectionReference tests = db.collection("tests");
                                        deletedId.add(Integer.valueOf(arrayList.get(position).get("Test_id")));
                                        Log.d("deletedId ArrayList", String.valueOf(deletedId.get(5)));
                                        tests.document(arrayList.get(position).get("Test_id")).delete();
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("deletedId",deletedId);
                                        db.collection("oth_info").document("tests").update(hashMap);
                                        Log.d("deleting test", "test id: "+arrayList.get(position).get("Test_id")+"cur email: "+cus.getEmail()+" | "+arrayList.get(position).get("test_maker_email"));
                                        updateTests();
                                        Log.d("deletedId ArrayList", "ya eblan");
                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                                    }
                                })
                        .setNegativeButton("Отмена",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                                    }
                                });
                AlertDialog alertDialog = mDialogBuilder.create();
                alertDialog.show();
                return true;
            }});
    }

    void updateTests(){
        arrayList.clear();
        questions.setAdapter(null);
        final FirebaseUser cus = mAuth.getCurrentUser();
        final CollectionReference tests = db.collection("tests");
        Query q = tests.whereEqualTo("test_maker_email",cus.getEmail());
        q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        Log.d("MortyList", document.getId() + " => " + document.getData());
                        map = new HashMap<>();
                        map.put("Test_id",document.getId());
                        map.put("Test_name",document.get("test_name").toString());
                        map.put("Q_count", "Вопросов: " + document.get("q_count").toString());
                        map.put("S_count",document.get("solved_cnt").toString());
                        if(document.get("name")!=null)
                            map.put("P_name", document.get("name").toString());

                        arrayList.add(map);
                        SimpleAdapter adapter = new SimpleAdapter(MyTestsActivity.this, arrayList, R.layout.my_tests_item,
                                new String[]{"Test_name", "Q_count", "P_name","S_count"},
                                new int[]{R.id.test_name, R.id.q_count, R.id.person_name, R.id.solved_count});
                        questions.setAdapter(adapter);
                        progressBar.setVisibility(View.GONE);
                        questions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                                Intent intent = new Intent(MyTestsActivity.this,test_view.class);
                                intent.putExtra("Test_id",arrayList.get((int)id).get("Test_id").toString());
                                startActivity(intent);
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
        final DocumentReference other_tests = db.collection("oth_info").document("tests");
        other_tests.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            if (task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                deletedId = (ArrayList)document.get("deletedId");
                Log.d("deletedId", "onComplete: " +deletedId.get(0));
            }
            }
        });

    }

}