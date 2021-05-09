package com.example.mywaste.Chesda.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mywaste.Chesda.Model.Item;
import com.example.mywaste.Chesda.Row.ListMyItemRowAdapter;
import com.example.mywaste.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MyItemActivity extends AppCompatActivity {

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference itemRef = firestore.collection("items");

    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private StorageReference storageReference = firebaseStorage.getReference().child("profile");

    private ListView myItemListView;
    private ProgressBar progressBar;
    private ListMyItemRowAdapter listMyItemRowAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Item> itemList;
    private Handler handler;
    private View footerView;
    boolean isLoading = false;
    boolean isOutOfData = false;
    Query next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_item);

        initializeUI();

        itemList = new ArrayList<Item>();

        getDataFromFireStore();
        

        myItemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i<itemList.size()) {
                    Intent intent = new Intent(MyItemActivity.this, MyItemDetailActivity.class);
                    intent.putExtra("item", itemList.get(i));
                    startActivity(intent);
                }

            }
        });

        myItemListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if (!swipeRefreshLayout.isRefreshing()) {

//                If scroll to the second last, Initiate function by calling thread
                    if (absListView.getLastVisiblePosition() == i2 - 1
                            && myItemListView.getCount() >= 0 && !isLoading && next != null && !isOutOfData) {
                        isLoading = true;

                        Thread thread = new ThreadGetMoreData();
                        thread.start();
                    }
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isOutOfData = false;
                itemList.clear();
                listMyItemRowAdapter = new ListMyItemRowAdapter(MyItemActivity.this,itemList);
                myItemListView.setAdapter(listMyItemRowAdapter);

                getDataFromFireStore();

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu_item,menu);
        return true;
    }
    public void searchMenuItemClicked(MenuItem item) {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(),SearchActivity.class);
        intent.putExtra("lists", (Serializable) itemList);
        startActivity(intent);

    }

    private void getDataFromFireStore(){
//        This commented function is for querying the specific item of the user
//        whereEqualTo("userid", FirebaseAuth.getInstance().getUid())

//        This function is use instead because I don't have user sign in
        itemRef.whereEqualTo("userid", FirebaseAuth.getInstance().getUid()).limit(10).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                    Item curItem = documentSnapshot.toObject(Item.class);
                    curItem.setItemid(documentSnapshot.getId());
                    itemList.add(curItem);
                    listMyItemRowAdapter = new ListMyItemRowAdapter(MyItemActivity.this,itemList);
                    myItemListView.setAdapter(listMyItemRowAdapter);
                }

                progressBar.setVisibility(ProgressBar.INVISIBLE);
                swipeRefreshLayout.setRefreshing(false);

//                Function below was follow from the firebase documentation

//                Get Ready for the next query
                if (queryDocumentSnapshots.size()>0) {
                    DocumentSnapshot lastVisible = queryDocumentSnapshots.getDocuments()
                            .get(queryDocumentSnapshots.size() - 1);

                    // Construct a new query starting at this document,
                    // get the next 10 data.
                    next = itemRef.whereEqualTo("userid", FirebaseAuth.getInstance().getUid()).limit(10)
                            .startAfter(lastVisible)
                            .limit(10);
                }else{
//                  if cursor cannot go further no need to query anything
//                    user can still always refresh to do the same thing
                    isOutOfData=true;
                    Toast.makeText(MyItemActivity.this, "No Data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    //    Thread to send message to initiate the data retrieval by calling handler
    public class  ThreadGetMoreData extends Thread{
        @Override
        public void run() {
            handler.sendEmptyMessage(0);
            ArrayList<Item> items = getMoreData();
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Message msg = handler.obtainMessage(1,items);
            handler.sendMessage(msg);
        }
    }


//    Handler will handle with adding View of loading progressbar into BottomListView
    public class Handler extends android.os.Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 0:
                    myItemListView.addFooterView(footerView);
                    break;
                case 1:
                    listMyItemRowAdapter.addListItemToAdapter((ArrayList<Item>)msg.obj);
                    myItemListView.removeFooterView(footerView);
                    isLoading = false;
                    break;
                default:
                    break;
            }
        }
    }


//    Function call when another 10 data needed from database
    private ArrayList<Item> getMoreData(){
        final ArrayList<Item> anotherListItem = new ArrayList<Item>();
        if (next != null) {
            next.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (queryDocumentSnapshots.size()>0) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Item curItem = documentSnapshot.toObject(Item.class);
                            curItem.setItemid(documentSnapshot.getId());
                            anotherListItem.add(curItem);
                        }


                        DocumentSnapshot lastVisible = queryDocumentSnapshots.getDocuments()
                                .get(queryDocumentSnapshots.size() - 1);

                        // Construct a new query starting at this document,

                        next = itemRef.whereEqualTo("userid", FirebaseAuth.getInstance().getUid()).limit(10)
                                .startAfter(lastVisible)
                                .limit(10);
                    }else {
                        if (!isOutOfData) {
                            Toast.makeText(MyItemActivity.this, "No More Data to Load", Toast.LENGTH_SHORT).show();
                            isOutOfData = true;
                        }
                    }
                }
            });
        }
//        itemRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                for (QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
//                    Item curItem = documentSnapshot.toObject(Item.class);
//                    anotherListItem.add(curItem);
//                }
//            }
//        });
        return anotherListItem;
    }

    private void initializeUI(){
        setTitle("My Item(s)");

        progressBar = findViewById(R.id.progressBar);
        myItemListView = findViewById(R.id.myitemListview);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        swipeRefreshLayout = findViewById(R.id.pullToRefresh);

        swipeRefreshLayout.setColorSchemeColors(Color.argb(100,51,140,48));

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerView = layoutInflater.inflate(R.layout.footerview_myitem_listview,null);
        handler = new Handler();
    }








}