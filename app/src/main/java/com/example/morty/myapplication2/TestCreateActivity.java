package com.example.morty.myapplication2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class TestCreateActivity extends AppCompatActivity {
    public final ArrayList<String> Questions = new ArrayList<>();
    private EditText name,  testPass;
    private FirebaseAuth mAuth;
    private Button q_create, t_create;
    private ProgressBar progressBar;
    final Context context = this;
    private String private_status = "free";
    boolean isChange;
    private String testId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_create);
        q_create = (Button) findViewById(R.id.q_add);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        name = (EditText) findViewById(R.id.name_of_test);
        testPass = findViewById(R.id.test_pass);
        t_create = (Button) findViewById(R.id.t_create);
        final Spinner category = (Spinner) findViewById(R.id.list_tag);
        final ListView questionView = (ListView) findViewById(R.id.test_create_list);

        final String[] TagNames = getResources().getStringArray(R.array.tag_names);

        questionView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(TestCreateActivity.this, TestCreateView.class);
                intent.putExtra("q_text", Questions.get((int) id));
                intent.putExtra("number", (int) id);
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

        RadioGroup radioGroup = findViewById(R.id.private_radiogroup);
        final RadioButton all = findViewById(R.id.all_radio);
        final RadioButton sub = findViewById(R.id.sub_radio);
        final RadioButton pass = findViewById(R.id.private_radio);
        all.setChecked(true);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case -1:
                        break;
                    case R.id.private_radio:
                       testPass.setVisibility(View.VISIBLE);
                       private_status="pass";
                        break;
                    case R.id.sub_radio:
                        testPass.setVisibility(View.GONE);
                        private_status="sub";
                        break;
                    case R.id.all_radio:
                        testPass.setVisibility(View.GONE);
                        private_status="free";
                    default:
                        break;
                }
            }
        });

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

        t_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Введите название теста", Toast.LENGTH_SHORT).show();
                } else {
                    if (questionView.getCount() == 0) {
                        Toast.makeText(getApplicationContext(), "Нельзя создать тест без вопросов", Toast.LENGTH_SHORT).show();
                    } else {
                        if(isChange){
                            //редактирует
                            UpdateTest();
                        }else {
                            //создает
                            CreateTest();
                        }
                        revertDraft();
                        Intent intent = new Intent(TestCreateActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });

        testId = getIntent().getStringExtra("Test_id");
        if(testId!=null){
            Log.d("testCreate", "isChange");
            Log.d("testCreate", "test id: " + testId);
            isChange = true;
            mAuth = FirebaseAuth.getInstance();
            final FirebaseUser cus = mAuth.getCurrentUser();
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            final DocumentReference test = db.collection("tests").document(String.valueOf(testId));
            test.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("LOL", "DocumentSnapshot data: " + document.getData());

                            for (int i = 0; i < TagNames.length; i++) {
                                if(TagNames[i].equals(document.get("category"))) category.setSelection(i);
                            }

                            switch (document.getString("private_status")){
                                case "free":
                                    all.setChecked(true);
                                    private_status = "free";
                                    testPass.setVisibility(View.GONE);
                                    break;

                                case "pass":
                                    pass.setChecked(true);
                                    private_status = "pass";
                                    testPass.setVisibility(View.VISIBLE);
                                    testPass.setText(document.getString("test_pass"));
                                    break;

                                case "sub":
                                    sub.setChecked(true);
                                    private_status = "sub";
                                    testPass.setVisibility(View.GONE);
                                    break;
                            }
                            for (int i = 0; i < document.getLong("q_count"); i++) {
                                Questions.add(document.getString(String.valueOf(i)));
                            }

                            final CollectionReference a_draft = db.collection("users").document(cus.getEmail().toString()).collection("tests").document("draft").collection("answers");
                            final CollectionReference answers = test.collection("answers");
                            answers.get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                                    Log.d("LOL", doc.getId() + " => " + doc.getData());
                                                    HashMap<String,Object> map = new HashMap<>();
                                                    for (int i = 0; i < 4; i++) {
                                                        map.put(String.valueOf(i), doc.get(String.valueOf(i)));
                                                        map.put("is_cor_" + i,doc.get("is_cor_" + i));
                                                    }
                                                    a_draft.document(doc.getId()).set(map);

                                                }
                                            } else {
                                                Log.d("LOL", "Error getting documents: ", task.getException());
                                            }
                                        }
                                    });
                            name.setText(document.getString("test_name"));
                        } else {
                            Log.d("LOL", "No such document");
                        }
                    } else {
                        Log.d("LOL", "get failed with ", task.getException());
                    }
                }
            });
        }

        if(isChange){
            t_create.setText("обновить тест");
            q_create.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Нельзя создавать вопросы во время редактирования", Toast.LENGTH_SHORT).show();
                }
            });
        }else {
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
                                        public void onClick(DialogInterface dialog, int id) {
                                            //Вводим текст и отображаем в строке ввода на основном экране:
                                            if (userInput.getText().toString().equals("")) {
                                                Toast.makeText(getApplicationContext(), "Нельзя создать пустой вопрос", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Questions.add(userInput.getText().toString());
                                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                                                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                                                Intent intent = new Intent(TestCreateActivity.this, TestCreateView.class);
                                                intent.putExtra("q_text", userInput.getText().toString());
                                                intent.putExtra("number", Questions.size()-1);
                                                Log.e("NUMBER", String.valueOf(Questions.size()-1));
                                                startActivity(intent);
                                            }
                                        }
                                    })
                            .setNegativeButton("Отмена",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
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
        }

    }

    @Override
    public void onBackPressed() {
        revertDraft();
        super.onBackPressed();
    }

    private void UpdateTest(){
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser cus = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final CollectionReference a_draft = db.collection("users").document(cus.getEmail()).collection("tests").document("draft").collection("answers");
        final CollectionReference tests = db.collection("tests");

        final Map<String, Object> data = new HashMap<>();
        final Map<String, Object> test_inf = new HashMap<>();
        final Map<String, Object> data3 = new HashMap<>();
        final Map<String, Object> data1 = new HashMap<>();

        final Spinner category = (Spinner) findViewById(R.id.list_tag);

        data.put("category",category.getSelectedItem().toString());
        test_inf.put("private_status",private_status);
        if (testPass.getText()!=null){
            test_inf.put("test_pass",testPass.getText().toString());
        }
        test_inf.put("test_name",name.getText().toString());

        final DocumentReference test = tests.document(String.valueOf(testId));
        test.update(test_inf);

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
                                test.collection("answers").document(count).update(data1);
                                test.collection("answers").document(count).update(data3);
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

        test.update(data);

    }

    private void CreateTest(){
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser cus = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

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

        final Spinner category = (Spinner) findViewById(R.id.list_tag);


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
                        data.put("category",category.getSelectedItem().toString());
                        test_inf.put("solved_cnt",0);
                        test_inf.put("private_status",private_status);
                        if (testPass.getText()!=null){
                            test_inf.put("test_pass",testPass.getText().toString());
                        }
                        test_inf.put("test_name",name.getText().toString());
                        test_inf.put("test_maker_email",cus.getEmail());
                        ArrayList<Long> deletedId = (ArrayList<Long>) document.get("deletedId");
                        if(deletedId.size()!=0){
                            id_inf.put("last_id",(long)deletedId.get(0));
                            deletedId.remove(0);
                            HashMap<String,Object> deleted = new HashMap<>();
                            deleted.put("deletedId",deletedId);
                            other_tests.update(deleted);
                        }else {
                            id_inf.put("last_id", (long) document.get("last_id"));
                            other_tests.update(id_inf);
                        }

                        tests.document(id_inf.get("last_id").toString()).set(test_inf);
                        Log.d("LOL", "DocumentSnapshot data: " + document.get("last_id")+id_inf.get("test_id"));

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
}