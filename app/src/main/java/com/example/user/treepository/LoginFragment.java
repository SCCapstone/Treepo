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


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button btnLogin;
    private FirebaseAuth auth;
    private ProgressDialog pd;
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
        auth = FirebaseAuth.getInstance();
        pd = new ProgressDialog(getActivity());

//        if(auth.getCurrentUser() != null){
//            getActivity().finish();
//            startActivity(new Intent(getActivity(), MainActivity.class));
//        }

        btnLogin.setOnClickListener(this);
        // Inflate the layout for this fragment
        return view;
    }

    private void logInUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        final FirebaseUser user = auth.getCurrentUser();

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
                        pd.dismiss();
                        if(task.isSuccessful()){
                            getActivity().finish();
                            startActivity(new Intent(getActivity(), MainActivity.class));
                            Toast.makeText(getActivity(), "Welcome " + user.getEmail(), Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getActivity(), "Login failed, please try again", Toast.LENGTH_LONG).show();
                        }


                    }
                });
    }

    @Override
    public void onClick(View v) {
        logInUser();
    }


}
