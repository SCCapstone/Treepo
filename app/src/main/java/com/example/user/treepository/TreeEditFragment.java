package com.example.user.treepository;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by brycebware on 11/23/16.
 * class to add a new tree
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
    private Button buttonTakePicture;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private ProgressDialog pd;
    private Uri pathToImage;
    private String pathToImageString;
    private ImageView imageSelectionView;
    private byte[] imageData;
    private boolean imageAdded;
    private boolean imageAddedFromCamera;
    View view;

    static final int REQUEST_CHOOSE_IMAGE = 234;
    static final int REQUEST_IMAGE_CAPTURE = 567;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.tree_edit,container,false);
        buttonSubmit = (Button) view.findViewById(R.id.buttonSubmit);
        buttonChooseImage = (Button) view.findViewById(R.id.buttonChooseImage);
        buttonTakePicture = (Button) view.findViewById(R.id.buttonTakePicture);
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
        buttonTakePicture.setOnClickListener(this);
        auth = FirebaseAuth.getInstance();
        pd = new ProgressDialog(getActivity());

        //check if a user is logged in
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Toast.makeText(getActivity(), "Please log in to add a tree", Toast.LENGTH_SHORT).show();
                }
            }
        };

        return view;

    }
    public void onClick(View v) {

        if (v == buttonChooseImage) {
            //mark image added as true
            imageAdded = true;

            //open a picture selector on the device
            Intent choosePictureIntent = new Intent();
            choosePictureIntent.setType("image/*");
            choosePictureIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(choosePictureIntent, "Select Picture"), REQUEST_CHOOSE_IMAGE);

        } else if (v == buttonTakePicture) {
            //mark that picture was taken
            imageAddedFromCamera = true;

            //open the camera and take picture
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                //create file to receive the photo
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                if (photoFile != null) {
                    Uri photoUri = Uri.fromFile(photoFile);
                    pathToImage = photoUri;
                    //initiate camera
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }

        } else if (v == buttonSubmit) {

            Float latitude = (float)0;
            Float longitude = (float)0;
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
            try {
                latitude = Float.parseFloat(editTextLatitude.getText().toString().trim());
            }
            catch (Exception e){
                latitude = (float)0;
            }
            try {
                longitude = Float.parseFloat(editTextLongitude.getText().toString().trim());
            }
            catch (Exception e){
                longitude = (float)0;
            }

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
            if (auth.getCurrentUser() != null && latitude != 0 && longitude != 0) {

                //create a new key and location for the new tree
                DatabaseReference newRef = rootRef.push();
                //read information into the database
                newRef.setValue(tree);
                //add the image to firebase storage
                String newKey = newRef.getKey();
                if (pathToImage != null && imageAdded == true) {
                    storageRef.child("tree_images/" + newKey + ".jpg").putFile(pathToImage);
                } else if (imageData != null && imageAddedFromCamera == true) {
                    storageRef.child("tree_images/" + newKey + ".jpg").putBytes(imageData);
                }

                startActivity(new Intent(getActivity(), MainActivity.class));
                Toast.makeText(getActivity(), "New tree added!", Toast.LENGTH_SHORT).show();
            }
            else if (latitude == 0 || longitude == 0){
                Toast.makeText(getActivity(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            }
            else {
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
        //code to handle choosing existing picture
        if (requestCode == REQUEST_CHOOSE_IMAGE && resultCode == android.app.Activity.RESULT_OK &&
                data != null && data.getData() != null) {
            pathToImage = data.getData();
            try {
               imageSelectionView.setImageURI(null);
               imageSelectionView.setImageURI(pathToImage);
            } catch (Exception e) {
                e.printStackTrace();
            }

        //code to handle new photo from camera
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == android.app.Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageSelectionView.setImageBitmap(imageBitmap);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            this.imageData = baos.toByteArray();

        }
    }

    //method creates a file to store an image taken by the user
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        return image;
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

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Add a Tree");
    }
}

