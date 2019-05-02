package com.example.morty.myapplication2;

import android.app.SearchManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class Users_search extends AppCompatActivity implements SearchView.OnQueryTextListener{
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference users = db.collection("users");
    private final ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
    private  HashMap<String, String> map;
    ListView usersList;
    TextView testsCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_search);
        usersList = (ListView) findViewById(R.id.users_list);
        Search("");
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

        Search(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // User changed the text

        Search(newText);
        return false;
    }

    void Search(String name){
        Log.d("SearchUsers", "Search calling");
        usersList.setAdapter(null);
        arrayList.clear();

        Query q = users.whereGreaterThanOrEqualTo("name",name);

        q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                            if(testsCount.equals("null")) {
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

                            arrayList.add(map);
                            SimpleAdapter adapter = new SimpleAdapter(Users_search.this, arrayList, R.layout.users_item,
                                    new String[]{"Email", "TestsCount", "Name"},
                                    new int[]{R.id.user_email, R.id.tests_count, R.id.user_name});
                            usersList.setAdapter(adapter);

                            usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                                    Intent intent = new Intent(Users_search.this, User_profile.class);
                                    intent.putExtra("Email", arrayList.get((int) id).get("Email").toString());
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                }
            }
        });
    }
}
