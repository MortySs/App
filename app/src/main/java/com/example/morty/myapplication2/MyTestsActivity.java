package com.example.morty.myapplication2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

public class MyTestsActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
    private  HashMap<String, String> map;
    private ProgressBar progressBar;
    public ImageView Avatar;
    private TextView not_auth;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflater inflater = getLayoutInflater();
        View myView = inflater.inflate(R.layout.tests, null);
        View myView2 = inflater.inflate(R.layout.my_tests,null);

        Avatar = (ImageView) myView.findViewById(R.id.image_view2);
        not_auth = (TextView) myView2.findViewById(R.id.not_auth_text);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_tests);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser cus = mAuth.getCurrentUser();
        final CollectionReference tests = db.collection("users").document(cus.getEmail()).collection("created");

        FirebaseStorage storage = FirebaseStorage.getInstance();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyTestsActivity.this, TestCreateActivity.class);
                startActivity(intent);
            }
        });

        final StorageReference storageRef = storage.getReference();

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
                        map.put("Test_id",document.getId());
                        map.put("Test_name",document.get("test_name").toString());
                        map.put("Q_count", "Вопросов: " + document.get("q_count").toString());
                        if(document.get("name")!=null)
                            map.put("P_name", document.get("name").toString());

                        arrayList.add(map);
                        SimpleAdapter adapter = new SimpleAdapter(MyTestsActivity.this, arrayList, R.layout.tests,
                                new String[]{"Test_name", "Q_count", "P_name"},
                                new int[]{R.id.test_name, R.id.q_count, R.id.person_name});
                        questions.setAdapter(adapter);
                        progressBar.setVisibility(View.GONE);
                        questions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                                Intent intent = new Intent(MyTestsActivity.this,test_view.class);
                                intent.putExtra("Test_id",arrayList.get((int)id).get("Test_id"));
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

        questions.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.delete_prompt, null);
                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(context);
                mDialogBuilder.setView(promptsView);

                final TextView delete = (TextView) promptsView.findViewById(R.id.delete_tv);
                delete.setText("Удалить тест?");
                mDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Да",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        //TODO: удаление теста
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
                return true;
            }});
    }
}