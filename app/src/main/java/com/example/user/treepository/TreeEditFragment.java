package com.example.user.treepository;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.net.Uri;
import android.provider.MediaStore;
import android.graphics.Bitmap;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
    private EditText editTextLongitude;
    private EditText editTextLatitude;
    private Button buttonSubmit;
    private Button buttonChooseImage;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private ProgressDialog pd;
    private Uri pathToImage;
    private ImageView imageSelectionView;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.tree_edit,container,false);
        buttonSubmit = (Button) view.findViewById(R.id.buttonSubmit);
        buttonChooseImage = (Button) view.findViewById(R.id.buttonChooseImage);
        editTextType = (EditText) view.findViewById(R.id.editTextType);
        editTextAddress = (EditText) view.findViewById(R.id.editTextAddress);
        editTextAge = (EditText) view.findViewById(R.id.editTextAge);
        editTextHeight = (EditText) view.findViewById(R.id.editTextHeight);
        editTextLifespan = (EditText) view.findViewById(R.id.editTextLifespan);
        editTextDescription = (EditText) view.findViewById(R.id.editTextDescription);
        editTextLongitude = (EditText) view.findViewById(R.id.editTextLongitude);
        editTextLatitude = (EditText) view.findViewById(R.id.editTextLatitude);
        imageSelectionView = (ImageView) view.findViewById(R.id.imageSelectionView);
        buttonSubmit.setOnClickListener(this);
        buttonChooseImage.setOnClickListener(this);
        auth = FirebaseAuth.getInstance();
        pd = new ProgressDialog(getActivity());



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

        if (v == buttonChooseImage) {
            //open a picture selector on the device
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 234);

        } else if (v == buttonSubmit) {

            //Creating firebase object
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();

            //Getting values to store
            String type = editTextType.getText().toString().trim();
            String address = editTextAddress.getText().toString().trim();
            String age = editTextAge.getText().toString().trim();
            String height = editTextHeight.getText().toString().trim();
            String lifespan = editTextLifespan.getText().toString().trim();
            String description = editTextDescription.getText().toString().trim();
            Float latitude = Float.parseFloat(editTextLatitude.getText().toString().trim());
            Float longitude = Float.parseFloat(editTextLongitude.getText().toString().trim());

            //Creating Tree object
            TreeObject tree = new TreeObject();

            //Adding values
            tree.setType(type);
            tree.setAddress(address);
            tree.setAge(age);
            tree.setHeight(height);
            tree.setLifeSpan(lifespan);
            tree.setDescription(description);

            tree.setLatitude(latitude);
            tree.setLongitude(longitude);

            //Storing values to firebase
            //use of push generates unique key
            if (auth.getCurrentUser() != null) {

                //create a new key and location for the new tree
                DatabaseReference newRef = rootRef.push();
                //read information into the database
                newRef.setValue(tree);
                //add the image to firebase storage
                String newKey = newRef.getKey();
                storageRef.child("tree_images/" + newKey + ".jpg").putFile(pathToImage);

                startActivity(new Intent(getActivity(), MainActivity.class));
                Toast.makeText(getActivity(), "New tree added!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "You must log in to add a tree", Toast.LENGTH_SHORT).show();
            }


            //We need a notification that indicates a successful database write, can't figure
            //out how to check that yet before displaying this toast.
            //Toast.makeText(getActivity(),"Tree data written successfully", Toast.LENGTH_SHORT).show();
        }
    }

    //handle the result of the image selection
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 234 && resultCode == android.app.Activity.RESULT_OK &&
                data != null && data.getData() != null) {
            pathToImage = data.getData();
            try {
               imageSelectionView.setImageURI(null);
               imageSelectionView.setImageURI(pathToImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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

