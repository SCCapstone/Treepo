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
import android.text.Html;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by brycebware on 11/23/16.
 * class containing activity to edit existing trees
 */

public class EditExistingFragment extends AppCompatActivity implements View.OnClickListener {
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
    private ProgressDialog pd;
    private Uri pathToImage;
    private String pathToImageString;
    private ImageView imageSelectionView;
    private byte[] imageData;
    View view;

    static final int REQUEST_CHOOSE_IMAGE = 234;
    static final int REQUEST_IMAGE_CAPTURE = 567;

    private boolean imageChanged = false;
    private boolean imageChangedFromCamera = false;

    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_edit_existing);
        buttonSubmit = (Button) findViewById(R.id.buttonSubmit_edit);
        buttonChooseImage = (Button) findViewById(R.id.buttonChooseImage_edit);
        buttonTakePicture = (Button) findViewById(R.id.buttonTakePicture_edit);
        editTextType = (EditText) findViewById(R.id.editTextType_edit);
        editTextAddress = (EditText) findViewById(R.id.editTextAddress_edit);
        editTextAge = (EditText) findViewById(R.id.editTextAge_edit);
        editTextHeight = (EditText) findViewById(R.id.editTextHeight_edit);
        editTextLifespan = (EditText) findViewById(R.id.editTextLifespan_edit);
        editTextDescription = (EditText) findViewById(R.id.editTextDescription_edit);
        editTextLongitude = (EditText) findViewById(R.id.editTextLongitude_edit);
        editTextLatitude = (EditText) findViewById(R.id.editTextLatitude_edit);
        imageSelectionView = (ImageView) findViewById(R.id.imageSelectionView_edit);
        buttonSubmit.setOnClickListener(this);
        buttonChooseImage.setOnClickListener(this);
        buttonTakePicture.setOnClickListener(this);

        //load in existing tree information
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
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(imageSelectionView);
        } catch (Exception e) {
            //set picture to a default tree if download fails
            imageSelectionView.setImageResource(R.drawable.tree);
        }

        treeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //write in existing data
                editTextType.setText(snapshot.child("type").getValue().toString());
                editTextAddress.setText(snapshot.child("address").getValue().toString());
                editTextAge.setText(snapshot.child("age").getValue().toString());
                editTextDescription.setText(snapshot.child("description").getValue().toString());
                editTextHeight.setText(snapshot.child("height").getValue().toString());
                editTextLatitude.setText(snapshot.child("latitude").getValue().toString());
                editTextLifespan.setText(snapshot.child("lifeSpan").getValue().toString());
                editTextLongitude.setText(snapshot.child("longitude").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }
    public void onClick(View v) {

        if (v == buttonChooseImage) {
            //mark that image has been changed
            imageChanged = true;

            //open a picture selector on the device
            Intent choosePictureIntent = new Intent();
            choosePictureIntent.setType("image/*");
            choosePictureIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(choosePictureIntent, "Select Picture"), REQUEST_CHOOSE_IMAGE);

        } else if (v == buttonTakePicture) {
            //mark that image has been changed
            imageChangedFromCamera = true;

            //open the camera and take picture
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
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
            if (true) {

                //write the new values in
                DatabaseReference myRef = rootRef.child(MainActivity.currentTreeKey);
                //read information into the database
                myRef.setValue(tree);

                //add the image to firebase storage
                if (pathToImage != null && imageChanged == true) {
                    storageRef.child("tree_images/" + MainActivity.currentTreeKey + ".jpg").putFile(pathToImage);
                } else if (imageData != null && imageChangedFromCamera == true) {
                    storageRef.child("tree_images/" + MainActivity.currentTreeKey + ".jpg").putBytes(imageData);
                }
                startActivity(new Intent(this, TreeInfoFragment.class));
                Toast.makeText(this, "Changes Submitted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "You must log in to edit this tree", Toast.LENGTH_SHORT).show();
            }

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
                Toast.makeText(getBaseContext(), "updating image view", Toast.LENGTH_LONG).show();
                imageSelectionView.setImageURI(null);
                imageSelectionView.setImageURI(pathToImage);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //code to handle new photo from camera
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == android.app.Activity.RESULT_OK) {
            //display image on page
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageSelectionView.setImageBitmap(imageBitmap);

            //convet bitmap to byte array for reading into firebase
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            this.imageData = baos.toByteArray();
        }

    }

    //method creates a file to store an image taken by the user
    private File createImageFile() throws IOException {
        //create unique file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStorageDirectory();
        File image = new File(storageDir, "imageFileName" + ".jpg");

        pathToImageString = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.setTitle("Edit Existing Tree");
    }
}

