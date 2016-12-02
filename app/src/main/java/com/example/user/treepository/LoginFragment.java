package com.example.user.treepository;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button btnLogin;
    private Button btnLogout;
    private FirebaseAuth auth;
    private ProgressDialog pd;
    private FirebaseAuth.AuthStateListener authListener;
    private DatabaseReference database;
    View view;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);
        editTextEmail = (EditText) view.findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) view.findViewById(R.id.editTextPassword);
        btnLogin = (Button) view.findViewById(R.id.btnLogin);
        btnLogout = (Button) view.findViewById(R.id.btnLogout);
        auth = FirebaseAuth.getInstance();
        pd = new ProgressDialog(getActivity());

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(firebaseAuth.getCurrentUser() != null){
                    Toast.makeText(getActivity(), "You are logged in as " + user.getEmail(), Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(getActivity(), "You are not currently logged in", Toast.LENGTH_SHORT).show();
            }
        };

        btnLogin.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        // Inflate the layout for this fragment
        return view;
    }

    private void logInUser() {
        String email = editTextEmail.getText().toString().trim();
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

        pd.setMessage("Signing In...");
        pd.show();

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        FirebaseUser user = auth.getCurrentUser();
                        pd.dismiss();
                        if(task.isSuccessful()){
                            getActivity().finish();
                            startActivity(new Intent(getActivity(), MainActivity.class));
                            //Toast.makeText(getActivity(), "Welcome " + user.getEmail(), Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getActivity(), "Login failed, please try again", Toast.LENGTH_LONG).show();
                        }


                    }
                });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnLogin)
            logInUser();
        else if(v.getId() == R.id.btnLogout)
            logOutUser();
    }

    private void logOutUser() {
        auth.signOut();
        getActivity().finish();
        startActivity(new Intent(getActivity(), MainActivity.class));
        Toast.makeText(getActivity(), "Logout successful", Toast.LENGTH_SHORT).show();
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
