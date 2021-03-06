package com.example.user.treepository;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
 * SearchResult is the activity launched from the search menu button.
 * The activity runs the entire search function. The search filters based
 * on all tree's ages, heights, species, and their addresses. It returns
 * to a listview all tree addresses that match the criteria.
 */
public class SearchResult extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private ListView myList;
    private ArrayList<ArrayList<String>> databaseArray;
    ArrayAdapter<String> adapter;
    private EditText input;
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    ArrayList<String> searchResults;
    private String currentSpinnerSelection = "address";

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_search);
        searchResults = new ArrayList<String>(); // The filtered results will come here
        databaseArray = new ArrayList<ArrayList<String>>(); // This is a 2-dimensional arraylist that will hold all the tree information
        input = (EditText) findViewById(R.id.editSearchText);
        setTitle("Search Menu");
        Spinner spinner = (Spinner) findViewById(R.id.search_spinner); //Spinner to decide which element of the tree to filter based on
        ArrayAdapter<CharSequence> spinner_adapter = ArrayAdapter.createFromResource(this,R.array.spinner_options, android.R.layout.simple_spinner_item);
        spinner.setAdapter(spinner_adapter);
        spinner.setOnItemSelectedListener(this);
        Button button = (Button) findViewById(R.id.search_button); //Search button

        /* On click of the search button, the onClick function will decide which element the user wants to filter based on.
         * The onClick function then checks for all elements in the databaseArray that contain the search query as a substring.
         */
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                searchResults.clear();


                if(currentSpinnerSelection.equals("address")){
                    for(int i = 0; i < databaseArray.size(); i++){
                        if(databaseArray.get(i).get(1).toLowerCase().contains(input.getText().toString())){
                            searchResults.add(databaseArray.get(i).get(1));
                        }
                    }
                }
                else if(currentSpinnerSelection.equals("age")){
                    for(int i = 0; i < databaseArray.size(); i++){
                        if(databaseArray.get(i).get(2).toLowerCase().contains(input.getText().toString())){
                            searchResults.add(databaseArray.get(i).get(1));
                        }
                    }
                }
                else if(currentSpinnerSelection.equals("height")){
                    for(int i = 0; i < databaseArray.size(); i++){
                        if(databaseArray.get(i).get(3).toLowerCase().contains(input.getText().toString())){
                            searchResults.add(databaseArray.get(i).get(1));
                        }
                    }
                }
                else if(currentSpinnerSelection.equals("type")){
                    for(int i = 0; i < databaseArray.size(); i++){
                        if(databaseArray.get(i).get(4).toLowerCase().contains(input.getText().toString())){
                            searchResults.add(databaseArray.get(i).get(1));
                        }
                    }
                }

                adapter.notifyDataSetChanged();
            }
        });
        myList = (ListView) findViewById(R.id.listview);

        /* The onItemClick function reacts to an item in the listview being clicked.
         * It determines which tree was clicked and then sends the users to the information
         * page of the tree they selected.
         */

        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getAdapter().getItem(position).toString();
                for(int i = 0; i < databaseArray.size(); i++){
                    if(databaseArray.get(i).get(1).equals(selectedItem)){
                        MainActivity.currentTreeKey = databaseArray.get(i).get(0);
                        break;
                    }
                }
                Intent intent = new Intent(SearchResult.this, TreeInfoFragment.class);
                startActivity(intent);
            }
        });




        database.addChildEventListener(new ChildEventListener() {
            @Override

            // On child added populates the databaseArray
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ArrayList<String> tempArray = new ArrayList<String>();
                tempArray.add(dataSnapshot.getKey().toString());
                tempArray.add(dataSnapshot.child("address").getValue().toString());
                tempArray.add(dataSnapshot.child("age").getValue().toString());
                tempArray.add(dataSnapshot.child("height").getValue().toString());
                tempArray.add(dataSnapshot.child("type").getValue().toString());
                String address = dataSnapshot.child("address").getValue().toString();
                databaseArray.add(tempArray);
            }

            // onChildRemoved deleted the edited tree and replaces it with a new tree with the changes
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                for(int i = 0;i < databaseArray.size(); i++){
                    if(dataSnapshot.getKey().toString().equals(databaseArray.get(i).get(0))){
                        databaseArray.remove(i);
                        break;
                    }
                }

                ArrayList<String> tempArray = new ArrayList<String>();
                tempArray.add(dataSnapshot.getKey().toString());
                tempArray.add(dataSnapshot.child("address").getValue().toString());
                tempArray.add(dataSnapshot.child("age").getValue().toString());
                tempArray.add(dataSnapshot.child("height").getValue().toString());
                tempArray.add(dataSnapshot.child("type").getValue().toString());
                String address = dataSnapshot.child("address").getValue().toString();
                databaseArray.add(tempArray);
            }

            @Override
            // onChildRemoved removes the tree element
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                for(int i = 0;i < databaseArray.size(); i++){
                    if(dataSnapshot.getKey().toString().equals(databaseArray.get(i).get(0))){
                        databaseArray.remove(i);
                        break;
                    }
                }
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


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        currentSpinnerSelection = parent.getItemAtPosition(position).toString().toLowerCase();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}