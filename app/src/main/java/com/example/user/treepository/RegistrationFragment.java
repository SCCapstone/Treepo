package com.example.user.treepository;

import android.app.Application;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
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

import java.util.concurrent.Executor;

public class RegistrationFragment extends Fragment implements OnClickListener {

    private Button btnRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private ProgressDialog pd;
    private FirebaseAuth auth;
    //Application app;
    View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
          view = inflater.inflate(R.layout.fragment_registration,container,false);
        auth = FirebaseAuth.getInstance();
        //app  = new MainActivity().getApplication();
        //pd = new ProgressDialog(app);
        btnRegister = (Button) view.findViewById(R.id.btnRegister);
        editTextEmail = (EditText) view.findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) view.findViewById(R.id.editTextPassword);

       // btnRegister.setOnClickListener(this);

        // Inflate the layout for this fragment
        return view;

    }

    private void registerNewUser(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            //Invalid registration
            //Toast.makeText(,"Please enter an email address", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            //Invalid registration
            //Toast.makeText(app.getBaseContext(), "Please enter a password", Toast.LENGTH_SHORT).show();
            return;
        }

//        pd.setMessage("Registering User...");
//        pd.show();

//        auth.createUserWithEmailAndPassword(email,password)
//                .addOnCompleteListener((Executor) app, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if(task.isSuccessful()) {
//                            Toast.makeText(app.getBaseContext(), "Registration Successful", Toast.LENGTH_LONG).show();
//                        } else{
//                            Toast.makeText(app.getBaseContext(), "Registration failed, please try again", Toast.LENGTH_LONG).show();
//                        }
//
//
//                    }
//                });
    }

    @Override
    public void onClick(View v) {
        registerNewUser();

    }
}
