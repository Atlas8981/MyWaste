package com.example.mywaste.David.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mywaste.David.activity.NewsFeedActivity;
import com.example.mywaste.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // Intent
    private Intent intent = new Intent();

    // Variables
    private Context homeFragmentContext;
    private View v;
    private Bundle bundle;

    private ImageView plasticImage;
    private ImageView furnitureImage;
    private ImageView electronicImage;
    OnCategorySelectedListener callback;

    //**********************************
    // Property observer
    //**********************************
    public void setOnHeadlineSelectedListener(OnCategorySelectedListener callback) {
        this.callback = callback;
    }

    // This interface can be implemented by the Activity, parent Fragment,
    // or a separate test implementation.
    public interface OnCategorySelectedListener {
        public void onCategorySelected(Class destinationActivity, String categorySelected);
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        homeFragmentContext = context;
        if (context instanceof OnCategorySelectedListener) {
            callback = (OnCategorySelectedListener) context; // attach listener to hosting activity
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement HomeFragment.OnCategorySelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_home, container, false);

        // plasticImageView
        plasticImage = (ImageView)v.findViewById(R.id.plasticImageView);
        plasticImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(homeFragmentContext != null){
                    callback.onCategorySelected(NewsFeedActivity.class, "plastic");
                }else{
                    System.out.println("No context found");
                }
            }
        });

        // furnitureImageView
        furnitureImage = (ImageView)v.findViewById(R.id.furnitureImageView);
        furnitureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(homeFragmentContext != null){
                    callback.onCategorySelected(NewsFeedActivity.class, "furniture");
                }else{
                    System.out.println("No context found");
                }
            }
        });

        // electronicImageView
        electronicImage = (ImageView)v.findViewById(R.id.electronicImageView);
        electronicImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(homeFragmentContext != null){
                    callback.onCategorySelected(NewsFeedActivity.class, "electronic");
                }else{
                    System.out.println("No context found");
                }
            }
        });

        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        homeFragmentContext = null;
    }
}