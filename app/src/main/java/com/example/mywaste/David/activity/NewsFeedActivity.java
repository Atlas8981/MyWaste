package com.example.mywaste.David.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.mywaste.David.model.ItemData;
import com.example.mywaste.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class NewsFeedActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private ListView baseListView;
    private ArrayList<ItemData> dataArrayAdapter = new ArrayList<ItemData>();
    private CustomAdapter customAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private String getCategory;
    private Bundle dataBundle;
    private DecimalFormat df = new DecimalFormat("#0.00");

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);

        // Inflate listview
        baseListView = (ListView) findViewById(R.id.baseListView);
        // onItemSelected
        baseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                moveToDetailActivity(i);
            }
        });

        // Retrieve data from Firebase
        retrieveDataFromFirebase();

        // setAdapter
        customAdapter = new CustomAdapter(this, R.layout.custom_newsfeed, dataArrayAdapter);
        baseListView.setAdapter(customAdapter);

        // swipeLayout
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                dataArrayAdapter.clear();
                retrieveDataFromFirebase();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    //******************
    //Override method from SwipeRefreshLayout.OnRefreshListener class
    //******************
    @Override
    public void onRefresh() { }

    //******************
    // ViewHolder class for RecyclerView
    //******************
    private static class ViewHolder{
        TextView itemName;
        TextView address;
        TextView phone;
        TextView amount;
        TextView price;
        ImageView itemImage;
    }

    //******************
    // Custom adapter for RecyclerView
    //******************
    private class CustomAdapter extends ArrayAdapter<ItemData>{

        private Context mContext;
        private int mResource;
        private ArrayList<ItemData> dataObjects;

        public CustomAdapter(Context context, int resource, ArrayList<ItemData>dataObjects) {
            super(context, resource, dataObjects);
            this.mContext = context;
            this.mResource = resource;
            this.dataObjects = dataObjects;
        }

        public View getView(int pos, View cView, ViewGroup parent){
            ViewHolder viewHolder = new ViewHolder();
            if(cView == null){
                LayoutInflater inflater = LayoutInflater.from(mContext);
                cView = inflater.inflate(R.layout.custom_newsfeed,parent,false);
                viewHolder.itemName = (TextView)cView.findViewById(R.id.titleTextView);
                viewHolder.address = (TextView)cView.findViewById(R.id.addressTextView);
                viewHolder.phone = (TextView)cView.findViewById(R.id.phoneTextView);
                viewHolder.amount = (TextView)cView.findViewById(R.id.amountTextView);
                viewHolder.price = (TextView)cView.findViewById(R.id.priceTextView);
                viewHolder.itemImage = (ImageView)cView.findViewById(R.id.itemImageView);
                cView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) cView.getTag();
            }

            viewHolder.itemName.setText(dataObjects.get(pos).getName());
            viewHolder.address.setText("Address: "+dataObjects.get(pos).getAddress());
            viewHolder.phone.setText("Tel: "+dataObjects.get(pos).getPhone());
            viewHolder.amount.setText("Amount: "+String.valueOf(dataObjects.get(pos).getAmount()));
            viewHolder.price.setText(df.format(dataObjects.get(pos).getPrice()));
            Glide.with(mContext)
                    .load(dataObjects.get(pos).getImageurl())
                    .placeholder(R.drawable.loading)
                    .into(viewHolder.itemImage);

            return cView;
        }
    }

    //******************
    //Method use to inflate searchView and provide functionality
    //******************
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_listview, menu); //inflate search_listView.xml and display

        MenuItem menuItem=menu.findItem(R.id.searchMenu);
        SearchView searchView = (SearchView) menuItem.getActionView(); // return the objects of the class that is specified within the "actionViewClass" field

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) { // onEditingChanged

                ArrayList<ItemData> result = new ArrayList<>();
                for(ItemData data: dataArrayAdapter){
                    if(data.getName().toLowerCase().contains(s.toLowerCase())){
                        result.add(data);
                    }
                }
                customAdapter = new CustomAdapter(NewsFeedActivity.this, R.layout.custom_newsfeed, result);
                baseListView.setAdapter(customAdapter);

                return false;
            }

        });
        return super.onCreateOptionsMenu(menu);
    }

    //******************
    //Method to retrieve data from Firebase
    //******************
    private void retrieveDataFromFirebase(){
        db.collection("items").limit(15).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    for(DocumentSnapshot doc : list) {
                        dataBundle = getIntent().getExtras();
                        getCategory = dataBundle.getString("categorySelected");
                        if(doc.get("category").toString().equalsIgnoreCase(getCategory)){
                            ItemData itemData = doc.toObject(ItemData.class);
                            System.out.println(itemData.getName());
                            dataArrayAdapter.add(itemData);
                        }
                    }
                    customAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    //******************
    // Method use to move data to ItemDetailActivity by object
    //******************
    private void moveToDetailActivity(int pos) {
        Intent intent = new Intent();
        intent.setClass(this, ItemDetailActivity.class);
        intent.putExtra("ItemData", dataArrayAdapter.get(pos));
        startActivity(intent);
    }

} // End of class