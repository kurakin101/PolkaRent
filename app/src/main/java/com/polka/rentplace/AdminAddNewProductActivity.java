package com.polka.rentplace;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetector;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.polka.rentplace.model.Users;
import com.polka.rentplace.prevalent.Prevalent;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import io.paperdb.Paper;

public class AdminAddNewProductActivity extends AppCompatActivity {

    private String CategoryName, Description, Price, Pname, saveCurrentDate, saveCurrentTime;
    private MaterialButton AddNewProductButton;
    private ImageView InputProductImage;
    private TextInputEditText InputProductName, InputProductCategory, InputProductSubCategory, InputProductPrice;
    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private String productRandomKey, downloadImageUrl;
    private StorageReference ProductImagesRef;
    private DatabaseReference ProductsRef;
    private ProgressDialog loadingBar;

    private FirebaseVisionLabelDetector mDetector;
    private static final String TAG = AdminAddNewProductActivity.class.getName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);

        CategoryName = getIntent().getExtras().get("category").toString();
        ProductImagesRef = FirebaseStorage.getInstance().getReference().child("Products Images");
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");


        AddNewProductButton = (MaterialButton) findViewById(R.id.add_new_product);
        InputProductImage = (ImageView) findViewById(R.id.select_product_image);
        InputProductName =  findViewById(R.id.product_name_ed);
        InputProductCategory =  findViewById(R.id.product_category_ed);
        InputProductSubCategory =  findViewById(R.id.product_subcategory_ed);
        InputProductPrice = findViewById(R.id.product_price_ed);
        loadingBar = new ProgressDialog(this);



        Intent intent = getIntent();
        String category = intent.getStringExtra("category");
        InputProductCategory.setText(category);


        InputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });


        AddNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateProductData();

            }
        });

    }



    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null){
            ImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), ImageUri);
                InputProductImage.setImageBitmap(bitmap);
                processImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mDetector != null) {
            try {
                mDetector.close();
            } catch (IOException e) {
                Log.e(TAG, "Exception thrown while trying to close Image Labeling Detector: " + e);
            }
        }
    }

    private void ValidateProductData(){
        Description = InputProductSubCategory.getText().toString();
        Price = InputProductPrice.getText().toString();
        Pname = InputProductName.getText().toString();


        if (ImageUri == null){
            Toast.makeText(this, "product image is mandatory", Toast.LENGTH_SHORT).show();

        }else if (TextUtils.isEmpty(Description)){
            Toast.makeText(this, "Please write product Description", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(Price)){
            Toast.makeText(this, "Please write product Price", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(Pname)){
            Toast.makeText(this, "Please write product Name", Toast.LENGTH_SHORT).show();
        }else{
            StoreProductInformation();
        }
    }

    private void StoreProductInformation() {

        loadingBar.setTitle("Adding New Products");
        loadingBar.setMessage("Please wait, while we are adding the new product.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

//        Calendar calendar = Calendar.getInstance();
//
//        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
//        saveCurrentDate = currentDate.format(calendar.getTime());
//
//        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
//        saveCurrentTime = currentTime.format(calendar.getTime());




        productRandomKey = UUID.randomUUID().toString();


        final StorageReference filePath = ProductImagesRef.child(ImageUri.getLastPathSegment() + productRandomKey + ".jpg");

        final UploadTask uploadTask =  filePath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                String message = e.toString();
                Toast.makeText(AdminAddNewProductActivity.this, "Error: " +  message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText(AdminAddNewProductActivity.this, "Products Image uploaded Successfully..", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                        if (!task.isSuccessful()){
                            throw  task.getException();
                        }
                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        if (task.isSuccessful()) {

                            downloadImageUrl = task.getResult().toString();

                            Toast.makeText(AdminAddNewProductActivity.this, "Products image save to Database Successfully..", Toast.LENGTH_SHORT).show();

                            String phone = Paper.book().read(Prevalent.UserPhoneKey);
                            String password = Paper.book().read(Prevalent.UserPasswordKey);

                            if (phone != "" && password != "") {

                                if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(password)) {

                                    SaveProductIntoToDatabase(phone, password);
                                }


                            }
                        }
                    }
                });

            }
        });


    }








    private void SaveProductIntoToDatabase(final String phone, final String password) {


        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();



        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                String users = "Users";
                if (dataSnapshot.child(users).child(phone).exists())
                {
                    Users usersData = dataSnapshot.child(users).child(phone).getValue(Users.class);

//                    if (usersData.getPhone().equals(phone))
//                    {
//                        if (usersData.getPassword().equals(password))
//                        {
//                           if (users.equals("Users"))
//                            {
                                HashMap<String, Object> productMap = new HashMap<>();
                                productMap.put("pid", productRandomKey);
                                productMap.put("date", saveCurrentDate);
                                productMap.put("time", saveCurrentTime);
                                productMap.put("description", Description);
                                productMap.put("image", downloadImageUrl);
                                productMap.put("category", CategoryName);
                                productMap.put("price", Price);
                                productMap.put("pname", Pname);
                                productMap.put("phone", phone);
                                productMap.put("password", password);

                                ProductsRef.child(productRandomKey).updateChildren(productMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){

                                                    Intent intent = new Intent(AdminAddNewProductActivity.this, AdminCategoryActivity.class);
                                                    startActivity(intent);


                                                    loadingBar.dismiss();
                                                    Toast.makeText(AdminAddNewProductActivity.this, "Products is added successfully.. ", Toast.LENGTH_SHORT).show();

                                                }else{
                                                    loadingBar.dismiss();
                                                    String message = task.getException().toString();
                                                    Toast.makeText(AdminAddNewProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        });
//                            }
//                        }
//                        else
//                        {
//                            loadingBar.dismiss();
//                            Toast.makeText(AdminAddNewProductActivity.this, "Password is incorrect.", Toast.LENGTH_SHORT).show();
//                        }
//                    }
                }
                else
                {
                    Toast.makeText(AdminAddNewProductActivity.this, "Account with this " + phone + " number do not exists.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




            }

    private void processImage() {
        if (InputProductImage.getDrawable() == null) {
            // ImageView has no image
        } else {
            // ImageView contains image
            Bitmap bitmap = ((BitmapDrawable) InputProductImage.getDrawable()).getBitmap();
            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
            mDetector = FirebaseVision.getInstance().getVisionLabelDetector();
            mDetector.detectInImage(image)
                    .addOnSuccessListener(
                            new OnSuccessListener<List<FirebaseVisionLabel>>() {
                                @Override
                                public void onSuccess(List<FirebaseVisionLabel> labels) {
                                    // Task completed successfully
                                    Object element0 = labels.get(0);
                                    InputProductSubCategory.setText(((FirebaseVisionLabel) element0).getLabel());
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Task failed with an exception
                                    Log.e(TAG, "Image labelling failed " + e);
                                }
                            });
        }
    }
}
