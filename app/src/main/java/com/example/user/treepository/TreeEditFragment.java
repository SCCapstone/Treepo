package com.example.user.treepository;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.Firebase;

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
        return view;

    }
    public void onClick(View v) {

        //Creating firebase object
        Firebase ref = new Firebase(Config.FIREBASE_URL);

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
        ref.child("Tree").setValue(tree);
    }
}

