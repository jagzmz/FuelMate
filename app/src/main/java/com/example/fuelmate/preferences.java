package com.example.fuelmate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

import static android.content.ContentValues.TAG;

public class preferences extends Fragment {
private SharedPreferences.Editor se;
    private AutoCompleteTextView sp,locality;
    private Button setPref;
    private DatabaseReference mDbRef;
    private FirebaseUser mUser;
    private TextView curPref;
    private String pref, colg, name, dep;
    private HashMap<String, String> dat;

    @Nullable
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

        sp = (AutoCompleteTextView) getView().findViewById(R.id.prefDrop);
        locality = (AutoCompleteTextView) getView().findViewById(R.id.prefLocal);
        locality.setThreshold (2);
            se= getActivity ().getSharedPreferences ("localdata", Context.MODE_PRIVATE).edit ();
        setPref = (Button) getView().findViewById(R.id.setPref);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        curPref = (TextView) getView().findViewById(R.id.curPref);

        sp.setThreshold(100);
        sp.setWidth(setPref.getWidth());


        dat = new HashMap<String, String>();


        mDbRef = FirebaseDatabase.getInstance().getReference();

        mDbRef.child("Users/" + mUser.getUid() + "/preferences").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.getValue().toString().equals("null"))
                    sp.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        name = ((TextView) getActivity().findViewById(R.id.nav_username)).getText().toString();
        colg = ((TextView) getActivity().findViewById(R.id.nav_college)).getText().toString();
        pref = ((TextView) getActivity().findViewById(R.id.nav_pref)).getText().toString();


        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getView().getContext(), R.array.pref, R.layout.support_simple_spinner_dropdown_item);
        sp.setAdapter(adapter);
        final ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getView().getContext(), R.array.locality, R.layout.support_simple_spinner_dropdown_item);
        locality.setAdapter(adapter1);

        sp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.showDropDown();


            }
        });


        mDbRef = FirebaseDatabase.getInstance().getReference();

        mDbRef.child("Users/" + mUser.getUid() + "/preferences").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.getValue().toString().equals("null"))
                    sp.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        setPref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sp.getText().equals("")&&!locality.equals ("")) {

                        se.putString ("locality",locality.getText ().toString ());
                        se.commit ();
                    if (sp.getText().toString().contains("Department")) {
                        mDbRef.child("Users/" + mUser.getUid() + "/preferences").setValue(sp.getText().toString());




                        mDbRef.child("Users/" + mUser.getUid() + "/department").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                //Removing Campus values first
                                mDbRef.child("Preferences/Campus/" + colg + "/" + locality.getText ().toString () + "/" + mUser.getUid()).removeValue();


                                dep = dataSnapshot.getValue().toString();

                                dat.put("name", name);
                                dat.put("college", colg);
                                mDbRef.child("Preferences/Department/" + colg + "/" + dep + "/" + locality.getText ().toString () + "/" + mUser.getUid()).setValue(dat);

                                dat.clear();




                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }

                        });


                    } else {
                        mDbRef.child("Users/" + mUser.getUid() + "/preferences").setValue(sp.getText().toString());
                        mDbRef.child ("Users/"+mUser.getUid ()+"/phone").addListenerForSingleValueEvent (
                                new ValueEventListener () {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        dat.put ("phone",dataSnapshot.getValue ().toString ());
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                }
                        );
                        mDbRef.child("Users/" + mUser.getUid() + "/department").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                dep = dataSnapshot.getValue().toString();

                                mDbRef.child("Preferences/Department/" + colg + "/" + dep + "/"+ locality.getText ().toString () +"/" + mUser.getUid()).removeValue();

                                dat.put("name", name);
                                dat.put("college", colg);

                                mDbRef.child("Preferences/Campus/" + colg + "/" + locality.getText ().toString () + "/" + mUser.getUid()).setValue(dat);

                                dat.clear();


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }


                }


                        getActivity ().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new search ()).commit();


            }


        });


    }
}
