package com.example.mywaste.David.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mywaste.Chesda.Model.Item;
import com.example.mywaste.David.model.ItemData;
import com.example.mywaste.R;
import com.example.mywaste.Viseth.RegisterActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // Firebase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Variables
    private Uri imageUri;
    private Bitmap bitmap;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    // Views
    private View v;
    private Spinner spinner;
    private Context addFragmentContext;
    private EditText itemName;
    private String category;
    private EditText amount;
    private EditText price;
    private EditText address;
    private EditText phone;
    private EditText description;
    private String imageUrl;
    private Button attachImageButton;
    private ImageView imageToBeUploaded;
    private String imageName;
    private Button uploadButton;




    public AddFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddFragment newInstance(String param1, String param2) {
        AddFragment fragment = new AddFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }


    //****************
    // onCreate
    //****************
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    //****************
    // onAttach
    //****************
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        addFragmentContext = context;
    }

    //****************
    // onCreateView
    //****************
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_add, container, false);

        // Spinner
        setUpSpinner();
        // Setup item
        inflateViews();

        amount.setKeyListener(DigitsKeyListener.getInstance(true,true));
        price.setKeyListener(DigitsKeyListener.getInstance(true,true));


        //Upload Button's Action
        uploadButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(itemName.getText().toString().isEmpty()
                        || amount.getText().toString().isEmpty()
                        || price.getText().toString().isEmpty()
                        || address.getText().toString().isEmpty()
                        || phone.getText().toString().isEmpty()
                        || description.getText().toString().isEmpty()
                        || imageUri == null){
                    Toast.makeText(addFragmentContext, "Please fill in all the information", Toast.LENGTH_LONG).show();
                }else{
                    // Upload data
                    new BackgroundImageResize(bitmap).execute(imageUri);
                }

            }
        });

        //attachImageButton's action
        attachImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePicture();
            }
        });

       return v;
    } // End of onCreateView()


    //****************
    // METHOD USE TO NAVIGATE TO GALLERY
    //****************
    private void choosePicture(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 7997);
    }

    //****************
    // METHOD USE FOR GETTING BACK IMAGE'S SOURCE BY CODE 7997
    //****************
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 7997 && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();
            imageToBeUploaded.setImageURI(imageUri);

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else{
            Toast.makeText(addFragmentContext, "Cannot upload image, try choosing another one", Toast.LENGTH_SHORT).show();
        }
    }

    //****************
    // METHOD USE FOR UPLOAD DATA EXCEPT IMAGE URL
    //****************
    private void uploadData(){
        Item item = new Item(itemName.getText().toString() ,
                address.getText().toString(),
                category,
                description.getText().toString(),
                FirebaseAuth.getInstance().getUid(),
                phone.getText().toString(),
                Integer.parseInt(amount.getText().toString()),
                Double.parseDouble(price.getText().toString()),
                imageUrl,
                imageName);
        // Add a new document with a generated ID
        db.collection("items").add(item).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                System.out.println("Document added successfully!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Fail to add document!");
            }
        });
    }

    //****************
    // METHOD USE FOR UPLOAD DATA WITH IMAGEURL
    //****************
    private void uploadDataAndPicture(byte[] bytes){
        final ProgressDialog pd = new ProgressDialog(addFragmentContext);
        pd.setTitle("Uploading...");
        pd.show();

        final String randomKey = UUID.randomUUID().toString();
        final StorageReference finalRef = FirebaseStorage.getInstance().getReference().child("images/" +randomKey);

        UploadTask uploadTask = finalRef.putBytes(bytes);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get url from Firebase storage
                        finalRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imageUrl = uri.toString();
                                // Dismiss ProgressDialog when successfully upload image
                                pd.dismiss();
                                Toast.makeText(addFragmentContext, "Upload Successfully", Toast.LENGTH_LONG).show();
                                // Get image's name
                                imageName = randomKey;
                                // upload data and url to FireStore
                                uploadData();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(addFragmentContext, "Upload Fail, TRY AGAIN", Toast.LENGTH_LONG).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progressPercent = (100.00 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                pd.setMessage("Percentage: " + (int) progressPercent + "%");
            }
        });
    }

    //****************
    // METHOD USE FOR CONVERTING BITMAP TO BYTES
    //****************
    private static byte[] getBytesFromBitmap (Bitmap bitmap, int quality){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,quality,stream);
        return stream.toByteArray();
    }

    //****************
    // METHOD USE TO PERFORM TASK AT THE BACKGROUND
    //****************
    private class BackgroundImageResize extends AsyncTask<Uri, Integer, byte[]> {

        private Bitmap bitmap;

        public BackgroundImageResize(Bitmap bitmap) {
            if (bitmap != null)
                this.bitmap = bitmap;
        }

        @Override
        protected byte[] doInBackground(Uri... uris) {

            return getBytesFromBitmap(bitmap, 75);
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            uploadDataAndPicture(bytes);
        }
    }

    //****************
    // METHOD USE FOR INFLATING VIEWS
    //****************
    private void inflateViews(){
        itemName = (EditText)v.findViewById(R.id.itemNameTextField);
        amount = (EditText)v.findViewById(R.id.amountTextField);
        price = (EditText)v.findViewById(R.id.priceTextField);
        address = (EditText)v.findViewById(R.id.addressTextField);
        phone = (EditText)v.findViewById(R.id.phoneTextField);
        description = (EditText)v.findViewById(R.id.descriptionTextField);
        imageToBeUploaded = (ImageView)v.findViewById(R.id.attachImageView);
        attachImageButton = (Button)v.findViewById(R.id.imageUploadButton);
        uploadButton = (Button)v.findViewById(R.id.uploadButton);
    }

    //****************
    // METHOD USE FOR SETTING UP SPINNER
    //****************
    private void setUpSpinner(){

        spinner = v.findViewById(R.id.categorySpinner);
        if (addFragmentContext != null){
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(addFragmentContext, R.array.categoryList, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(this);
        }else{
            System.out.println("No context found");
        }
    }

    //****************
    // OVERRIDE METHODS FOR SPINNER
    //****************
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        category = spinner.getSelectedItem().toString().toLowerCase();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    //****************
    // END OF OVERRIDE METHODS FOR SPINNER
    //****************

    //****************
    // DETACH FRAGMENT
    //****************
    @Override
    public void onDetach() {
        super.onDetach();
        addFragmentContext = null;
    }


}

