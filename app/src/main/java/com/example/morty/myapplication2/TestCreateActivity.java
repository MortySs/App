package com.example.morty.myapplication2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

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


public class TestCreateActivity extends AppCompatActivity {
    public final ArrayList<String> Questions = new ArrayList<>();
    private EditText name;
    private FirebaseAuth mAuth;
    private Button q_create, t_create;
    private ProgressBar progressBar;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_create);
        q_create = (Button) findViewById(R.id.q_add);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        name = (EditText) findViewById(R.id.name_of_test);
        t_create = (Button) findViewById(R.id.t_create);
        Spinner category = (Spinner) findViewById(R.id.list_tag);
        ListView questionView = (ListView) findViewById(R.id.test_create_list);

        final String[] TagNames = getResources().getStringArray(R.array.tag_names);


        questionView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(TestCreateActivity.this, TestCreateView.class);
                intent.putExtra("q_text", Questions.get((int) id));
                intent.putExtra("number", (int) id + 1);
                startActivity(intent);
                //Toast.makeText(TestCreateActivity.this, "Нажат вопрос номер " + id, Toast.LENGTH_SHORT).show();
            }
        });

        questionView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long q_id) {
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.delete_prompt, null);
                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(context);
                mDialogBuilder.setView(promptsView);

                final TextView delete = (TextView) promptsView.findViewById(R.id.delete_tv);
                delete.setText("Удалить вопрос?");
                mDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Да",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        Questions.remove(position);
                                        Log.d("deleting", "onClick: удален вопрос номер "+q_id);
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



        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String item = (String) parent.getItemAtPosition(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        category.setOnItemSelectedListener(itemSelectedListener);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, TagNames);
        category.setAdapter(adapter);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, Questions);
        questionView.setAdapter(adapter2);

        q_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.prompt, null);
                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(context);
                mDialogBuilder.setView(promptsView);

                //Настраиваем отображение поля для ввода текста в открытом диалоге:
                final EditText userInput = (EditText) promptsView.findViewById(R.id.input_text);
                mDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        //Вводим текст и отображаем в строке ввода на основном экране:
                                        Questions.add(userInput.getText().toString());
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

            }
        });

        t_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Upd_test();
                Intent intent = new Intent(TestCreateActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void Upd_test(){
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser cus = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final CollectionReference createdTests = db.collection("users").document(cus.getEmail()).collection("created");
        final CollectionReference a_draft = db.collection("users").document(cus.getEmail()).collection("tests").document("draft").collection("answers");
        final CollectionReference tests = db.collection("tests"); //document(name.getText().toString());
        final DocumentReference other_tests = db.collection("oth_info").document("tests");
        final DocumentReference us = db.collection("users").document(cus.getEmail());

        final Map<String, Object> data = new HashMap<>();
        final Map<String, Object> id_inf = new HashMap<>();
        final Map<String, Object> test_inf = new HashMap<>();
        final Map<String, Object> data3 = new HashMap<>();
        final Map<String, Object> us_data = new HashMap<>();
        final Map<String, Object> data1 = new HashMap<>();

        final Spinner category = (Spinner) findViewById(R.id.list_tag);


        other_tests.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        data.put("category",category.getSelectedItem().toString());
                        id_inf.put("last_id",(long)document.get("last_id")+1);
                        test_inf.put("test_name",name.getText().toString());
                        Log.d("LOL", "DocumentSnapshot data: " + document.get("last_id")+id_inf.get("test_id"));
                        other_tests.update(id_inf);
                        tests.document(id_inf.get("last_id").toString()).set(test_inf);
                        createdTests.document(id_inf.get("last_id").toString()).set(test_inf);
                        for (int i = 0;i<Questions.size();i++){
                            final String count = ""+i;
                            data.put(count, Questions.get(i));

                            for (int j = 0;j<4;j++){
                                final int k = j;
                                DocumentReference a = a_draft.document(""+(i+1));
                                a.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                data1.put(""+k,document.get(""+k).toString());
                                                data3.put("is_cor_"+k,document.get("is_cor_"+k));
                                                tests.document(id_inf.get("last_id").toString()).collection("answers").document(count).set(data1);
                                                tests.document(id_inf.get("last_id").toString()).collection("answers").document(count).update(data3);
                                                createdTests.document(id_inf.get("last_id").toString()).collection("answers").document(count).set(data1);
                                                createdTests.document(id_inf.get("last_id").toString()).collection("answers").document(count).update(data3);
                                                Log.d("LOL", "DocumentSnapshot data: " + document.get(""+k));
                                            } else {
                                                Log.d("LOL", "No such document");
                                            }
                                        } else {
                                            Log.d("LOL", "get failed with ", task.getException());
                                        }
                                    }
                                });
                            }

                            data1.clear();
                        }
                        data.put("q_count",Questions.size());

                        tests.document(id_inf.get("last_id").toString()).update(data);
                        createdTests.document(id_inf.get("last_id").toString()).update(data);
                        us.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        us_data.put("name",document.get("name").toString());
                                        tests.document(id_inf.get("last_id").toString()).update(us_data);
                                        createdTests.document(id_inf.get("last_id").toString()).update(us_data);
                                        Log.d("LOL", "DocumentSnapshot data: " + document.get("name"));
                                    } else {
                                        Log.d("LOL", "No such document");
                                    }
                                } else {
                                    Log.d("LOL", "get failed with ", task.getException());
                                }
                            }
                        });

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