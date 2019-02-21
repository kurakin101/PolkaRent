package com.polka.rentplace;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetector;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetectorOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class AdminAddNewProductActivity extends AppCompatActivity {

    private String CategoryName, Description, Price, Pname, saveCurrentDate, saveCurrentTime;
    private Button AddNewProductButton;
    private ImageView InputProductImage;
    private EditText InputProductName, InputProductDescriotion, InputProductPrice;
    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private String productRandomKey, downloadImageUrl;
    private StorageReference ProductImagesRef;
    private DatabaseReference ProductsRef;
    private Bitmap mBitmap;
    private ProgressDialog loadingBar;
    public static final int RC_STORAGE_PERMS1 = 101;
    public static final int RC_SELECT_PICTURE = 103;
    public static final String ACTION_BAR_TITLE = "action_bar_title";
    public File imageFile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);

        CategoryName = getIntent().getExtras().get("category").toString();
        ProductImagesRef = FirebaseStorage.getInstance().getReference().child("Products Images");
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");


        AddNewProductButton = (Button) findViewById(R.id.add_new_product);
        InputProductImage = (ImageView) findViewById(R.id.select_product_image);
        InputProductName = (EditText) findViewById(R.id.product_name);
        InputProductDescriotion = (EditText) findViewById(R.id.product_description);
        InputProductPrice = (EditText) findViewById(R.id.product_price);
        loadingBar = new ProgressDialog(this);



        InputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                OpenGallery();

                checkStoragePermission(RC_STORAGE_PERMS1);

            }
        });


        AddNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ValidateProductData();

                if (mBitmap != null) {
                    FirebaseVisionLabelDetectorOptions options = new FirebaseVisionLabelDetectorOptions.Builder()
                            .setConfidenceThreshold(0.7f)
                            .build();
                    FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(mBitmap);
                    FirebaseVisionLabelDetector detector = FirebaseVision.getInstance().getVisionLabelDetector(options);
                    detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionLabel>>() {
                        @Override
                        public void onSuccess(List<FirebaseVisionLabel> labels) {
                            for (FirebaseVisionLabel label : labels) {
                                InputProductDescriotion.append(label.getLabel() + "\n");
                                InputProductDescriotion.append(label.getConfidence() + "\n\n");
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            InputProductDescriotion.setText(e.getMessage());
                        }
                    });
                }


            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case RC_STORAGE_PERMS1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    OpenGallery();
                } else {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }


    public void checkStoragePermission(int requestCode) {
        switch (requestCode) {
            case RC_STORAGE_PERMS1:
                int hasWriteExternalStoragePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (hasWriteExternalStoragePermission == PackageManager.PERMISSION_GRANTED) {
                    OpenGallery();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                }
                break;

        }
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
//            ImageUri = data.getData();
//            InputProductImage.setImageURI(ImageUri);

            switch (requestCode) {
                case RC_STORAGE_PERMS1:
                    checkStoragePermission(requestCode);
                    break;
                case RC_SELECT_PICTURE:
                    Uri dataUri = data.getData();
                    String path = MyHelper.getPath(this, dataUri);
                    if (path == null) {
                        mBitmap = MyHelper.resizeImage(imageFile, this, dataUri, InputProductImage);
                    } else {
                        mBitmap = MyHelper.resizeImage(imageFile, path, InputProductImage);
                    }
                    if (mBitmap != null) {
                        InputProductDescriotion.setText(null);
                        InputProductImage.setImageBitmap(mBitmap);
                    }

            }

        }
    }

    private void ValidateProductData(){
        Description = InputProductDescriotion.getText().toString();
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

                        if (task.isSuccessful()){

                            downloadImageUrl = task.getResult().toString();

                            Toast.makeText(AdminAddNewProductActivity.this, "Products image save to Database Successfully..", Toast.LENGTH_SHORT).show();

                            SaveProductIntoToDatabase();
                        }
                    }
                });

            }
        });


    }

    private void SaveProductIntoToDatabase() {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid", productRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("description", Description);
        productMap.put("image", downloadImageUrl);
        productMap.put("category", CategoryName);
        productMap.put("price", Price);
        productMap.put("pname", Pname);

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

    }
}
