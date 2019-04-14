package com.example.morty.myapplication2;

import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.expansionpanel.ExpansionLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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


public class TestCreateActivity extends AppCompatActivity{
    public final ArrayList<String> Questions = new ArrayList<>();
    private EditText name;
    private FirebaseAuth mAuth;
    private TextView mTextMessage;
    private Button q_create,t_create;
    private ProgressBar progressBar;
    final Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_create);
        mTextMessage = (TextView) findViewById(R.id.tag_name);
        q_create = (Button)findViewById(R.id.q_add);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        name = (EditText)findViewById(R.id.name_of_test);
        t_create = (Button)findViewById(R.id.t_create);
        ListView listView = (ListView)findViewById(R.id.list_tag);
        ListView questionView = (ListView)findViewById(R.id.test_create_list);

       final String[] TagNames = getResources().getStringArray(R.array.tag_names);

       final ExpansionLayout expansionLayout = findViewById(R.id.expansionLayout);

       questionView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

               Intent intent = new Intent(TestCreateActivity.this,TestCreateView.class);
               intent.putExtra("q_text",Questions.get((int)id));
               intent.putExtra("number",(int)id+1);
               startActivity(intent);
               //Toast.makeText(TestCreateActivity.this, "Нажат вопрос номер " + id, Toast.LENGTH_SHORT).show();
           }
       });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {

                expansionLayout.toggle(true);

                TextView textView = (TextView) itemClicked;
                mTextMessage.setText( textView.getText().toString());
            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, TagNames);
        listView.setAdapter(adapter);
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
        long q_count;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference a_draft = db.collection("users").document(cus.getEmail().toString()).collection("tests").document("draft").collection("answers");
        final CollectionReference tests = db.collection("tests"); //document(name.getText().toString());
        final DocumentReference us = db.collection("users").document(cus.getEmail().toString());
        Map<String, Object> data = new HashMap<>();
        final Map<String, Object> data3 = new HashMap<>();
        final Map<String, Object> us_data = new HashMap<>();
        final Map<String, Object> data1 = new HashMap<>();
        for (int i = 0;i<Questions.size();i++){
            final String count = ""+i;
            data.put(count, Questions.get(i).toString());

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
                                tests.document(name.getText().toString()).collection("answers").document(count).set(data1);
                                tests.document(name.getText().toString()).collection("answers").document(count).update(data3);
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
        tests.document(name.getText().toString()).set(data);
        us.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        us_data.put("name",document.get("name").toString());
                        tests.document(name.getText().toString()).update(us_data);
                        Log.d("LOL", "DocumentSnapshot data: " + document.get("name"));
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





