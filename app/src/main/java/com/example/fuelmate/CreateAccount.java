package com.example.fuelmate;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.HashMap;

public class CreateAccount extends AppCompatActivity {


    private Toolbar toolbar;
    private FirebaseAuth mAuth;
    private Button createAcc;
    private EditText nameE, email, pass, addrR, phoneE;
    private AutoCompleteTextView deptT, clgG;
    private String name,emailId,password,address,phone,dept,clg;
    private ProgressDialog proDiag;
    private DatabaseReference database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);




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
        deptT = findViewById(R.id.dept);
        deptT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deptT.showDropDown();
            }
        });

//        deptT.lis

        phoneE=(EditText)findViewById(R.id.phone);
        clgG = (AutoCompleteTextView) findViewById(R.id.colg);
        clgG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clgG.showDropDown();
            }
        });


        ArrayAdapter<CharSequence> depAdap = ArrayAdapter.createFromResource(CreateAccount.this, R.array.depts, R.layout.support_simple_spinner_dropdown_item);
        depAdap.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        deptT.setAdapter(depAdap);


        ArrayAdapter<CharSequence> colgAdap = ArrayAdapter.createFromResource(CreateAccount.this, R.array.colleges, R.layout.support_simple_spinner_dropdown_item);
        colgAdap.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        clgG.setAdapter(colgAdap);


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
                dept = deptT.getText().toString();
                clg=clgG.getText().toString();


                if (TextUtils.isEmpty(name)) {
                    nameE.setError("Enter Email Address");
                } else if (TextUtils.isEmpty(emailId))
                {
                    email.setError("Enter Email Address");
                }
                else if(TextUtils.isEmpty(password)){
                    pass.setError("Enter Password");
                } else if (TextUtils.isEmpty(address)) {
                    pass.setError("Enter Address");
                } else if (TextUtils.isEmpty(phone)) {
                    pass.setError("Enter Phone");
                } else if (TextUtils.isEmpty(dept)) {
                    pass.setError("Enter Department");
                } else if (TextUtils.isEmpty(clg)) {
                    pass.setError("Enter College");
                } else {
                    proDiag.show();
                    proDiag.setCancelable(false);
                    createAccount(name,emailId,password,address,phone,clg,dept);
                }

            }
        });

    }

    public void createAccount(final String name, final String emailId, String password, final String address, final String phone, final String clg ,final String dept) {

        mAuth.createUserWithEmailAndPassword(emailId,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    database= FirebaseDatabase.getInstance().getReference();

                    HashMap<String,String> data = new HashMap<>();

                    data.put("name", name);
                    data.put("email",emailId.toLowerCase());
                    data.put("address", address);
                    data.put("phone",phone.toLowerCase());
                    data.put("college", clg);
                    data.put("department", dept);
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
                    //Snehaa
                }
            }
        });
    }
}
