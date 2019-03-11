package com.example.fuelmate;

import android.support.v7.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private Button login,resetPass;
    private EditText email,pass;
    private String emailId,password;
    private ProgressDialog proDiag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Firebase
        mAuth=FirebaseAuth.getInstance();


        //Button
        login=(Button)findViewById(R.id.loginButton);
        resetPass=(Button)findViewById(R.id.resetPass);

        //Input Fields
        email=(EditText)findViewById(R.id.email);
        pass=(EditText)findViewById(R.id.pass);


        //Progress Dialog
        proDiag= new ProgressDialog(Login.this);
        proDiag.setMessage("Logging In");
    }


    @Override
    protected void onStart() {
        super.onStart();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailId=email.getText().toString();
                password=pass.getText().toString();

                if(TextUtils.isEmpty(emailId))
                {
                    email.setError("Enter Email Address");
                }
                else if(TextUtils.isEmpty(password)){
                    pass.setError("Enter Password");
                }
                else {
                    proDiag.show();
                    loginAccount(emailId,password);
                }

            }
        });



        resetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().toString().equals(""))
                {
                    email.setError("Input Email First");
                }
                else {

                    AlertDialog b1=new AlertDialog.Builder(Login.this)
                            .setTitle("Reset Password")
                            .setMessage("Are you sure?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseAuth.getInstance().sendPasswordResetEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(Login.this,"Email sent",Toast.LENGTH_LONG).show();
                                            }
                                            else {
                                                Toast.makeText(Login.this,"Invalid Email",Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).create();
                    b1.show();

                }
            }
        });

    }

    public void loginAccount(String emailId, String password) {

        mAuth.signInWithEmailAndPassword(emailId,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    proDiag.dismiss();
                    Intent i= new Intent(Login.this,MainActivity.class);
                    startActivity(i);
                    finish();

                }
                else {
                    task.toString();
                    Toast.makeText(Login.this,"Something went wrong.",Toast.LENGTH_LONG).show();
                    proDiag.dismiss();
                }
            }
        });
    }
}


