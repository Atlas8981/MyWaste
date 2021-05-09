package com.example.mywaste.Chesda.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.mywaste.Chesda.Model.Item;
import com.example.mywaste.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class EditMyItemActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final CollectionReference itemRef = firestore.collection("items");

    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    private Spinner categorySpinner;
    private LinearLayout deleteBtn;
    private TextView nameEdt, amountEdt, priceEdt, addressEdt, phoneEdt, descriptionEdt;
    private ImageView previewImageView;
    private ProgressBar progressBar;
    private Bundle bundle;
    private Item mitem;
    private Uri imageURI;
    private Bitmap bitmap;
    private UploadTask uploadtask;

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_my_item);

        initializeUI();

        putDataIntoViews();

        progressDialog = new ProgressDialog(EditMyItemActivity.this);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditMyItemActivity.this);
                alertDialog.setTitle("Are you sure you want to delete this data ?");
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        progressDialog.setTitle("Deleting...");
                        progressDialog.show();
                        deleteDataInFirestore();
                    }
                });
                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alertDialog.show();
            }
        });

        previewImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.update_item_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.update) {
//            Start Showing the dialog box when update button is click
            progressDialog.setTitle("Updating...");
            progressDialog.show();
//            Check view isEmpty, NumberFormatException
            if (checkView()) {
                new CompressAndUpload().execute(bitmap);
            }else {
                progressDialog.dismiss();
            }

        }
        return super.onOptionsItemSelected(item);
    }


//  Function get open the image/file app of the phone
    public void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

//    The function to get the image from file
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
//            Image uri will be use for covert image to bitmap that will be upload to the database
            imageURI = data.getData();

            try {
//                Convert Image Uri To bitmap for compression and upload to database
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageURI);

//                Glide will have load image in or from cache
                Glide.with(this)
                        .load(imageURI)
                        .centerCrop()
                        .into(previewImageView);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    //    The first function or class to be call when update button is click
    private class CompressAndUpload extends AsyncTask<Bitmap, Integer, byte[]> {

        public CompressAndUpload(){}

        @Override
        protected byte[] doInBackground(Bitmap... bitmaps) {
//            progressDialog.setTitle("Compressing Image...");
//            Start Compressing Image in the back thread
            return getBytesFromBitmap(bitmap, 75);
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
//            progressDialog.setTitle("Image Compressed");

//            After Compress Upload the image to the Database
            uploadImageToFirestore(bytes);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {}
    }

    private void uploadImageToFirestore (byte[] bytes){
//        Generate Random Keyid as the name of image
        final String randomKey = UUID.randomUUID().toString();

//        Referencing the firebase storage
        final StorageReference ref = firebaseStorage.getReference().child("images/" + randomKey);

//        This function is follow from the documentation of firebase website
//        ref.putBytes(bytes) is for uploading but without any knowledge of the progress
//        That why Task uploadtask is created to check the progress and download the imageUrl after finish uploading
        uploadtask = ref.putBytes(bytes);

//        uploadtask = ref.putUri(Dak Uri jol mok)
        uploadtask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
//                To check the progress of upload in case file is too big
//                double progressPercent = (100.00 * snapshot.getBytesTransferred()/ snapshot.getTotalByteCount());
            }
        }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                After task is successful the firebase storage would be able generate a url of the uploaded image
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }

                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {

//                Delete Old Image after the new one is uploaded
                firebaseStorage.getReference().child("images/" + mitem.getImagename()).delete();

//                Get The new image url
                Uri url = task.getResult();

//                Gathering all the data from view in a organize manner
//                Not all data is needed to change
//                Some are here for referencing but all are useful
                String itemid = mitem.getItemid();
                String name = nameEdt.getText().toString();
                String address = addressEdt.getText().toString();
                String category = categorySpinner.getSelectedItem().toString();
                String description = descriptionEdt.getText().toString();
                String userid = mitem.getUserid();
                String phone = phoneEdt.getText().toString();
                String imageurl = url.toString();
                int amount = Integer.parseInt(amountEdt.getText().toString());
                double price = Double.parseDouble(priceEdt.getText().toString());

//                Put those data into a constructor of Item class
                Item uploadItem = new Item(itemid,name,address,category,description,userid,phone,amount,price,imageurl,randomKey);

//                Start Uploading data to firestore
                itemRef.document(mitem.getItemid()).set(uploadItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
//                        Dismiss the dialog box if or if not the data is uploaded because we want user to be control of what happen
                        progressDialog.dismiss();

                        if (task.isSuccessful()){
                            Toast.makeText(EditMyItemActivity.this, "Data Updated", Toast.LENGTH_SHORT).show();
//                            When upload is successful launch the myitemAcitivity without user ability to come back
                            launchActivityWithoutBack();
                        }else {
//                            Else stay in the same activity until everything workout
                            Toast.makeText(EditMyItemActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                        }

                    }
                });



            }
        });

    }

//    Getting Byte from bitmap to covert
    public static byte[] getBytesFromBitmap (Bitmap bitmap, int quality){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,quality,stream);
        return stream.toByteArray();
    }

//    FindViewById in the layout and other necessary things
    private void initializeUI(){
        setTitle("Edit Item");

        categorySpinner = findViewById(R.id.categorySpinner);
        deleteBtn = findViewById(R.id.deleteBtn);
        nameEdt = findViewById(R.id.nameEdt);
        amountEdt = findViewById(R.id.amountEdt);
        priceEdt = findViewById(R.id.priceEdt);
        addressEdt = findViewById(R.id.addressEdt);
        phoneEdt = findViewById(R.id.phoneEdt);
        descriptionEdt = findViewById(R.id.descriptionEdt);
        previewImageView = findViewById(R.id.previewImageView);
        progressBar = findViewById(R.id.progressBar2);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerAdapter);
    }

//    Data from Detail Activity will be place accordingly to each views
    private void putDataIntoViews(){
        bundle = getIntent().getExtras();
        mitem = new Item();
        if (bundle != null) {
            mitem = (Item) bundle.get("myitem");

            nameEdt.setText(mitem.getName());

//            This have to be done like this because spinner view is tricky
            for (int i = 0; i<categorySpinner.getCount();i++){
                if (categorySpinner.getItemAtPosition(i).toString().equalsIgnoreCase(mitem.getCategory())){
                    categorySpinner.setSelection(i);
                    System.out.println(categorySpinner.getItemAtPosition(i));
                    System.out.println(mitem.getCategory());
                    break;
                }
            }

            amountEdt.setText(String.valueOf(mitem.getAmount()));
            priceEdt.setText(String.valueOf(mitem.getPrice()));
            addressEdt.setText(mitem.getAddress());
            descriptionEdt.setText(mitem.getDescription());
            phoneEdt.setText(mitem.getPhone());

            ImageView cameraImage = findViewById(R.id.imageView5);
            cameraImage.setVisibility(View.INVISIBLE);

//            We have to get the bitmap data from imageview in case user did not change picture
//            Since item data did not include bitmap data and imageview cannot get bit then this has to be done
            Glide.with(this)
                    .load(mitem.getImageurl())
                    .placeholder(R.drawable.ic_baseline_camera_alt_24)
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            bitmap = ((BitmapDrawable)resource).getBitmap();
                            Glide.with(getApplicationContext())
                                    .load(bitmap)
                                    .placeholder(R.drawable.ic_baseline_camera_alt_24)
                                    .into(previewImageView);
                        }
                    });



        }


    }

//    Delete Specific Data from firebase Firestore
    private void deleteDataInFirestore(){

        itemRef.document(mitem.getItemid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()){
                    Toast.makeText(EditMyItemActivity.this, "Data Deleted", Toast.LENGTH_SHORT).show();
                    launchActivityWithoutBack();
                }else {
                    Toast.makeText(EditMyItemActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

//    Launch Activity that user cannot go back
    private void launchActivityWithoutBack(){
        Intent intent = new Intent(EditMyItemActivity.this, MyItemActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

//    A simple but long function body just to check if the views are all corrected and no bad data
    private boolean checkView (){
        String errorMsg = "Here";
        boolean flag = true;

        if (nameEdt.getText().toString().isEmpty()){
            nameEdt.setError(errorMsg);
            flag = false;
        }
        if (!amountEdt.getText().toString().isEmpty()){
            try {
                Integer.parseInt(amountEdt.getText().toString());
            }catch (NumberFormatException e){
                amountEdt.setError("Number Format Error");
                flag = false;
            }
        }else {
            amountEdt.setError(errorMsg);
            flag = false;
        }
        if (!priceEdt.getText().toString().isEmpty()) {
            try {
                Double.parseDouble(priceEdt.getText().toString());
            } catch (NumberFormatException e) {
                priceEdt.setError("Number Format Error");
                flag = false;
            }
        }else {
            priceEdt.setError(errorMsg);
            flag = false;
        }
        if (addressEdt.getText().toString().isEmpty()){
            addressEdt.setError(errorMsg);
            flag = false;
        }
        if (phoneEdt.getText().toString().isEmpty()){
            phoneEdt.setError(errorMsg);
            flag = false;
        }
        if (descriptionEdt.getText().toString().isEmpty()){
            descriptionEdt.setError(errorMsg);
            flag = false;
        }
        if (previewImageView.getDrawable()==null){
            Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
            flag = false;
        }
        if (categorySpinner.getSelectedItemPosition()<0){
            Toast.makeText(this, "Please Choose a Category", Toast.LENGTH_SHORT).show();
            flag = false;
        }

        return flag;
    }
}