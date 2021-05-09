package com.example.mywaste.Viseth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mywaste.Chesda.Activity.EditMyItemActivity;
import com.example.mywaste.Chesda.Activity.MyItemActivity;
import com.example.mywaste.David.activity.HomeActivity;
import com.example.mywaste.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();

        Button register = (Button) findViewById(R.id.signupBtn);
        register.setOnClickListener(runregister);

        Button signIn = findViewById(R.id.signinBtn);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(),LoginActivity.class);
                startActivity(intent);

            }
        });



    }

    private View.OnClickListener runregister = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            registermethod();
        }
    };

    private void registermethod() {

        //Register Fields
        EditText inputfullname = (EditText) findViewById(R.id.fullnameEditText);
        EditText inputusername = (EditText) findViewById(R.id.usernameEditText);
        EditText inputemail = (EditText) findViewById(R.id.emailEditText);
        EditText inputpassword = (EditText) findViewById(R.id.passwordEditText);
        EditText inputphonenumber = (EditText) findViewById(R.id.phonenumberEditText);
        Button signin = (Button) findViewById(R.id.signinBtn);

        //convert to String And Integer
        final String fullname = inputfullname.getText().toString();
        final String username = inputusername.getText().toString().trim();
        final String email = inputemail.getText().toString().trim();
        final String password = inputpassword.getText().toString().trim();
        final String phonenumber = inputphonenumber.getText().toString().trim();

        //Validate Field
        if (TextUtils.isEmpty(fullname)) {
            inputfullname.setError("Please fill in Fullname");
            return;
        } else if (TextUtils.isEmpty(username)) {
            inputusername.setError("Please fill in Username");
            return;
        }else if (TextUtils.isEmpty(email)) {
            inputemail.setError("Please fill in Email");
            return;
        }else if (TextUtils.isEmpty(password)) {
            inputpassword.setError("Please fill in the Password");
            return;
        }else if (password.length() < 6 ) {
            inputpassword.setError("Password Must be >= 6 Characters");
            return;
        }else if (TextUtils.isEmpty(phonenumber)) {
            inputphonenumber.setError("Please fill in Phonenumber");
            return;
        }

        //Create New User with email and password
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this,"Successfully Created" ,Toast.LENGTH_SHORT).show();
//                        Modify by combiner
                        AddDataIntoFirebase(fullname,username,email,password,phonenumber,auth.getUid());
                } else {
                        Toast.makeText(RegisterActivity.this,"Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }});
    }

    public void AddDataIntoFirebase(String fullname, String username, String email, String password, String phonenumber,String userid){

        FirebaseFirestore data = FirebaseFirestore.getInstance();

//        Modify by Combiner
        User userdata = new User(fullname,username,email,password,phonenumber,userid);
        data.collection("users").add(userdata).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
//                Start new Activity

                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }
}