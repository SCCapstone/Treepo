package com.example.user.treepository;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;


/**
 * Created by brycebware on 11/24/16.
 */

public class TreeInfoFragment extends Fragment {
    private TextView textViewTreeInfo;
    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_treeinfo,container,false);
        textViewTreeInfo = (TextView) view.findViewById(R.id.textViewTreeInfo);
        Firebase ref = new Firebase(Config.FIREBASE_URL);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (MainActivity.currentTreeKey != "") {
                    DataSnapshot treeSnapshot = snapshot.child(MainActivity.currentTreeKey);
                    TreeObject tree = treeSnapshot.getValue(TreeObject.class);

                    //Adding it to a string
                    String string = "Type: " + tree.getType() + "\nAddress: " + tree.getAddress();
                    string += "\nAge: " + tree.getAge() + "\nHeight: " + tree.getHeight();
                    string += "\nExpected Lifespan: " + tree.getLifeSpan();
                    string += "\nDescription: " + tree.getDescription() + "\n\n";

                    //Displaying it on textview
                    textViewTreeInfo.setText(string);
                } else {
                    textViewTreeInfo.setText("No tree Selected");
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        return view;

    }
}
