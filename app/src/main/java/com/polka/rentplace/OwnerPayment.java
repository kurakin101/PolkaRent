package com.polka.rentplace;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class OwnerPayment extends AppCompatActivity {
    EditText text;
    Button gen_btn;
    ImageView image;
    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_payment);

        fab = findViewById(R.id.fab);
        text=findViewById(R.id.text);
//        gen_btn=findViewById(R.id.btn);
        image=findViewById(R.id.image);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        MultiFormatWriter multiFormatWriter=new MultiFormatWriter();
        try {
            String data="723984789 2893748923";
            BitMatrix bitMatrix=multiFormatWriter.encode(data, BarcodeFormat.QR_CODE,200,200);
            BarcodeEncoder barcodeEncoder=new BarcodeEncoder();
            Bitmap bitmap=barcodeEncoder.createBitmap(bitMatrix);
            image.setImageBitmap(bitmap);
        }catch (WriterException e){
            e.printStackTrace();
        }
//        gen_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
    }
}
