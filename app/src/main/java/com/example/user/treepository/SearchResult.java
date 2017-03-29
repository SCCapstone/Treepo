package com.example.user.treepository;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchResult extends AppCompatActivity {

    private ListView myList;
    ArrayAdapter<String> adapter;
    EditText input;
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    ArrayList<String> searchResults = new ArrayList<String>();
    private HashMap<String, String> treeMarkers = new HashMap<String, String>();

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_search);
        setTitle("Search Menu");
        myList = (ListView) findViewById(R.id.listview);
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getAdapter().getItem(position).toString();
                MainActivity.currentTreeKey = treeMarkers.get(selectedItem);
                Intent intent = new Intent(SearchResult.this, TreeInfoFragment.class);
                startActivity(intent);
            }
        });


        input = (EditText) findViewById(R.id.editSearchText);

        database.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String address = dataSnapshot.child("address").getValue().toString();
                treeMarkers.put(address, dataSnapshot.getKey());
                searchResults.add(address);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.tree_location, searchResults);
        myList.setAdapter(adapter);


        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SearchResult.this.adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

}