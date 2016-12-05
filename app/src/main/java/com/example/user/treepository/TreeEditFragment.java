package com.example.user.treepository;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by brycebware on 11/23/16.
 */

public class TreeEditFragment extends Fragment implements OnClickListener {
    private EditText editTextType;
    private EditText editTextAddress;
    private EditText editTextAge;
    private EditText editTextHeight;
    private EditText editTextLifespan;
    private EditText editTextDescription;
    private Button buttonSubmit;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.tree_edit,container,false);
        buttonSubmit = (Button) view.findViewById(R.id.buttonSubmit);
        editTextType = (EditText) view.findViewById(R.id.editTextType);
        editTextAddress = (EditText) view.findViewById(R.id.editTextAddress);
        editTextAge = (EditText) view.findViewById(R.id.editTextAge);
        editTextHeight = (EditText) view.findViewById(R.id.editTextHeight);
        editTextLifespan = (EditText) view.findViewById(R.id.editTextLifespan);
        editTextDescription = (EditText) view.findViewById(R.id.editTextDescription);
        buttonSubmit.setOnClickListener(this);
        auth = FirebaseAuth.getInstance();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (firebaseAuth.getCurrentUser() == null) {
                    Toast.makeText(getActivity(), "Please log in to edit a tree", Toast.LENGTH_SHORT).show();
                }
            }
        };

        return view;

    }
    public void onClick(View v) {

        //Creating firebase object
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        //Getting values to store
        String type = editTextType.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        String age = editTextAge.getText().toString().trim();
        String height = editTextHeight.getText().toString().trim();
        String lifespan = editTextLifespan.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        //Creating Tree object
        TreeObject tree = new TreeObject();

        //Adding values
        tree.setType(type);
        tree.setAddress(address);
        tree.setAge(age);
        tree.setHeight(height);
        tree.setLifeSpan(lifespan);
        tree.setDescription(description);

        //Storing values to firebase
        //use of push generates unique key
        rootRef.push().setValue(tree);

        //We need a notification that indicates a successful database write, can't figure
        //out how to check that yet before displaying this toast.
        //Toast.makeText(getActivity(),"Tree data written successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(authListener != null)
            auth.removeAuthStateListener(authListener);
    }
}

