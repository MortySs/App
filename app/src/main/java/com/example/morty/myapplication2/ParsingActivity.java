package com.example.morty.myapplication2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ParsingActivity extends AppCompatActivity {
    String[] answers = new String[4];
    Boolean[] rightAnswers = new Boolean[4];
    final ArrayList<String[]> Answers = new ArrayList<>();
    final ArrayList<Boolean[]> RightAnswers = new ArrayList<>();
    final ArrayList<String> Questions = new ArrayList<>();
    String name,category,private_status,test_pass;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private Button find_btn,create_btn;
    private TextView file_inf;
    int n;
    boolean isTxt, categoryIsRight, testIsCorrectly;

    void parse(String filePath) {
        Log.d("filepath", filePath);
        if (!(filePath.endsWith(".txt"))){
            Toast.makeText(getApplicationContext(), "Файл должен иметь txt формат",Toast.LENGTH_SHORT).show();
            isTxt = false;
        }else {
            isTxt = true;
            String[] words;
            String[] categories = getResources().getStringArray(R.array.tag_names);
            try {
                FileInputStream fstream = new FileInputStream(filePath);
                Scanner sc = new Scanner(fstream, "UTF-8");
                String strLine;
                name = sc.nextLine();
                category = sc.nextLine();
                private_status = sc.nextLine();
                if(private_status.equals("pass")){
                    test_pass = sc.nextLine();
                }
                for (int i = 0; i < categories.length; i++) {
                    if(category.equalsIgnoreCase(categories[i])){
                        category = categories[i];
                        categoryIsRight = true;
                    }
                }
                if(!categoryIsRight){
                    Toast.makeText(getApplicationContext(), "Проверьте категорию", Toast.LENGTH_SHORT).show();
                }else {
                    Log.d("parsing tests", "parse name: " + name);
                    while (sc.hasNext()) {
                        strLine = sc.nextLine();
                        words = strLine.split(";");
                        if (words.length == 6 && (words[5].equals("1")
                                || words[5].equals("2") || words[5].equals("3") || words[5].equals("4"))) {
                            testIsCorrectly = true;
                            Questions.add(words[0]);
                            Log.d("parsing tests", "parse question: " + words[0]);
                            answers[0] = words[1];
                            Log.d("parsing tests", "parse answer [1]: " + words[1]);
                            answers[1] = words[2];
                            Log.d("parsing tests", "parse answer [2]: " + words[2]);
                            answers[2] = words[3];
                            Log.d("parsing tests", "parse answer [3]: " + words[3]);
                            answers[3] = words[4];
                            Log.d("parsing tests", "parse answer [4]: " + words[4]);
                            Answers.add(answers);

                            for (int i = 0; i < 4; i++) {
                                if (words[5].equals(String.valueOf(i + 1))) {
                                    rightAnswers[i] = true;
                                } else rightAnswers[i] = false;
                            }

                            RightAnswers.add(rightAnswers);
                            save();
                            n++;
                        } else {
                            Toast.makeText(getApplicationContext(), "Проверьте файл", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            } catch (IOException e) {
                Log.e("Parse", e.getMessage());
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parsing);
        find_btn = findViewById(R.id.find_file_btn);
        file_inf =  findViewById(R.id.parsing_file_inf);
        create_btn = findViewById(R.id.create_parsed);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);

        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    10);
        }

        find_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FileChooser(ParsingActivity.this)
                        .setFileListener(new FileChooser.FileSelectedListener() {
                            @Override public void fileSelected(final File file) {
                                parse(file.getAbsolutePath());
                                file_inf.setText("имя файла: "+file.getName()+"\n"+"путь к файлу: "+file.getAbsolutePath());
                                Log.d("parsfile", "file selected name: "+file.getName()+" | file selected path" +file.getAbsolutePath() +" /// " );
                            }}).showDialog();

            }
            });
        create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isTxt == true) {
                    if(categoryIsRight){
                        if(testIsCorrectly) {
                            progressBar.setVisibility(View.VISIBLE);
                            Upd_test();
                            revertDraft();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                    Intent intent = new Intent(ParsingActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            }, 2000);

                        }else Toast.makeText(getApplicationContext(), "Проверьте файл", Toast.LENGTH_SHORT).show();
                    }else Toast.makeText(getApplicationContext(), "Проверьте категорию", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(getApplicationContext(), "Файл должен иметь txt формат",Toast.LENGTH_SHORT).show();
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
        final DocumentReference user = db.collection("users").document(cus.getEmail());

        final Map<String, Object> data = new HashMap<>();
        final Map<String, Object> id_inf = new HashMap<>();
        final Map<String, Object> test_inf = new HashMap<>();
        final Map<String, Object> data3 = new HashMap<>();
        final Map<String, Object> us_data = new HashMap<>();
        final Map<String, Object> data1 = new HashMap<>();


        other_tests.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        Long testsCnt = (Long)document.get("tests_count");
                                        HashMap<String,Object> tests_count = new HashMap<>();
                                        if (testsCnt != null){
                                            tests_count.put("tests_count", testsCnt + 1);
                                        }else{
                                            tests_count.put("tests_count", 1);
                                        }
                                        user.update(tests_count);
                                        Log.d("testsCount", "DocumentSnapshot data: " + document.getData());
                                    } else {
                                        Log.d("testsCount", "No such document");
                                    }
                                } else {
                                    Log.d("testsCount", "get failed with ", task.getException());
                                }
                            }
                        });
                        data.put("category",category);
                        id_inf.put("last_id",(long)document.get("last_id")+1);
                        test_inf.put("solved_cnt",0);
                        test_inf.put("test_name",name);
                        test_inf.put("private_status",private_status);
                        createdTests.document(id_inf.get("last_id").toString()).set(test_inf);

                        test_inf.put("test_maker_email",cus.getEmail());
                        Log.d("LOL", "DocumentSnapshot data: " + document.get("last_id")+id_inf.get("test_id"));
                        other_tests.update(id_inf);
                        tests.document(id_inf.get("last_id").toString()).set(test_inf);
                        for (int i = 0;i<Questions.size();i++){
                            final String count = ""+i;
                            data.put(count, Questions.get(i));

                            for (int j = 0;j<4;j++){
                                final int k = j;
                                DocumentReference a = a_draft.document(""+(i));
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
                        us.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        us_data.put("name",document.get("name").toString());
                                        tests.document(id_inf.get("last_id").toString()).update(us_data);
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

    private void revertDraft(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final Map<String, Object> data3_draft = new HashMap<>();
                final Map<String, Object> data1_draft = new HashMap<>();
                mAuth = FirebaseAuth.getInstance();
                final FirebaseUser cus = mAuth.getCurrentUser();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                final CollectionReference a_draft = db.collection("users").document(cus.getEmail()).collection("tests").document("draft").collection("answers");
                for (int i = 0;i<Questions.size();i++) {
                    DocumentReference a = a_draft.document("" + (i));
                    data1_draft.put("0", "");
                    data1_draft.put("1", "");
                    data1_draft.put("2", "");
                    data1_draft.put("3", "");
                    data3_draft.put("is_cor_0", false);
                    data3_draft.put("is_cor_1", false);
                    data3_draft.put("is_cor_2", false);
                    data3_draft.put("is_cor_3", false);
                    a.update(data1_draft);
                    a.update(data3_draft);
                }
            }
        }, 2000);
    }
    void save(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser cus = mAuth.getCurrentUser();
        final CollectionReference a_draft = db.collection("users").document(cus.getEmail().toString()).collection("tests").document("draft").collection("answers");
        DocumentReference doc = a_draft.document("" + n);
        DocumentReference draft = db.collection("users").document(cus.getEmail().toString()).collection("tests").document("draft");
        Map<String, Object> data2 = new HashMap<>();
        data2.put("" + n, Questions.get(n));
        Log.d("parsing tests", "вопрос: "+ Questions.get(n));
        draft.update(data2);
        Map<String, Object> data = new HashMap<>();
        for (int i = 0; i < 4; i++) {
            String count = "" + i;

            data.put(count, answers[i].toString());
        }
        data.put("is_cor_" + 0, rightAnswers[0]);
        data.put("is_cor_" + 1, rightAnswers[1]);
        data.put("is_cor_" + 2, rightAnswers[2]);
        data.put("is_cor_" + 3, rightAnswers[3]);
        doc.set(data);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
// permission granted
                    new FileChooser(this).setFileListener(new FileChooser.FileSelectedListener() {
                        @Override public void fileSelected(final File file) {
// do something with the file
                        }}).showDialog();

                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Для парсинга предоставьте разрешение на доступ к памяти телефона", Toast.LENGTH_LONG);
                    toast.show();
                    Intent intent = new Intent(ParsingActivity.this,MainActivity.class);
                    startActivity(intent);
                }
                return;
        }
    }

}