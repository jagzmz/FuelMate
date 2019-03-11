package com.example.fuelmate;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CreateAccount extends AppCompatActivity {


    private Toolbar toolbar;
    private FirebaseAuth mAuth;
    private Button createAcc;
    private EditText nameE,email,pass,addrR,phoneE,deptT,clgG;
    private String name,emailId,password,address,phone,dept,clg;
    private ProgressDialog proDiag;
    private DatabaseReference database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);





        //Toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Firebase
        mAuth=FirebaseAuth.getInstance();



        //Button
        createAcc=(Button)findViewById(R.id.createAccButton);

        //Input Fields
        nameE=(EditText)findViewById(R.id.name);
        email=(EditText)findViewById(R.id.email);
        pass=(EditText)findViewById(R.id.pass);
        addrR=(EditText)findViewById(R.id.addr);
        phoneE=(EditText)findViewById(R.id.phone);
        clgG=(EditText)findViewById(R.id.colg);


        //Progress Dialog
        proDiag= new ProgressDialog(CreateAccount.this);
        proDiag.setMessage("Registering User");


    }


    @Override
    protected void onStart() {
        super.onStart();
        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name=nameE.getText().toString();
                emailId=email.getText().toString();
                password=pass.getText().toString();
                address=addrR.getText().toString();
                phone=phoneE.getText().toString();
                clg=clgG.getText().toString();


                if(TextUtils.isEmpty(emailId))
                {
                    email.setError("Enter Email Address");
                }
                else if(TextUtils.isEmpty(password)){
                    pass.setError("Enter Password");
                }
                else {
                    proDiag.show();
                    proDiag.setCancelable(false);
                    createAccount(name,emailId,password,address,phone,clg);
                }

            }
        });

    }

    public void createAccount(final String name, final String emailId, String password, final String address, final String phone, final String clg) {

        mAuth.createUserWithEmailAndPassword(emailId,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    database= FirebaseDatabase.getInstance().getReference();

                    HashMap<String,String> data = new HashMap<>();

                    data.put("name",name.toLowerCase());
                    data.put("email",emailId.toLowerCase());
                    data.put("address",address.toLowerCase());
                    data.put("phone",phone.toLowerCase());
                    data.put("college",clg.toLowerCase());
                    data.put("image","default");
                    data.put("imateT","defaultT");
                    data.put("preferences","null");



                    HashMap<String,String> colg=new HashMap<String,String>();
                    colg.put("college",clg);

                    database.child("Colleges").child(clg).child(mAuth.getCurrentUser().getUid()).setValue(colg);



                    database.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                proDiag.dismiss();
                                Intent i= new Intent(CreateAccount.this,MainActivity.class);
                                startActivity(i);
                                finish();
                            }
                            else {
                                proDiag.hide();
                                mAuth.signOut();
                                Toast.makeText(CreateAccount.this,"Failed to write database.",Toast.LENGTH_LONG).show();
                            }
                        }
                    });


                }
                else {
                    task.toString();
                    Toast.makeText(CreateAccount.this,"Something went wrong.",Toast.LENGTH_LONG).show();
                    proDiag.dismiss();
                }
            }
        });
    }
}
