package com.example.morty.myapplication2;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class test_view extends AppCompatActivity {
    private Button a1,a2,a3,a4;
    long minute, seconds, testTime;
    private final ArrayList<String> cor_a = new ArrayList<>();
    private FirebaseAuth mAuth;
    private long q_count,i;
    private TextView question,curQuestCount, time;
    private FrameLayout f1,f2,f3,f4;
    LinkedHashMap<Button,FrameLayout> btnAndBackground = new LinkedHashMap<>();
    private int c_a_c; // кол-во правильных ответов, на которые отвечал пользователь

    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_view);

        i=0; // индекс вопроса, на который отвечает пользователь
        c_a_c=0;
        a1 = (Button)findViewById(R.id.a_button);
        a2 = (Button)findViewById(R.id.a_button2);
        a3 = (Button)findViewById(R.id.a_button3);
        a4 = (Button)findViewById(R.id.a_button4);
        time = (TextView) findViewById(R.id.time_test);
        question = (TextView)findViewById(R.id.q_text);
        curQuestCount = (TextView)findViewById(R.id.cur_q);
        f1 = (FrameLayout)findViewById(R.id.b1_back);
        f2 = (FrameLayout)findViewById(R.id.b2_back);
        f3 = (FrameLayout)findViewById(R.id.b3_back);
        f4 = (FrameLayout)findViewById(R.id.b4_back);
        btnAndBackground.put(a1,f1);
        btnAndBackground.put(a2,f2);
        btnAndBackground.put(a3,f3);
        btnAndBackground.put(a4,f4);
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser cus = mAuth.getCurrentUser();

        startTimerThread();

        Upd();

        a1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click(a1);
            }
        });

        a2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click(a2);
            }
        });

        a3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click(a3);
            }
        });

        a4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click(a4);
            }
        });
    }

    private void startTimerThread() {
        Thread th = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            testTime += 1;
                            minute = testTime / 60;
                            seconds = testTime % 60;
                            time.setText(minute + " : " + seconds);
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        th.start();
    }

    private void click(Button clickBtn){
        clickable(false);
        defaultBackgroundColor();
        if(i!=q_count){
            i++;
                if ((cor_a.get(0).equals("true") && btnAndBackground.get(clickBtn).equals(f1)) || (cor_a.get(1).equals("true") && btnAndBackground.get(clickBtn).equals(f2))
                 || (cor_a.get(2).equals("true") && btnAndBackground.get(clickBtn).equals(f3)) || (cor_a.get(3).equals("true") && btnAndBackground.get(clickBtn).equals(f4))){
                    c_a_c++;
                    btnAndBackground.get(clickBtn).setBackgroundColor(ContextCompat.getColor(test_view.this,R.color.greenColor));
                }else {
                    if (cor_a.get(0).equals("true") && !(btnAndBackground.get(clickBtn).equals(f1))) {
                        f1.setBackgroundColor(ContextCompat.getColor(test_view.this, R.color.greenColor));
                    }
                    if (cor_a.get(1).equals("true") && !(btnAndBackground.get(clickBtn).equals(f2))) {
                        f2.setBackgroundColor(ContextCompat.getColor(test_view.this, R.color.greenColor));
                    }
                    if (cor_a.get(2).equals("true") && !(btnAndBackground.get(clickBtn).equals(f3))) {
                        f3.setBackgroundColor(ContextCompat.getColor(test_view.this, R.color.greenColor));
                    }
                    if (cor_a.get(3).equals("true") && !(btnAndBackground.get(clickBtn).equals(f4))) {
                        f4.setBackgroundColor(ContextCompat.getColor(test_view.this, R.color.greenColor));
                    }
                    btnAndBackground.get(clickBtn).setBackgroundColor(ContextCompat.getColor(test_view.this, R.color.redColor));
                }
            }
            if(i<q_count) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cor_a.clear();
                        Upd();
                    }
                }, 2000);
            }else {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cor_a.clear();
                    }
                }, 2000);
            }
        if(i==q_count){
            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    clickable(true);
                    final Intent intent2 = getIntent();
                    final String Test_id = intent2.getStringExtra("Test_id");
                    Intent intent = new Intent(test_view.this,test_end.class);
                    intent.putExtra("c_a_c",c_a_c);
                    intent.putExtra("q_count",q_count);
                    intent.putExtra("Test_id",Test_id);
                    intent.putExtra("minute", minute);
                    intent.putExtra("seconds", seconds);
                    startActivity(intent);
                }
            },2000);
        }
    }

    private void Upd(){
        final Intent intent = getIntent();
        String Test_id = intent.getStringExtra("Test_id");
        final DocumentReference tests = db.collection("tests").document(Test_id);
        final CollectionReference test_a = db.collection("tests").document(Test_id).collection("answers");
        tests.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        q_count=(long) document.get("q_count");
                        curQuestCount.setText("Вопрос "+(i+1)+"/"+q_count);
                        question.setText(document.get(""+i).toString());
                        Log.d("LOL", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("LOL", "No such document");
                    }
                } else {
                    Log.d("LOL", "get failed with ", task.getException());
                }
            }
        });

        DocumentReference a = test_a.document(""+i);
        a.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        defaultBackgroundColor();
                        a1.setText(document.get("0").toString());
                        a2.setText(document.get("1").toString());
                        a3.setText(document.get("2").toString());
                        a4.setText(document.get("3").toString());

                        cor_a.add(0,document.get("is_cor_0").toString());
                        cor_a.add(1,document.get("is_cor_1").toString());
                        cor_a.add(2,document.get("is_cor_2").toString());
                        cor_a.add(3,document.get("is_cor_3").toString());

                        Log.d("LOL", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("LOL", "No such document");
                    }
                } else {
                    Log.d("LOL", "get failed with ", task.getException());
                }
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                clickable(true);
            }
        }, 1000);

    }

    private void clickable(boolean bool){
        a1.setClickable(bool);
        a2.setClickable(bool);
        a3.setClickable(bool);
        a4.setClickable(bool);
    }

    private void defaultBackgroundColor(){
        f1.setBackgroundColor(0);
        f2.setBackgroundColor(0);
        f3.setBackgroundColor(0);
        f4.setBackgroundColor(0);}

}
