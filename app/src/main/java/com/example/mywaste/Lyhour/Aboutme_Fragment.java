package com.example.mywaste.Lyhour;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.example.mywaste.Chesda.Activity.MyItemActivity;
import com.example.mywaste.David.activity.HomeActivity;
import com.example.mywaste.David.fragments.HomeFragment;
import com.example.mywaste.R;
import com.example.mywaste.Viseth.RegisterActivity;
import com.example.mywaste.Viseth.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


public class Aboutme_Fragment extends Fragment {

    private TextView textView;
    private TextView textView1;
    private TextView textView2;
    private com.example.mywaste.Viseth.User tempuser;

    private String id;
    private Button myItem;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collectionReference = db.collection("users");

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_aboutme, container, false);

//        Missing Features Added By _____________
        missingFeatures(view);

        //Register Field
        // final ImageView imageView1 = view.findViewById(R.id.imgview);
        textView= view.findViewById(R.id.txtviewname);
        textView1= view.findViewById(R.id.txtviewemail);
        textView2= view.findViewById(R.id.txtviewpnumber);





        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.edit_menu_item,menu);
        super.onCreateOptionsMenu(menu,inflater);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.edit:{
                Bundle bundle = new Bundle();
                String textview = textView.getText().toString();
                String textview1= textView1.getText().toString();
                String textview2=textView2.getText().toString();
                bundle.putString("name",textview);
                bundle.putString("email",textview1);
                bundle.putString("phone",textview2);
                bundle.putString("userid",id);
                Intent intent = new Intent();
                intent.setClass(getContext(), Edit_Activity.class);

                intent.putExtras(bundle);
                startActivity(intent);
            }break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void missingFeatures(View view){
        Button signOut= view.findViewById(R.id.buttonsignout);
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                if (auth.getUid() == null){
                    Toast.makeText(getContext(), "Sign Out", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(),HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }

            }
        });

        myItem = view.findViewById(R.id.myItemBtn);
        myItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getContext(), MyItemActivity.class);
                startActivity(intent);
            }
        });
    }



    @Override
    public void onStart() {
        super.onStart();
        //read data
        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot: queryDocumentSnapshots){

                    tempuser = documentSnapshot.toObject(User.class);
                    System.out.println(tempuser.toString());
                    if (tempuser !=null && tempuser.getUserid()!=null) {
                        if (tempuser.getUserid().equalsIgnoreCase(FirebaseAuth.getInstance().getUid())) {

                            textView.setText(tempuser.getFullname());
                            textView1.setText(tempuser.getEmail());
                            textView2.setText(tempuser.getPhonenumber());
                            id = documentSnapshot.getId();

                        }
                    }else {
                        Toast.makeText(getContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

    }
}