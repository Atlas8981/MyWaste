package com.example.mywaste.Chesda.Row;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.mywaste.Chesda.Model.Item;
import com.example.mywaste.R;

import java.util.ArrayList;
import java.util.List;

public class ListMyItemRowAdapter extends ArrayAdapter<Item> implements Filterable {
    private Activity context;
    private List<Item> items;
    private ArrayList<Item> itemArrayList;

    public ListMyItemRowAdapter(Activity context, List<Item> items){
        super(context, R.layout.row_myitemadapter, items);
        this.context = context;
        this.items = items;
        itemArrayList = new ArrayList<Item>();
    }

    public void addListItemToAdapter (List<Item> list){
        items.addAll(list);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView;

        TextView nameView, addressView, phoneView, amountView, priceView;
        ImageView imageView;

        rowView = inflater.inflate(R.layout.row_myitemadapter, null);
        nameView = rowView.findViewById(R.id.nameView);
        addressView = rowView.findViewById(R.id.addressEdt);
        phoneView = rowView.findViewById(R.id.phoneEdt);
        amountView = rowView.findViewById(R.id.amountEdt);
        priceView = rowView.findViewById(R.id.priceEdt);
        imageView = rowView.findViewById(R.id.imageView);


//      Start setting data into each view
        Item curItem = items.get(position);

        nameView.setText(curItem.getName());
        addressView.setText(String.format("Address: %s", curItem.getAddress()));
        phoneView.setText(String.format("Tel: %s", curItem.getPhone()));
        amountView.setText(String.format("Amount: %s",curItem.getAmount()));
        priceView.setText(String.valueOf(curItem.getPrice()));

        Glide.with(rowView)
                .load(curItem.getImageurl())
                .centerCrop()
                .placeholder(R.drawable.ic_baseline_camera_alt_24)
                .into(imageView);
//        new ImageDownloadTasks(imageView).execute(curItem.getImageurl());


        return rowView;

    }



}
