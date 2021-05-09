package com.example.mywaste.David.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.mywaste.David.model.ItemData;
import com.example.mywaste.R;

public class ItemDetailActivity extends AppCompatActivity {

    private TextView itemDetailName;
    private ImageView itemDetailImage;
    private TextView itemDetailAmount;
    private TextView itemDetailPrice;
    private TextView itemDetailDescription;
    private TextView itemDetailAddress;
    private Button itemDetailPhone;

    private Intent intent = getIntent();
    private ItemData item;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        // Inflate views
        inflateView();

        // Set data into views
        setData();

        // itemDetailPhone's action for navigating to Phone
        itemDetailPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", item.getPhone(), null));
                startActivity(intent);
            }
        });

        // itemDetailAddress's action for navigating to Google Maps
        itemDetailAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?q="+ item.getAddress()));
                startActivity(intent);
            }
        });
    }

    //*********************************
    // Method use to inflate views from xml
    //*********************************
    private void inflateView(){
        itemDetailName = (TextView) findViewById(R.id.itemDetailTitle);
        itemDetailImage = (ImageView) findViewById(R.id.itemDetailImage);
        itemDetailAmount = (TextView) findViewById(R.id.itemDetailAmount);
        itemDetailPrice = (TextView)findViewById(R.id.itemDetailPrice);
        itemDetailDescription = (TextView) findViewById(R.id.itemDetailDescription);

        itemDetailAddress = (TextView) findViewById(R.id.itemDetailAddress);
        itemDetailAddress.setPaintFlags(itemDetailAddress.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        itemDetailPhone = (Button) findViewById(R.id.itemDetailPhone);
        itemDetailPhone.setPaintFlags(itemDetailPhone.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    //*********************************
    // Method use to set data from NewsFeedActivity into views
    //*********************************
    private void setData(){
        item = (ItemData) getIntent().getSerializableExtra("ItemData"); // get object
        if (item != null) {
            itemDetailName.setText(item.getName());
            Glide.with(ItemDetailActivity.this)
                    .load(item.getImageurl())
                    .centerCrop()
                    .placeholder(R.drawable.loading)
                    .into(itemDetailImage);

            itemDetailAmount.setText("Amount: " + Integer.toString(item.getAmount()));
            itemDetailPrice.setText(Double.toString(item.getPrice()));
            itemDetailDescription.setText(item.getDescription());
            itemDetailAddress.setText(item.getAddress().toLowerCase());
            itemDetailPhone.setText(item.getPhone());
        }
    }


} // End of class