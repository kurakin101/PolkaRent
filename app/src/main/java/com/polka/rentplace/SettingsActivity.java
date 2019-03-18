package com.polka.rentplace;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.PopupMenu;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.polka.rentplace.prevalent.Prevalent;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import androidx.core.widget.ContentLoadingProgressBar;
import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity  {


    private CircleImageView profileImageView;
    private TextInputEditText fullNameEditText, fullFNameEditText, fullLNameEditText, userPhoneEditText, addressEditText, passportSeries;
    private TextView updateTxt;
    private MaterialButton profileChangeTextBtn;
    private Uri imageUri;
    private String myUrl = "";
    private StorageTask uploadTask;
    private StorageReference storageProfilePictureReference;
    private String checker = "";
    private MaterialSearchBar searchBar;
    private ProgressBar spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        storageProfilePictureReference = FirebaseStorage.getInstance().getReference().child("Profile Pictures");

        profileImageView = (CircleImageView) findViewById(R.id.settings_profile_image);
        fullNameEditText = (TextInputEditText) findViewById(R.id.settings_full_name_ed);
        userPhoneEditText = (TextInputEditText) findViewById(R.id.settings_phone_ed);
        addressEditText = (TextInputEditText) findViewById(R.id.settings_address_ed);
        fullFNameEditText = (TextInputEditText) findViewById(R.id.settings_fathers_name_ed);
        fullLNameEditText = (TextInputEditText) findViewById(R.id.settings_last_name_ed);
        passportSeries = (TextInputEditText) findViewById(R.id.settings_passport_ed);
        profileChangeTextBtn = (MaterialButton) findViewById(R.id.profile_image_change_btn);
        updateTxt = (TextView) findViewById(R.id.update_txt);
        spinner = (ProgressBar)findViewById(R.id.progressBar1);

        spinner.setVisibility(View.GONE);

        userInfoDisplay(profileImageView, fullNameEditText, userPhoneEditText, addressEditText);
        updateTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTxt.setVisibility(View.INVISIBLE);
                spinner.setVisibility(View.VISIBLE);
                if (checker.equals("clicked")){
                    userInfoSaved();
                }else{
                    updateOnlyUserInfo();
                }
            }
        });

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker = "clicked";

                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(SettingsActivity.this);
            }
        });
        profileChangeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker = "clicked";

                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(SettingsActivity.this);


            }
        });

    }

    private void updateOnlyUserInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("name", fullNameEditText.getText().toString());
        userMap.put("lastName", fullLNameEditText.getText().toString());
        userMap.put("fatherName", fullFNameEditText.getText().toString());
        userMap.put("address", addressEditText.getText().toString());
        userMap.put("passport", passportSeries.getText().toString());
//        userMap.put("phone", userPhoneEditText.getText().toString());
        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);


        startActivity(new Intent(SettingsActivity.this,HomeActivity.class ));
        Toast.makeText(SettingsActivity.this, "Profile Update", Toast.LENGTH_SHORT).show();
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            profileImageView.setImageURI(imageUri);
        }else{
            Toast.makeText(this, "Erorr, try again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
            finish();
        }
    }

    private void userInfoSaved() {
        if (TextUtils.isEmpty(fullNameEditText.getText().toString())){
            Toast.makeText(this, "Name is mandatory", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(addressEditText.getText().toString())){
            Toast.makeText(this, "Name is address", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(userPhoneEditText.getText().toString())){
            Toast.makeText(this, "Name is address", Toast.LENGTH_SHORT).show();
        }else if (checker.equals("clicked")){
            uploadImage();
        }
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Upload Profile");
        progressDialog.setMessage("please wait.");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri != null){
            final  StorageReference fileRef = storageProfilePictureReference
                    .child(Prevalent.currentOnlineUser.getPhone() + ".jpg");
            uploadTask = fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            })
            .addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUrl = task.getResult();
                        myUrl = downloadUrl.toString();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("name", fullNameEditText.getText().toString());
                        userMap.put("lastName", fullLNameEditText.getText().toString());
                        userMap.put("fatherName", fullFNameEditText.getText().toString());
                        userMap.put("address", addressEditText.getText().toString());
                        userMap.put("passport", passportSeries.getText().toString());
                        userMap.put("phone", userPhoneEditText.getText().toString());
                        userMap.put("image", myUrl);
                        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

                        progressDialog.dismiss();

                        startActivity(new Intent(SettingsActivity.this,HomeActivity.class ));
                        Toast.makeText(SettingsActivity.this, "Profile Update", Toast.LENGTH_SHORT).show();
                        spinner.setVisibility(View.INVISIBLE);
                        finish();
                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(SettingsActivity.this, "Error: ", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            Toast.makeText(this, "image is not selected", Toast.LENGTH_SHORT).show();

        }

    }

    private void userInfoDisplay(final CircleImageView profileImageView, final EditText fullNameEditText, final EditText userPhoneEditText, final EditText addressEditText) {
        DatabaseReference UserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                        String image = dataSnapshot.child("image").getValue().toString();
                        String name = dataSnapshot.child("name").getValue().toString();
                        String Lname = dataSnapshot.child("lastName").getValue().toString();
                        String Fname = dataSnapshot.child("fatherName").getValue().toString();
                        String passport = dataSnapshot.child("passport").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();

                        if (name.isEmpty() || phone.isEmpty()){
                            fullNameEditText.setText(Prevalent.currentOnlineUser.getName());
                            userPhoneEditText.setText(Prevalent.currentOnlineUser.getPhone());
                        }else{
                            Picasso.get().load(image).into(profileImageView);
                            fullNameEditText.setText(name);
                            fullLNameEditText.setText(Lname);
                            fullFNameEditText.setText(Fname);
                            userPhoneEditText.setText(phone);
                            addressEditText.setText(address);
                            passportSeries.setText(passport);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
