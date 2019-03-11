package com.example.fuelmate;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.HashMap;

public class preferences extends Fragment  {

    private MaterialSpinner sp;
    private Button setPref;
    private DatabaseReference mDbRef;
    private FirebaseUser mUser;
    private TextView curPref;
    private String pref,colg,name;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {



        return inflater.inflate(R.layout.preferences_fragment, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        sp=(MaterialSpinner) getView().findViewById(R.id.prefDrop);
        setPref=(Button) getView().findViewById(R.id.setPref);
        mUser=FirebaseAuth.getInstance().getCurrentUser();
        curPref=(TextView)getView().findViewById(R.id.curPref);


        mDbRef= FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid());

        mDbRef.child("preferences").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                curPref.setText("Current Preference: "+dataSnapshot.getValue().toString().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        name=((TextView)getActivity().findViewById(R.id.nav_username)).getText().toString();
        colg=((TextView)getActivity().findViewById(R.id.nav_college)).getText().toString();
        pref=((TextView)getActivity().findViewById(R.id.nav_pref)).getText().toString();

        mDbRef= FirebaseDatabase.getInstance().getReference();

        String[] items = new String[]{"Departments","Computer Technology","Information Technology","Electronics and telecommunication","Civil Engineering","Mechanical Engineering","Electrical Engineering"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),R.layout.support_simple_spinner_dropdown_item,items);
        sp.setAdapter(adapter);

        setPref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String curP=curPref.getText().toString();

                mDbRef.child("Users/"+mUser.getUid()+"/preferences").setValue(sp.getItems().get(sp.getSelectedIndex()).toString());

                mDbRef.child("Preferences/"+pref+"/"+colg).child(mUser.getUid()).removeValue();


                pref=sp.getItems().get(sp.getSelectedIndex()).toString();

                HashMap<String,String> data=new HashMap<>();
                data.put("name",name);
                data.put("college",colg);

                mDbRef.child("Preferences/"+pref+"/"+colg).child(mUser.getUid()).setValue(data);


            }
        });

    }
}
