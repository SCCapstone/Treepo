package com.example.user.treepository;

import android.app.Application;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.Executor;

public class RegistrationFragment extends Fragment implements View.OnClickListener {

    private Button btnRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private ProgressDialog pd;
    private FirebaseAuth auth;
    private DatabaseReference database;
    View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_registration,container,false);
        auth = FirebaseAuth.getInstance();
        pd = new ProgressDialog(getActivity());
        btnRegister = (Button) view.findViewById(R.id.btnRegister);
        editTextEmail = (EditText) view.findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) view.findViewById(R.id.editTextPassword);
        database = FirebaseDatabase.getInstance().getReference().child("Users");

        btnRegister.setOnClickListener(this);

        // Inflate the layout for this fragment
        return view;

    }

    private void registerNewUser(){
        final String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            //Invalid registration
            Toast.makeText(getActivity(),"Please enter an email address", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            //Invalid registration
            Toast.makeText(getActivity(), "Please enter a password", Toast.LENGTH_SHORT).show();
            return;
        }

        pd.setMessage("Registering User...");
        pd.show();

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        FirebaseUser user = auth.getCurrentUser();
                        pd.dismiss();
                        if (task.isSuccessful()) {
//                            String userID = user.getUid();
//                            DatabaseReference currentUser = database.child(userID);
//                            currentUser.child("Email").setValue(email);
                            getActivity().finish();
                            startActivity(new Intent(getActivity(), MainActivity.class));
                            Toast.makeText(getActivity(), "Registration Successful", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity(), "Registration failed, please try again", Toast.LENGTH_LONG).show();
                        }


                    }
                });
    }

    @Override
    public void onClick(View v) {
        registerNewUser();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Registration");
    }
}
