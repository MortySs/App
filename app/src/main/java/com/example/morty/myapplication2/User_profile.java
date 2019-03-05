package com.example.morty.myapplication2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.squareup.picasso.Picasso;

public class User_profile extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView mTextMessage;
    private TextView mTextMessage2;
    private ImageView Avatar;
    private Button btn;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:

                    return true;
                case R.id.navigation_dashboard:

                    return true;
                case R.id.navigation_notifications:

                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReference();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

<<<<<<< HEAD:app/src/main/java/com/example/morty/myapplication2/User_profile.java
        //   Uri file = Uri.fromFile(new File());
        //   StorageReference riversRef = storageRef.child("Default/"+file.getLastPathSegment());
        //   UploadTask uploadTask = riversRef.putFile(file);
// //gister observers to listen for when the download is done or if it fails
        //   uploadTask.addOnFailureListener(new OnFailureListener() {
        //       @Override
        //       public void onFailure(@NonNull Exception exception) {
        //          Log.d("","PROBLEM");
        //       }
        //   }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
        //       @Override
        //       public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
        //           // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
        //           // ...
        //       }
        //   });
   //   Uri file = Uri.fromFile(new File());
   //   StorageReference riversRef = storageRef.child("Default/"+file.getLastPathSegment());
   //   UploadTask uploadTask = riversRef.putFile(file);
// //gister observers to listen for when the download is done or if it fails
   //   uploadTask.addOnFailureListener(new OnFailureListener() {
   //       @Override
   //       public void onFailure(@NonNull Exception exception) {
   //          Log.d("","PROBLEM");
   //       }
   //   }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
   //       @Override
   //       public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
   //           // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
   //           // ...
   //       }
   //   });
=======

>>>>>>> c3a28ac76eeee7848abc6e67dccc6d484d596c1a:app/src/main/java/com/example/morty/myapplication2/user_profile.java


        mTextMessage = (TextView) findViewById(R.id.uzname_field);
        mTextMessage2 = (TextView) findViewById(R.id.uz_email);
        Avatar = (ImageView) findViewById(R.id.uz_avatar);
        btn = (Button) findViewById(R.id.LogOut);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent( User_profile.this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(User_profile.this, "Вы успешно вышли из аккаунта", Toast.LENGTH_SHORT).show();
            }
        });

        final FirebaseUser cus = mAuth.getCurrentUser();
        DocumentReference users = db.collection("users").document(cus.getEmail());
        storageRef.child(cus.getEmail()+"/user_avatar/avatar_pic.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(Avatar);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/leotest-2k1n.appspot.com/o/Default%2Favatar_pic.png?alt=media&token=0a264da6-7d1b-44cd-aaee-9230bd2d0b2d").into(Avatar);
            }
        });


        users.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    if (snapshot.get("name")!=null) {
                        mTextMessage.setText(snapshot.get("name").toString());
                    }else{
                        mTextMessage.setText("Ошибка загрузки данных");
                    }
                    mTextMessage2.setText(cus.getEmail());



                    Log.d("", "Current data: " + snapshot.getData());
                } else {
                    Log.d("", "Current data: null");
                }
            }
        });



        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
