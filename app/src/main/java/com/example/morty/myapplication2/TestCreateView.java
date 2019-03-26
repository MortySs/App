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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TestCreateView extends AppCompatActivity {
    private Button a1,a2,a3,a4,c_a,save;
    private TextView text;
    final Context context = this;
    private FirebaseAuth mAuth;
    private int n;
    public final ArrayList<String> Answers = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_create_view);

        a1 = (Button)findViewById(R.id.a_e_button);
        a2 = (Button)findViewById(R.id.a_e_button2);
        a3 = (Button)findViewById(R.id.a_e_button3);
        a4 = (Button)findViewById(R.id.a_e_button4);
        c_a = (Button)findViewById(R.id.correct_a_choose);
        text = (TextView)findViewById(R.id.q_e_text);
        save = (Button)findViewById(R.id.save_btn);
        final Intent intent = getIntent();
        n=intent.getIntExtra("number",0);
        text.setText(intent.getStringExtra("q_text"));
        a1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.prompt, null);
                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(context);
                mDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView.findViewById(R.id.input_text);
                mDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        a1.setText(userInput.getText().toString());
                                        Answers.add(userInput.getText().toString());
                                    }
                                })
                        .setNegativeButton("Отмена",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                AlertDialog alertDialog = mDialogBuilder.create();
                alertDialog.show();
            }
        });
        a2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.prompt, null);
                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(context);
                mDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView.findViewById(R.id.input_text);
                mDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        a2.setText(userInput.getText().toString());
                                        Answers.add(userInput.getText().toString());
                                    }
                                })
                        .setNegativeButton("Отмена",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                AlertDialog alertDialog = mDialogBuilder.create();
                alertDialog.show();
            }
        });
        a3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.prompt, null);
                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(context);
                mDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView.findViewById(R.id.input_text);
                mDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        a3.setText(userInput.getText().toString());
                                        Answers.add(userInput.getText().toString());
                                    }
                                })
                        .setNegativeButton("Отмена",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                AlertDialog alertDialog = mDialogBuilder.create();
                alertDialog.show();
            }
        });
        a4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.prompt, null);
                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(context);
                mDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView.findViewById(R.id.input_text);
                mDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                       a4.setText(userInput.getText().toString());
                                        Answers.add(userInput.getText().toString());
                                    }
                                })
                        .setNegativeButton("Отмена",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                AlertDialog alertDialog = mDialogBuilder.create();
                alertDialog.show();
            }
        });
        c_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                mAuth = FirebaseAuth.getInstance();
                final FirebaseUser cus = mAuth.getCurrentUser();
                final CollectionReference a_draft = db.collection("users").document(cus.getEmail().toString()).collection("tests").document("draft").collection("answers");
                DocumentReference doc = a_draft.document(""+n);
                DocumentReference draft = db.collection("users").document(cus.getEmail().toString()).collection("tests").document("draft");
                Map<String, Object> data2 = new HashMap<>();
                data2.put(""+n,intent.getStringExtra("q_text"));
                draft.update(data2);
                Map<String, Object> data = new HashMap<>();
                for (int i = 0;i<4;i++){
                    String count = ""+i;
                    data.put(count, Answers.get(i).toString());
                    doc.set(data);
                }
                TestCreateView.this.finish();
            }



        });
    }

}
