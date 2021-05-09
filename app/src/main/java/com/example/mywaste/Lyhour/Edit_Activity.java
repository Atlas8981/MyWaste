package com.example.mywaste.Lyhour;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.mywaste.David.activity.HomeActivity;
import com.example.mywaste.R;
import com.example.mywaste.Viseth.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Edit_Activity extends AppCompatActivity {

    private TextView textView;
    private TextView textView1;
    private TextView textView2;
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_myaccount);

        textView = (TextView) findViewById(R.id.edtname);
        textView1 = (TextView) findViewById(R.id.edtemail);
        textView2 = findViewById(R.id.edtpnumber);

        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection("users");


        final Bundle bundle = getIntent().getExtras();


        //receive activity
        textView.setText(bundle.getString("name"));
        textView1.setText(bundle.getString("email"));
        textView2.setText(bundle.getString("phone"));





        Button save = findViewById(R.id.btnsave);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                fullname,username,email,password,phonenumber,userid
                com.example.mywaste.Viseth.User user = new User(textView.getText().toString(),
                        textView1.getText().toString(),
                        textView2.getText().toString(), FirebaseAuth.getInstance().getUid());

                collectionReference.document(bundle.getString("userid")).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(Edit_Activity.this, "Account Updated", Toast.LENGTH_SHORT).show();
                            Edit_Activity.this.onBackPressed();
//                            startActivity(new Intent(Edit_Activity.this, HomeActivity.class));

                        }else {
                            Toast.makeText(Edit_Activity.this, "Something Went Horribly Wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}