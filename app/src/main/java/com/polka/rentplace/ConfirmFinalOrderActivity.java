package com.polka.rentplace;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
<<<<<<< HEAD
import com.polka.rentplace.model.Cart;
=======
import com.google.firebase.storage.FirebaseStorage;
>>>>>>> 4c6cadcc433fc4cccfbb2764dc89713ce4d8141e
import com.polka.rentplace.prevalent.Prevalent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
<<<<<<< HEAD
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
=======
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
>>>>>>> 4c6cadcc433fc4cccfbb2764dc89713ce4d8141e

public class ConfirmFinalOrderActivity extends AppCompatActivity {

    private TextInputEditText nameEditText, lastNameEditTxt, fatherNameEditTxt, phoneEditText, addressEditText, passportEditTxt;
    private MaterialButton confirmOrderBtn;
    private FirebaseDatabase mFirebaseInstance;

    private String totalAmount = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);


        totalAmount = getIntent().getStringExtra("Total Price");
        Toast.makeText(this, "Total Price = " + totalAmount, Toast.LENGTH_SHORT).show();

        confirmOrderBtn = (MaterialButton) findViewById(R.id.confirm_final_order_btn);
        nameEditText = (TextInputEditText) findViewById(R.id.shipment_name_ed);
        lastNameEditTxt = (TextInputEditText) findViewById(R.id.shipment_last_name_ed);
        fatherNameEditTxt = (TextInputEditText) findViewById(R.id.shipment_fathers_name_ed);
        phoneEditText = (TextInputEditText) findViewById(R.id.shipment_phone_ed);
        addressEditText = (TextInputEditText) findViewById(R.id.shipment_address_ed);
        passportEditTxt = (TextInputEditText) findViewById(R.id.shipment_passport_ed);

        DatabaseReference UserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.child("image").exists()){
                        String name = dataSnapshot.child("name").getValue().toString();
                        String Lname = dataSnapshot.child("lastName").getValue().toString();
                        String Fname = dataSnapshot.child("fatherName").getValue().toString();
                        String passport = dataSnapshot.child("passport").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();

                        nameEditText.setText(name);
                        lastNameEditTxt.setText(Lname);
                        fatherNameEditTxt.setText(Fname);
                        phoneEditText.setText(phone);
                        addressEditText.setText(address);
                        passportEditTxt.setText(passport);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference cartRef;
                cartRef = FirebaseDatabase.getInstance().getReference()
                        .child("Cart List")
                        .child("User View")
                        .child(Prevalent.currentOnlineUser.getPhone())
                        .child("Products");

                cartRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            String shippingState = dataSnapshot.getValue().toString();

                            List <String> l =  new ArrayList<>();
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                l.add(dataSnapshot1.child("phone").getValue().toString());
                            }
                            phoneEditText.setText(TextUtils.join(",", l));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


//                Check();
            }
        });

    }

    private void Check() {
        if (TextUtils.isEmpty(nameEditText.getText().toString())){
            Toast.makeText(this, "Please provide your Full name.", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(lastNameEditTxt.getText().toString())){
            Toast.makeText(this, "Please provide your LastName.", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(fatherNameEditTxt.getText().toString())){
            Toast.makeText(this, "Please provide your FatherName.", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(phoneEditText.getText().toString())){
            Toast.makeText(this, "Please provide your Phone Number.", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(addressEditText.getText().toString())){
            Toast.makeText(this, "Please provide your Address.", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(passportEditTxt.getText().toString())){
            Toast.makeText(this, "Please provide your Passport.", Toast.LENGTH_SHORT).show();
        }else{
            ConfirmOrder();
        }

    }

    private void ConfirmOrder() {
        final String saveCurrentDate, saveCurrentTime;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

<<<<<<< HEAD
=======
        final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(Prevalent.currentOnlineUser.getPhone());




        HashMap<String, Object> orderMap = new HashMap<>();
>>>>>>> 4c6cadcc433fc4cccfbb2764dc89713ce4d8141e

        DatabaseReference cartRef;
        cartRef = FirebaseDatabase.getInstance().getReference()
                .child("Cart List")
                .child("User View")
                .child(Prevalent.currentOnlineUser.getPhone())
                .child("Products");

        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String shippingState = dataSnapshot.getValue().toString();

                    List<String> l =  new ArrayList<>();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        l.add(dataSnapshot1.child("phone").getValue().toString());
                    }
                    String s = TextUtils.join(" ", l);




                    final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference()
                            .child("Orders")
                            .child(Prevalent.currentOnlineUser.getPhone());



                    HashMap<String, Object> orderMap = new HashMap<>();

                    orderMap.put("totalAmount", totalAmount);
                    orderMap.put("name", nameEditText.getText().toString());
                    orderMap.put("lastName", lastNameEditTxt.getText().toString());
                    orderMap.put("fatherName", fatherNameEditTxt.getText().toString());
                    orderMap.put("phoneOrder", phoneEditText.getText().toString());
                    orderMap.put("passport", passportEditTxt.getText().toString());
                    orderMap.put("address", addressEditText.getText().toString());
                    orderMap.put("date", saveCurrentDate);
                    orderMap.put("time", saveCurrentTime);
                    orderMap.put("phone", s);
                    orderMap.put("state", "not shipped");

                    orderRef.updateChildren(orderMap);

                    Toast.makeText(ConfirmFinalOrderActivity.this, "your final order has been placed successfully.", Toast.LENGTH_SHORT).show();

                    Intent intent1 = new Intent(ConfirmFinalOrderActivity.this, UserPayment.class);
                    startActivity(intent1);

//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if (task.isSuccessful()){
//                                FirebaseDatabase.getInstance().getReference()
//                                        .child("Cart List")
//                                        .child("User View")
//                                        .child(Prevalent.currentOnlineUser.getPhone())
//                                        .removeValue()
//                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<Void> task) {
//                                                if (task.isSuccessful()){
//                                                    Toast.makeText(ConfirmFinalOrderActivity.this, "your final order has been placed successfully.", Toast.LENGTH_SHORT).show();
//
//                                                    Intent intent1 = new Intent(ConfirmFinalOrderActivity.this, UserPayment.class);
//                                                    startActivity(intent1);
////
////                                          Intent intent = new Intent(ConfirmFinalOrderActivity.this, HomeActivity.class);
////                                          intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                                          startActivity(intent);
////                                          finish();
//                                                }
//                                            }
//                                        });
//                            }
//                        }
//                    });



                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





    }



}
