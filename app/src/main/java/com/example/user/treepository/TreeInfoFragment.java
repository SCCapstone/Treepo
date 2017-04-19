package com.example.user.treepository;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;


/**
 * Created by brycebware on 11/24/16.
 */

public class TreeInfoFragment extends AppCompatActivity implements View.OnClickListener {
    private TextView textViewTreeName;
    private TextView textViewTreeAddress;
    private TextView textViewTreeAge;
    private TextView textViewTreeHeight;
    private TextView textViewTreeLifespan;
    private TextView textViewTreeDescription;


    private ImageView treeImageView;
    private Button buttonShare;
    private Button buttonEditExisting;
    private Button buttonDeleteTree;

    View view;
    String treeName;
    String address;
    String age;
    String description;
    String height;
    String lat;
    String lifeSpan;
    String longitude;

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_treeinfo);
        buttonShare = (Button) findViewById(R.id.buttonShare);
        buttonShare.setOnClickListener(this);
        buttonEditExisting = (Button) findViewById(R.id.buttonEditExisting);
        buttonEditExisting.setOnClickListener(this);
        buttonDeleteTree = (Button) findViewById(R.id.buttonDeleteTree);
        buttonDeleteTree.setOnClickListener(this);

        textViewTreeName = (TextView) findViewById(R.id.textViewTreeName);
        textViewTreeAddress = (TextView) findViewById(R.id.textViewTreeAddress);
        textViewTreeAge = (TextView) findViewById(R.id.textViewTreeAge);
        textViewTreeHeight = (TextView) findViewById(R.id.textViewTreeHeight);
        textViewTreeLifespan = (TextView) findViewById(R.id.textViewTreeLifespan);
        textViewTreeDescription = (TextView) findViewById(R.id.textViewTreeDescription);
        //textViewTreeName = (TextView) findViewByID(R.id.textViewTreeName);
        treeImageView = (ImageView) findViewById(R.id.imageView2);

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (firebaseAuth.getCurrentUser() == null) {
                    buttonEditExisting.setVisibility(View.GONE);
                    buttonDeleteTree.setVisibility(View.GONE);
                }
            }
        };

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

                String viewType = "<b>Type:</b> " + treeName;
                String viewAddress = "<b>Location:</b> " + address;
                String viewAge = "<b>Age:</b> " + age;
                String viewHeight = "<b>Height:</b> " + height;
                String viewLifespan = "<b>Expected Lifespan:</b> " + lifeSpan;
                String viewDescription = "<b>Description:</b> " + description ;

                    //Displaying it on textview
                textViewTreeName.setText(Html.fromHtml(viewType));
                textViewTreeAddress.setText(Html.fromHtml(viewAddress));
                textViewTreeAge.setText(Html.fromHtml(viewAge));
                textViewTreeHeight.setText(Html.fromHtml(viewHeight));
                textViewTreeLifespan.setText(Html.fromHtml(viewLifespan));
                textViewTreeDescription.setText(Html.fromHtml(viewDescription));

            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }
    public void onClick(View v) {


        if (v == buttonShare) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            Uri bmpUri;
            // Get access to the URI for the bitmap
            try {
                bmpUri = getLocalBitmapUri(treeImageView);
            } catch (NullPointerException e) {
                bmpUri = null;
            }
            if (bmpUri != null) {
                // Construct a ShareIntent with link to image
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                String shareBody = "Check out this " + treeName + " tree at " + address + ". Download the" +
                    " Treasured Trees App to find more trees near you! #TreasuredTrees";
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "#TreasuredTrees");
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                shareIntent.setType("image/*");
                // Launch sharing dialog for image
                startActivity(Intent.createChooser(shareIntent, "Share Via"));

            } else {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                String shareBody = "Check out this " + treeName + " tree at " + address + ".\nDownload the" +
                        " Treasured Trees App to find more trees near you!";
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "#TreasuredTrees");
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                shareIntent.setType("text/plain");
                // Launch sharing dialog for image
                startActivity(Intent.createChooser(shareIntent, "Share Via"));
            }
        } else if (v == buttonEditExisting) {

            Intent intent = new Intent(TreeInfoFragment.this, EditExistingFragment.class);
            startActivity(intent);
            setTitle("Detailed Tree Information");

        } else if (v == buttonDeleteTree) {
            //obtain reference to current tree
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            DatabaseReference treeRef = ref.child(MainActivity.currentTreeKey);

            //remove from database
            treeRef.removeValue();
            Toast.makeText(this, "Tree Has Been Deleted", Toast.LENGTH_SHORT).show();
        }
    }

    // Returns the URI path to the Bitmap displayed in specified ImageView
    public Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable;
        try {
            drawable = imageView.getDrawable();
        } catch (NullPointerException e) {
            drawable = null;
        }
        Bitmap bmp;
        try {
            bmp = ((GlideBitmapDrawable) imageView.getDrawable()).getBitmap();
        } catch (NullPointerException e) {
            bmp = null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file =  new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.setTitle("Detailed Tree Information");
    }
/*
        Uri bmpUri=null;
        Drawable drawable = treeImageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable){
            bmp = ((BitmapDrawable) treeImageView.getDrawable()).getBitmap();
        } else {
            bmp = null;
        }
        // Store image to default external storage directory

        try {
            File file =  new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("*");
        String shareBody = "Check out this " + treeName + " tree at " + address + ".\nDownload the" +
                " Treasured Trees App to find more trees near you!";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Treasured Trees");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
        */




}
