package com.example.morty.myapplication2;

import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class user_profile extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView mTextMessage;
    private TextView mTextMessage1;
    private TextView mTextMessage2;
    private ImageView Avatar;


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


        mTextMessage = (TextView) findViewById(R.id.uzname_field);
        mTextMessage1 = (TextView) findViewById(R.id.uzmob_field);
        mTextMessage2 = (TextView) findViewById(R.id.uz_email);
        Avatar = (ImageView) findViewById(R.id.uz_avatar);

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
                // Handle any errors
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
                    mTextMessage.setText(snapshot.get("name").toString());
                    mTextMessage1.setText(snapshot.get("mobile").toString());
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