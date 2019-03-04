package com.polka.rentplace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.Result;

import static android.Manifest.permission_group.CAMERA;

public class UserPayment extends AppCompatActivity {

    private ZXingScannerView scannerView;
    private static final int REQUEST_CAMERA =1;
    private EditText editText;
    String TAG="MainActivity";
    String ss=null;
    boolean isDialogOpen=false;
    static int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_payment);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkPermission()){
                showScanner();
                //Toast.makeText(this, "Permission is granted!", Toast.LENGTH_SHORT).show();
            }else {
                requestPermission();
            }
        }else {
            showScanner();
        }

    }
//    public void scann(View v){
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//            if(checkPermission()){
//                showScanner();
//                //Toast.makeText(this, "Permission is granted!", Toast.LENGTH_SHORT).show();
//            }else {
//                requestPermission();
//            }
//        }else {
//            showScanner();
//        }
//    }
    private void showScanner(){
        scannerView = new ZXingScannerView(this);
        scannerView.setResultHandler(new ZXingScannerResultHandler());

        setContentView(scannerView);
        scannerView.startCamera();
    }
    private boolean checkPermission(){
        return (ContextCompat.checkSelfPermission(UserPayment.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},REQUEST_CAMERA);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CAMERA:
                if(grantResults.length >0){
                    boolean cameraAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted){
                        showScanner();
                        //Toast.makeText(this, "permissin granted!", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(this, "permissin denied!", Toast.LENGTH_SHORT).show();
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                            if(shouldShowRequestPermissionRationale(CAMERA)){
                                displayAlertMessage("You need to all access for both permission",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                requestPermission();
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
        // super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    public void displayAlertMessage(String message, DialogInterface.OnClickListener listener){
        new AlertDialog.Builder(UserPayment.this)
                .setTitle(message)
                .setPositiveButton("OK",listener)
                .setNegativeButton("Cancel",listener)
                .create()
                .show();
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(scannerView!=null) {
            scannerView.stopCamera();
        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ss=null;
    }

    class ZXingScannerResultHandler implements ZXingScannerView.ResultHandler{

        @Override
        public void handleResult(Result result) {
            String text=result.getText();
            setContentView(R.layout.activity_user_payment);
//            editText = findViewById(R.id.edit_query);
//            editText.setText(text);
            scannerView.stopCamera();
            Toast.makeText(UserPayment.this, "Payed Successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UserPayment.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
