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
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.firebase.ui.storage.images.FirebaseImageLoader;

import com.bumptech.glide.Glide;

import java.util.Locale;


/**
 * Created by brycebware on 11/24/16.
 */

public class TreeInfoFragment extends Fragment implements View.OnClickListener {
    private TextView textViewTreeInfo;
    private ImageView treeImageView;
    private Button buttonShare;
    View view;
    String treeName;
    String address;
    String age;
    String description;
    String height;
    String lat;
    String lifeSpan;
    String longitude;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_treeinfo,container,false);
        buttonShare = (Button) view.findViewById(R.id.buttonShare);
        buttonShare.setOnClickListener(this);

        textViewTreeInfo = (TextView) view.findViewById(R.id.textViewTreeInfo);
        treeImageView = (ImageView) view.findViewById(R.id.imageView2);

        //create reference to tree database
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference treeRef = ref.child(MainActivity.currentTreeKey);

        //create reference to tree image storage
        StorageReference storage = FirebaseStorage.getInstance().getReference();
        StorageReference treeImageRef = storage.child("tree_images/" + MainActivity.currentTreeKey + ".jpg");

        //load tree image into image view
        try {
            Glide.with(this)
                    .using(new FirebaseImageLoader())
                    .load(treeImageRef)
                    .into(treeImageView);
        } catch (Exception e) {
            //set picture to a default tree if download fails
           treeImageView.setImageResource(R.drawable.tree);
        }

        treeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                treeName = snapshot.child("type").getValue().toString();
                address = snapshot.child("address").getValue().toString();
                age = snapshot.child("age").getValue().toString();
                description = snapshot.child("description").getValue().toString();
                height = snapshot.child("height").getValue().toString();
                lat = snapshot.child("latitude").getValue().toString();
                lifeSpan = snapshot.child("lifeSpan").getValue().toString();
                longitude = snapshot.child("longitude").getValue().toString();
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
    public void onClick(View v) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Check out this " + treeName + " tree at " + address + ".\nDownload the" +
                " Treasured Trees App to find more trees near you!";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Treasured Trees");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));

    }


}
