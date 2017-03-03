package com.example.user.treepository;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchResult extends AppCompatActivity implements View.OnClickListener {
    private EditText editQuery;
    private Button btnSearch;
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    public String query;
    private ArrayList<String> searchResults = new ArrayList<String>();
    private ArrayList<String> tempResults = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    ListView list;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_search);
        list = (ListView) findViewById(R.id.listview);

        database.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String value = dataSnapshot.child("address").getValue().toString();
                searchResults.add(value);
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
        editQuery = (EditText) findViewById(R.id.editSearchText);
        query = editQuery.getText().toString();
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(this);
        // Inflate the layout for this fragment
    }


    @Override
    public void onClick(View v) {
        query = editQuery.getText().toString();
        if(TextUtils.isEmpty(query)){
            //Empty search
            Toast.makeText(this,"Please enter an a search query", Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            tempResults.clear();
            for(int i=0;i<searchResults.size();i++){
                if(searchResults.get(i).toLowerCase().contains(query.toLowerCase())){
                    tempResults.add(searchResults.get(i));
                }
            }
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tempResults);
//            Toast.makeText(this,searchResults.size(), Toast.LENGTH_SHORT).show();

            list.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        }
    }

    private void searchDatabase() {


    }

}