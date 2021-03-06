package com.example.morty.myapplication2;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;

import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.RatingBar;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener,
        SwipeRefreshLayout.OnRefreshListener, SearchView.OnCloseListener{
    private FirebaseAuth mAuth;
    private FirebaseUser cus;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference tests = db.collection("tests");
    private final ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
    private HashMap<String, String> map;
    private ProgressBar progressBar;
    private TextView not_auth;

    ListView questions;
    byte pressedCount = 1;
    Date pressedMoment1 = new Date(0);
    Date pressedMoment2 = new Date(0);
    TabLayout tabLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    String[] categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        cus = mAuth.getCurrentUser();

        LayoutInflater inflater = getLayoutInflater();
        View myView = inflater.inflate(R.layout.my_tests, null);

        categories = getResources().getStringArray(R.array.tag_names);

        not_auth = (TextView) myView.findViewById(R.id.not_auth_text);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(mAuth.getCurrentUser()==null){
            Intent intent = new Intent(this,EmailPasswordActivity.class);
            startActivity(intent);
        }else{

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Все тесты"), 0);
        tabLayout.addTab(tabLayout.newTab().setText(categories[0]), 1);
        tabLayout.addTab(tabLayout.newTab().setText(categories[1]), 2);
        tabLayout.addTab(tabLayout.newTab().setText(categories[2]), 3);
        tabLayout.addTab(tabLayout.newTab().setText(categories[3]), 4);
        tabLayout.addTab(tabLayout.newTab().setText(categories[4]), 5);
        tabLayout.addTab(tabLayout.newTab().setText(categories[5]), 6);
        tabLayout.addTab(tabLayout.newTab().setText(categories[6]), 7);
        tabLayout.addTab(tabLayout.newTab().setText(categories[7]), 8);
        tabLayout.addTab(tabLayout.newTab().setText(categories[8]), 9);
        tabLayout.addTab(tabLayout.newTab().setText(categories[9]), 10);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TestCreateActivity.class);
                startActivity(intent);
            }
        });



        if (cus == null) {
            fab.setClickable(false);
            Toast.makeText(this, "Для доступа к контенту, пожалуйста, зарегистрируйтесь", Toast.LENGTH_LONG).show();
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        questions = (ListView) findViewById(R.id.list);

        setAllTests();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tabLayout.getSelectedTabPosition() == 0) setAllTests();
                else caseVoid(categories[tabLayout.getSelectedTabPosition() - 1]);
                Log.i("TAG", "onTabSelected: " + tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                Log.i("TAG", "onTabUnselected: " + tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (tabLayout.getSelectedTabPosition() == 0) setAllTests();
                else caseVoid(categories[tabLayout.getSelectedTabPosition() - 1]);
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
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
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
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));

        searchView.setOnQueryTextListener(this);


        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(this, "Searching by: "+ query, Toast.LENGTH_SHORT).show();

        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            String uri = intent.getDataString();
            Toast.makeText(this, "Suggestion: "+ uri, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // User pressed the search button
        if (query.equals("")){
            if(tabLayout.getSelectedTabPosition() == 0) setAllTests();
            else caseVoid(categories[tabLayout.getSelectedTabPosition()-1]);
        } else {
            if(tabLayout.getSelectedTabPosition() == 0) searchAllTests(query);
            else searchTestsInCategory(query, categories[tabLayout.getSelectedTabPosition()-1]);
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // User changed the text
        if (newText.equals("")){
            if(tabLayout.getSelectedTabPosition() == 0) setAllTests();
            else caseVoid(categories[tabLayout.getSelectedTabPosition() - 1]);
        } else {
            if(tabLayout.getSelectedTabPosition() == 0) searchAllTests(newText);
            else searchTestsInCategory(newText, categories[tabLayout.getSelectedTabPosition() - 1]);
        }
        return false;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


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
            if (cus == null){
                Toast.makeText(this, "Для доступа к контенту, пожалуйста, зарегистрируйтесь", Toast.LENGTH_LONG).show();
            }else {
                Intent intent = new Intent(MainActivity.this, MyTestsActivity.class);
                startActivity(intent);
            }
            // Toast.makeText(this, "Ещё чуть-чуть и вы сможете создать собственный тест!", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_done) {
            if (cus == null){
                Toast.makeText(this, "Для доступа к контенту, пожалуйста, зарегистрируйтесь", Toast.LENGTH_LONG).show();
            }else {
                Intent intent = new Intent(MainActivity.this, SolvedTestsActivity.class);
                startActivity(intent);
            }
            //Toast.makeText(this, "Вы пока не выполнили ни одного теста", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_manage) {
            if (cus == null){
                Toast.makeText(this, "Для доступа к контенту, пожалуйста, зарегистрируйтесь", Toast.LENGTH_LONG).show();
            }else {
                Intent intent = new Intent(MainActivity.this, Users_search.class);
                startActivity(intent);
            }
            //Toast.makeText(this, "Настройки ещё не настроены(", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_writes) {
            if (cus == null){
                Toast.makeText(this, "Для доступа к контенту, пожалуйста, зарегистрируйтесь", Toast.LENGTH_LONG).show();
            }else {
                Intent intent = new Intent(MainActivity.this, ParsingActivity.class);
                startActivity(intent);
            }
            //Toast.makeText(this, "У вас пока нет ни одного черновика", Toast.LENGTH_SHORT).show();
        }  else if(id==R.id.nav_pr_tests) {
            if (cus == null) {
                Toast.makeText(this, "Для доступа к контенту, пожалуйста, зарегистрируйтесь", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(MainActivity.this, PrivateTestsActivity.class);
                startActivity(intent);
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void caseVoid (String category){
        progressBar.setVisibility(View.VISIBLE);
        questions.setAdapter(null);
        arrayList.clear();

        Query q = tests.whereEqualTo("category",category).whereEqualTo("private_status","free");

        q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if(!task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("MortyList", document.getId() + " => " + document.getData());
                            DecimalFormat df = new DecimalFormat("#.##");
                            map = new HashMap<>();
                            map.put("Test_id", document.getId());
                            map.put("Test_name", document.get("test_name").toString());
                            map.put("Q_count", "Вопросов: " + document.get("q_count").toString());
                            map.put("test_maker_email",document.get("test_maker_email").toString());
                            if (document.get("rating")!=null)
                                map.put("Rating",df.format(document.get("rating")));
                            map.put("S_count",document.get("solved_cnt").toString());
                            if (document.get("name") != null)
                                map.put("P_name", document.get("name").toString());

                            arrayList.add(map);
                            SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, arrayList, R.layout.my_tests_item,
                                    new String[]{"Test_name", "Q_count", "P_name", "S_count","Rating"},
                                    new int[]{R.id.test_name, R.id.q_count, R.id.person_name, R.id.solved_count, R.id.test_rating});
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
                    }else{
                        progressBar.setVisibility(View.GONE);
                        not_auth.setVisibility(View.VISIBLE);
                        not_auth.setText("Нет тестов");
                    }
                }
            }
        });

    }

    void searchTestsInCategory(final String name, String category){
        progressBar.setVisibility(View.VISIBLE);
        questions.setAdapter(null);
        arrayList.clear();

        Query q = tests.whereEqualTo("category",category).whereEqualTo("private_status","free");

        q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                boolean search = false;
                if (task.isSuccessful()){
                    if(!task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String docName = (String)document.get("test_name");
                            if (docName != null && !name.equals("") && !docName.equals("") && docName.contains(name)) {
                                search = true;
                                Log.d("MortyList", document.getId() + " => " + document.getData());
                                DecimalFormat df = new DecimalFormat("#.##");
                                map = new HashMap<>();
                                map.put("Test_id", document.getId());
                                map.put("Test_name", document.get("test_name").toString());
                                map.put("Q_count", "Вопросов: " + document.get("q_count").toString());
                                map.put("test_maker_email", document.get("test_maker_email").toString());
                                if (document.get("rating") != null)
                                    map.put("Rating", df.format(document.get("rating")));
                                map.put("S_count", document.get("solved_cnt").toString());
                                if (document.get("name") != null)
                                    map.put("P_name", document.get("name").toString());

                                arrayList.add(map);
                                SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, arrayList, R.layout.my_tests_item,
                                        new String[]{"Test_name", "Q_count", "P_name", "S_count", "Rating"},
                                        new int[]{R.id.test_name, R.id.q_count, R.id.person_name, R.id.solved_count, R.id.test_rating});
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
                        } if(!search){
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Нет тестов, удовлетворяющих вашему условию", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Нет тестов", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });
    }

    void searchAllTests(final String name){
        progressBar.setVisibility(View.VISIBLE);
        questions.setAdapter(null);
        arrayList.clear();
        Query q = tests.whereEqualTo("private_status","free");
        q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                boolean search = false;
                if (task.isSuccessful()){
                    if(!task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String docName = (String)document.get("test_name");
                            if (docName != null && !name.equals("") && !docName.equals("") && docName.contains(name)) {
                                search = true;
                                Log.d("MortyList", document.getId() + " => " + document.getData());
                                DecimalFormat df = new DecimalFormat("#.##");
                                map = new HashMap<>();
                                map.put("Test_id", document.getId());
                                map.put("Test_name", document.get("test_name").toString());
                                map.put("Q_count", "Вопросов: " + document.get("q_count").toString());
                                map.put("test_maker_email", document.get("test_maker_email").toString());
                                if (document.get("rating") != null)
                                    map.put("Rating", df.format(document.get("rating")));
                                map.put("S_count", document.get("solved_cnt").toString());
                                if (document.get("name") != null)
                                    map.put("P_name", document.get("name").toString());

                                arrayList.add(map);
                                SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, arrayList, R.layout.my_tests_item,
                                        new String[]{"Test_name", "Q_count", "P_name", "S_count", "Rating"},
                                        new int[]{R.id.test_name, R.id.q_count, R.id.person_name, R.id.solved_count, R.id.test_rating});
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
                        } if(!search){
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Нет тестов, удовлетворяющих вашему условию", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Нет тестов", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });
    }

    void setAllTests(){
        arrayList.clear();
        questions.setAdapter(null);
        Query q = tests.whereEqualTo("private_status","free");
        q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("MortyList", document.getId() + " => " + document.getData());
                        DecimalFormat df = new DecimalFormat("#.##");
                        map = new HashMap<>();
                        map.put("Test_id", document.getId());
                        map.put("Test_name", document.get("test_name").toString());
                        map.put("Q_count", "Вопросов: " + document.get("q_count").toString());
                        map.put("S_count",document.get("solved_cnt").toString());
                        map.put("test_maker_email",document.get("test_maker_email").toString());
                        if (document.get("rating")!=null)
                            map.put("Rating",df.format(document.get("rating")));
                        if (document.get("name") != null)
                            map.put("P_name", document.get("name").toString());

                        arrayList.add(map);
                        SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, arrayList, R.layout.my_tests_item,
                                new String[]{"Test_name", "Q_count", "P_name", "S_count","Rating"},
                                new int[]{R.id.test_name, R.id.q_count, R.id.person_name, R.id.solved_count, R.id.test_rating});
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
                } else {
                    not_auth.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    Log.d("MortyList", "Error getting documents: ", task.getException());
                }

            }
        });
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
                if(tabLayout.getSelectedTabPosition() == 0) setAllTests();
                else caseVoid(categories[tabLayout.getSelectedTabPosition() - 1]);
            }
        }, 1000);
    }

    @Override
    protected void onStart() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        for (int i = 0; i < navigationView.getMenu().size(); i++)  navigationView.getMenu().getItem(i).setChecked(false);
        super.onStart();
    }

    @Override
    public boolean onClose() {
        if(tabLayout.getSelectedTabPosition() == 0) setAllTests();
        else caseVoid(categories[tabLayout.getSelectedTabPosition() - 1]);
        return false;
    }
}