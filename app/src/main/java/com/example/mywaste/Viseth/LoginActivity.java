package com.example.mywaste.Viseth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mywaste.David.activity.HomeActivity;
import com.example.mywaste.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button login = (Button) findViewById(R.id.loginBtn);
        login.setOnClickListener(runlogin);
    }

    private View.OnClickListener runlogin = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            loginmethod();
        }
    };

    private void loginmethod() {

        FirebaseAuth auth = FirebaseAuth.getInstance();

        //Register Fields
        EditText inputemail = (EditText) findViewById(R.id.emailEditText);
        EditText inputpassword = (EditText) findViewById(R.id.passwordEditText);

        //convert to String
        String email = inputemail.getText().toString();
        String password = inputpassword.getText().toString();

        //Login User
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Successfully Log In", Toast.LENGTH_LONG).show();
//                    Start New Activity
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    finish();
                }else {
                    Toast.makeText(LoginActivity.this,"Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}