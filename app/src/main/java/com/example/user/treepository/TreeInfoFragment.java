package com.example.user.treepository;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.ValueEventListener;


/**
 * Created by brycebware on 11/24/16.
 */

public class TreeInfoFragment extends Fragment implements View.OnClickListener {
    private TextView textViewTreeInfo;
    private Button btnTour;
    View view;
    DataSnapshot dSnap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_treeinfo,container,false);
        textViewTreeInfo = (TextView) view.findViewById(R.id.textViewTreeInfo);
        btnTour = (Button) view.findViewById(R.id.btnTour);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference treeRef = ref.child(MainActivity.currentTreeKey);

        btnTour.setOnClickListener(this);

        treeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String treeName = snapshot.child("type").getValue().toString();
                String address = snapshot.child("address").getValue().toString();
                String age = snapshot.child("age").getValue().toString();
                String description = snapshot.child("description").getValue().toString();
                String height = snapshot.child("height").getValue().toString();
                String lat = snapshot.child("latitude").getValue().toString();
                String lifeSpan = snapshot.child("lifeSpan").getValue().toString();
                String longitude = snapshot.child("longitude").getValue().toString();
                    //Adding it to a string
                    String string = "Type: " + treeName + "\nAddress: " + address;
                    string += "\nAge: " + age + "\nHeight: " + height;
                    string += "\nExpected Lifespan: " + lifeSpan;
                    string += "\nDescription: " + description + "\n\n";

                    //Displaying it on textview
                    textViewTreeInfo.setText(string);
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        return view;

    }

    @Override
    public void onClick(View v) {
//        Float newLat =  Float.parseFloat(dSnap.child("latitude").getValue().toString());
//        Float newLong = Float.parseFloat(dSnap.child("longitude").getValue().toString());
//        LatLng nextTree = new LatLng(newLat, newLong);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference treeRef = ref.child(MainActivity.currentTreeKey);
        treeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Float lat = Float.parseFloat(snapshot.child("latitude").getValue().toString());
                Float longitude = Float.parseFloat(snapshot.child("longitude").getValue().toString());

                Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?" + "daddr=" + lat + "," +longitude);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

    }
}
