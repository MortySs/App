package com.example.morty.myapplication2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;

public class ParsingActivity extends AppCompatActivity {
private Button find_btn;
private TextView file_inf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parsing);
        find_btn = (Button) findViewById(R.id.find_file_btn);
        file_inf = (TextView) findViewById(R.id.parsing_file_inf);
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

                    //TODO здесь получаем всю инфу о файле
                    file_inf.setText("имя файла: "+file.getName()+"\n"+"путь к файлу: "+file.getAbsolutePath());
                    Log.d("file", "file selected name: "+file.getName()+" | file selected path" +file.getAbsolutePath());
                }}).showDialog();

        }
    });
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
                            "Для парсинга дайте разрешение на доступ к памяти телефона", Toast.LENGTH_LONG);
                    toast.show();
                    Intent intent = new Intent(ParsingActivity.this,MainActivity.class);
                    startActivity(intent);
                }
                return;
        }
    }

}
