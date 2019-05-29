package com.example.morty.myapplication2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class User_profile extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private TextView uzname_field;
    private TextView uzemail_field,avatarUri;
    private ImageView Avatar;
    private Button logout_btn, avatarChoose_btn, solvedTestsWatch_btn;
    private ProgressBar progressBar;
    private HashMap<String,Object> map;
    String email;
    Intent intent;
    boolean isCus;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<String> CurrentSubscribers = new ArrayList<>(), CurrentSubscriptions = new ArrayList<>(),
            OtherSubscribers = new ArrayList<>(), OtherSubscriptions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReference();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    10);
        }

        swipeRefreshLayout = findViewById(R.id.swipe_user);
        progressBar = findViewById(R.id.progressBar);
        uzname_field = findViewById(R.id.uzname_field);
        uzemail_field = findViewById(R.id.uz_email);
        Avatar = findViewById(R.id.uz_avatar);
        logout_btn = findViewById(R.id.LogOut);
        avatarChoose_btn = findViewById(R.id.av_ch);
        solvedTestsWatch_btn = findViewById(R.id.solved_watch);
        avatarUri = findViewById(R.id.av_url);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        final FirebaseUser cus = mAuth.getCurrentUser();
        intent = getIntent();
        String stringIntent;
        if(intent.getStringExtra("Email") != null) {
            stringIntent = intent.getStringExtra("Email");
        } else {
            stringIntent = "";
        }

        //узнаем это CurrentUser или нет
        if (stringIntent.equals(cus.getEmail()) || stringIntent.equals("")){
            isCus = true;
            email = cus.getEmail();
            logout_btn.setText("Выйти из аккаунта");
            avatarChoose_btn.setText("Выбрать аватарку");
        } else{
            isCus = false;
            email = stringIntent;
            avatarChoose_btn.setText("Посмотреть тесты");
            solvedTestsWatch_btn.setVisibility(View.VISIBLE);
        }

        final DocumentReference CurrentUser = db.collection("users").document(cus.getEmail());
        final DocumentReference OtherUser = db.collection("users").document(email);

        CurrentUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("getFriends", "DocumentSnapshot data: " + document.getData());
                        CurrentSubscriptions = (ArrayList<String>) document.get("subscriptions");
                        //узнаем подписаны ли мы на него(неё)
                        if(!isCus && CurrentSubscriptions.contains(email)){
                            logout_btn.setText("Отписаться");
                        } else if(!isCus) logout_btn.setText("Подписаться");

                    } else {
                        Log.d("getFriends", "No such document");
                    }
                } else {
                    Log.d("getFriends", "get failed with ", task.getException());
                }
            }
        });

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isCus) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(User_profile.this, MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(User_profile.this, "Вы успешно вышли из аккаунта", Toast.LENGTH_SHORT).show();
                }else if (logout_btn.getText().equals("Отписаться")){
                    logout_btn.setText("Подписаться");
                    CurrentUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Log.d("getFriends", "DocumentSnapshot data: " + document.getData());
                                    CurrentSubscriptions = (ArrayList<String>) document.get("subscriptions");
                                    CurrentSubscriptions.remove(email);
                                    HashMap<String, Object> Cmap = new HashMap<>();
                                    Cmap.put("subscriptions", CurrentSubscriptions);
                                    CurrentUser.update(Cmap);

                                } else {
                                    Log.d("getFriends", "No such document");
                                }
                            } else {
                                Log.d("getFriends", "get failed with ", task.getException());
                            }
                        }
                    });

                    OtherUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Log.d("getFriends", "DocumentSnapshot data: " + document.getData());
                                    OtherSubscribers = (ArrayList<String>) document.get("subscribers");
                                    OtherSubscribers.remove(cus.getEmail());
                                    HashMap<String, Object> Omap = new HashMap<>();
                                    Omap.put("subscribers", OtherSubscribers);
                                    OtherUser.update(Omap);
                                } else {
                                    Log.d("getFriends", "No such document");
                                }
                            } else {
                                Log.d("getFriends", "get failed with ", task.getException());
                            }
                        }
                    });

                } else {
                    logout_btn.setText("Отписаться");
                    CurrentUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Log.d("getFriends", "DocumentSnapshot data: " + document.getData());
                                    CurrentSubscriptions = (ArrayList<String>) document.get("subscriptions");
                                    CurrentSubscriptions.add(email);
                                    HashMap<String, Object> Cmap = new HashMap<>();
                                    Cmap.put("subscriptions", CurrentSubscriptions);
                                    CurrentUser.update(Cmap);

                                } else {
                                    Log.d("getFriends", "No such document");
                                }
                            } else {
                                Log.d("getFriends", "get failed with ", task.getException());
                            }
                        }
                    });

                    OtherUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Log.d("getFriends", "DocumentSnapshot data: " + document.getData());
                                    OtherSubscribers = (ArrayList<String>) document.get("subscribers");
                                    OtherSubscribers.add(cus.getEmail());
                                    HashMap<String, Object> Omap = new HashMap<>();
                                    Omap.put("subscribers", OtherSubscribers);
                                    OtherUser.update(Omap);
                                } else {
                                    Log.d("getFriends", "No such document");
                                }
                            } else {
                                Log.d("getFriends", "get failed with ", task.getException());
                            }
                        }
                    });
                }
            }
        });

        solvedTestsWatch_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(User_profile.this, SolvedTestsActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });

        progressBar.setVisibility(View.VISIBLE);

        final DocumentReference user = db.collection("users").document(email);

        user.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("", "Listen failed.", e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                        if (snapshot.get("avatar_link")!=null) {
                            storageRef.child(snapshot.get("avatar_link").toString())
                                    .getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Picasso.get().load(uri).into(Avatar);
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        } else {
                            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/leotest-2k1n.appspot.com/o/Default%2Favatar_pic.png?alt=media&token=0a264da6-7d1b-44cd-aaee-9230bd2d0b2d").into(Avatar);
                            progressBar.setVisibility(View.GONE);
                        }
                    if (snapshot.get("name")!=null) {
                        uzname_field.setText(snapshot.get("name").toString());
                    }else{
                        uzname_field.setText("Ошибка загрузки данных");
                    }
                    uzemail_field.setText(email);
                    Log.d("", "Current data: " + snapshot.getData());
                } else {
                    Log.d("", "Current data: null");
                }
            }
        });


        avatarChoose_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isCus) {
                    new FileChooser(User_profile.this).setFileListener(new FileChooser.FileSelectedListener() {
                         @Override
                         public void fileSelected(final File file) {
                             if (file.getAbsolutePath().endsWith(".png") || file.getAbsolutePath().endsWith(".jpg")) {
                                 Uri file2 = Uri.fromFile(new File(file.getAbsolutePath()));
                                 StorageReference riversRef = storageRef.child("users_avatars/" + email + "/" + "avatar");
                                 UploadTask uploadTask = riversRef.putFile(file2);

                                 // Register observers to listen for when the download is done or if it fails
                                 uploadTask.addOnFailureListener(new OnFailureListener() {
                                     @Override
                                     public void onFailure(@NonNull Exception exception) {
                                         // Handle unsuccessful uploads
                                     }
                                 }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                     @Override
                                     public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                                         Log.d("avatar", "onSuccess: " + taskSnapshot.getMetadata().getPath());
                                         Handler handler = new Handler();
                                         handler.postDelayed(new Runnable() {
                                             @Override
                                             public void run() {
                                                 map = new HashMap<>();
                                                 map.put("avatar_link", taskSnapshot.getMetadata().getPath());
                                                 user.update(map);

                                                 // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                                 // ...
                                             }
                                         }, 5000);
                                     }
                                 });
                             } else
                                 Toast.makeText(User_profile.this, "Неверный формат файла!", Toast.LENGTH_LONG).show();

                             avatarUri.setText(file.getAbsolutePath());
                             Log.d("parsfile", "file selected name: " + file.getName() + " | file selected path" + file.getAbsolutePath() + " /// ");
                         }
                    }).showDialog();
                } else {
                    Intent intent = new Intent(User_profile.this, MyTestsActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final DocumentReference user = db.collection("users").document(email);
        final StorageReference storageRef = storage.getReference();
            user.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot,
                                    @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w("", "Listen failed.", e);
                        return;
                    }
                    if (snapshot != null && snapshot.exists()) {
                        if (snapshot.get("avatar_link")!=null) {
                            storageRef.child(snapshot.get("avatar_link").toString())
                                    .getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Picasso.get().load(uri).into(Avatar);
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    });
                        } else {
                            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/leotest-2k1n.appspot.com/o/Default%2Favatar_pic.png?alt=media&token=0a264da6-7d1b-44cd-aaee-9230bd2d0b2d").into(Avatar);
                            progressBar.setVisibility(View.GONE);
                        }
                        if (snapshot.get("name")!=null) {
                            uzname_field.setText(snapshot.get("name").toString());
                        }else{
                            uzname_field.setText("Ошибка загрузки данных");
                        }
                        uzemail_field.setText(email);
                        Log.d("", "Current data: " + snapshot.getData());
                    } else {
                        Log.d("", "Current data: null");
                    }
                }
            });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
// permission granted
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Для загрузки аватара предоставьте разрешение на доступ к памяти телефона", Toast.LENGTH_LONG);
                    toast.show();
                    Intent intent = new Intent(User_profile.this,MainActivity.class);
                    startActivity(intent);
                }
                return;
        }
    }
}
