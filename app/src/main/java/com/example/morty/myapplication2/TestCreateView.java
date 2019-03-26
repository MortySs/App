package com.example.morty.myapplication2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TestCreateView extends AppCompatActivity {
    private EditText a1,a2,a3,a4,c_a;
    private Button save;
    private TextView text;
    final Context context = this;
    private FirebaseAuth mAuth;
    private int n;
    public final ArrayList<String> Answers = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_answer);

        a1 = (EditText) findViewById(R.id.answer1);
        a2 = (EditText)findViewById(R.id.answer2);
        a3 = (EditText)findViewById(R.id.answer3);
        a4 = (EditText)findViewById(R.id.answer4);
        text = (TextView)findViewById(R.id.question);
        save = (Button)findViewById(R.id.save_btn);
        final Intent intent = getIntent();
        n=intent.getIntExtra("number",0);
        text.setText(intent.getStringExtra("q_text"));




        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (a1.getText().toString().equals("") || a2.getText().toString().equals("") || a3.getText().toString().equals("") || a4.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Введите все варианты ответов", Toast.LENGTH_SHORT).show();
                }else {
                    Answers.add(a1.getText().toString());
                    Answers.add(a2.getText().toString());
                    Answers.add(a3.getText().toString());
                    Answers.add(a4.getText().toString());
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    mAuth = FirebaseAuth.getInstance();
                    final FirebaseUser cus = mAuth.getCurrentUser();
                    final CollectionReference a_draft = db.collection("users").document(cus.getEmail().toString()).collection("tests").document("draft").collection("answers");
                    DocumentReference doc = a_draft.document("" + n);
                    DocumentReference draft = db.collection("users").document(cus.getEmail().toString()).collection("tests").document("draft");
                    Map<String, Object> data2 = new HashMap<>();
                    data2.put("" + n, intent.getStringExtra("q_text"));
                    draft.update(data2);
                    Map<String, Object> data = new HashMap<>();
                    for (int i = 0; i < 4; i++) {
                        String count = "" + i;
                        data.put(count, Answers.get(i).toString());
                        doc.set(data);
                    }

                    TestCreateView.this.finish();
                }
            }


        });
    }

}
