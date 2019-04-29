package com.example.morty.myapplication2;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.inputmethod.InputMethodManager;
import android.widget.RatingBar;
import android.widget.SearchView;
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
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener{
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference tests = db.collection("tests");
    private final ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
    private  HashMap<String, String> map;
    private ProgressBar progressBar;
    private ImageView Avatar ;
    private TextView not_auth;
    private ListView listView;
    private RatingBar testRt;
    ListView questions;
    byte pressedCount = 1;
    Date pressedMoment1 = new Date(0);
    Date pressedMoment2 = new Date(0);
    TabLayout tabLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    String[] categories = {"Все тесты", "Английский язык", "Математика", "Русский язык", "Биология",
            "География", "Информатика", "Физика", "Химия", "Другое"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflater inflater = getLayoutInflater();
        View myView = inflater.inflate(R.layout.my_tests_item, null);
        View myView2 = inflater.inflate(R.layout.my_tests,null);
        View bar = inflater.inflate(R.layout.app_bar_main, null);


        not_auth = (TextView) myView2.findViewById(R.id.not_auth_text);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(categories[0]),0);
        tabLayout.addTab(tabLayout.newTab().setText(categories[1]),1);
        tabLayout.addTab(tabLayout.newTab().setText(categories[2]),2);
        tabLayout.addTab(tabLayout.newTab().setText(categories[3]),3);
        tabLayout.addTab(tabLayout.newTab().setText(categories[4]),4);
        tabLayout.addTab(tabLayout.newTab().setText(categories[5]),5);
        tabLayout.addTab(tabLayout.newTab().setText(categories[6]),6);
        tabLayout.addTab(tabLayout.newTab().setText(categories[7]),7);
        tabLayout.addTab(tabLayout.newTab().setText(categories[8]),8);
        tabLayout.addTab(tabLayout.newTab().setText(categories[9]),9);



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




        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        final FirebaseUser cus = mAuth.getCurrentUser();
        final StorageReference storageRef = storage.getReference();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        questions = (ListView) findViewById(R.id.list);

        setAllTests();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    default:
                        break;

                    case 0:
                        setAllTests();
                        break;

                    case 1:
                        caseVoid("Английский язык");
                        break;
                    case 2:
                        caseVoid("Математика");
                        break;
                    case 3:
                        caseVoid("Русский язык");
                        break;
                    case 4:
                        caseVoid("Немецкий язык");
                        break;
                    case 5:
                        caseVoid("География");
                        break;
                    case 6:
                        caseVoid("Информатика");
                        break;
                    case 7:
                        caseVoid("Физика");
                        break;
                    case 8:
                        caseVoid("Химия");
                        break;
                    case 9:
                        caseVoid("Другое");
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
                switch (tab.getPosition()){
                    default:
                        break;

                    case 0:
                        setAllTests();
                        break;

                    case 1:
                        caseVoid("Английский язык");
                        break;
                    case 2:
                        caseVoid("Математика");
                        break;
                    case 3:
                        caseVoid("Русский язык");
                        break;
                    case 4:
                        caseVoid("Немецкий язык");
                        break;
                    case 5:
                        caseVoid("География");
                        break;
                    case 6:
                        caseVoid("Информатика");
                        break;
                    case 7:
                        caseVoid("Физика");
                        break;
                    case 8:
                        caseVoid("Химия");
                        break;
                    case 9:
                        caseVoid("Другое");
                        break;
                }
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
        Toast.makeText(this, "Нажата кнопка поиск "+ query, Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // User changed the text
        if (newText.equals("")){
            if(tabLayout.getSelectedTabPosition() == 0) setAllTests();
            else caseVoid(categories[tabLayout.getSelectedTabPosition()]);
        }
        searchTests(newText);
        Toast.makeText(this, "Вы ищите: "+ newText, Toast.LENGTH_SHORT).show();
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
            Intent intent = new Intent(MainActivity.this,MyTestsActivity.class);
            startActivity(intent);
            // Toast.makeText(this, "Ещё чуть-чуть и вы сможете создать собственный тест!", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_done) {
            Intent intent = new Intent(MainActivity.this,SolvedTestsActivity.class);
            startActivity(intent);
            //Toast.makeText(this, "Вы пока не выполнили ни одного теста", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_manage) {
            Toast.makeText(this, "Настройки ещё не настроены(", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_writes) {
            Intent intent = new Intent(MainActivity.this,ParsingActivity.class);
            startActivity(intent);
            //Toast.makeText(this, "У вас пока нет ни одного черновика", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_share) {
            //Toast.makeText(this, "Скоро вы сможете поделиться вашим прогрессом", Toast.LENGTH_SHORT).show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void caseVoid (String category){
        progressBar.setVisibility(View.VISIBLE);
        questions.setAdapter(null);
        arrayList.clear();

        Query q = tests.whereEqualTo("category",category);

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
                            questions.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long q_id) {
                                    LayoutInflater li = LayoutInflater.from(MainActivity.this);
                                    View promptsView = li.inflate(R.layout.delete_prompt, null);
                                    AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                                    mDialogBuilder.setView(promptsView);

                                    final TextView delete = (TextView) promptsView.findViewById(R.id.delete_tv);
                                    delete.setText("Удалить тест?");
                                    mDialogBuilder
                                            .setCancelable(false)
                                            .setPositiveButton("Да",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog,int id) {

                                                            final FirebaseUser cus = mAuth.getCurrentUser();
                                                            if (cus.getEmail().equals(arrayList.get(position).get("test_maker_email"))){
                                                                Log.d("deleting test", "test id: "+arrayList.get(position).get("Test_id")+"cur email: "+cus.getEmail());
                                                                tests.document(arrayList.get(position).get("Test_id")).delete();
                                                            }else{
                                                                Log.d("deleting test", "test id: "+arrayList.get(position).get("Test_id")+"cur email: "+cus.getEmail()+" | "+arrayList.get(position).get("test_maker_email"));
                                                                Toast.makeText(MainActivity.this,"Нужно быть создателем теста,чтобы его удалить!",Toast.LENGTH_LONG).show();
                                                                dialog.cancel();
                                                            }
                                                            if(tabLayout.getSelectedTabPosition() == 0) setAllTests();
                                                            else caseVoid(categories[tabLayout.getSelectedTabPosition()]);
                                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                                                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                                                        }
                                                    })
                                            .setNegativeButton("Отмена",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog,int id) {
                                                            dialog.cancel();
                                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                                                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                                                        }
                                                    });
                                    AlertDialog alertDialog = mDialogBuilder.create();
                                    alertDialog.show();
                                    return true;
                                }});


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

    void searchTests(String name){
        progressBar.setVisibility(View.VISIBLE);
        questions.setAdapter(null);
        arrayList.clear();

        Log.d("ssss", "testName: "+name);
        Query q = tests.whereLessThanOrEqualTo("test_name",name);
        q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        Log.d("MortyList", document.getId() + " => " + document.getData());
                        DecimalFormat df = new DecimalFormat("#.##");
                        map = new HashMap<>();
                        map.put("Test_id", document.getId());
                        map.put("test_maker_email",document.get("test_maker_email").toString());
                        map.put("Test_name", document.get("test_name").toString());
                        map.put("Q_count", "Вопросов: " + document.get("q_count").toString());
                        map.put("S_count",document.get("solved_cnt").toString());
                        if (document.get("rating")!=null)
                            map.put("Rating",df.format(document.get("rating")));
                        if (document.get("name") != null)
                            map.put("P_name", document.get("name").toString());

                        arrayList.add(map);
                        SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, arrayList, R.layout.my_tests_item,
                                new String[]{"Test_name", "Q_count", "P_name","S_count","Rating"},
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
                        questions.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long q_id) {
                                LayoutInflater li = LayoutInflater.from(MainActivity.this);
                                View promptsView = li.inflate(R.layout.delete_prompt, null);
                                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                                mDialogBuilder.setView(promptsView);

                                final TextView delete = (TextView) promptsView.findViewById(R.id.delete_tv);
                                delete.setText("Удалить тест?");
                                mDialogBuilder
                                        .setCancelable(false)
                                        .setPositiveButton("Да",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog,int id) {
                                                        final FirebaseUser cus = mAuth.getCurrentUser();
                                                        if (cus.getEmail().equals(arrayList.get(position).get("test_maker_email"))){
                                                            Log.d("deleting test", "test id: "+arrayList.get(position).get("Test_id")+"cur email: "+cus.getEmail());
                                                            tests.document(arrayList.get(position).get("Test_id")).delete();
                                                        }else{
                                                            Log.d("deleting test", "test id: "+arrayList.get(position).get("Test_id")+"cur email: "+cus.getEmail()+" | "+arrayList.get(position).get("test_maker_email"));
                                                            Toast.makeText(MainActivity.this,"Нужно быть создателем теста,чтобы его удалить!",Toast.LENGTH_LONG).show();
                                                            dialog.cancel();
                                                        }
                                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                                                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                                                    }
                                                })
                                        .setNegativeButton("Отмена",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog,int id) {
                                                        dialog.cancel();
                                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                                                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                                                    }
                                                });
                                AlertDialog alertDialog = mDialogBuilder.create();
                                alertDialog.show();
                                return true;
                            }});

                    }
                }
            }
        });
    }
    void setAllTests(){
        arrayList.clear();
        questions.setAdapter(null);
        tests.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                        questions.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long q_id) {
                                LayoutInflater li = LayoutInflater.from(MainActivity.this);
                                View promptsView = li.inflate(R.layout.delete_prompt, null);
                                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                                mDialogBuilder.setView(promptsView);

                                final TextView delete = (TextView) promptsView.findViewById(R.id.delete_tv);
                                delete.setText("Удалить тест?");
                                mDialogBuilder
                                        .setCancelable(false)
                                        .setPositiveButton("Да",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog,int id) {
                                                        final FirebaseUser cus = mAuth.getCurrentUser();
                                                        if (cus.getEmail().equals(arrayList.get(position).get("test_maker_email"))){
                                                            Log.d("deleting test", "test id: "+arrayList.get(position).get("Test_id")+"cur email: "+cus.getEmail());
                                                            tests.document(arrayList.get(position).get("Test_id")).delete();
                                                        }else{
                                                            Log.d("deleting test", "test id: "+arrayList.get(position).get("Test_id")+"cur email: "+cus.getEmail()+" | "+arrayList.get(position).get("test_maker_email"));
                                                            Toast.makeText(MainActivity.this,"Нужно быть создателем теста,чтобы его удалить!",Toast.LENGTH_LONG).show();
                                                            dialog.cancel();
                                                        }
                                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                                                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                                                    }
                                                })
                                        .setNegativeButton("Отмена",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog,int id) {
                                                        dialog.cancel();
                                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                                                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                                                    }
                                                });
                                AlertDialog alertDialog = mDialogBuilder.create();
                                alertDialog.show();
                                return true;
                            }});
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
                else caseVoid(categories[tabLayout.getSelectedTabPosition()]);
            }
        }, 1000);
    }

    @Override
    protected void onStart() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        for (int i = 0; i < navigationView.getMenu().size(); i++)  navigationView.getMenu().getItem(i).setChecked(false);
        super.onStart();
    }
}