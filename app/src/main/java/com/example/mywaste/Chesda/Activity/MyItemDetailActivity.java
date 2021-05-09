package com.example.mywaste.Chesda.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.mywaste.Chesda.Model.Item;
import com.example.mywaste.R;

public class MyItemDetailActivity extends AppCompatActivity {

    private TextView nameText,priceText,amountText,descriptionText,addressText,categoryText,phoneText,itemIdText;
    private ImageView bigImage;
    private Bundle bundle;
    private Item mitem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_item_detail);
        bundle = getIntent().getExtras();
        mitem = new Item();
        initializeUI();
        putDataIntoViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.edit) {
            Intent intent = new Intent(MyItemDetailActivity.this,EditMyItemActivity.class);
            intent.putExtra("myitem",mitem);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);

    }

    private void initializeUI(){
        nameText = findViewById(R.id.nameText);
        priceText = findViewById(R.id.priceText);
        amountText = findViewById(R.id.amountText);
        descriptionText = findViewById(R.id.descriptionText);
        addressText = findViewById(R.id.addressText);
        categoryText = findViewById(R.id.categoryText);
        phoneText = findViewById(R.id.phoneText);
        itemIdText = findViewById(R.id.itemIdText);
        bigImage = findViewById(R.id.bigImage);

    }

    private void putDataIntoViews(){

        if (bundle != null) {
            mitem = (Item) bundle.get("item");

            nameText.setText(mitem.getName());
            phoneText.setText(mitem.getPhone());
            amountText.setText("Amount: " + String.valueOf(mitem.getAmount()));
            priceText.setText(String.valueOf(mitem.getPrice()));
            addressText.setText(mitem.getAddress());
            descriptionText.setText(mitem.getDescription());
            categoryText.setText(mitem.getCategory());
            itemIdText.setText(mitem.getItemid());

            Glide.with(this)
                    .load(mitem.getImageurl())
                    .placeholder(R.drawable.ic_baseline_camera_alt_24)
                    .into(bigImage);

        }


    }
}