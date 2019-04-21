package com.example.morty.myapplication2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference tests = db.collection("tests");
    private final ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
    private  HashMap<String, String> map;
    private ProgressBar progressBar;
    private ImageView Avatar ;
    private TextView not_auth;
    private ListView listView;
    byte pressedCount = 1;
    Date pressedMoment1 = new Date(0);
    Date pressedMoment2 = new Date(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflater inflater = getLayoutInflater();
        View myView = inflater.inflate(R.layout.my_tests_item, null);
        View myView2 = inflater.inflate(R.layout.my_tests,null);

        Avatar = (ImageView) myView.findViewById(R.id.image_view2);
        not_auth = (TextView) myView2.findViewById(R.id.not_auth_text);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Все тесты"),0);
        tabLayout.addTab(tabLayout.newTab().setText("Английский язык"),1);
        tabLayout.addTab(tabLayout.newTab().setText("Математика"),2);
        tabLayout.addTab(tabLayout.newTab().setText("Русский язык"),3);
        tabLayout.addTab(tabLayout.newTab().setText("Биология"),4);
        tabLayout.addTab(tabLayout.newTab().setText("География"),5);
        tabLayout.addTab(tabLayout.newTab().setText("Информатика"),6);
        tabLayout.addTab(tabLayout.newTab().setText("Физика"),7);
        tabLayout.addTab(tabLayout.newTab().setText("Химия"),8);
        tabLayout.addTab(tabLayout.newTab().setText("Другое"),9);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mAuth = FirebaseAuth.getInstance();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        listView = myView2.findViewById(R.id.list);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TestCreateActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        final FirebaseUser cus = mAuth.getCurrentUser();
        final StorageReference storageRef = storage.getReference();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        final ListView questions = (ListView) findViewById(R.id.list);

        tests.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                  @Override
                 public void onComplete(@NonNull Task<QuerySnapshot> task) {
                      if (task.isSuccessful()) {
                          for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.get("category").equals("Английский язык")) {
                                 Log.d("MortyList", document.getId() + " => " + document.getData());
                                  map = new HashMap<>();
                                  map.put("Test_id", document.getId());
                                  map.put("Test_name", document.get("test_name").toString());
                                  map.put("Q_count", "Вопросов: " + document.get("q_count").toString());

                                  if (document.get("name") != null)
                                      map.put("P_name", document.get("name").toString());

                                  arrayList.add(map);
                                  SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, arrayList, R.layout.my_tests_item,
                                          new String[]{"Test_name", "Q_count", "P_name"},
                                          new int[]{R.id.test_name, R.id.q_count, R.id.person_name});
                                  questions.setAdapter(adapter);
                                  progressBar.setVisibility(View.GONE);
                                  questions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                      @Override
                                      public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                                          Intent intent = new Intent(MainActivity.this, test_view.class);
                                          intent.putExtra("Test_id", arrayList.get((int) id).get("Test_id").toString());
                                          startActivity(intent);
                                      }
                                  });
                              }
                          }
                      } else {
                          not_auth.setVisibility(View.VISIBLE);
                          progressBar.setVisibility(View.GONE);
                          Log.d("MortyList", "Error getting documents: ", task.getException());
                      }

                  }
              });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    default:
                        break;

                    case 0:
                        tests.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if (document.get("category").equals("Английский язык")) {
                                            Log.d("MortyList", document.getId() + " => " + document.getData());
                                            map = new HashMap<>();
                                            map.put("Test_id", document.getId());
                                            map.put("Test_name", document.get("test_name").toString());
                                            map.put("Q_count", "Вопросов: " + document.get("q_count").toString());

                                            if (document.get("name") != null)
                                                map.put("P_name", document.get("name").toString());

                                            arrayList.add(map);
                                            SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, arrayList, R.layout.my_tests_item,
                                                    new String[]{"Test_name", "Q_count", "P_name"},
                                                    new int[]{R.id.test_name, R.id.q_count, R.id.person_name});
                                            questions.setAdapter(adapter);
                                            progressBar.setVisibility(View.GONE);
                                            questions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                                                    Intent intent = new Intent(MainActivity.this, test_view.class);
                                                    intent.putExtra("Test_id", arrayList.get((int) id).get("Test_id").toString());
                                                    startActivity(intent);
                                                }
                                            });
                                        }
                                    }
                                } else {
                                    not_auth.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                    Log.d("MortyList", "Error getting documents: ", task.getException());
                                }

                            }
                        });
                        break;

                    case 1:
                        progressBar.setVisibility(View.VISIBLE);
                        questions.setAdapter(null);
                        arrayList.clear();
                        Query eng_q = tests.whereEqualTo("category","Английский язык");
                        eng_q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    for (QueryDocumentSnapshot document : task.getResult()){
                                        Log.d("MortyList", document.getId() + " => " + document.getData());
                                        map = new HashMap<>();
                                        map.put("Test_id", document.getId());
                                        map.put("Test_name", document.get("test_name").toString());
                                        map.put("Q_count", "Вопросов: " + document.get("q_count").toString());

                                        if (document.get("name") != null)
                                            map.put("P_name", document.get("name").toString());

                                        arrayList.add(map);
                                        SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, arrayList, R.layout.my_tests_item,
                                                new String[]{"Test_name", "Q_count", "P_name"},
                                                new int[]{R.id.test_name, R.id.q_count, R.id.person_name});
                                        questions.setAdapter(adapter);
                                        progressBar.setVisibility(View.GONE);
                                        questions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                                                Intent intent = new Intent(MainActivity.this, test_view.class);
                                                intent.putExtra("Test_id", arrayList.get((int) id).get("Test_id").toString());
                                                startActivity(intent);
                                            }
                                        });

                                    }
                                }
                            }
                        });
                    break;
                    case 2:
                        progressBar.setVisibility(View.VISIBLE);
                        questions.setAdapter(null);
                        arrayList.clear();
                        Query math_q = tests.whereEqualTo("category","Математика");
                        math_q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    for (QueryDocumentSnapshot document : task.getResult()){
                                        Log.d("MortyList", document.getId() + " => " + document.getData());
                                        map = new HashMap<>();
                                        map.put("Test_id", document.getId());
                                        map.put("Test_name", document.get("test_name").toString());
                                        map.put("Q_count", "Вопросов: " + document.get("q_count").toString());

                                        if (document.get("name") != null)
                                            map.put("P_name", document.get("name").toString());

                                        arrayList.add(map);
                                        SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, arrayList, R.layout.my_tests_item,
                                                new String[]{"Test_name", "Q_count", "P_name"},
                                                new int[]{R.id.test_name, R.id.q_count, R.id.person_name});
                                        questions.setAdapter(adapter);
                                        progressBar.setVisibility(View.GONE);
                                        questions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                                                Intent intent = new Intent(MainActivity.this, test_view.class);
                                                intent.putExtra("Test_id", arrayList.get((int) id).get("Test_id").toString());
                                                startActivity(intent);
                                            }
                                        });

                                    }
                                }
                            }
                        });
                        break;
                    case 3:
                        progressBar.setVisibility(View.VISIBLE);
                        questions.setAdapter(null);
                        arrayList.clear();
                        Query rus_q = tests.whereEqualTo("category","Русский язык");
                        rus_q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    for (QueryDocumentSnapshot document : task.getResult()){
                                        Log.d("MortyList", document.getId() + " => " + document.getData());
                                        map = new HashMap<>();
                                        map.put("Test_id", document.getId());
                                        map.put("Test_name", document.get("test_name").toString());
                                        map.put("Q_count", "Вопросов: " + document.get("q_count").toString());

                                        if (document.get("name") != null)
                                            map.put("P_name", document.get("name").toString());

                                        arrayList.add(map);
                                        SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, arrayList, R.layout.my_tests_item,
                                                new String[]{"Test_name", "Q_count", "P_name"},
                                                new int[]{R.id.test_name, R.id.q_count, R.id.person_name});
                                        questions.setAdapter(adapter);
                                        progressBar.setVisibility(View.GONE);
                                        questions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                                                Intent intent = new Intent(MainActivity.this, test_view.class);
                                                intent.putExtra("Test_id", arrayList.get((int) id).get("Test_id").toString());
                                                startActivity(intent);
                                            }
                                        });

                                    }
                                }
                            }
                        });
                        break;
                    case 4:
                        progressBar.setVisibility(View.VISIBLE);
                        questions.setAdapter(null);
                        arrayList.clear();
                        Query germ_q = tests.whereEqualTo("category","Немецкий язык");
                       germ_q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    for (QueryDocumentSnapshot document : task.getResult()){
                                        Log.d("MortyList", document.getId() + " => " + document.getData());
                                        map = new HashMap<>();
                                        map.put("Test_id", document.getId());
                                        map.put("Test_name", document.get("test_name").toString());
                                        map.put("Q_count", "Вопросов: " + document.get("q_count").toString());

                                        if (document.get("name") != null)
                                            map.put("P_name", document.get("name").toString());

                                        arrayList.add(map);
                                        SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, arrayList, R.layout.my_tests_item,
                                                new String[]{"Test_name", "Q_count", "P_name"},
                                                new int[]{R.id.test_name, R.id.q_count, R.id.person_name});
                                        questions.setAdapter(adapter);
                                        progressBar.setVisibility(View.GONE);
                                        questions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                                                Intent intent = new Intent(MainActivity.this, test_view.class);
                                                intent.putExtra("Test_id", arrayList.get((int) id).get("Test_id").toString());
                                                startActivity(intent);
                                            }
                                        });

                                    }
                                }
                            }
                        });
                        break;
                    case 5:
                        progressBar.setVisibility(View.VISIBLE);
                        questions.setAdapter(null);
                        arrayList.clear();
                        Query geog_q = tests.whereEqualTo("category","География");
                        geog_q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    for (QueryDocumentSnapshot document : task.getResult()){
                                        Log.d("MortyList", document.getId() + " => " + document.getData());
                                        map = new HashMap<>();
                                        map.put("Test_id", document.getId());
                                        map.put("Test_name", document.get("test_name").toString());
                                        map.put("Q_count", "Вопросов: " + document.get("q_count").toString());

                                        if (document.get("name") != null)
                                            map.put("P_name", document.get("name").toString());

                                        arrayList.add(map);
                                        SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, arrayList, R.layout.my_tests_item,
                                                new String[]{"Test_name", "Q_count", "P_name"},
                                                new int[]{R.id.test_name, R.id.q_count, R.id.person_name});
                                        questions.setAdapter(adapter);
                                        progressBar.setVisibility(View.GONE);
                                        questions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                                                Intent intent = new Intent(MainActivity.this, test_view.class);
                                                intent.putExtra("Test_id", arrayList.get((int) id).get("Test_id").toString());
                                                startActivity(intent);
                                            }
                                        });

                                    }
                                }
                            }
                        });
                        break;
                    case 6:
                        progressBar.setVisibility(View.VISIBLE);
                        questions.setAdapter(null);
                        arrayList.clear();
                        Query inf_q = tests.whereEqualTo("category","Информатика");
                        inf_q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    for (QueryDocumentSnapshot document : task.getResult()){
                                        Log.d("MortyList", document.getId() + " => " + document.getData());
                                        map = new HashMap<>();
                                        map.put("Test_id", document.getId());
                                        map.put("Test_name", document.get("test_name").toString());
                                        map.put("Q_count", "Вопросов: " + document.get("q_count").toString());

                                        if (document.get("name") != null)
                                            map.put("P_name", document.get("name").toString());

                                        arrayList.add(map);
                                        SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, arrayList, R.layout.my_tests_item,
                                                new String[]{"Test_name", "Q_count", "P_name"},
                                                new int[]{R.id.test_name, R.id.q_count, R.id.person_name});
                                        questions.setAdapter(adapter);
                                        progressBar.setVisibility(View.GONE);
                                        questions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                                                Intent intent = new Intent(MainActivity.this, test_view.class);
                                                intent.putExtra("Test_id", arrayList.get((int) id).get("Test_id").toString());
                                                startActivity(intent);
                                            }
                                        });

                                    }
                                }
                            }
                        });
                        break;
                    case 7:
                        progressBar.setVisibility(View.VISIBLE);
                        questions.setAdapter(null);
                        arrayList.clear();
                        Query phs_q = tests.whereEqualTo("category","Физика");
                        phs_q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    for (QueryDocumentSnapshot document : task.getResult()){
                                        Log.d("MortyList", document.getId() + " => " + document.getData());
                                        map = new HashMap<>();
                                        map.put("Test_id", document.getId());
                                        map.put("Test_name", document.get("test_name").toString());
                                        map.put("Q_count", "Вопросов: " + document.get("q_count").toString());

                                        if (document.get("name") != null)
                                            map.put("P_name", document.get("name").toString());

                                        arrayList.add(map);
                                        SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, arrayList, R.layout.my_tests_item,
                                                new String[]{"Test_name", "Q_count", "P_name"},
                                                new int[]{R.id.test_name, R.id.q_count, R.id.person_name});
                                        questions.setAdapter(adapter);
                                        progressBar.setVisibility(View.GONE);
                                        questions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                                                Intent intent = new Intent(MainActivity.this, test_view.class);
                                                intent.putExtra("Test_id", arrayList.get((int) id).get("Test_id").toString());
                                                startActivity(intent);
                                            }
                                        });

                                    }
                                }
                            }
                        });
                        break;
                    case 8:
                        progressBar.setVisibility(View.VISIBLE);
                        questions.setAdapter(null);
                        arrayList.clear();
                        Query chem_q = tests.whereEqualTo("category","Химия");
                        chem_q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    for (QueryDocumentSnapshot document : task.getResult()){
                                        Log.d("MortyList", document.getId() + " => " + document.getData());
                                        map = new HashMap<>();
                                        map.put("Test_id", document.getId());
                                        map.put("Test_name", document.get("test_name").toString());
                                        map.put("Q_count", "Вопросов: " + document.get("q_count").toString());

                                        if (document.get("name") != null)
                                            map.put("P_name", document.get("name").toString());

                                        arrayList.add(map);
                                        SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, arrayList, R.layout.my_tests_item,
                                                new String[]{"Test_name", "Q_count", "P_name"},
                                                new int[]{R.id.test_name, R.id.q_count, R.id.person_name});
                                        questions.setAdapter(adapter);
                                        progressBar.setVisibility(View.GONE);
                                        questions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                                                Intent intent = new Intent(MainActivity.this, test_view.class);
                                                intent.putExtra("Test_id", arrayList.get((int) id).get("Test_id").toString());
                                                startActivity(intent);
                                            }
                                        });

                                    }
                                }
                            }
                        });
                        break;
                    case 9:
                        progressBar.setVisibility(View.VISIBLE);
                        questions.setAdapter(null);
                        arrayList.clear();
                        Query othr_q = tests.whereEqualTo("category","Другое");
                        othr_q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    for (QueryDocumentSnapshot document : task.getResult()){
                                        Log.d("MortyList", document.getId() + " => " + document.getData());
                                        map = new HashMap<>();
                                        map.put("Test_id", document.getId());
                                        map.put("Test_name", document.get("test_name").toString());
                                        map.put("Q_count", "Вопросов: " + document.get("q_count").toString());

                                        if (document.get("name") != null)
                                            map.put("P_name", document.get("name").toString());

                                        arrayList.add(map);
                                        SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, arrayList, R.layout.my_tests_item,
                                                new String[]{"Test_name", "Q_count", "P_name"},
                                                new int[]{R.id.test_name, R.id.q_count, R.id.person_name});
                                        questions.setAdapter(adapter);
                                        progressBar.setVisibility(View.GONE);
                                        questions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                                                Intent intent = new Intent(MainActivity.this, test_view.class);
                                                intent.putExtra("Test_id", arrayList.get((int) id).get("Test_id").toString());
                                                startActivity(intent);
                                            }
                                        });

                                    }
                                }
                            }
                        });
                        break;

                }
                Log.i("TAG", "onTabSelected: " + tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                Log.i("TAG", "onTabUnselected: " + tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.i("TAG", "onTabReselected: " + tab.getPosition());
            }
        });


        //     if (mAuth.getCurrentUser()!=null) {
        //         storageRef.child(cus.getEmail() + "/user_avatar/avatar_pic.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
        //             @Override
        //             public void onSuccess(Uri uri) {
        //                 Picasso.get().load(uri).into(Avatar);
        //                 progressBar.setVisibility(View.GONE);
        //             }
        //         }).addOnFailureListener(new OnFailureListener() {
        //             @Override
        //             public void onFailure(@NonNull Exception exception) {
        //                 Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/leotest-2k1n.appspot.com/o/Default%2Favatar_pic.png?alt=media&token=0a264da6-7d1b-44cd-aaee-9230bd2d0b2d").into(Avatar);
        //                 progressBar.setVisibility(View.GONE);
        //             }
        //         });
        //     }else{
        //         not_auth.setVisibility(View.VISIBLE);
        //     }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            pressedCount = 1;
            drawer.closeDrawer(GravityCompat.START);
        } else if(pressedCount==1){
            pressedCount++;
            pressedMoment1 = new Date();
            Toast.makeText(this, "Нажмите еще раз для выхода", Toast.LENGTH_SHORT).show();
        } else{
            pressedCount = 1;
            pressedMoment2 = new Date();
            if(pressedMoment2.getTime()-pressedMoment1.getTime() < 1000) {
                super.onBackPressed();
                pressedMoment1 = new Date(0);
                pressedMoment2 = new Date(0);
            }else{
                pressedCount++;
                pressedMoment1 = new Date();
                Toast.makeText(this, "Нажмите еще раз для выхода", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser!=null){
                Intent intent = new Intent(MainActivity.this, User_profile.class);
                startActivity(intent);

            }else{
                Intent intent = new Intent(MainActivity.this, EmailPasswordActivity.class);
                startActivity(intent);
            }

        } else if (id == R.id.nav_tests) {
            Intent intent = new Intent(MainActivity.this,MyTestsActivity.class);
            startActivity(intent);
            // Toast.makeText(this, "Ещё чуть-чуть и вы сможете создать собственный тест!", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_done) {
            Toast.makeText(this, "Вы пока не выполнили ни одного теста", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_manage) {
            Toast.makeText(this, "Настройки ещё не настроены(", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_writes) {
            Toast.makeText(this, "У вас пока нет ни одного черновика", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_share) {
            //Toast.makeText(this, "Скоро вы сможете поделиться вашим прогрессом", Toast.LENGTH_SHORT).show();

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}