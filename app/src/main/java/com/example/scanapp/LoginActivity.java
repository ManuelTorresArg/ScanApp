package com.example.scanapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.scanapp.databinding.LoginActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    LoginActivityMainBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_activity_main);

        binding = LoginActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mAuth = FirebaseAuth.getInstance();

        binding.progressBar.setVisibility(View.INVISIBLE);

        binding.loginBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.progressBar.setVisibility(View.VISIBLE);

                String user_email = binding.editTextTextPersonName.getText().toString().trim();
                String user_pass = binding.editTextTextPassword.getText().toString().trim();


                if (user_email.isEmpty() || user_pass.isEmpty()) {
                    Toast.makeText(LoginActivity.this,"Datos Incorrectos", Toast.LENGTH_LONG).show();
                    binding.progressBar.setVisibility(View.INVISIBLE);
                } else {

                    loginUser(user_email,user_pass);
                }


            }
        });



    }

    private void loginUser(String user_email, String user_pass) {

        mAuth.signInWithEmailAndPassword(user_email,user_pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    finish();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            Intent myIntent = new Intent(LoginActivity.this,WelcomeActivity.class);
                            LoginActivity.this.startActivity(myIntent);
                        }
                    }, 3000);   //3 segundos

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "Error de Credenciales", Toast.LENGTH_SHORT).show();
                binding.progressBar.setVisibility(View.INVISIBLE);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser ActiveUser = mAuth.getCurrentUser();

        if(ActiveUser != null) {
            Intent myIntent = new Intent(LoginActivity.this,WelcomeActivity.class);
            LoginActivity.this.startActivity(myIntent);
        }

    }
}