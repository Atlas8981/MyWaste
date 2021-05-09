package com.example.mywaste.Chesda.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mywaste.Chesda.Model.Item;
import com.example.mywaste.Chesda.Row.ListMyItemRowAdapter;
import com.example.mywaste.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference itemRef = firestore.collection("items");

    private EditText searchField;
    private List<Item> filteredItems;
    private ListMyItemRowAdapter searchActivityListAdapter;
    private ListView searchListview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setTitle("Search");
        searchField = findViewById(R.id.searchField);
        filteredItems = new ArrayList<>();
        searchListview = findViewById(R.id.searchedListview);



        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()>0){
                    Search(charSequence.toString().toLowerCase());
                }else{
                    filteredItems.clear();
                    searchActivityListAdapter = new ListMyItemRowAdapter(SearchActivity.this, filteredItems);
                    searchListview.setAdapter(searchActivityListAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        searchListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i<filteredItems.size()) {
                    Intent intent = new Intent(SearchActivity.this, MyItemDetailActivity.class);
                    intent.putExtra("item", filteredItems.get(i));
                    startActivity(intent);
                }
            }
        });

    }



    private void Search(final String s){
        itemRef.whereEqualTo("userid", FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                filteredItems.clear();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Item curItem = documentSnapshot.toObject(Item.class);
                    if (curItem.getName().toLowerCase().contains(s)) {
                        filteredItems.add(curItem);
                    }
                }
                if (s.length()>0) {
                    searchActivityListAdapter = new ListMyItemRowAdapter(SearchActivity.this, filteredItems);
                    searchListview.setAdapter(searchActivityListAdapter);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SearchActivity.this, "No Result", Toast.LENGTH_SHORT).show();
            }
        });
    }
}