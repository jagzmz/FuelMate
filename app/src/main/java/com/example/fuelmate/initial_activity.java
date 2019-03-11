package com.example.fuelmate;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

public class initial_activity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_INTERNET= 1010;
    private static final String TAG ="Initial Activity" ;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDb;
    private FirebaseUser mUser;
    private ProgressBar processing;
    private TextView info;
    private Button login,createAcc;
    private static final int SUCCESS=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_activity);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window= initial_activity.this.getWindow();
            window.setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }



            //Resource Allocation
            processing = (ProgressBar) findViewById(R.id.initProcess);
            processing.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.textColor), PorterDuff.Mode.SRC_ATOP);

            //Buttons
            login = (Button) findViewById(R.id.loginButton);
            createAcc = (Button) findViewById(R.id.createAccountButton);
            login.setBackgroundColor(Color.WHITE);
            createAcc.setBackgroundColor(Color.WHITE);

            //TextView
            info = (TextView) findViewById(R.id.info);

            //Requesting Perms
            showPhoneStatePermission();

        //Initialization
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDb = FirebaseDatabase.getInstance();

        DbUtils chck = new DbUtils();

        chck.check();



    }


    @Override
    protected void onStart() {
        super.onStart();




        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {



                    if (mUser == null) {

                        processing.setVisibility(View.GONE);
                        info.setVisibility(View.GONE);

                        createAcc.setVisibility(View.VISIBLE);
                        login.setVisibility(View.VISIBLE);


                    } else {
                        Intent i = new Intent(initial_activity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                }

        },1000);

        new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if(isInternetAvailable()==false)
                    {
                        AlertDialog a=new AlertDialog.Builder(initial_activity.this).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        }).setMessage("Please check your internet connection and try again").setCancelable(false).create();
                        a.show();
                    }
                }
            });



        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(initial_activity.this,CreateAccount.class);
                startActivityForResult(i,SUCCESS);
//                finish();
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(initial_activity.this,Login.class);
                startActivityForResult(i,SUCCESS);
//                finish();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode)
        {
            case SUCCESS:
                recreate();
                break;
        }
    }

    private void showPhoneStatePermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.INTERNET);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.INTERNET)) {
                showExplanation("Permission Needed", "Rationale", Manifest.permission.READ_PHONE_STATE, MY_PERMISSIONS_REQUEST_INTERNET);
            } else {
                requestPermission(Manifest.permission.READ_PHONE_STATE, MY_PERMISSIONS_REQUEST_INTERNET);
            }
        } else {
//            Toast.makeText(initial_activity.this, "Permission (already) Granted!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this,
                new String[]{permissionName}, permissionRequestCode);
    }


    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String permissions[],
            int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_INTERNET:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(initial_activity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(initial_activity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }
    public boolean isInternetAvailable() {

        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;

    }
}
