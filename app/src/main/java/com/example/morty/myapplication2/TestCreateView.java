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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
    private RadioButton rb1,rb2,rb3,rb4;
    private boolean c1,c2,c3,c4;
    private int n;
    public final ArrayList<String> Answers = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_answer);

        a1 = (EditText) findViewById(R.id.answer1);
        a2 = (EditText) findViewById(R.id.answer2);
        a3 = (EditText) findViewById(R.id.answer3);
        a4 = (EditText) findViewById(R.id.answer4);
        rb1 = (RadioButton) findViewById(R.id.check_answer1);
        rb2 = (RadioButton) findViewById(R.id.check_answer2);
        rb3 = (RadioButton) findViewById(R.id.check_answer3);
        rb4 = (RadioButton) findViewById(R.id.check_answer4);
        text = (TextView) findViewById(R.id.question);
        save = (Button) findViewById(R.id.save_btn);
        final Intent intent = getIntent();
        n = intent.getIntExtra("number", 0);
        text.setText(intent.getStringExtra("q_text"));
        rb1.setOnClickListener(radioButtonClickListener);
        rb2.setOnClickListener(radioButtonClickListener);
        rb3.setOnClickListener(radioButtonClickListener);
        rb4.setOnClickListener(radioButtonClickListener);


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (a1.getText().toString().equals("") || a2.getText().toString().equals("") || a3.getText().toString().equals("") || a4.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Введите все варианты ответов", Toast.LENGTH_SHORT).show();
                } else {
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

                    }
                    data.put("is_cor_" + 0, c1);
                    data.put("is_cor_" + 1, c2);
                    data.put("is_cor_" + 2, c3);
                    data.put("is_cor_" + 3, c4);
                    doc.set(data);
                    TestCreateView.this.finish();

                    //TODO else{ //Toast.makeText(TestCreateView.this,"Проверьте вопросы!",Toast.LENGTH_SHORT).show();}
                }
            }

        });
    }

    View.OnClickListener radioButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RadioButton rb = (RadioButton)v;
            switch (rb.getId()) {
                case R.id.check_answer1: click(rb1);
                    break;
                case R.id.check_answer2: click(rb2);
                    break;
                case R.id.check_answer3: click(rb3);
                    break;
                case R.id.check_answer4: click(rb4);
                    break;
            }
        }
    };

    void click(RadioButton clickBox){
        clickBox.toggle();
        if(rb1.equals(clickBox)) c1=true;
        else {
            rb1.setChecked(false);
            c1 = false;
        }
        if(rb2.equals(clickBox)) c2=true;
        else {
            rb2.setChecked(false);
            c2 = false;
        }
        if(rb3.equals(clickBox)) c3=true;
        else {
            rb3.setChecked(false);
            c3 = false;
        }
        if(rb4.equals(clickBox)) c4=true;
        else {
            rb4.setChecked(false);
            c4 = false;
        }
    }
}
