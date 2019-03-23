package com.example.morty.myapplication2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.expansionpanel.ExpansionLayout;

import java.util.ArrayList;


public class TestCreateActivity extends AppCompatActivity{

    private TextView mTextMessage;
    private Button q_create;
    private ProgressBar progressBar;
    final Context context = this;
    public final ArrayList<String> Questions = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_create);
        mTextMessage = (TextView) findViewById(R.id.tag_name);
        q_create = (Button)findViewById(R.id.q_add);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        ListView listView = (ListView)findViewById(R.id.list_tag);
        ListView questionView = (ListView)findViewById(R.id.test_create_list);

       final String[] TagNames = getResources().getStringArray(R.array.tag_names);

       final ExpansionLayout expansionLayout = findViewById(R.id.expansionLayout);

       questionView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

               Intent intent = new Intent(TestCreateActivity.this,TestCreateView.class);
               intent.putExtra("q_text",Questions.get((int)id));
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



    }



}
