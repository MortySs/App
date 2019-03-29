package com.example.morty.myapplication2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class test_end extends AppCompatActivity {
    private Button btn_end;
    private TextView txt;
    private int c_a_c;
    private long q_count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_end);
        txt = findViewById(R.id.test_result_txt);
        btn_end = findViewById(R.id.test_end_btn);

        final Intent intent = getIntent();
        c_a_c = intent.getIntExtra("c_a_c", 0);
        q_count=intent.getLongExtra("q_count",0) ;//("q_count",0);

        txt.setText("Вы решили "+c_a_c+" / "+q_count);

        btn_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(test_end.this,MainActivity.class);
                startActivity(intent);

            }
        });
    }
}
