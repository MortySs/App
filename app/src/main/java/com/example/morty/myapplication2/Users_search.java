package com.example.morty.myapplication2;

import android.app.SearchManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.v7.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class Users_search extends AppCompatActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener{
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference users = db.collection("users");
    private final ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
    private  HashMap<String, String> map;
    ListView usersList;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_search);
        mAuth = FirebaseAuth.getInstance();
        usersList = (ListView) findViewById(R.id.users_list);
        tabLayout = (TabLayout) findViewById(R.id.users_tab);
        tabLayout.addTab(tabLayout.newTab().setText("Все пользователи"),0);
        tabLayout.addTab(tabLayout.newTab().setText("Подписки"),1);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tabLayout.getSelectedTabPosition() == 0) SetAll();
                else SetSubscriptions();
                Log.i("TAG", "onTabSelected: " + tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                Log.i("TAG", "onTabUnselected: " + tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if(tabLayout.getSelectedTabPosition() == 0) SetAll();
                else SetSubscriptions();
                Log.i("TAG", "onTabReselected: " + tab.getPosition());
            }
        });
        SetAll();
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
            if(tabLayout.getSelectedTabPosition() == 0) SetAll();
            else SetSubscriptions();
        } else if(tabLayout.getSelectedTabPosition() == 0) SearchAll(query);
        else SearchSubscriptions(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // User changed the text
        if (newText.equals("")){
            if(tabLayout.getSelectedTabPosition() == 0) SetAll();
            else SetSubscriptions();
        } else if(tabLayout.getSelectedTabPosition() == 0) SearchAll(newText);
        else SearchSubscriptions(newText);
        return false;
    }

    void SetAll(){
        usersList.setAdapter(null);
        arrayList.clear();

        users.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("MortyList", document.getId() + " => " + document.getData());
                            map = new HashMap<>();
                            map.put("Email", document.getId());
                            map.put("Name", String.valueOf(document.get("name")));
                            String testsCount = String.valueOf(document.get("tests_count"));
                            if(testsCount.equals("null") || testsCount.equals("0")) {
                                map.put("TestsCount", "нет тестов");
                            } else {
                                if(document.getLong("tests_count") <= 20) {
                                    if (testsCount.equals("1")) {
                                        map.put("TestsCount", "1 тест");
                                    } else {
                                        if (testsCount.equals("2") || testsCount.equals("3") || testsCount.equals("4")) {
                                            map.put("TestsCount", testsCount + " теста");
                                        } else {
                                            map.put("TestsCount", testsCount + " тестов");
                                        }
                                    }
                                }else {
                                    if (testsCount.endsWith("1")) {
                                        map.put("TestsCount", testsCount + " тест");
                                    } else {
                                        if (testsCount.endsWith("2") || testsCount.endsWith("3") || testsCount.endsWith("4")) {
                                            map.put("TestsCount", testsCount + " теста");
                                        } else {
                                            map.put("TestsCount", testsCount + " тестов");
                                        }
                                    }
                                }
                            }
                            Log.d("users_testsCount", map.get("TestsCount"));
                            ArrayList<String> subscribers = (ArrayList<String>)document.get("subscribers");
                            ArrayList<String> subscriptions = (ArrayList<String>)document.get("subscriptions");
                            String subs = subscribers.size() + "/" + subscriptions.size();
                            map.put("Subs", subs);

                            arrayList.add(map);
                            SimpleAdapter adapter = new SimpleAdapter(Users_search.this, arrayList, R.layout.users_item,
                                    new String[]{"Email", "TestsCount", "Name", "Subs"},
                                    new int[]{R.id.user_email, R.id.tests_count, R.id.user_name, R.id.subscribers_subscriptions});
                            usersList.setAdapter(adapter);

                            usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                                    Intent intent = new Intent(Users_search.this, User_profile.class);
                                    intent.putExtra("Email", arrayList.get((int) id).get("Email"));
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                }
            }
        });

    }

    void SearchAll(final String name){
        Log.d("SearchUsers", "Search calling");
        usersList.setAdapter(null);
        arrayList.clear();

        users.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String docName = (String)document.get("name");
                            if (docName != null && !name.equals("") && !docName.equals("") && docName.contains(name)) {
                                Log.d("MortyList", document.getId() + " => " + document.getData());
                                map = new HashMap<>();
                                map.put("Email", document.getId());
                                map.put("Name", String.valueOf(document.get("name")));
                                String testsCount = String.valueOf(document.get("tests_count"));
                                if (testsCount.equals("null") || testsCount.equals("0")) {
                                    map.put("TestsCount", "нет тестов");
                                } else {
                                    if (document.getLong("tests_count") <= 20) {
                                        if (testsCount.equals("1")) {
                                            map.put("TestsCount", "1 тест");
                                        } else {
                                            if (testsCount.equals("2") || testsCount.equals("3") || testsCount.equals("4")) {
                                                map.put("TestsCount", testsCount + " теста");
                                            } else {
                                                map.put("TestsCount", testsCount + " тестов");
                                            }
                                        }
                                    } else {
                                        if (testsCount.endsWith("1")) {
                                            map.put("TestsCount", testsCount + " тест");
                                        } else {
                                            if (testsCount.endsWith("2") || testsCount.endsWith("3") || testsCount.endsWith("4")) {
                                                map.put("TestsCount", testsCount + " теста");
                                            } else {
                                                map.put("TestsCount", testsCount + " тестов");
                                            }
                                        }
                                    }
                                }
                                Log.d("users_testsCount", map.get("TestsCount"));
                                ArrayList<String> subscribers = (ArrayList<String>) document.get("subscribers");
                                ArrayList<String> subscriptions = (ArrayList<String>) document.get("subscriptions");
                                String subs = subscribers.size() + "/" + subscriptions.size();
                                map.put("Subs", subs);

                                arrayList.add(map);
                                SimpleAdapter adapter = new SimpleAdapter(Users_search.this, arrayList, R.layout.users_item,
                                        new String[]{"Email", "TestsCount", "Name", "Subs"},
                                        new int[]{R.id.user_email, R.id.tests_count, R.id.user_name, R.id.subscribers_subscriptions});
                                usersList.setAdapter(adapter);

                                usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                                        Intent intent = new Intent(Users_search.this, User_profile.class);
                                        intent.putExtra("Email", arrayList.get((int) id).get("Email"));
                                        startActivity(intent);
                                    }
                                });
                            }
                        }
                    }
                }
            }
        });
    }

    void SetSubscriptions(){
        Log.d("SearchUsers", "Search calling");
        usersList.setAdapter(null);
        arrayList.clear();
        final FirebaseUser cus = mAuth.getCurrentUser();
        final DocumentReference currentUser = users.document(cus.getEmail());

        users.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        for (final QueryDocumentSnapshot document : task.getResult()) {
                            currentUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot doc = task.getResult();
                                        if (doc.exists()) {
                                            Log.d("Search", "DocumentSnapshot data: " + doc.getData());
                                            ArrayList<String> mySubscriptions = (ArrayList) doc.get("subscriptions");
                                            for (int i = 0; i < mySubscriptions.size(); i++) {
                                                if (mySubscriptions.get(i).equals(document.getId())) {
                                                    Log.d("MortyList", document.getId() + " => " + document.getData());
                                                    map = new HashMap<>();
                                                    map.put("Email", document.getId());
                                                    map.put("Name", String.valueOf(document.get("name")));
                                                    String testsCount = String.valueOf(document.get("tests_count"));
                                                    if (testsCount.equals("null") || testsCount.equals("0")) {
                                                        map.put("TestsCount", "нет тестов");
                                                    } else {
                                                        if (document.getLong("tests_count") <= 20) {
                                                            if (testsCount.equals("1")) {
                                                                map.put("TestsCount", "1 тест");
                                                            } else {
                                                                if (testsCount.equals("2") || testsCount.equals("3") || testsCount.equals("4")) {
                                                                    map.put("TestsCount", testsCount + " теста");
                                                                } else {
                                                                    map.put("TestsCount", testsCount + " тестов");
                                                                }
                                                            }
                                                        } else {
                                                            if (testsCount.endsWith("1")) {
                                                                map.put("TestsCount", testsCount + " тест");
                                                            } else {
                                                                if (testsCount.endsWith("2") || testsCount.endsWith("3") || testsCount.endsWith("4")) {
                                                                    map.put("TestsCount", testsCount + " теста");
                                                                } else {
                                                                    map.put("TestsCount", testsCount + " тестов");
                                                                }
                                                            }
                                                        }
                                                    }
                                                    Log.d("users_testsCount", map.get("TestsCount"));
                                                    ArrayList<String> subscribers = (ArrayList<String>) document.get("subscribers");
                                                    ArrayList<String> subscriptions = (ArrayList<String>) document.get("subscriptions");
                                                    String subs = subscribers.size() + "/" + subscriptions.size();
                                                    map.put("Subs", subs);

                                                    arrayList.add(map);
                                                    SimpleAdapter adapter = new SimpleAdapter(Users_search.this, arrayList, R.layout.users_item,
                                                            new String[]{"Email", "TestsCount", "Name", "Subs"},
                                                            new int[]{R.id.user_email, R.id.tests_count, R.id.user_name, R.id.subscribers_subscriptions});
                                                    usersList.setAdapter(adapter);

                                                    usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                        @Override
                                                        public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                                                            Intent intent = new Intent(Users_search.this, User_profile.class);
                                                            intent.putExtra("Email", arrayList.get((int) id).get("Email"));
                                                            startActivity(intent);
                                                        }
                                                    });
                                                }
                                            }
                                        } else {
                                                Log.d("Search", "No such document");
                                            }
                                        } else {
                                            Log.d("Search", "get failed with ", task.getException());
                                        }
                                    }
                                });
                        }
                    }
                }
            }
        });
    }

    void SearchSubscriptions(final String name){
        Log.d("SearchUsers", "Search calling");
        usersList.setAdapter(null);
        arrayList.clear();
        final FirebaseUser cus = mAuth.getCurrentUser();
        final DocumentReference currentUser = users.document(cus.getEmail());

        users.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        for (final QueryDocumentSnapshot document : task.getResult()) {
                            if (String.valueOf(document.get("name")).contains(name)) {
                                currentUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot doc = task.getResult();
                                            if (doc.exists()) {
                                                Log.d("Search", "DocumentSnapshot data: " + doc.getData());
                                                ArrayList<String> mySubscriptions = (ArrayList) doc.get("subscriptions");
                                                for (int i = 0; i < mySubscriptions.size(); i++) {
                                                    if (mySubscriptions.get(i).equals(document.getId())) {
                                                        Log.d("MortyList", document.getId() + " => " + document.getData());
                                                        map = new HashMap<>();
                                                        map.put("Email", document.getId());
                                                        map.put("Name", String.valueOf(document.get("name")));
                                                        String testsCount = String.valueOf(document.get("tests_count"));
                                                        if (testsCount.equals("null") || testsCount.equals("0")) {
                                                            map.put("TestsCount", "нет тестов");
                                                        } else {
                                                            if (document.getLong("tests_count") <= 20) {
                                                                if (testsCount.equals("1")) {
                                                                    map.put("TestsCount", "1 тест");
                                                                } else {
                                                                    if (testsCount.equals("2") || testsCount.equals("3") || testsCount.equals("4")) {
                                                                        map.put("TestsCount", testsCount + " теста");
                                                                    } else {
                                                                        map.put("TestsCount", testsCount + " тестов");
                                                                    }
                                                                }
                                                            } else {
                                                                if (testsCount.endsWith("1")) {
                                                                    map.put("TestsCount", testsCount + " тест");
                                                                } else {
                                                                    if (testsCount.endsWith("2") || testsCount.endsWith("3") || testsCount.endsWith("4")) {
                                                                        map.put("TestsCount", testsCount + " теста");
                                                                    } else {
                                                                        map.put("TestsCount", testsCount + " тестов");
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        Log.d("users_testsCount", map.get("TestsCount"));
                                                        ArrayList<String> subscribers = (ArrayList<String>) document.get("subscribers");
                                                        ArrayList<String> subscriptions = (ArrayList<String>) document.get("subscriptions");
                                                        String subs = subscribers.size() + "/" + subscriptions.size();
                                                        map.put("Subs", subs);

                                                        arrayList.add(map);
                                                        SimpleAdapter adapter = new SimpleAdapter(Users_search.this, arrayList, R.layout.users_item,
                                                                new String[]{"Email", "TestsCount", "Name", "Subs"},
                                                                new int[]{R.id.user_email, R.id.tests_count, R.id.user_name, R.id.subscribers_subscriptions});
                                                        usersList.setAdapter(adapter);

                                                        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                            @Override
                                                            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                                                                Intent intent = new Intent(Users_search.this, User_profile.class);
                                                                intent.putExtra("Email", arrayList.get((int) id).get("Email"));
                                                                startActivity(intent);
                                                            }
                                                        });
                                                    }
                                                }
                                            } else {
                                                Log.d("Search", "No such document");
                                            }
                                        } else {
                                            Log.d("Search", "get failed with ", task.getException());
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public boolean onClose() {
        if(tabLayout.getSelectedTabPosition() == 0) SetAll();
        else SetSubscriptions();
        return false;
    }
}
